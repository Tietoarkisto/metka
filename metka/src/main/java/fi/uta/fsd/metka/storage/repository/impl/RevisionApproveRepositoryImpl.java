/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.restrictions.ValidateResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.AipCompletePayload;
import fi.uta.fsd.metkaAmqp.payloads.VersionChangePayload;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

// TODO: at the moment does mostly DEFAULT language approval for restrictions
@Repository
public class RevisionApproveRepositoryImpl implements RevisionApproveRepository {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private JSONUtil json;

    @Autowired
    private RevisionRemoveRepository remove;

    @Autowired
    private RestrictionValidator validator;

    @Autowired
    private Cascader cascader;

    @Autowired
    private Messenger messenger;

    @Autowired
    private SearcherComponent searcher;

    @Override
    public Pair<OperationResponse, TransferData> approve(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }

        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(transferData.getKey().getId(), false, transferData.getConfiguration().getType());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "No revision to approve for " + transferData.getKey().toString());
            return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), transferData);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(transferData.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Can't find configuration "+transferData.getConfiguration().toString()+" and so halting approval process.");
            return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), transferData);
        }

        Configuration configuration = configPair.getRight();

        RevisionData data = dataPair.getRight();
        if(data.getState() != RevisionState.DRAFT) {
            // Still do cascade since there could be drafts under this revision
            for(Operation operation : configuration.getCascade()) {
                if(!(operation.getType() == OperationType.APPROVE || operation.getType() == OperationType.ALL)) {
                    continue;
                }
                cascader.cascade(CascadeInstruction.build(OperationType.APPROVE, info), data, operation.getTargets(), configuration);
            }

            Logger.info(getClass(), "Can't approve revision "+data.getKey().toString()+" since it is not in DRAFT state");
            return new ImmutablePair<>(OperationResponse.build(ReturnResult.REVISION_NOT_A_DRAFT), transferData);
        }

        // Do validation
        for(Operation operation : configuration.getRestrictions()) {
            if(!(operation.getType() == OperationType.APPROVE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            ValidateResult vr = validator.validate(data, operation.getTargets(), configuration);
            if(!vr.getResult()) {
                return new ImmutablePair<>(
                        OperationResponse.build(vr),
                        transferData);
            }
        }

        ReturnResult result = ReturnResult.OPERATION_SUCCESSFUL;

        // Do cascade
        for(Operation operation : configuration.getCascade()) {
            if(!(operation.getType() == OperationType.APPROVE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!cascader.cascade(CascadeInstruction.build(OperationType.APPROVE, info), data, operation.getTargets(), configuration)) {
                result = ReturnResult.CASCADE_FAILURE;
            }
        }
        // If cascade fails then don't approve this revision
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {
            return new ImmutablePair<>(OperationResponse.build(result), transferData);
        }

        // Do approve
        result = approveData(data, transferData);

        // TODO: Check that all SELECTION values are still valid (e.g. that they can be found and that the values are not marked deprecated
        // TODO: Check that other references like series are still valid (e.g. they point to existing revisionables

        // If validation was successful then check all languages that should be approved (i.e. which languages have content
        // that has changed in this revision).
        // If revision has no changed content for given language then that language doesn't need updated approval information.
        if(result == ReturnResult.OPERATION_SUCCESSFUL) {
            Set<Language> changesIn = hasChanges(data, configuration);

            // No changes in this revision, remove it instead
            if(changesIn.isEmpty()) {
                remove.removeDraft(transferData.getKey(), info);
                return new ImmutablePair<>(OperationResponse.build(ReturnResult.NO_CHANGES), transferData);
            }

            for(Language language : changesIn) {
                data.approveRevision(language, new ApproveInfo(data.getKey().getNo(), info));
            }

            // Do final operations before saving data to database
            result = finalizeApproval(data, configuration, info);
            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return new ImmutablePair<>(OperationResponse.build(result), transferData);
            }

            data.setState(RevisionState.APPROVED);
            data.setHandler("");
            Pair<SerializationResults, String> string = json.serialize(data);
            if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
                Logger.error(getClass(), "Couldn't serialize data "+data.toString()+", halting approval process");
                return new ImmutablePair<>(OperationResponse.build(ReturnResult.OPERATION_FAIL), transferData);
            }

            ReturnResult updateResult = revisions.updateRevisionData(data);

            /*RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(data.getKey().getId(), data.getKey().getNo()));
            revision.setState(RevisionState.APPROVED);
            revision.setData(string.getRight());*/

            /*

            RevisionableEntity revisionable = em.find(RevisionableEntity.class, data.getKey().getId());
            revisionable.setCurApprovedNo(data.getKey().getNo());*/

            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(OperationResponse.build(updateResult), transferData);
            }

            revisions.indexRevision(data.getKey());
            return new ImmutablePair<>(OperationResponse.build(ReturnResult.OPERATION_SUCCESSFUL), TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE));
        } else {
            return new ImmutablePair<>(OperationResponse.build(result), transferData);
        }
    }

    // There's no point in sending configuration to here since all of the checks are very much dependent on knowledge of the content.
    // When restriction configuration is done then we can apply it here but even then it can be fetched at this method.
    // Also TransferData is not needed in sub object approvals since it's never going to be returned to user.
    private ReturnResult approveData(RevisionData revision, TransferData transferData) {
        switch(revision.getConfiguration().getType()) {
            /*case STUDY:
                //return approveStudy(revision, transferData);
            case STUDY_ATTACHMENT:
                //return approveStudyAttachment(revision);
            case STUDY_VARIABLES:
                //return approveStudyVariables(revision);*/
            default:
                return ReturnResult.OPERATION_SUCCESSFUL;
        }
    }

    /*private ReturnResult approveStudy(RevisionData revision, TransferData transferData) {
        ReturnResult result = ReturnResult.OPERATION_SUCCESSFUL;*/
        // Try to approve sub revisions of study. Just get all relevant revisions and check if they are drafts, if so construct TransferData and call
        // approve recursively

        // Try to approve all study attachments linked to this study (this should move files from temporary location to their actual location)
        /*ReturnResult studyAttachmentCheckResult = checkStudyAttachments(revision, transferData);
        if(studyAttachmentCheckResult != ReturnResult.OPERATION_SUCCESSFUL) {
            result = ReturnResult.OPERATION_FAIL;
            // For study attachment approval to fail there has to be a field and content.
            transferData.getField("files").addError(FieldError.APPROVE_FAILED);
        }*/

        // Try to approve study variables linked to this study, this should try to approve all study variables that are linked to it
        // If there are errors in study variables (either the collection or individual variables then just mark an error to study variables field in transferData
        /*ReturnResult variablesCheckResult = checkStudyVariables(revision, transferData);
        if(variablesCheckResult != ReturnResult.OPERATION_SUCCESSFUL) {
            result = ReturnResult.OPERATION_FAIL;
            // We know that if study variables approval failed there has to be variables field in transfer data since it's added during checking
            // if it was missing before
            TransferField field = transferData.getField(Fields.VARIABLES);
            field.addErrorFor(Language.DEFAULT, FieldError.APPROVE_FAILED);
        }*/
/*
        return result;
    }*/

    /*private ReturnResult approveStudyAttachment(RevisionData revision) {
        // File status has been checked during save, we don't need to do it again here.

        return ReturnResult.OPERATION_SUCCESSFUL;
    }*/

/*
    private ReturnResult approveStudyVariables(RevisionData revision) {
        ReturnResult result = ReturnResult.OPERATION_SUCCESSFUL;
*/

        /*// Loop through all variables and check if they need approval.
        // Try to approve every one but if even one fails then return APPROVE_FAILED since the process of study approval can't continue
        Pair<StatusCode, ReferenceContainerDataField> fieldPair = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // Nothing to loop through
            return result;
        }
        ReferenceContainerDataField variables = fieldPair.getRight();
        if(!variables.hasRows()) {
            // Nothing to loop through
            return result;
        }

        // TODO: Cascade these using operations
        for(ReferenceRow reference : variables.getReferences()) {
            // Just assume that each row is correctly formed
            Pair<ReturnResult, RevisionData> variablePair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), false, ConfigurationType.STUDY_VARIABLE);
            if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for " + reference.getActualValue() + " while approving study variables. Continuin approval.");
                continue;
            }
            RevisionData variable = variablePair.getRight();
            if(variable.getState() != RevisionState.DRAFT) {
                // Variable doesn't require approving
                continue;
            }
            Pair<ReturnResult, TransferData> approveResult = approve(TransferData.buildFromRevisionData(variable, RevisionableInfo.FALSE));
            if(approveResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                Logger.error(getClass(), "Tried to approve "+variable.toString()+" and failed with result "+approveResult.getLeft());
                result = ReturnResult.OPERATION_FAIL;
                // Continue to approve variables since, no need to mark errors on transfer data since this data will never be sent to client from here
            }
        }*/
/*
        return result;
    }*/

    /**
     * This does operations that have to be done just before revision can be saved to database.
     * These operations should be such that they can not fail and that they can not affect any other data.
     * @param revision    RevisionData to be finalized
     */
    private ReturnResult finalizeApproval(RevisionData revision, Configuration configuration, DateTimeUserPair info) {
        switch(revision.getConfiguration().getType()) {
            case STUDY:
                return finalizeStudyApproval(revision, info);
            case STUDY_ERROR:
                finalizeStudyErrorApproval(revision, configuration);
                return ReturnResult.OPERATION_SUCCESSFUL;
            default:
                return ReturnResult.OPERATION_SUCCESSFUL;
        }
    }

    /**
     * Checks that if there are approved languages that don't yet have aipcomplete date then inserts
     * that approval time to aipcomplete for that language;
     * @param revision    RevisionData to be finalized
     */
    private ReturnResult finalizeStudyApproval(RevisionData revision, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<StatusCode, ValueDataField> aipcompletePair = revision.dataField(ValueDataFieldCall.get("aipcomplete"));
        for(Language language : Language.values()) {
            if(revision.isApprovedFor(language)) {
                // We have approved value for this language, check if it's missing from aipcomplete
                if(aipcompletePair.getLeft() != StatusCode.FIELD_FOUND || !aipcompletePair.getRight().hasValueFor(language)) {
                    messenger.sendAmqpMessage(messenger.FB_AIP, new AipCompletePayload(revision, language, "", info.getTime().toString()));
                    aipcompletePair = revision.dataField(ValueDataFieldCall
                            .set("aipcomplete", new Value(new LocalDate(revision.approveInfoFor(language).getApproved().getTime()).toString()), language)
                            .setInfo(info)
                            .setChangeMap(revision.getChanges()));
                }
            }
        }

        if(revision.getChange(Fields.DATAVERSIONS) != null || revision.getChange(Fields.DESCVERSIONS) != null) {
            messenger.sendAmqpMessage(messenger.FB_VERSION_CHANGES, new VersionChangePayload(revision));
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    /**
     * Special case check for study errors.
     * Checks that if stidy related to study error has combined error score of >= 10
     * then sends a AMQP-message about that
     * @param revision StudyError revision data
     * @return Always returns OPERATION_SUCCESSFUL
     */
    private void finalizeStudyErrorApproval(RevisionData revision, Configuration configuration) {
        revisions.sendStudyErrorMessageIfNeeded(revision, configuration);
    }

    /**
     * Checks RevisionData for changes in all languages and marks every language that contains changes.
     * Changes are detected only from terminating DataFields i.e. ValueDataFields and ReferenceContainerDataFields.
     * ContainerDataFields can't terminate sensibly and so we don't care about changes in those alone, there has
     * to always be a change in a terminating field within the Container.
     *
     * This process checks the actual fields for changes and does not rely on change map since definite data is needed.
     * ValueDataFields are checked for current value and reference containers are checked for unapproved rows.
     *
     * @param data             RevisionData to check for changes in languages
     * @param configuration    Configuration for RevisionData
     * @return Set containing all Languages that have changes from previous revision
     */
    private Set<Language> hasChanges(RevisionData data, Configuration configuration) {
        Set<Language> changesIn = new HashSet<>();
        for(DataField dataField : data.getFields().values()) {
            boolean missing = false;
            for(Language l : Language.values()) {
                if(!changesIn.contains(l)) {
                    missing = true;
                    break;
                }
            }
            if(!missing) {
                // We have a change in all languages, no point in continuing
                break;
            }

            if(dataField instanceof ContainerDataField) {
                checkContainerForChanges(changesIn, (ContainerDataField)dataField, configuration);
            } else {
                checkTerminatingFieldForChanges(changesIn, dataField, configuration);
            }
        }
        return changesIn;
    }

    private void checkContainerForChanges(Set<Language> changesIn, ContainerDataField container, Configuration configuration) {
        Field fieldConf = configuration.getField(container.getKey());

        if(!fieldConf.getTranslatable()) {
            // Container is not translatable, check only DEFAULT rows
            checkContainerForChangesIn(changesIn, container, configuration, Language.DEFAULT);

        } else {
            // Container is translatable, check all rows we don't need to forward the language since configuration
            // has forced translatable true for all fields inside this container tree and so all languages will be checked anyway
            for(Language language : Language.values()) {
                if(changesIn.contains(language)) {
                    // We can skip the language since we know that all values inside the row will be of the given language
                    continue;
                }
                checkContainerForChangesIn(changesIn, container, configuration, language);
            }
        }

    }

    private void checkContainerForChangesIn(Set<Language> changesIn, ContainerDataField container, Configuration configuration, Language language) {
        if(!container.hasRowsFor(language)) {
            // No rows for given language, continue loop
            return;
        }
        for(DataRow row : container.getRowsFor(language)) {
            // We can shortcut some checks.
            // Unapproved rows and deleted rows are automatically changes in provided language.
            // We can set the change and then terminate the search later
            if(row.getUnapproved() || row.getRemoved()) {
                changesIn.add(language);
            }

            // Check only changed rows.
            for(DataField dataField : row.getFields().values()) {
                if(dataField instanceof ContainerDataField) {
                    checkContainerForChanges(changesIn, (ContainerDataField)dataField, configuration);
                } else {
                    checkTerminatingFieldForChanges(changesIn, dataField, configuration);
                }
            }
        }
    }

    private void checkTerminatingFieldForChanges(Set<Language> changesIn, DataField dataField, Configuration configuration) {
        if(dataField instanceof ReferenceContainerDataField) {
            checkReferenceContainerForChanges(changesIn, (ReferenceContainerDataField)dataField);
        } else {
            checkValueForChanges(changesIn, (ValueDataField)dataField, configuration);
        }
    }

    private void checkReferenceContainerForChanges(Set<Language> changesIn, ReferenceContainerDataField container) {
        // We're checking ReferenceContainerDataField
        // Reference containers are not translated and so we are only checking for changes in DEFAULT language.
        // Furthermore reference rows are not updatable and so the only change we are interested in is added or removed rows.
        // Rows that are added have property 'unapproved' set to true so we can check additions with this.
        // Rows that are removed are not copied to new revisions so removed rows also constitute a change

        if(changesIn.contains(Language.DEFAULT)) {
            // If there is already a marked change for default then we don't need to check further
            return;
        }

        for(ReferenceRow row : container.getReferences()) {
            if(row.getUnapproved() || row.getRemoved()) {
                changesIn.add(Language.DEFAULT);
            }
        }
    }

    /**
     * Checks single ValueDataField for changes and records all languages where changes have happened.
     * Uses change as a guide but checks all possible values for equality with original value before
     * deciding if change has taken place or not.
     * @param changesIn        Set for all languages that have changes
     * @param valueField       Value field object to check
     * @param configuration    Configuration
     */
    private void checkValueForChanges(Set<Language> changesIn, ValueDataField valueField, Configuration configuration) {
        Field fieldConf = configuration.getField(valueField.getKey());

        if(!fieldConf.getTranslatable() && !changesIn.contains(Language.DEFAULT)) {
            // field is not translatable, no need to check other than DEFAULT
            checkValueForChangesIn(changesIn, valueField, Language.DEFAULT);
        } else {
            // Field is translatable, check all languages
            for(Language language : Language.values()) {
                if(changesIn.contains(language)) {
                    continue;
                }
                checkValueForChangesIn(changesIn, valueField, language);
            }
        }
    }

    /**
     * Checks given change and field for changes in given language and if change found marks it to given set.
     * If there's already a change for given language then no further checking is done in this method.
     * @param changesIn        Set for all languages that have changes
     * @param valueField            ValueDataField to check for changes
     * @param language         Language to check changes for
     */
    private void checkValueForChangesIn(Set<Language> changesIn, ValueDataField valueField, Language language) {
        if(valueField.hasCurrentFor(language) && !valueField.currentForEqualsOriginal(language)) {
            changesIn.add(language);
        }
    }


    /*private ReturnResult checkStudyAttachments(RevisionData revision, TransferData transferData) {
        Pair<StatusCode, ReferenceContainerDataField> pair = revision.dataField(ReferenceContainerDataFieldCall.get("files"));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || pair.getRight().getReferences().isEmpty()) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        ReturnResult result = ReturnResult.OPERATION_SUCCESSFUL;
        TransferField tf = transferData.getField("files");
        for(ReferenceRow reference : pair.getRight().getReferences()) {
            Pair<ReturnResult, RevisionData> variablePair =
                    revisions.getLatestRevisionForIdAndType(
                        Long.parseLong(reference.getActualValue()),
                        false, ConfigurationType.STUDY_ATTACHMENT);
            if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for " + reference.getActualValue() + " while approving study attachments. Continuin approval.");
                continue;
            }
            RevisionData variable = variablePair.getRight();
            if(variable.getState() != RevisionState.DRAFT) {
                // Variable doesn't require approving
                continue;
            }
            Pair<ReturnResult, TransferData> approveResult = approve(TransferData.buildFromRevisionData(variable, RevisionableInfo.FALSE));
            if(approveResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                Logger.error(getClass(), "Tried to approve "+variable.toString()+" and failed with result "+approveResult.getLeft());
                result = ReturnResult.OPERATION_FAIL;
                // Let's mark the specific TransferRow with approval error.
                // Missing rows should have been inserted during saving process before this phase so we can be confident that everything exists
                // and since we are returning APPROVE_FAILED from here the TransferData provided to this method is the one that will be returned
                // to the user.
                tf.getRow(reference.getRowId()).addError(FieldError.APPROVE_FAILED);
            }
        }

        return result;
    }*/

    /*private ReturnResult checkStudyVariables(RevisionData revision, TransferData transferData) {
        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Check that transferData actually has the field with this value (it should).
            // If the field or value is missing then add them
            TransferField field = transferData.getField(Fields.VARIABLES);
            if(field == null) {
                field = new TransferField(Fields.VARIABLES, TransferFieldType.VALUE);
                TransferValue transferValue = TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, fieldPair.getRight());
                field.addValueFor(Language.DEFAULT, transferValue);
                transferData.getFields().put(field.getKey(), field);
            }

            // We have variables reference and it contains a value
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
                    Long.parseLong(fieldPair.getRight().getActualValueFor(Language.DEFAULT)), false, ConfigurationType.STUDY_VARIABLES);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for study variables "+ fieldPair.getRight().getActualValueFor(Language.DEFAULT)
                        + " even though it should have existed, not halting approval");
                return ReturnResult.OPERATION_SUCCESSFUL;
            }
            RevisionData variables = dataPair.getRight();
            if(variables.getState() != RevisionState.DRAFT) {
                // No need for approval
                return ReturnResult.OPERATION_SUCCESSFUL;
            }
            Pair<ReturnResult, TransferData> approveResult = approve(TransferData.buildFromRevisionData(variables, RevisionableInfo.FALSE));
            if(approveResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                Logger.error(getClass(), "Tried to approve "+variables.toString()+" and failed with result "+approveResult.getLeft());
                return ReturnResult.OPERATION_FAIL;
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }*/
}
