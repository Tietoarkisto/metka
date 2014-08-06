package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.TransferFieldType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionApproveRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RemovedInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.series.SeriesAbbreviationUniquenessSearchCommand;
import fi.uta.fsd.metkaSearch.results.BooleanResult;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RevisionApproveRepositoryImpl implements RevisionApproveRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionApproveRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private JSONUtil json;

    @Autowired
    private SearcherComponent searcher;

    @Override
    public Pair<ReturnResult, TransferData> approve(TransferData transferData) {
        // TODO: General and type specific approvals
        Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(transferData.getKey().getId(),
                false, transferData.getConfiguration().getType());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("No revision to approve for "+transferData.getKey().toString());
            return new ImmutablePair<>(dataPair.getLeft(), transferData);
        }
        RevisionData data = dataPair.getRight();
        if(data.getState() != RevisionState.DRAFT) {
            logger.error("Can't approve revision "+data.getKey().toString()+" since it is not in DRAFT state");
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_A_DRAFT, transferData);
        }
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Can't find configuration "+data.getConfiguration().toString()+" and so halting approval process.");
            return new ImmutablePair<>(configPair.getLeft(), transferData);
        }

        ReturnResult result = approveData(data, transferData, configPair.getRight());

        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            data.setState(RevisionState.APPROVED);
            data.setApprovalDate(new LocalDateTime());
            // TODO: Set approvedBy
            Pair<ReturnResult, String> string = json.serialize(data);
            if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
                logger.error("Couldn't serialize data "+data.toString()+", halting approval process");
                return new ImmutablePair<>(string.getLeft(), transferData);
            }

            RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(data.getKey().getId(), data.getKey().getNo()));
            revision.setState(RevisionState.APPROVED);
            revision.setData(string.getRight());

            RevisionableEntity revisionable = em.find(RevisionableEntity.class, data.getKey().getId());
            revisionable.setCurApprovedNo(data.getKey().getNo());

            return new ImmutablePair<>(ReturnResult.APPROVE_SUCCESSFUL, TransferData.buildFromRevisionData(data, RemovedInfo.FALSE));
        } else {
            return new ImmutablePair<>(ReturnResult.APPROVE_FAILED, transferData);
        }
    }

    private ReturnResult approveData(RevisionData revision, TransferData transferData, Configuration configuration) {
        switch(revision.getConfiguration().getType()) {
            case SERIES:
                return approveSeries(revision, transferData, configuration);
            case STUDY:
                return approveStudy(revision, transferData, configuration);
            case STUDY_ATTACHMENT:
                return approveStudyAttachment(revision, transferData, configuration);
            case STUDY_VARIABLES:
                return approveStudyVariables(revision, transferData, configuration);
            case STUDY_VARIABLE:
                return approveStudyVariable(revision, transferData, configuration);
            default:
                return ReturnResult.APPROVE_SUCCESSFUL;
        }
    }

    private ReturnResult approveSeries(RevisionData revision, TransferData transferData, Configuration configuration) {
        // Check that if seriesabbr has been set in this revision that it is unique amongst all series
        Pair<StatusCode, SavedDataField> pair = revision.dataField(SavedDataFieldCall.get("seriesabbr"));
        SavedDataField field = pair.getRight();
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !field.hasModifiedValue()) {
            TransferField tf = transferData.getField("seriesabbr");
            if(tf == null) {
                tf = new TransferField("seriesabbr", TransferFieldType.VALUE);
                transferData.getFields().put(tf.getKey(), tf);
            }
            tf.getErrors().add(FieldError.MISSING_VALUE);
            logger.warn("Series is missing abbreviation, can't approve until it is set");
            return ReturnResult.APPROVE_FAILED;
        }

        // Let's assume that we have not managed to change immutable value and instead just check that if value is set in this revision
        if(!field.hasOriginalValue() && field.hasOriginalValue()) {
            // appreviation has changed from empty to containing something
            // TODO: Make this more sensible
            ResultList<BooleanResult> results = searcher.executeSearch(SeriesAbbreviationUniquenessSearchCommand.build("fi", revision.getKey().getId(), field.getActualValue()));
            // Result list should contain exactly one result
            if(!results.getResults().get(0).getResult()) {
                TransferField tf = transferData.getField("seriesabbr");
                if(tf == null) {
                    tf = new TransferField("seriesabbr", TransferFieldType.VALUE);
                    transferData.getFields().put(tf.getKey(), tf);
                }
                tf.getErrors().add(FieldError.NOT_UNIQUE);
                logger.warn("Series abbreviation is not unique, can't approve until it is changed to unique value");
                return ReturnResult.APPROVE_FAILED;
            }
        }

        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    private ReturnResult approveStudy(RevisionData revision, TransferData transferData, Configuration configuration) {
        ReturnResult result = ReturnResult.APPROVE_FAILED;
        Pair<StatusCode, SavedDataField> pair;
        // Try to approve sub revisions of study. Just get all relevant revisions and check if they are drafts, if so construct TransferData and call
        // approve recursively

        // Try to approve all study attachments linked to this study (this should move files from temporary location to their actual location)
        // TODO:

        // Try to approve study variables linked to this study, this should try to approve all study variables that are linked to it
        // If there are errors in study variables (either the collection or individual variables then just mark an error to study variables field in transferData
        // TODO:

        // TODO: Check that all SELECTION values are still valid (e.g. that they can be found and that the values are not marked deprecated
        // TODO: Check that other references like series are still valid (e.g. they point to existing revisionables

        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            // Approval of sub objects was successful, set aipcomplete if it was not set already
            pair = revision.dataField(SavedDataFieldCall.get("aipcomplete"));
            if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValue()) {
                // aipcomplete has not been set yet. let's try to set it and just assume it succeeded
                revision.dataField(SavedDataFieldCall.set("aipcomplete").setConfiguration(configuration).setChangeMap(revision.getChanges()).setValue(new LocalDate().toString()));
            }
        }
        return result;
    }

    private ReturnResult approveStudyAttachment(RevisionData revision, TransferData transferData, Configuration configuration) {
        // TODO:

        // Check that if the file location has changed then move the file to correct location in file system and update path field

        return ReturnResult.APPROVE_FAILED;
    }

    private ReturnResult approveStudyVariables(RevisionData revision, TransferData transferData, Configuration configuration) {
        // TODO:

        // Loop through all variables and check if they need approval.
        // Try to approve every one but if even one fails then return APPROVE_FAILED since the process of study approval can't continue

        return ReturnResult.APPROVE_FAILED;
    }

    private ReturnResult approveStudyVariable(RevisionData revision, TransferData transferData, Configuration configuration) {
        // There's really nothing to do here right now
        return ReturnResult.APPROVE_SUCCESSFUL;
    }
}
