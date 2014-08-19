package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionApproveRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.series.SeriesAbbreviationUniquenessSearchCommand;
import fi.uta.fsd.metkaSearch.results.BooleanResult;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

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

        // Do validation
        ReturnResult result = approveData(data, transferData, configPair.getRight());

        // If validation was successful then check all languages that should be approved (i.e. which languages have content
        // that has changed in this revision).
        // If revision has no changed content for given language then that language doesn't need updated approval information.
        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            DateTimeUserPair info = DateTimeUserPair.build();
            Set<Language> changesIn = hasChanges(data, configPair.getRight());
            for(Language language : changesIn) {
                data.getApproved().put(language, info);
            }
            data.setState(RevisionState.APPROVED);
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

            return new ImmutablePair<>(ReturnResult.APPROVE_SUCCESSFUL, TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE));
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

    /**
     * Checks RevisionData for changes in all languages and marks every language that contains changes.
     * Changes are detected only from terminating DataFields i.e. ValueDataFields and ReferenceContainerDataFields.
     * ContainerDataFields can't terminate sensibly and so we don't care about changes in those alone, there has
     * to always be a change in a terminating field within the Container.
     * Languages require that there is at least one change for that language in a field that is marked
     * translatable (in theory there should not be translation data in fields that are not translatable but in practice it
     * is always a possibility that illegal translation data gets to the system one way or another and that changes are not
     * always up to date on the translatability of fields).
     * NOTICE: This method assumes that ChangeMap data in RevisionData is correct and reliable so it is used to
     *         find possible changes. Terminating ValueDataFields are checked for actual value change and ReferenceRows
     *         are checked for saved information that is newer than previous approve info (if any) in RevisionData.
     *         If approval is missed when it should have been set then there's fault in updating Change values somewhere.
     * @param data             RevisionData to check for changes in languages
     * @param configuration    Configuration for RevisionData
     * @return Set containing all Languages that have changes from previous revision
     */
    private Set<Language> hasChanges(RevisionData data, Configuration configuration) {
        Set<Language> changesIn = new HashSet<>();
        for(Change change : data.getChanges().values()) {
            switch(change.getType()) {
                case VALUE:
                    checkValueChange(changesIn, change, data, data, configuration);
                    break;
                case CONTAINER:
                    checkContainerChange(changesIn, (ContainerChange)change, data, data, configuration);
                    break;
            }
        }
        return changesIn;
    }

    private void checkContainerRowChange(Set<Language> changesIn, RowChange rowChange, DataRow row, RevisionData data, Configuration configuration) {
        for(Change change : rowChange.getChanges().values()) {
            switch(change.getType()) {
                case VALUE:
                    checkValueChange(changesIn, change, row, data, configuration);
                    break;
                case CONTAINER:
                    checkContainerChange(changesIn, (ContainerChange)change, row, data, configuration);
                    break;
            }
        }
    }

    private void checkContainerChange(Set<Language> changesIn, ContainerChange change, DataFieldContainer dataFields,
                                      RevisionData data, Configuration configuration) {
        Field fieldConf = configuration.getField(change.getKey());
        if(fieldConf.getType() == FieldType.REFERENCECONTAINER) {
            // Field is a reference container, utilise specialized checking and return since rest of this method
            // deal with checking ContainerDataField instead
            checkReferenceContainerChange(changesIn, change, dataFields, data);
            return;
        }

        Pair<StatusCode, ContainerDataField> fieldPair = dataFields.dataField(ContainerDataFieldCall.get(change.getKey()).setConfiguration(configuration));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // No field, no changes
            return;
        }

        ContainerDataField field = fieldPair.getRight();
        if(!fieldConf.getTranslatable()) {
            // Container is not translatable, check only DEFAULT rows
            checkContainerChangeFor(changesIn, change, data, configuration, field, Language.DEFAULT);

        } else {
            // Container is translatable, check all rows we don't need to forward the language since configuration
            // has forced translatable true for all fields inside this container tree and so all languages will be checked anyway
            for(Language language : Language.values()) {
                checkContainerChangeFor(changesIn, change, data, configuration, field, language);
            }
        }

    }

    private void checkContainerChangeFor(Set<Language> changesIn, ContainerChange change, RevisionData data,
                                         Configuration configuration, ContainerDataField field, Language language) {
        if(!change.hasRowsFor(language)) {
            // No rows for given language, continue loop
            return;
        }
        for(RowChange rowChange : change.getRowsFor(language).values()) {
            // Check only changed rows.
            Pair<StatusCode, DataRow> rowPair = field.getRowWithIdFrom(language, rowChange.getRowId());
            if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
                continue;
            }
            checkContainerRowChange(changesIn, rowChange, rowPair.getRight(), data, configuration);
        }
    }

    private void checkReferenceContainerChange(Set<Language> changesIn, ContainerChange change, DataFieldContainer dataFields,
                                               RevisionData data) {
        // We're checking ReferenceContainerDataField
        // Reference containers are not translated and as such we need to only check saved info in rows to see if they are newer than
        // than the previous approved date

        if(changesIn.contains(Language.DEFAULT)) {
            // If there is already a marked change for default then we don't need to check further
            return;
        }

        Pair<StatusCode, ReferenceContainerDataField> fieldPair = dataFields.dataField(ReferenceContainerDataFieldCall.get(change.getKey()));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // If we have no field then there can be no changes
            return;
        }
        ReferenceContainerDataField field = fieldPair.getRight();
        if(data.approveInfoFor(Language.DEFAULT) == null) {
            // If there's no previous approval date then the check is simple. If we have references
            // we have changes, otherwise we don't have changes.
            if(!field.getReferences().isEmpty()) {
                changesIn.add(Language.DEFAULT);
            }
            return;
        }

        // This is somewhat too complicated but keeps the general theme of checking only the references marked as changed
        // If something is missed with this then that just means there's an error somewhere else.
        for(RowChange rowChange : change.getRowsFor(Language.DEFAULT).values()) {
            Pair<StatusCode, ReferenceRow> rowPair = field.getReferenceWithId(rowChange.getRowId());
            if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
                // No row, no change
                continue;
            }
            ReferenceRow row = rowPair.getRight();
            if(row.getSaved().getTime().compareTo(data.approveInfoFor(Language.DEFAULT).getTime()) > 0) {
                // There is a change in reference container, mark change and break since no further checking is needed
                changesIn.add(Language.DEFAULT);
                break;
            }
        }
    }


    /**
     * Checks single ValueDataField for changes and records all languages where changes have happened.
     * Uses change as a guide but checks all possible values for equality with original value before
     * deciding if change has taken place or not.
     * @param changesIn
     * @param change
     * @param dataFields
     * @param data
     * @param configuration
     */
    private void checkValueChange(Set<Language> changesIn, Change change, DataFieldContainer dataFields,
                                  RevisionData data, Configuration configuration) {
        Field fieldConf = configuration.getField(change.getKey());
        Pair<StatusCode, ValueDataField> fieldPair = dataFields.dataField(ValueDataFieldCall.get(change.getKey()).setConfiguration(configuration));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // Since fields, once set, should never be removed we can assume that no change has happened.
            return;
        }
        ValueDataField field = fieldPair.getRight();
        // Check default

        // Check rest
        if(!fieldConf.getTranslatable()) {
            // field is not translatable, no need to check other than DEFAULT
            checkValueChangeFor(changesIn, change, field, Language.DEFAULT);
        } else {
            // Field is translatable, check all languages
            for(Language language : Language.values()) {
                checkValueChangeFor(changesIn, change, field, language);
            }
        }
    }

    /**
     * Checks given change and field for changes in given language and if change found marks it to given set.
     * If there's already a change for given language then no further checking is done in this method.
     * @param changesIn
     * @param change
     * @param field
     * @param language
     */
    private void checkValueChangeFor(Set<Language> changesIn, Change change, ValueDataField field, Language language) {
        if(!changesIn.contains(language)
                && change.getChangeIn().contains(language)
                && !field.currentForEqualsOriginal(language)) {
            // We have a change in default
            changesIn.add(language);
        }
    }

    private ReturnResult approveSeries(RevisionData revision, TransferData transferData, Configuration configuration) {
        // Check that seriesabbr has been set in this revision and that it is unique amongst all series
        Pair<StatusCode, ValueDataField> pair = revision.dataField(ValueDataFieldCall.get("seriesabbr"));
        // If we can't find the whole seriesabbr field or if the field has no value then we're missing series abbreviation.
        // Add error and set result to APPROVE_FAILED
        ReturnResult result = ReturnResult.APPROVE_SUCCESSFUL;
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
            TransferField tf = transferData.getField("seriesabbr");
            if(tf == null) {
                tf = new TransferField("seriesabbr", TransferFieldType.VALUE);
                transferData.getFields().put(tf.getKey(), tf);
            }
            if(!tf.hasValueFor(Language.DEFAULT)) {
                tf.getValues().put(Language.DEFAULT, new TransferValue());
            }
            tf.getValues().get(Language.DEFAULT).addError(FieldError.MISSING_VALUE);
            logger.warn("Series is missing abbreviation, can't approve until it is set");
            result = ReturnResult.APPROVE_FAILED;
        }

        // Let's assume that we have not managed to change immutable value and instead just check that if value is set in this revision
        // We know that if current result is APPROVE_SUCCESSFUL then we do have some kind of value to check.
        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            ResultList<BooleanResult> results =
                    searcher
                            .executeSearch(
                                    SeriesAbbreviationUniquenessSearchCommand
                                            .build(revision.getKey().getId(), pair.getRight().getActualValueFor(Language.DEFAULT)));
            // Result list should contain exactly one result
            if(!results.getResults().get(0).getResult()) {
                TransferField tf = transferData.getField("seriesabbr");
                if(tf == null) {
                    // We should really have this field but let's be careful
                    tf = new TransferField("seriesabbr", TransferFieldType.VALUE);
                    transferData.getFields().put(tf.getKey(), tf);
                }
                if(!tf.hasValueFor(Language.DEFAULT)) {
                    TransferValue tv = TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, pair.getRight());
                    tf.getValues().put(Language.DEFAULT, new TransferValue());
                }
                tf.getValues().get(Language.DEFAULT).addError(FieldError.NOT_UNIQUE);
                logger.warn("Series abbreviation is not unique, can't approve until it is changed to unique value");
                result = ReturnResult.APPROVE_FAILED;
            }
        }

        return result;
    }

    private ReturnResult approveStudy(RevisionData revision, TransferData transferData, Configuration configuration) {
        ReturnResult result = ReturnResult.APPROVE_SUCCESSFUL;
        Pair<StatusCode, ValueDataField> pair;
        // Try to approve sub revisions of study. Just get all relevant revisions and check if they are drafts, if so construct TransferData and call
        // approve recursively

        // Try to approve all study attachments linked to this study (this should move files from temporary location to their actual location)
        // TODO:

        // Try to approve study variables linked to this study, this should try to approve all study variables that are linked to it
        // If there are errors in study variables (either the collection or individual variables then just mark an error to study variables field in transferData
        ReturnResult variablesCheckResult = checkStudyVariables(revision, transferData, configuration);
        if(variablesCheckResult != ReturnResult.APPROVE_SUCCESSFUL) {
            result = ReturnResult.APPROVE_FAILED;
            // We know that if study variables approval failed there has to be variables field in transfer data since it's added during checking
            // if it was missing before
            TransferField field = transferData.getField("variables");
            field.getValues().get(Language.DEFAULT).addError(FieldError.APPROVE_FAILED);
        }


        // TODO: Check that all SELECTION values are still valid (e.g. that they can be found and that the values are not marked deprecated
        // TODO: Check that other references like series are still valid (e.g. they point to existing revisionables

        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            // Approval of sub objects was successful, set aipcomplete if it was not set already
            pair = revision.dataField(ValueDataFieldCall.get("aipcomplete"));
            if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
                // aipcomplete has not been set yet. let's try to set it and just assume it succeeded
                revision.dataField(ValueDataFieldCall
                        .set("aipcomplete", new Value(new LocalDate().toString(), ""), Language.DEFAULT)
                        .setInfo(DateTimeUserPair.build())
                        .setConfiguration(configuration)
                        .setChangeMap(revision.getChanges()));
            }
        }
        return result;
    }

    private ReturnResult checkStudyVariables(RevisionData revision, TransferData transferData, Configuration configuration) {
        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("variables"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Check that transferData actually has the field with this value (it should).
            // If the field or value is missing then add them
            TransferField field = transferData.getField("variables");
            if(field == null) {
                field = new TransferField("variables", TransferFieldType.VALUE);
                field.getValues().put(Language.DEFAULT, new TransferValue());
                TransferValue transferValue = TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, fieldPair.getRight());
                transferData.getFields().put(field.getKey(), field);
            }

            // We have variables reference and it contains a value
            Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(
                    Long.parseLong(fieldPair.getRight().getActualValueFor(Language.DEFAULT)), false, ConfigurationType.STUDY_VARIABLES);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for study variables "+ fieldPair.getRight().getActualValueFor(Language.DEFAULT)+" even though it should have existed, not halting approval");
                return ReturnResult.APPROVE_SUCCESSFUL;
            }
            RevisionData variables = dataPair.getRight();
            if(variables.getState() != RevisionState.DRAFT) {
                // No need for approval
                return ReturnResult.APPROVE_SUCCESSFUL;
            }
            Pair<ReturnResult, TransferData> approveResult = approve(TransferData.buildFromRevisionData(variables, RevisionableInfo.FALSE));
            if(approveResult.getLeft() != ReturnResult.APPROVE_SUCCESSFUL) {
                logger.error("Tried to approve "+variables.toString()+" and failed with result "+approveResult.getLeft());
                return ReturnResult.APPROVE_FAILED;
            }
        }
        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    private ReturnResult approveStudyAttachment(RevisionData revision, TransferData transferData, Configuration configuration) {
        // TODO:

        // Check that if the file location has changed then move the file to correct location in file system and update path field

        return ReturnResult.APPROVE_FAILED;
    }

    private ReturnResult approveStudyVariables(RevisionData revision, TransferData transferData, Configuration configuration) {
        ReturnResult result = ReturnResult.APPROVE_SUCCESSFUL;

        // Loop through all variables and check if they need approval.
        // Try to approve every one but if even one fails then return APPROVE_FAILED since the process of study approval can't continue
        Pair<StatusCode, ReferenceContainerDataField> fieldPair = revision.dataField(ReferenceContainerDataFieldCall.get("variables"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // Nothing to loop through
            return result;
        }
        ReferenceContainerDataField variables = fieldPair.getRight();
        if(variables.getReferences().size() == 0) {
            // Nothing to loop through
            return result;
        }

        for(ReferenceRow reference : variables.getReferences()) {
            // Just assume that each row is correctly formed
            Pair<ReturnResult, RevisionData> variablePair = general.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), false, ConfigurationType.STUDY_VARIABLE);
            if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for " + reference.getActualValue() + " while approving study variables. Continuin approval.");
                continue;
            }
            RevisionData variable = variablePair.getRight();
            if(variable.getState() != RevisionState.DRAFT) {
                // Variable doesn't require approving
                continue;
            }
            Pair<ReturnResult, TransferData> approveResult = approve(TransferData.buildFromRevisionData(variable, RevisionableInfo.FALSE));
            if(approveResult.getLeft() != ReturnResult.APPROVE_SUCCESSFUL) {
                logger.error("Tried to approve "+variable.toString()+" and failed with result "+approveResult.getLeft());
                result = ReturnResult.APPROVE_FAILED;
                // Continue to approve variables since, no need to mark errors on transfer data since this data will never be sent to client from here
            }
        }

        return result;
    }

    private ReturnResult approveStudyVariable(RevisionData revision, TransferData transferData, Configuration configuration) {
        // There's really nothing to do here right now
        return ReturnResult.APPROVE_SUCCESSFUL;
    }
}
