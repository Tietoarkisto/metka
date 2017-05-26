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
import fi.uta.fsd.metkaAmqp.payloads.*;
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

        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(transferData.getKey().getId().toString());
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
            // Fetch the data again since cascade might have changed things
            dataPair = revisions.getRevisionData(transferData.getKey().getId().toString());
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "No revision to approve for " + transferData.getKey().toString());
                return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), transferData);
            }
            data = dataPair.getRight();
            if(data.getState() != RevisionState.DRAFT) {
                return new ImmutablePair<>(OperationResponse.build(ReturnResult.REVISION_NOT_A_DRAFT), transferData);
            }
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
            Logger.debug(this.getClass(),"Cascading operation " + operation.getType() + " for " + transferData.getKey().getId().toString());
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

        // Fetch the data again since cascade might have changed things
        dataPair = revisions.getRevisionData(transferData.getKey().getId().toString());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "No revision to approve for " + transferData.getKey().toString());
            return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), transferData);
        }
        data = dataPair.getRight();
        if(data.getState() != RevisionState.DRAFT) {
            return new ImmutablePair<>(OperationResponse.build(ReturnResult.REVISION_NOT_A_DRAFT), transferData);
        }

        // TODO: Check that all SELECTION values are still valid (e.g. that they can be found and that the values are not marked deprecated
        // TODO: Check that other references like series are still valid (e.g. they point to existing revisionables

        // If validation was successful then check all languages that should be approved (i.e. which languages have content
        // that has changed in this revision).
        // If revision has no changed content for given language then that language doesn't need updated approval information.
        if(result == ReturnResult.OPERATION_SUCCESSFUL) {
            Set<Language> changesIn = hasChanges(data, configuration);

            // No changes in this revision, remove it instead
            if(changesIn.isEmpty() && transferData.getState().getSaved() == null) {
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

            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(OperationResponse.build(updateResult), transferData);
            }

            messenger.sendAmqpMessage(messenger.FD_APPROVE, new RevisionPayload(data));
            //revisions.indexRevision(data.getKey());
            return new ImmutablePair<>(OperationResponse.build(ReturnResult.OPERATION_SUCCESSFUL), TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE));
        } else {
            return new ImmutablePair<>(OperationResponse.build(result), transferData);
        }
    }

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

        if (fieldConf == null) {
            //configuration error
            return;
        }

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
}
