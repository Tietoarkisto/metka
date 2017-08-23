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
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.restrictions.ValidateResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.FileRemovalPayload;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs a removal to given transfer data if possible
 *
 * Valid results are:
 *      SUCCESS_DRAFT - Draft was removed successfully, user should be moved to latest approved revision
 *      SUCCESS_LOGICAL - Logical removal was performed successfully, new up to date data should be loaded for the revision
 *      SUCCESS_REVISIONABLE - Draft was removed but no further revisions existed for the revisionable so the revisionable was removed also.
 * All other return values are errors
 * TODO: Should cascaded removals halt the original remove if they fail?
 */
@Repository
public class RevisionRemoveRepositoryImpl implements RevisionRemoveRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionRestoreRepository restore;

    @Autowired
    private RevisionableRepository revisionables;

    @Autowired
    private ConfigurationRepository configurations;

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
    private RevisionApproveRepository approve;

    @Override
    public OperationResponse remove(RevisionKey key, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(key);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return OperationResponse.build(RemoveResult.ALREADY_REMOVED);
        }
        if(dataPair.getRight().getState() == RevisionState.DRAFT) {
            return removeDraft(key, info);
        } else if(dataPair.getRight().getState() == RevisionState.APPROVED) {
            RevisionableInfo revInfo = revisions.getRevisionableInfo(key.getId()).getRight();
            if(!revInfo.getRemoved()) {
                return removeLogical(key, info, false);
            }
        }

        return OperationResponse.build(RemoveResult.ALREADY_REMOVED);
    }

    @Override
    public OperationResponse removeDraft(RevisionKey key, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }

        RemoveResult result = allowRemoval(pair.getRight());
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return OperationResponse.build(result);
        }

        RevisionData data = pair.getRight();

        if(data.getState() != RevisionState.DRAFT) {
            return OperationResponse.build(RemoveResult.NOT_DRAFT);
        }

        if(!AuthenticationUtil.isHandler(data)) {
            Logger.error(getClass(), "User " + AuthenticationUtil.getUserName() + " tried to remove draft belonging to " + data.getHandler());
            return OperationResponse.build(RemoveResult.WRONG_USER);
        }

        RevisionableInfo revInfo = revisions.getRevisionableInfo(data.getKey().getId()).getRight();

        OperationResponse response = validateAndCascade(data, info, OperationType.REMOVE_DRAFT, revInfo);
        if(!response.getResult().equals(RemoveResult.ALLOW_REMOVAL.name())) {
            return response;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(data.getKey()));
        RevisionData revisionData = revisions.getRevisionData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo()).getRight();
        if (revisionData != null && revisionData.getConfiguration().getType().equals(ConfigurationType.STUDY)) {
            checkFileStates(revisions.getRevisionData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo()).getRight(), info);
        }
        if(revision == null) {
            Logger.error(getClass(), "Draft revision with key "+data.getKey()+" was slated for removal but was not found from database.");
        } else {
            revisions.removeRevision(revision.getKey().toModelKey());
        }

        if(revInfo != null && revInfo.getApproved() == null) {
            revisionables.removeRevisionable(data.getKey().getId());
            finalizeFinalRevisionRemoval(data, info);
            result = RemoveResult.SUCCESS_REVISIONABLE;
        } else {
            revisionables.updateRevisionableRevisionNumber(data.getKey().getId());
            finalizeDraftRemoval(data, info);
            result = RemoveResult.SUCCESS_DRAFT;
        }
        messenger.sendAmqpMessage(messenger.FD_REMOVE, new RevisionPayload(data));
        addRemoveIndexCommand(data.getKey(), result);
        return OperationResponse.build(result);
    }


    @Override
    public OperationResponse removeLogical(RevisionKey key, DateTimeUserPair info, boolean isCascadeRemove) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }

        RemoveResult result;
        if (!isCascadeRemove) {
            result = allowRemoval(pair.getRight());
            if(result != RemoveResult.ALLOW_REMOVAL) {
                return OperationResponse.build(result);
            }
        }

        pair = revisions.getRevisionData(key.getId().toString());

        // NOTICE: These could be moved to restrictions quite easily
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // This should never happen since we found the revision data provided for this method
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }
        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return OperationResponse.build(RemoveResult.OPEN_DRAFT);
        }

        RevisionData data = pair.getRight();

        OperationResponse response = validateAndCascade(data, info, OperationType.REMOVE_LOGICAL, null);
        if(!response.getResult().equals(RemoveResult.ALLOW_REMOVAL.name())) {
            return response;
        }

        if(revisionables.logicallyRemoveRevisionable(info, data.getKey().getId())) {
            return OperationResponse.build(RemoveResult.ALREADY_REMOVED);
        }

        /*if(data.getConfiguration().getType() == ConfigurationType.STUDY) {
            propagateStudyLogicalRemoval(data);
        } else if(data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
            propagateStudyVariablesLogicalRemoval(data);
        }*/

        // TODO: What do we do about study errors and binder pages in this case?

        finalizeLogicalRemoval(data, info, isCascadeRemove);

        result = RemoveResult.SUCCESS_LOGICAL;
        messenger.sendAmqpMessage(messenger.FD_REMOVE, new RevisionPayload(data));
        addRemoveIndexCommand(data.getKey(), result);
        return OperationResponse.build(result);
    }

    private void finalizeFinalRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT: {
                finalizeFinalStudyAttachmentRevisionRemoval(data, info);
                break;
            }
            case STUDY_VARIABLES: {
                finalizeFinalStudyVariablesRevisionRemoval(data, info);
                break;
            }
            case STUDY_VARIABLE: {
                finalizeFinalStudyVariableRevisionRemoval(data, info);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void finalizeFinalStudyVariablesRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        // The latest revision for study should always be a draft in this case since we can't create new variables draft without study being a draft
        // and if we're performing the final revision removal then there has not been an approve operation between the events
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                RevisionData study = revPair.getRight();
                Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES));
                if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                    fieldPair = data.dataField(ValueDataFieldCall.get(Fields.LANGUAGE));
                    if(fieldPair.getLeft() == StatusCode.FIELD_FOUND) {
                        String varLang = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
                        Pair<StatusCode, DataRow> rowPair = conPair.getRight().getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang));
                        if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                            conPair.getRight().removeRow(rowPair.getRight().getRowId(), study.getChanges(), info);
                            revisions.updateRevisionData(study);
                        }
                    }
                }
            }
        }

        fieldPair = data.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                RevisionData attachment = revPair.getRight();
                fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    attachment.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(""), Language.DEFAULT).setInfo(info));
                    revisions.updateRevisionData(attachment);
                }
            }
        }
    }

    private void finalizeFinalStudyVariableRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        // The latest revision for study should always be a draft in this case since we can't create new variable draft without study variables being a draft
        // and if we're performing the final revision removal then there has not been an approve operation between the events
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                RevisionData variables = revPair.getRight();
                Pair<StatusCode, ReferenceContainerDataField> conPair = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
                if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                    Pair<StatusCode, ReferenceRow> rowPair = conPair.getRight().getReferenceIncludingValue(data.getKey().asPartialKey());
                    if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                        conPair.getRight().removeReference(rowPair.getRight().getRowId(), variables.getChanges(), info);
                    }
                }

                ContainerDataField variableGroups = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();
                if(variableGroups != null) {
                    for(DataRow dataRow : variableGroups.getRowsFor(Language.DEFAULT)) {
                        conPair = dataRow.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS));
                        if(conPair.getRight() != null) {
                            // See that respective rows are removed from VARGROUPVARS
                            //    Remove from variables list
                            ReferenceRow reference = conPair.getRight().getReferenceIncludingValue(data.getKey().asPartialKey()).getRight();
                            if(reference != null) {
                                conPair.getRight().removeReference(reference.getRowId(), variables.getChanges(), info).getLeft();
                                // Since variable should always be only in one group at a time we can break out.
                                break;
                            }
                        }
                    }
                }

                revisions.updateRevisionData(variables);
            }
        }
    }

    private void finalizeFinalStudyAttachmentRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        // The latest revision for study should always be a draft in this case since we can't create new attachment draft without study being a draft
        // and if we're performing the final revision removal then there has not been an approve operation between the events
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                RevisionData study = revPair.getRight();
                Pair<StatusCode, ReferenceContainerDataField> conPair = study.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
                if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                    Pair<StatusCode, ReferenceRow> rowPair = conPair.getRight().getReferenceIncludingValue(data.getKey().asPartialKey());
                    if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                        conPair.getRight().removeReference(rowPair.getRight().getRowId(), study.getChanges(), info);
                        revisions.updateRevisionData(study);
                    }
                }
            }
        }

        // Remove the associated file as well
        if (data.getField(Fields.FILE) != null) {
            String path = ((ValueDataField) data.getField(Fields.FILE)).getActualValueFor(Language.DEFAULT);
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }

        // TODO: Remove link from study variables if it still exists
    }

    private void finalizeDraftRemoval(RevisionData data, DateTimeUserPair info) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT: {
                finalizeStudyAttachmentDraftRemoval(data, info);
                break;
            }
            case STUDY_VARIABLES: {
                finalizeStudyVariablesDraftRemoval(data, info);
                break;
            }
            case STUDY_VARIABLE: {
                finalizeStudyVariableDraftRemoval(data, info);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void checkFileStates(RevisionData data, DateTimeUserPair info){
        ReferenceContainerDataField files = (ReferenceContainerDataField)data.getField(Fields.FILES);
        if (files == null)
            return;
        Pair<ReturnResult, RevisionData> latestPair = revisions.getRevisionData(data.getKey().getId().toString(), true);
        if (!latestPair.getLeft().equals(ReturnResult.REVISION_FOUND))
            return;
        for (ReferenceRow row: files.getReferences()) {
            String fileId = row.getActualValue();
            Pair<ReturnResult, RevisionableInfo> currAttachment = revisions.getRevisionableInfo(Long.parseLong(fileId.split("-")[0]));
            ValueDataField field = (ValueDataField) latestPair.getRight().getField(Fields.APPROVED_FILES);
            if (field == null){
                continue;
            }
            String value = field.getCurrentFor(Language.DEFAULT).getActualValue();
            if (value == null ||currAttachment.getLeft().equals(ReturnResult.REVISIONABLE_NOT_FOUND)){
                continue;
            }
            if (value.contains(fileId.split("-")[0]) && currAttachment.getRight().getRemoved()) {
                restore.restore(Long.parseLong(fileId.split("-")[0]));
            } else if (!field.getCurrentFor(Language.DEFAULT).getActualValue().contains(fileId.split("-")[0]) && !currAttachment.getRight().getRemoved()) {
                if (currAttachment.getRight().getApproved() != currAttachment.getRight().getCurrent()){
                    removeDraft(new RevisionKey(Long.parseLong(fileId.split("-")[0]), Integer.parseInt(fileId.split("-")[1])), info);
                }
                remove(new RevisionKey(Long.parseLong(fileId.split("-")[0]), Integer.parseInt(fileId.split("-")[1])), info);
            }
        }
    }

    private void finalizeStudyAttachmentDraftRemoval(RevisionData data, DateTimeUserPair info) {
        List<Integer> revisionNos = revisions.getAllRevisionNumbers(data.getKey().getId()); // Last number is the newest revision
        if(revisionNos.isEmpty()) {
            return;
        }

        checkStudyAttachmentStudy(data, info, revisionNos);

        checkStudyAttachmentStudyVariables(data, info, revisionNos);

        checkStudyAttachmentVariables(data,info);
    }

    private void checkStudyAttachmentStudy(RevisionData attachment, DateTimeUserPair info, List<Integer> revisionNos) {

        ValueDataField field = attachment.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight();

        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            return;
        }

        RevisionData study = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
        if(study == null) {
            return;
        }

        ReferenceContainerDataField container = study.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES)).getRight();
        if(container == null || !container.hasValidRows()) {
            return;
        }

        ReferenceRow row = container.getReferenceWithValue(attachment.getKey().asCongregateKey()).getRight();
        if(row == null) {
            return;
        }

        container.replaceRow(row.getRowId(), ReferenceRow.build(container, new Value(attachment.getKey().getId() + "-" + revisionNos.get(revisionNos.size() - 1)), info),
                study.getChanges());
        revisions.updateRevisionData(study);
    }

    private void checkStudyAttachmentStudyVariables(RevisionData data, DateTimeUserPair info, List<Integer> revisionNos) {

        ValueDataField variablesField = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();

        if (variablesField == null) {
            return;
        }

        RevisionData variables = revisions.getRevisionData(variablesField.getActualValueFor(Language.DEFAULT)).getRight();


        if(variables == null) {
            return;
        }

        //if attachment reference is not changed , return
        if(variables.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight().getActualValueFor(Language.DEFAULT)
                .equals(data.getKey().getId() + "-" + revisionNos.get(revisionNos.size() - 1))) {
            return;
        }

        variables.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(data.getKey().getId() + "-" + revisionNos.get(revisionNos.size() - 1)), Language.DEFAULT)
                .setInfo(info)
                .setChangeMap(variables.getChanges()));
        revisions.updateRevisionData(variables);
    }

    private void checkStudyAttachmentVariables(RevisionData data,DateTimeUserPair info) {

        ValueDataField variablesField = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();

        if (variablesField == null) {
            return;
        }

        RevisionData variables = revisions.getRevisionData(variablesField.getActualValueFor(Language.DEFAULT)).getRight();

        if(variables == null) {
            return;
        }

        ReferenceContainerDataField variablesContainer = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();

        List<ReferenceRow> varRows = variablesContainer.getReferences();

        for (ReferenceRow row : varRows) {
            restore.restore(Long.parseLong(row.getActualValue().split("-")[0]));
        }

    }

    private void finalizeStudyVariableDraftRemoval(RevisionData data, DateTimeUserPair info) {
        List<Integer> revisionNos = revisions.getAllRevisionNumbers(data.getKey().getId()); // Last number is the newest revision
        if(revisionNos.isEmpty()) {
            return;
        }

        ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            return;
        }

        RevisionData variables = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
        if(variables == null) {
            return;
        }

        ReferenceContainerDataField container = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(container == null || !container.hasValidRows()) {
            return;
        }

        ReferenceRow row = container.getReferenceWithValue(data.getKey().asCongregateKey()).getRight();
        if(row == null) {
            return;
        }

        container.replaceRow(row.getRowId(), ReferenceRow.build(container, new Value(data.getKey().getId() + "-" + revisionNos.get(revisionNos.size() - 1)), info),
                variables.getChanges());

        ContainerDataField variableGroups = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();
        if(variableGroups != null) {
            for(DataRow dataRow : variableGroups.getRowsFor(Language.DEFAULT)) {
                container = dataRow.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS)).getRight();
                if(container != null) {
                    // See that respective rows are removed from VARGROUPVARS
                    //    Remove from variables list
                    ReferenceRow reference = container.getReferenceIncludingValue(data.getKey().asPartialKey()).getRight();
                    if(reference != null) {
                        container.replaceRow(reference.getRowId(), ReferenceRow.build(container, new Value(data.getKey().getId() + "-" + revisionNos.get(revisionNos.size() - 1)), info),
                                variables.getChanges());
                        // Since variable should always be only in one group at a time we can break out.
                        break;
                    }
                }
            }
        }

        revisions.updateRevisionData(variables);
    }

    private void finalizeStudyVariablesDraftRemoval(RevisionData data, DateTimeUserPair info) {
        List<Integer> revisionNos = revisions.getAllRevisionNumbers(data.getKey().getId()); // Last number is the newest revision
        if(revisionNos.isEmpty()) {
            return;
        }

        ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight();
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            // Something weird has happened but this is not the place to react to it, just return
            return;
        }

        RevisionData study = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
        if(study == null || study.getState() != RevisionState.DRAFT) {
            return;
        }

        ContainerDataField variables = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES)).getRight();
        if(variables == null || !variables.hasValidRows()) {
            return;
        }

        DataRow row = variables.getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLES, new Value(data.getKey().asCongregateKey())).getRight();
        if(row == null) {
            return;
        }

        ContainerChange cc = (ContainerChange)study.getChange(Fields.STUDYVARIABLES);
        if(cc == null) {
            cc = new ContainerChange(Fields.STUDYVARIABLES);
            study.putChange(cc);
        }

        RowChange rc = cc.get(row.getRowId());
        if(rc == null) {
            rc = new RowChange(row.getRowId());
            cc.put(rc);
        }

        row.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(data.getKey().getId()+"-"+revisionNos.get(revisionNos.size()-1)), Language.DEFAULT).setInfo(info).setChangeMap(rc.getChanges()));
        revisions.updateRevisionData(study);
    }

    private void finalizeLogicalRemoval(RevisionData data, DateTimeUserPair info, boolean isCascade) {
        // TODO: Generalize this and other respective cases with configuration analysis
        // Since we don't have a mapping object for references we need to do this by type for now.
        // It might be handy to do some processing of data configurations when they are saved and to form a reference web from them.
        // This would allow for automatic clean operations at certain key points like this. Basically collecting the foreign keys
        // and enabling cascade effects.

        switch(data.getConfiguration().getType()) {
            case STUDY_ERROR: {
                revisions.sendStudyErrorMessageIfNeeded(data, configurations.findConfiguration(data.getConfiguration()).getRight());
                break;
            }
            case STUDY_VARIABLES: {
                if(!isCascade) {
                    Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
                    if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                        Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
                        if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                            RevisionData study = revPair.getRight();
                            Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES));
                            if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                                fieldPair = data.dataField(ValueDataFieldCall.get(Fields.LANGUAGE));
                                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND) {
                                    String varLang = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
                                    Pair<StatusCode, DataRow> rowPair = conPair.getRight().getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang));
                                    if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                                        conPair.getRight().removeRow(rowPair.getRight().getRowId(), study.getChanges(), info);
                                        revisions.updateRevisionData(study);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case STUDY_ATTACHMENT: {
                if(!isCascade) {
                    ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight();
                    if(field == null || !field.hasValueFor(Language.DEFAULT)) {
                        break;
                    }
                    String value = field.getActualValueFor(Language.DEFAULT);
                    RevisionData study = null;
                    if(value.split("-").length > 1) {
                        study = revisions.getRevisionData(Long.parseLong(value.split("-")[0]), Integer.parseInt(value.split("-")[1])).getRight();
                    } else {
                        study = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
                    }
                    if(study == null) {
                        break;
                    }
                    messenger.sendAmqpMessage(messenger.FB_FILE_REMOVAL, new FileRemovalPayload(data, study));
                }
                break;
            }
            case STUDY_VARIABLE: {
                if(!isCascade) {
                    ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();
                    if(field == null || !field.hasValueFor(Language.DEFAULT)) {
                        return;
                    }

                    RevisionData variables = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
                    if(variables == null) {
                        return;
                    }

                    ReferenceContainerDataField container = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
                    if(container == null || !container.hasValidRows()) {
                        return;
                    }

                    ReferenceRow row = container.getReferenceWithValue(data.getKey().asCongregateKey()).getRight();
                    if(row == null) {
                        return;
                    }
                    container.removeReference(row.getRowId(), variables.getChanges(), info);

                    ContainerDataField variableGroups = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();
                    if(variableGroups != null) {
                        for(DataRow dataRow : variableGroups.getRowsFor(Language.DEFAULT)) {
                            container = dataRow.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS)).getRight();
                            if(container != null) {
                                // See that respective rows are removed from VARGROUPVARS
                                //    Remove from variables list
                                ReferenceRow reference = container.getReferenceIncludingValue(data.getKey().asPartialKey()).getRight();
                                if(reference != null) {
                                    container.removeReference(reference.getRowId(), variables.getChanges(), info).getLeft();
                                    // Since variable should always be only in one group at a time we can break out.
                                    break;
                                }
                            }
                        }
                    }

                    revisions.updateRevisionData(variables);
                }
                break;
            }
            case PUBLICATION:
                try {
                    finalizePublicationRemoval(data, info);
                } catch (QueryNodeException e) {
                    return;
                }
                break;
            default: {
                break;
            }
        }
    }

    private void finalizePublicationRemoval(RevisionData revision, DateTimeUserPair info) throws QueryNodeException {
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
            ExpertRevisionSearchCommand command = ExpertRevisionSearchCommand.build("+key.configuration.type:STUDY +series:"+reference.getReference().getValue()+" +state.removed:FALSE", configurations);
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
            approve.approve(transferData, info);
        }
    }

    private OperationResponse validateAndCascade(RevisionData data, DateTimeUserPair info, OperationType opType, RevisionableInfo revInfo) {
        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(data.getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Could not find configuration for data "+data.toString());
            return OperationResponse.build(RemoveResult.CONFIGURATION_NOT_FOUND);
        }

        // Do validation
        ReturnResult rr = ReturnResult.OPERATION_SUCCESSFUL;
        for(Operation operation : confPair.getRight().getRestrictions()) {
            if(!(operation.getType() == OperationType.REMOVE
                    || operation.getType() == (opType)
                    || (opType == OperationType.REMOVE_DRAFT && revInfo != null && revInfo.getApproved() == null && operation.getType() == OperationType.REMOVE_REVISIONABLE)
                    || operation.getType() == OperationType.ALL)) {
                continue;
            }
            ValidateResult vr = validator.validate(data, operation.getTargets(), confPair.getRight());
            if(!vr.getResult()) {
                return OperationResponse.build(vr);
            }
        }

        // Do cascade
        for(Operation operation : confPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.REMOVE
                    || operation.getType() == (opType)
                    || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!cascader.cascade(CascadeInstruction.build(opType, info), data, operation.getTargets(), confPair.getRight())) {
                rr = ReturnResult.CASCADE_FAILURE;
            }
        }

        // Do REMOVE_REVISIONABLE cascade, results don't matter
        if(opType == OperationType.REMOVE_DRAFT && revInfo != null && revInfo.getApproved() == null) {
            for(Operation operation : confPair.getRight().getCascade()) {
                if(operation.getType() != OperationType.REMOVE_REVISIONABLE) {
                    continue;
                }
                if(!cascader.cascade(CascadeInstruction.build(OperationType.REMOVE_REVISIONABLE, info), data, operation.getTargets(), confPair.getRight())) {
                    rr = ReturnResult.CASCADE_FAILURE;
                }
            }
        }

        // If cascade fails then don't approve this revision
        if(rr != ReturnResult.OPERATION_SUCCESSFUL) {
            return OperationResponse.build(RemoveResult.CASCADE_FAILURE);
        }

        return OperationResponse.build(RemoveResult.ALLOW_REMOVAL);
    }

    private RemoveResult allowRemoval(RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                return checkStudyAttachmentRemoval(data);
            default:
                return RemoveResult.ALLOW_REMOVAL;
        }
    }

    private RemoveResult checkStudyAttachmentRemoval(RevisionData data) {
        ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight();
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        return AuthenticationUtil.isHandler(pair.getRight())
                ? RemoveResult.ALLOW_REMOVAL
                : (pair.getRight().getState() != RevisionState.DRAFT
                        ? RemoveResult.STUDY_NOT_DRAFT
                        : RemoveResult.WRONG_USER);
    }

    private void addRemoveIndexCommand(RevisionKey key, RemoveResult result) {
        switch(result) {
            case SUCCESS_REVISIONABLE:
                // TODO: In case of study we should check that references pointing to this study will get removed from revisions (from which point is the question).
            case SUCCESS_DRAFT:
                // One remove operation should be enough for both of these since there should only be one affected document
                revisions.removeRevisionFromIndex(key);
                break;
            case SUCCESS_LOGICAL:
                // In this case we need to reindex all affected documents instead
                revisions.indexRevision(key);
                /*List<Integer> nos = revisions.getAllRevisionNumbers(key.getId());
                for(Integer no : nos) {
                    Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key.getId(), key.getNo());
                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        revisions.indexRevision(pair.getRight().getKey());
                    }
                }*/
                break;
            default:
                // Errors don't need special handling
                break;
        }
    }
}