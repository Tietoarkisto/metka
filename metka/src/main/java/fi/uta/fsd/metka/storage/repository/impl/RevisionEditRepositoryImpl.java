package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionEditRepositoryImpl implements RevisionEditRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionSaveRepositoryImpl.class);

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private GeneralRepository general;

    @Override
    public Pair<ReturnResult, RevisionData> edit(TransferData transferData) {
        Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(
                transferData.getKey().getId(), false, transferData.getConfiguration().getType());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("No revision for "+transferData.getConfiguration().getType()+" with id "+transferData.getKey().getId()+". Can't get editable revision.");
            return dataPair;
        }
        RevisionData data = dataPair.getRight();
        if(data.getState() != RevisionState.DRAFT) {
            // Data is not draft, we need a new revision
            ReturnResult result = checkEditPermissions(data);
            if(result != ReturnResult.CAN_CREATE_DRAFT) {
                logger.warn("User can't create draft revision because: "+result);

            }
            Pair<ReturnResult, Configuration> configPair = configurations.findLatestConfiguration(data.getConfiguration().getType());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                logger.error("Couldn't find newest configuration for "+data.getConfiguration().getType()+" so can't create new editable revision for "+data.toString());
                return new ImmutablePair<>(configPair.getLeft(), data);
            }
        }
        return new ImmutablePair<>(ReturnResult.REVISION_FOUND, data);
    }

    private ReturnResult checkEditPermissions(RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                Pair<StatusCode, SavedDataField> field = data.dataField(SavedDataFieldCall.get("study"));
                if(field.getLeft() != StatusCode.FIELD_FOUND) {
                    logger.error("Didn't find study reference on "+data.toString()+" can't create new draft.");
                    return ReturnResult.REVISIONABLE_NOT_FOUND;
                }
                return checkStudyDraftStatus(field.getRight().valueAsInteger());
            default:
                return ReturnResult.CAN_CREATE_DRAFT;
        }
    }

    private ReturnResult checkStudyDraftStatus(Long id) {
        Pair<ReturnResult, RevisionData> pair = general.getLatestRevisionForIdAndType(id, false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Didn't find revision for study id "+id+" with result "+pair.getLeft());
            return pair.getLeft();
        }
        if(pair.getRight().getState() != RevisionState.DRAFT) {
            return ReturnResult.REVISION_NOT_A_DRAFT;
        }
        // TODO: get user name
        String userName = "";
        if(!pair.getRight().getHandler().equals(userName)) {
            return ReturnResult.USER_NOT_HANDLER;
        }
        return ReturnResult.CAN_CREATE_DRAFT;
    }
}
