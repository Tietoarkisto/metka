package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Operation;
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
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionApproveRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// TODO: at the moment does mostly DEFAULT language approval for restrictions
@Repository
public class RevisionApproveRepositoryImpl implements RevisionApproveRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionApproveRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private JSONUtil json;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private RestrictionValidator validator;

    @Override
    public Pair<ReturnResult, TransferData> approve(TransferData transferData) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(transferData.getKey().getId(),
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
        ReturnResult result = approveData(data, transferData);
        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            for(Operation operation : configPair.getRight().getRestrictions()) {
                if(operation.getType() != OperationType.APPROVE) {
                    continue;
                }
                if(!validator.validate(data, operation.getTargets())) {
                    result = ReturnResult.APPROVE_FAILED_DURING_VALIDATION;
                    break;
                }
            }
        }

        // If validation was successful then check all languages that should be approved (i.e. which languages have content
        // that has changed in this revision).
        // If revision has no changed content for given language then that language doesn't need updated approval information.
        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            DateTimeUserPair info = DateTimeUserPair.build();
            Set<Language> changesIn = hasChanges(data, configPair.getRight());
            for(Language language : changesIn) {
                data.getApproved().put(language, info);
            }

            // Do final operations before saving data to
            finalizeApproval(data);

            data.setState(RevisionState.APPROVED);
            data.setHandler("");
            Pair<SerializationResults, String> string = json.serialize(data);
            if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
                logger.error("Couldn't serialize data "+data.toString()+", halting approval process");
                return new ImmutablePair<>(ReturnResult.APPROVE_FAILED, transferData);
            }

            ReturnResult updateResult = revisions.updateRevisionData(data);

            /*RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(data.getKey().getId(), data.getKey().getNo()));
            revision.setState(RevisionState.APPROVED);
            revision.setData(string.getRight());*/

            /*

            RevisionableEntity revisionable = em.find(RevisionableEntity.class, data.getKey().getId());
            revisionable.setCurApprovedNo(data.getKey().getNo());*/

            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(updateResult, transferData);
            }

            return new ImmutablePair<>(ReturnResult.APPROVE_SUCCESSFUL, TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE));
        } else {
            return new ImmutablePair<>(result, transferData);
        }
    }

    // There's no point in sending configuration to here since all of the checks are very much dependent on knowledge of the content.
    // When restriction configuration is done then we can apply it here but even then it can be fetched at this method.
    // Also TransferData is not needed in sub object approvals since it's never going to be returned to user.
    private ReturnResult approveData(RevisionData revision, TransferData transferData) {
        switch(revision.getConfiguration().getType()) {
            case SERIES:
                return approveSeries(revision, transferData);
            case STUDY:
                return approveStudy(revision, transferData);
            case STUDY_ATTACHMENT:
                return approveStudyAttachment(revision);
            case STUDY_VARIABLES:
                return approveStudyVariables(revision);
            case STUDY_VARIABLE:
                return approveStudyVariable(revision);
            default:
                return ReturnResult.APPROVE_SUCCESSFUL;
        }
    }

    private ReturnResult approveSeries(RevisionData revision, TransferData transferData) {
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
            tf.addErrorFor(Language.DEFAULT, FieldError.MISSING_VALUE);
            logger.warn("Series is missing abbreviation, can't approve until it is set");
            result = ReturnResult.APPROVE_FAILED;
        }

        // Let's assume that we have not managed to change immutable value and instead just check that if value is set in this revision
        // We know that if current result is APPROVE_SUCCESSFUL then we do have some kind of value to check.
        if(result == ReturnResult.APPROVE_SUCCESSFUL) {
            // This should be performed by RestrictionValidation although we don't get failure conditions yet
            /*ResultList<BooleanResult> results =
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
                    transferData.addField(tf);
                }
                if(!tf.containsValueFor(Language.DEFAULT)) {
                    TransferValue tv = TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, pair.getRight());
                    tf.addValueFor(Language.DEFAULT, tv);
                }
                tf.addErrorFor(Language.DEFAULT, FieldError.NOT_UNIQUE);
                logger.warn("Series abbreviation is not unique, can't approve until it is changed to unique value");
                result = ReturnResult.APPROVE_FAILED;
            }*/
        }

        return result;
    }

    private ReturnResult approveStudy(RevisionData revision, TransferData transferData) {
        ReturnResult result = ReturnResult.APPROVE_SUCCESSFUL;
        // Try to approve sub revisions of study. Just get all relevant revisions and check if they are drafts, if so construct TransferData and call
        // approve recursively

        // Try to approve all study attachments linked to this study (this should move files from temporary location to their actual location)
        ReturnResult studyAttachmentCheckResult = checkStudyAttachments(revision, transferData);
        if(studyAttachmentCheckResult != ReturnResult.APPROVE_SUCCESSFUL) {
            result = ReturnResult.APPROVE_FAILED;
            // For study attachment approval to fail there has to be a field and content.
            transferData.getField("files").addError(FieldError.APPROVE_FAILED);
        }

        // Try to approve study variables linked to this study, this should try to approve all study variables that are linked to it
        // If there are errors in study variables (either the collection or individual variables then just mark an error to study variables field in transferData
        ReturnResult variablesCheckResult = checkStudyVariables(revision, transferData);
        if(variablesCheckResult != ReturnResult.APPROVE_SUCCESSFUL) {
            result = ReturnResult.APPROVE_FAILED;
            // We know that if study variables approval failed there has to be variables field in transfer data since it's added during checking
            // if it was missing before
            TransferField field = transferData.getField("variables");
            field.addErrorFor(Language.DEFAULT, FieldError.APPROVE_FAILED);
        }

        // TODO: Check all required fields using restriction configuration

        // TODO: Check that all SELECTION values are still valid (e.g. that they can be found and that the values are not marked deprecated
        // TODO: Check that other references like series are still valid (e.g. they point to existing revisionables

        return result;
    }

    private ReturnResult approveStudyAttachment(RevisionData revision) {
        if(checkTopFieldForValue(revision, "file", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        File file = new File(revision.dataField(ValueDataFieldCall.get("file")).getRight().getActualValueFor(Language.DEFAULT));
        if(!file.exists() || file.isDirectory()) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "fileaip", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "filecategory", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "filedip", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "filelanguage", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "fileoriginal", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        if(checkTopFieldForValue(revision, "filepublication", Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            return ReturnResult.APPROVE_FAILED;
        }

        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    private ReturnResult checkTopFieldForValue(RevisionData revision, String key, Language language) {
        if(checkFieldPairForValue(revision.dataField(ValueDataFieldCall.get(key)), Language.DEFAULT) == ReturnResult.APPROVE_FAILED) {
            logger.error("There has to be a non empty value in "+key+" for study attachment to be approved.");
            return ReturnResult.APPROVE_FAILED;
        }
        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    /**
     * Checks that given field pair contains a value (can be extended to check for valid value in selection).
     * Returns APPROVE_FAILED if not valid.
     * @param fieldPair Pair - StatusCode, ValueDataField
     * @param language Language
     * @return ReturnResult
     */
    private ReturnResult checkFieldPairForValue(Pair<StatusCode, ValueDataField> fieldPair, Language language) {
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(language)) {
            return ReturnResult.APPROVE_FAILED;
        }
        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    private ReturnResult approveStudyVariables(RevisionData revision) {
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
            Pair<ReturnResult, RevisionData> variablePair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), false, ConfigurationType.STUDY_VARIABLE);
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

    private ReturnResult approveStudyVariable(RevisionData revision) {
        // There's really nothing to do here right now
        return ReturnResult.APPROVE_SUCCESSFUL;
    }

    /**
     * This does operations that have to be done just before revision can be saved to database.
     * These operations should be such that they can not fail and that they can not affect any other data.
     * @param revision    RevisionData to be finalized
     */
    private void finalizeApproval(RevisionData revision) {
        switch(revision.getConfiguration().getType()) {
            case STUDY:
                finalizeStudyApproval(revision);
                break;
            case STUDY_ATTACHMENT:
                finalizeStudyAttachmentApproval(revision);
                break;
            default:
                break;
        }
    }

    /**
     * Checks that if there are approved languages that don't yet have aipcomplete date then inserts
     * that approval time to aipcomplete for that language;
     * @param revision    RevisionData to be finalized
     */
    private void finalizeStudyApproval(RevisionData revision) {
        Pair<StatusCode, ValueDataField> aipcompletePair = revision.dataField(ValueDataFieldCall.get("aipcomplete"));
        for(Language language : Language.values()) {
            if(revision.getApproved().containsKey(language) && revision.getApproved().get(language).getTime() != null) {
                // We have approved value for this language, check if it's missing from aipcomplete
                if(aipcompletePair.getLeft() != StatusCode.FIELD_FOUND || !aipcompletePair.getRight().hasValueFor(language)) {
                    aipcompletePair = revision.dataField(ValueDataFieldCall
                            .set("aipcomplete", new Value(new LocalDate(revision.getApproved().get(language).getTime()).toString()), language)
                            .setInfo(DateTimeUserPair.build())
                            .setChangeMap(revision.getChanges()));
                }
            }
        }
    }

    /**
     * Check that if the current file path differs from original file path (there has to be a file to approve study attachment)
     * then calculate a correct long term path for the file and move it there (possibly replacing a previous file in the process).
     *
     * @param revision
     */
    private void finalizeStudyAttachmentApproval(RevisionData revision) {
        // From this point onwards we can assume that all relevant fields have values since we can't be here without approved values
        String pathFromRoot = "/";

        Pair<ReturnResult, String> fileDirectory = revisions.getStudyFileDirectory(
                Long.parseLong(revision.dataField(ValueDataFieldCall.get("study")).getRight().getActualValueFor(Language.DEFAULT)));

        if(fileDirectory.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            logger.error("Could not find revisionable when fetching file root for study in attachment "+revision.toString());
            return;
        }

        String pathRoot = fileDirectory.getRight();

        // Get path
        ValueDataField pathField = revision.dataField(ValueDataFieldCall.get("file")).getRight();
        String fileName = FilenameUtils.getName(pathField.getActualValueFor(Language.DEFAULT));

        // If path current differs from path original then we need to move the file
        boolean requiresMove = !pathField.currentForEqualsOriginal(Language.DEFAULT);

        String origValue = revision.dataField(ValueDataFieldCall.get("fileoriginal")).getRight().getActualValueFor(Language.DEFAULT);
        boolean origLocation = origValue.equals("1");

        if(origLocation) pathFromRoot += "original";

        boolean dataLocation = false;
        if(!origLocation) {
            String namePrefix = fileName.substring(0, 3).toUpperCase();
            switch(namePrefix) {
                default:
                    dataLocation = false;
                    break;
                case "ARF":
                case "SYF":
                case "ANF":
                case "DAF":
                    dataLocation = true;
                    break;

            }

            if(dataLocation) pathFromRoot += "data";
        }

        String destLoc = pathRoot+pathFromRoot+(pathFromRoot.length() > 1 ? "/" : "");

        // If requiresMove is false check if pathFromRoot differs from current path and if so then
        if(!requiresMove) {
            if(!(destLoc+fileName).equals(pathField.getActualValueFor(Language.DEFAULT))) {
                requiresMove = true;
            }
        }
        try {
            // If we need to move the file then try to move the file
            if(requiresMove) FileUtils.moveFileToDirectory(new File(pathField.getActualValueFor(Language.DEFAULT)), new File(destLoc), true);
        } catch(IOException ioe) {
            logger.error("IOException when trying to move file from "+pathField.getActualValueFor(Language.DEFAULT)+" to "+destLoc, ioe);
        }

        // TODO: Check if filedip value is correct for file data
        ValueDataField filedip = revision.dataField(ValueDataFieldCall.get("filedip")).getRight();

        if(!filedip.getActualValueFor(Language.DEFAULT).equals("2")) {
            // User has selected something else than 'No' as the filedip value, check if this is valid and correct if not
            boolean fixdip = false;
            if(origLocation) {
                fixdip = true;
            }

            if(!fixdip && FilenameUtils.getExtension(fileName).toUpperCase().equals("XML")) {
                fixdip = true;
            }

            if(!fixdip && fileName.substring(0, 2).toUpperCase().equals("AR")) {
                fixdip = true;
            }

            if(!fixdip) {
                switch(fileName.substring(0, 3).toUpperCase()) {
                    case "SYF":
                    case "ANF":
                        fixdip = true;
                        break;
                }
            }

            if(fixdip) {
                // TODO: We need to inform the user that this value was changed automatically
                // We need to fix filedip to 2 (i.e. 'No'), let's just assume that this succeeds
                revision.dataField(ValueDataFieldCall.set("filedip", new Value("2"), Language.DEFAULT));
            }
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
            if(change instanceof ContainerChange) {
                checkContainerChange(changesIn, (ContainerChange)change, data, data, configuration);
            } else {
                checkValueChange(changesIn, change, data, configuration);
            }
        }
        return changesIn;
    }

    private void checkContainerRowChange(Set<Language> changesIn, RowChange rowChange, DataRow row, RevisionData data, Configuration configuration) {
        for(Change change : rowChange.getChanges().values()) {
            if(change instanceof ContainerChange) {
                checkContainerChange(changesIn, (ContainerChange)change, row, data, configuration);
            } else {
                checkValueChange(changesIn, change, row, configuration);
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
        if(!change.hasRows()) {
            // No rows for given language, continue loop
            return;
        }
        for(RowChange rowChange : change.getRows().values()) {
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
        for(RowChange rowChange : change.getRows().values()) {
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
     * @param changesIn        Set for all languages that have changes
     * @param change           Change object to check
     * @param dataFields       DataFieldContainer containing field to check
     * @param configuration    Configuration
     */
    private void checkValueChange(Set<Language> changesIn, Change change, DataFieldContainer dataFields, Configuration configuration) {
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
     * @param changesIn        Set for all languages that have changes
     * @param change           Change object to check
     * @param field            ValueDataField to check for changes
     * @param language         Language to check changes for
     */
    private void checkValueChangeFor(Set<Language> changesIn, Change change, ValueDataField field, Language language) {
        if(!changesIn.contains(language)
                && change.getChangeIn().contains(language)
                && !field.currentForEqualsOriginal(language)) {
            // We have a change in default
            changesIn.add(language);
        }
    }



    private ReturnResult checkStudyAttachments(RevisionData revision, TransferData transferData) {
        Pair<StatusCode, ReferenceContainerDataField> pair = revision.dataField(ReferenceContainerDataFieldCall.get("files"));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || pair.getRight().getReferences().isEmpty()) {
            return ReturnResult.APPROVE_SUCCESSFUL;
        }

        ReturnResult result = ReturnResult.APPROVE_SUCCESSFUL;
        TransferField tf = transferData.getField("files");
        for(ReferenceRow reference : pair.getRight().getReferences()) {
            Pair<ReturnResult, RevisionData> variablePair =
                    revisions.getLatestRevisionForIdAndType(
                        Long.parseLong(reference.getActualValue()),
                        false, ConfigurationType.STUDY_ATTACHMENT);
            if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for " + reference.getActualValue() + " while approving study attachments. Continuin approval.");
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
                // Let's mark the specific TransferRow with approval error.
                // Missing rows should have been inserted during saving process before this phase so we can be confident that everything exists
                // and since we are returning APPROVE_FAILED from here the TransferData provided to this method is the one that will be returned
                // to the user.
                tf.getRow(reference.getRowId()).addError(FieldError.APPROVE_FAILED);
            }
        }

        return result;
    }

    private ReturnResult checkStudyVariables(RevisionData revision, TransferData transferData) {
        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("variables"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Check that transferData actually has the field with this value (it should).
            // If the field or value is missing then add them
            TransferField field = transferData.getField("variables");
            if(field == null) {
                field = new TransferField("variables", TransferFieldType.VALUE);
                TransferValue transferValue = TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, fieldPair.getRight());
                field.addValueFor(Language.DEFAULT, transferValue);
                transferData.getFields().put(field.getKey(), field);
            }

            // We have variables reference and it contains a value
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
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
}
