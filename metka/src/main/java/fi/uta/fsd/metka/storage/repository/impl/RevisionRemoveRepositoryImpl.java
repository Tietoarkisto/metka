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
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.restrictions.ValidateResult;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    private ConfigurationRepository configurations;

    @Autowired
    private RestrictionValidator validator;

    @Autowired
    private Cascader cascader;

    @Override
    public OperationResponse remove(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        if(transferData.getState().getUiState() == UIRevisionState.DRAFT) {
            return removeDraft(transferData, info);
        } else if(transferData.getState().getUiState() == UIRevisionState.APPROVED) {
            return removeLogical(transferData, info);
        } else {
            return OperationResponse.build(RemoveResult.ALREADY_REMOVED);
        }
    }

    @Override
    public OperationResponse removeDraft(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }

        RemoveResult result = allowRemoval(transferData);
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

        OperationResponse response = validateAndCascade(data, info, true, revInfo);
        if(!response.getResult().equals(RemoveResult.ALLOW_REMOVAL.name())) {
            return response;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, RevisionKey.fromModelKey(data.getKey()));
        if(revision == null) {
            Logger.error(getClass(), "Draft revision with key "+data.getKey()+" was slated for removal but was not found from database.");
        } else {
            em.remove(revision);
        }



        if(revInfo != null && revInfo.getApproved() == null) {
            em.remove(em.find(RevisionableEntity.class, data.getKey().getId()));
            finalizeFinalRevisionRemoval(data, info);
            result = RemoveResult.SUCCESS_REVISIONABLE;
        } else {
            RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            result = RemoveResult.SUCCESS_DRAFT;
        }
        addRemoveIndexCommand(transferData, result);
        return OperationResponse.build(result);
    }

    @Override
    public OperationResponse removeLogical(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }

        RemoveResult result = allowRemoval(transferData);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return OperationResponse.build(result);
        }

        pair = revisions.getLatestRevisionForIdAndType(transferData.getKey().getId(), false, transferData.getConfiguration().getType());

        // NOTICE: These could be moved to restrictions quite easily
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // This should never happen since we found the revision data provided for this method
            return OperationResponse.build(RemoveResult.NOT_FOUND);
        }
        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return OperationResponse.build(RemoveResult.OPEN_DRAFT);
        }

        RevisionData data = pair.getRight();

        OperationResponse response = validateAndCascade(data, info, false, null);
        if(!response.getResult().equals(RemoveResult.ALLOW_REMOVAL.name())) {
            return response;
        }

        RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
        entity.setRemoved(true);
        entity.setRemovalDate(info.getTime());
        entity.setRemovedBy(info.getUser());

        /*if(data.getConfiguration().getType() == ConfigurationType.STUDY) {
            propagateStudyLogicalRemoval(data);
        } else if(data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
            propagateStudyVariablesLogicalRemoval(data);
        }*/

        // TODO: What do we do about study errors and binder pages in this case?

        finalizeLogicalRemoval(data, info);

        result = RemoveResult.SUCCESS_LOGICAL;
        addRemoveIndexCommand(transferData, result);
        return OperationResponse.build(result);
    }

    private void finalizeFinalRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        // TODO: Generalize this with configuration analysis
        // Since we don't have a mapping object for references we need to do this by type for now.
        // It might be handy to do some processing of data configurations when they are saved and to form a reference web from them.
        // This would allow for automatic clean operations at certain key points like this. Basically collecting the foreign keys
        // and enabling cascade effects.

        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT: {
                Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY);
                    if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                        RevisionData study = revPair.getRight();
                        Pair<StatusCode, ReferenceContainerDataField> conPair = study.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
                        if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                            Pair<StatusCode, ReferenceRow> rowPair = conPair.getRight().getReferenceWithValue(data.getKey().getId() + "");
                            if(rowPair.getLeft() == StatusCode.ROW_FOUND && !rowPair.getRight().getRemoved()) {
                                conPair.getRight().removeReference(rowPair.getRight().getRowId(), study.getChanges(), info);
                                revisions.updateRevisionData(study);
                            }
                        }
                    }
                }
                break;
            }
            case STUDY_VARIABLES: {
                Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY);
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
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY_ATTACHMENT);
                    if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                        RevisionData attachment = revPair.getRight();
                        fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
                        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                            attachment.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(""), Language.DEFAULT).setInfo(info));
                            revisions.updateRevisionData(attachment);
                        }
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void finalizeLogicalRemoval(RevisionData data, DateTimeUserPair info) {
        // TODO: Generalize this with configuration analysis
        // Since we don't have a mapping object for references we need to do this by type for now.
        // It might be handy to do some processing of data configurations when they are saved and to form a reference web from them.
        // This would allow for automatic clean operations at certain key points like this. Basically collecting the foreign keys
        // and enabling cascade effects.

        switch(data.getConfiguration().getType()) {

            case STUDY_VARIABLES: {
                Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY);
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
                break;
            }
            default: {
                break;
            }
        }
    }

    private OperationResponse validateAndCascade(RevisionData data, DateTimeUserPair info, Boolean draft, RevisionableInfo revInfo) {
        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(data.getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Could not find configuration for data "+data.toString());
            return OperationResponse.build(RemoveResult.CONFIGURATION_NOT_FOUND);
        }

        // Do validation
        ReturnResult rr = ReturnResult.OPERATION_SUCCESSFUL;
        for(Operation operation : confPair.getRight().getRestrictions()) {
            if(!(operation.getType() == OperationType.REMOVE
                    || operation.getType() == (draft?OperationType.REMOVE_DRAFT:OperationType.REMOVE_LOGICAL)
                    || (draft && revInfo != null && revInfo.getApproved() == null && operation.getType() == OperationType.REMOVE_REVISIONABLE)
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
                    || operation.getType() == (draft?OperationType.REMOVE_DRAFT:OperationType.REMOVE_LOGICAL)
                    || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!cascader.cascade(CascadeInstruction.build(OperationType.REMOVE, info, draft), data, operation.getTargets(), confPair.getRight())) {
                rr = ReturnResult.CASCADE_FAILURE;
            }
        }

        // Do REMOVE_REVISIONABLE cascade, results don't matter
        if(draft && revInfo != null && revInfo.getApproved() == null) {
            for(Operation operation : confPair.getRight().getCascade()) {
                if(operation.getType() != OperationType.REMOVE_REVISIONABLE) {
                    continue;
                }
                if(!cascader.cascade(CascadeInstruction.build(OperationType.REMOVE_REVISIONABLE, info, true), data, operation.getTargets(), confPair.getRight())) {
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

    private RemoveResult allowRemoval(TransferData transferData) {
        switch(transferData.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                return checkStudyAttachmentRemoval(transferData);
            default:
                return RemoveResult.ALLOW_REMOVAL;
        }
    }

    private RemoveResult checkStudyAttachmentRemoval(TransferData transferData) {
        TransferField field = transferData.getField(Fields.STUDY);
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(field.asValueFor(Language.DEFAULT).asInteger(), false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        return AuthenticationUtil.isHandler(pair.getRight())
                ? RemoveResult.ALLOW_REMOVAL
                : (pair.getRight().getState() != RevisionState.DRAFT
                        ? RemoveResult.STUDY_NOT_DRAFT
                        : RemoveResult.WRONG_USER);
    }

    private void addRemoveIndexCommand(TransferData transferData, RemoveResult result) {
        switch(result) {
            case SUCCESS_REVISIONABLE:
                // TODO: In case of study we should check that references pointing to this study will get removed from revisions (from which point is the question).
            case SUCCESS_DRAFT:
                // One remove operation should be enough for both of these since there should only be one affected document
                revisions.removeRevision(transferData.getKey());
                break;
            case SUCCESS_LOGICAL:
                // In this case we need to reindex all affected documents instead
                List<Integer> nos = revisions.getAllRevisionNumbers(transferData.getKey().getId());
                for(Integer no : nos) {
                    Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(transferData.getKey().getId(), no);
                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        revisions.indexRevision(pair.getRight().getKey());
                    }
                }
                break;
            default:
                // Errors don't need special handling
                break;
        }
    }
}