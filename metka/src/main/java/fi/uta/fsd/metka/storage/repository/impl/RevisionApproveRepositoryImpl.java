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
import fi.uta.fsd.metka.model.data.change.ValueChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
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
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private EhCacheCacheManager cacheManager;

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
            if (data.getConfiguration().getType().equals(ConfigurationType.STUDY)) {
                ReferenceContainerDataField files = (ReferenceContainerDataField) data.getField(Fields.FILES);
                String approvedFiles = "";
                if (files != null) {
                    for (ReferenceRow file : files.getReferences()) {
                        Pair<ReturnResult, RevisionableInfo> attachmentInfo = revisions.getRevisionableInfo(Long.parseLong(file.getReference().getValue().split("-")[0]));
                        if (!attachmentInfo.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)) {
                            continue;
                        }
                        if (!attachmentInfo.getRight().getRemoved() && !approvedFiles.contains(file.getReference().getValue().split("-")[0])) {
                            if (!approvedFiles.equals("")) {
                                approvedFiles += ",";
                            }
                            approvedFiles += file.getReference().getValue().split("-")[0];
                        }
                    }
                }
                data.putChange(new ValueChange(Fields.APPROVED_FILES));
                if (data.getField(Fields.APPROVED_FILES) != null) {
                    ((ValueDataField) data.getField(Fields.APPROVED_FILES)).setCurrentFor(Language.DEFAULT, new ValueContainer(null, new Value(approvedFiles)));
                } else {
                    data.getFields().put(Fields.APPROVED_FILES, new ValueDataField(Fields.APPROVED_FILES));
                    ((ValueDataField) data.getField(Fields.APPROVED_FILES)).setCurrentFor(Language.DEFAULT, new ValueContainer(null, new Value(approvedFiles)));
                }
            }
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
            data.setLatest("approved");
            data.setHandler("");
            Pair<SerializationResults, String> string = json.serialize(data);
            if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
                Logger.error(getClass(), "Couldn't serialize data "+data.toString()+", halting approval process");
                return new ImmutablePair<>(OperationResponse.build(ReturnResult.OPERATION_FAIL), transferData);
            }
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(data.getKey().getId());
            if (infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                Pair<ReturnResult, RevisionData> oldLatestPair = revisions.getRevisionData(data.getKey().getId(), infoPair.getRight().getApproved());
                if (oldLatestPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                    RevisionData oldLatestData = oldLatestPair.getRight();
                    oldLatestData.setLatest("false");
                    revisions.updateRevisionData(oldLatestData);
                }
            }
            ReturnResult updateResult = revisions.updateRevisionData(data);

            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(OperationResponse.build(updateResult), transferData);
            }

            messenger.sendAmqpMessage(messenger.FD_APPROVE, new RevisionPayload(data));
            //revisions.indexRevision(data.getKey());

            // We need to clear the cache in order to keep cache vs db integrity over large transactions.
            cacheManager.getCache("revision-cache").clear();

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
            case PUBLICATION:
                return finalizePublicationApproval(revision, info);
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
     * Special case check for publications.
     * Handles descversions of studies related to the approved publication.
     * @param revision StudyError revision data
     * @return Always returns OPERATION_SUCCESSFUL
     */
    private ReturnResult finalizePublicationApproval(RevisionData revision, DateTimeUserPair info) {
        List<Long> handled = new ArrayList<>();
        // Iterate through related studies
        ReferenceContainerDataField studies = (ReferenceContainerDataField) revision.getField("studies");
        for (ReferenceRow reference : studies.getReferences()){
            if (!handled.contains(Long.parseLong(reference.getActualValue()))) {
                Pair<ReturnResult, RevisionData> studyPair = revisions.getRevisionData(reference.getActualValue());
                if (!studyPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                    continue;
                }
                handleDescVersion(studyPair.getRight(), info);
                handled.add(studyPair.getRight().getKey().getId());
            }
        }

        // Iterate through related series
        ReferenceContainerDataField series = (ReferenceContainerDataField) revision.getField("series");
        for (ReferenceRow reference : series.getReferences()){
            Pair<ReturnResult, RevisionData> seriesPair = revisions.getRevisionData(reference.getReference().getValue());
            if (!seriesPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                continue;
            }
            // Query for studies with the found series
            ExpertRevisionSearchCommand command = null;
            try {
                command = ExpertRevisionSearchCommand.build("+key.configuration.type:STUDY +series:"+reference.getReference().getValue()+" +state.removed:FALSE", configurations);
            } catch (QueryNodeException e) {
                continue;
            }
            ResultList<RevisionResult> seriesStudiesResult = searcher.executeSearch(command);
            for (RevisionResult result: seriesStudiesResult.getResults()){
                if (!handled.contains(result.getId())){
                    Pair<ReturnResult, RevisionData> studyPair = revisions.getRevisionData(result.getId().toString());
                    if (!studyPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                        continue;
                    }
                    handleDescVersion(studyPair.getRight(), info);
                    handled.add(studyPair.getRight().getKey().getId());
                }
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    /**
     * Adds a new descversion for a revision given as the parameter. If the revision is
     * APPROVED, create a new DRAFT, add a descrevision and approve the revision.
     * if the revision is already a DRAFT, only add a descrevision and save.
     * @param revision RevisionData of the revision being handled
     * @param info Date and User information for the ongoing publication approval operation.
     */
    private void handleDescVersion(RevisionData revision, DateTimeUserPair info){
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(revision.getKey().getId());
        if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
            return;
        }
        if (revision.getState().equals(RevisionState.DRAFT)){
            TransferData transferData = TransferData.buildFromRevisionData(revision, infoPair.getRight());
            TransferField descversions = null;
            if (transferData.hasField("descversions")){
                descversions = transferData.getField("descversions");
            } else {
                descversions = new TransferField("descversions", TransferFieldType.CONTAINER);
                transferData.getFields().put("descversions", descversions);
            }
            TransferRow newRow = new TransferRow("descversions");

            TransferField newField = new TransferField("versionlabeldesc", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent("23");
            newRow.addField(newField);

            newField = new TransferField("versionpro", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getUser());
            newRow.addField(newField);

            Double newVersionNo = 0.0;
            Double currVersionNo;
            if (descversions.hasRows()) {
                for (TransferRow row : descversions.getRows().get(Language.DEFAULT)) {
                    if (row.getField("descversion") == null){
                        continue;
                    }
                    try {
                        currVersionNo = Double.parseDouble(row.getField("descversion").getValueFor(Language.DEFAULT).getValue());
                    } catch (NumberFormatException ex) {
                        currVersionNo = 1.0;
                    }
                    if (currVersionNo > newVersionNo) {
                        newVersionNo = currVersionNo;
                    }
                }
            }
            newVersionNo = newVersionNo + 0.1;
            newField = new TransferField("descversion", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(BigDecimal.valueOf(newVersionNo).setScale(1, RoundingMode.DOWN).toString());
            newRow.addField(newField);

            newField = new TransferField("versiondate", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            newRow.addField(newField);

            descversions.addRowFor(Language.DEFAULT, newRow);
            save.saveRevision(transferData, info);
        } else if (revision.getState().equals(RevisionState.APPROVED)) {
            Pair<OperationResponse, RevisionData> editPair = edit.edit(revision.getKey(), info);
            TransferData transferData = TransferData.buildFromRevisionData(editPair.getRight(), infoPair.getRight());
            TransferField descversions = null;
            if (transferData.hasField("descversions")){
                descversions = transferData.getField("descversions");
            } else {
                descversions = new TransferField("descversions", TransferFieldType.CONTAINER);
                transferData.getFields().put("descversions", descversions);
            }
            TransferRow newRow = new TransferRow("descversions");

            TransferField newField = new TransferField("versionlabeldesc", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent("23");
            newRow.addField(newField);

            newField = new TransferField("versionpro", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getUser());
            newRow.addField(newField);

            Double newVersionNo = 0.0;
            Double currVersionNo;
            if (descversions.hasRows()) {
                for (TransferRow row : descversions.getRows().get(Language.DEFAULT)) {
                    if (row.getField("descversion") == null){
                        continue;
                    }
                    try {
                        currVersionNo = Double.parseDouble(row.getField("descversion").getValueFor(Language.DEFAULT).getValue());
                    } catch (NumberFormatException ex) {
                        currVersionNo = 1.0;
                    }
                    if (currVersionNo > newVersionNo) {
                        newVersionNo = currVersionNo;
                    }
                }
            }
            newVersionNo = newVersionNo + 0.1;
            newField = new TransferField("descversion", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(BigDecimal.valueOf(newVersionNo).setScale(1, RoundingMode.DOWN).toString());
            newRow.addField(newField);

            newField = new TransferField("versiondate", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            newRow.addField(newField);

            descversions.addRowFor(Language.DEFAULT, newRow);
            save.saveRevision(transferData, info);
            approve(transferData, info);
        }
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
        if (fieldConf == null){
            return;
        }
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
            // TODO: More reliable monitoring on reference changes?
            // We need to be able to revert back to a revision.
            // Therefore we need to be able to tell which reference targets were removed or approved
            // during which revision.
            //if(row.getUnapproved() || row.getRemoved()) {
                changesIn.add(Language.DEFAULT);
            //}
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
