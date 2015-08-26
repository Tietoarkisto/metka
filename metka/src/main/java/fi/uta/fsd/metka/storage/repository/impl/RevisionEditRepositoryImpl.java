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
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.restrictions.ValidateResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class RevisionEditRepositoryImpl implements RevisionEditRepository {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionHandlerRepository handler;

    @Autowired
    private RestrictionValidator validator;

    @Autowired
    private Cascader cascader;

    @Autowired
    private Messenger messenger;

    @Override
    public Pair<OperationResponse, RevisionData> edit(fi.uta.fsd.metka.model.general.RevisionKey key, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
                key.getId(), false, null);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "No revision with id " + key.getId() + ". Can't get editable revision.");
            return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), null);
        }
        RevisionData data = dataPair.getRight();
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            Logger.error(getClass(), "No revisionable for object for which revision was already found "+data.toString());
            return new ImmutablePair<>(OperationResponse.build(infoPair.getLeft()), data);
        }

        if(infoPair.getRight().getRemoved()) {
            Logger.warning(getClass(), "Can't create draft for removed Revisionable " + data.toString());
            return new ImmutablePair<>(OperationResponse.build(ReturnResult.REVISIONABLE_REMOVED), data);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Can't find configuration "+data.getConfiguration().toString()+" and so halting approval process.");
            return new ImmutablePair<>(OperationResponse.build(configPair.getLeft()), data);
        }

        // Do validation
        for(Operation operation : configPair.getRight().getRestrictions()) {
            if(!(operation.getType() == OperationType.EDIT || operation.getType() == OperationType.ALL)) {
                continue;
            }
            ValidateResult vr = validator.validate(data, operation.getTargets(), configPair.getRight());
            if(!vr.getResult()) {
                return new ImmutablePair<>(
                        OperationResponse.build(vr),
                        data);
            }
        }

        ReturnResult result;
        // If data is not a draft then try to create a new draft
        // If data is missing a handler then try to claim the data
        // Return either a claimed draft or a draft that is handled by someone else
        if(data.getState() != RevisionState.DRAFT) {
            // Data is not draft, we need a new revision
            result = checkEditPermissions(data);
            if(result != ReturnResult.CAN_CREATE_DRAFT) {
                Logger.warning(getClass(), "User can't create draft revision because: "+result);
                return new ImmutablePair<>(OperationResponse.build(result), data);
            }
            Pair<ReturnResult, RevisionKey> keyPair = revisions.createNewRevision(data);
            if(keyPair.getLeft() != ReturnResult.REVISION_CREATED) {
                return new ImmutablePair<>(OperationResponse.build(keyPair.getLeft()), data);
            }

            // TODO: If update fails then the possibly created revision should be removed
            RevisionData newData = new RevisionData(keyPair.getRight().toModelKey(), configPair.getRight().getKey());
            newData.setState(RevisionState.DRAFT);
            copyDataToNewRevision(data, newData);

            ReturnResult update = revisions.updateRevisionData(newData);
            if(update != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(OperationResponse.build(update), data);
            } else {
                data = newData;
            }
            result = ReturnResult.REVISION_CREATED;
        } else {
            result = ReturnResult.REVISION_FOUND;
        }

        if(!StringUtils.hasText(data.getHandler())) {
            // Try to claim revision since it hasn't been claimed yet
            handler.changeHandler(RevisionKey.fromModelKey(data.getKey()), false);
            // Get the revision again so that we have a claimed version
            dataPair = revisions.getRevisionData(RevisionKey.fromModelKey(data.getKey()));
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                return new ImmutablePair<>(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
            }
            data = dataPair.getRight();
        }

        // Do cascade
        for(Operation operation : configPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.EDIT || operation.getType() == OperationType.ALL)) {
                continue;
            }
            cascader.cascade(CascadeInstruction.build(OperationType.EDIT, info), data, operation.getTargets(), configPair.getRight());
        }
        // Cascade can't stop the main operation
        /*if(!(result != ReturnResult.OPERATION_SUCCESSFUL)) {
            return new ImmutablePair<>(result, data);
        }*/

        if(result == ReturnResult.REVISION_CREATED) {
            messenger.sendAmqpMessage(messenger.FD_DRAFT, new RevisionPayload(data));
            //revisions.indexRevision(data.getKey());
        }

        finalizeRevisionEdit(result, data);

        return new ImmutablePair<>(OperationResponse.build(result), data);
    }

    // TODO: Move to restrictions when time, requires making state checking restrictions but should be easy
    private ReturnResult checkEditPermissions(RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                Pair<StatusCode, ValueDataField> field = data.dataField(ValueDataFieldCall.get("study"));
                if(field.getLeft() != StatusCode.FIELD_FOUND) {
                    Logger.error(getClass(), "Didn't find study reference on "+data.toString()+" can't create new draft.");
                    return ReturnResult.REVISIONABLE_NOT_FOUND;
                }
                ValueContainer vc = field.getRight().getValueFor(Language.DEFAULT);
                return vc == null ? ReturnResult.REVISION_FOUND : checkStudyDraftStatus(vc.valueAsInteger());
            default:
                return ReturnResult.CAN_CREATE_DRAFT;
        }
    }

    private ReturnResult checkStudyDraftStatus(Long id) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(id, false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Didn't find revision for study id "+id+" with result "+pair.getLeft());
            return pair.getLeft();
        }
        if(pair.getRight().getState() != RevisionState.DRAFT) {
            return ReturnResult.REVISION_NOT_A_DRAFT;
        }

        if(!AuthenticationUtil.isHandler(pair.getRight())) {
            return ReturnResult.USER_NOT_HANDLER;
        }
        return ReturnResult.CAN_CREATE_DRAFT;
    }

    private void copyDataToNewRevision(RevisionData oldData, RevisionData newData) {
        for(DataField field : oldData.getFields().values()) {
            newData.getFields().put(field.getKey(), field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }

        // Move approve information to new revision, these are updated as needed when actual approval is required.
        for(Language lang : oldData.getApproved().keySet()) {
            newData.approveRevision(lang, oldData.approveInfoFor(lang));
        }
    }

    private void finalizeRevisionEdit(ReturnResult result, RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT: {
                finalizeStudyAttachmentEdit(result, data);
                break;
            }
            case STUDY_VARIABLES: {
                finalizeStudyVariablesEdit(result, data);
            }
            case STUDY_VARIABLE: {
                finalizeStudyVariableEdit(result, data);
            }
        }
    }

    private void finalizeStudyAttachmentEdit(ReturnResult result, RevisionData data) {
        if(result != ReturnResult.REVISION_CREATED) {
            return;
        }

        // TODO:
        // * Get study revision
        // * Check that revision is DRAFT (this should always be the case but if not for some reason then just stop the finalization)
        // * Get files container
        // * Find row that points to this revisionable (i.e. value starts with '{id}-')
        // * If value is different from {key} then update to {key}
    }

    private void finalizeStudyVariablesEdit(ReturnResult result, RevisionData data) {
        if(result != ReturnResult.REVISION_CREATED) {
            return;
        }

        // TODO:
        // * Get study revision
        // * Check that revision is DRAFT (this should always be the case but if not for some reason then just stop the finalization)
        // * Get studyvariables container
        // * Find row with this revisionables language
        // * If variables-value is different from {key} then update to {key}
        //
        // * Get attachment referenced in this study variables
        // * QUESTION: What to do if the attachment is not DRAFT?
        //   Should we just create a new draft and then trust that if the user makes no changes and the variables draft is removed then all changes caused by this are reverted automatically?
        // * When we have a draft of attachment then check if the value in variables-field is equal to {key} and if not then update it to {key}
    }

    private void finalizeStudyVariableEdit(ReturnResult result, RevisionData data) {
        if(result != ReturnResult.REVISION_CREATED) {
            return;
        }

        // TODO:
        // * Get variables
        // * QUESTION: same as with study variables case with attachment, what to do if there is no DRAFT?
        // * When we have draft get variables container
        // * Find row that points to this revisionable (i.e. value starts with '{id}-')
        // * If value is different from {key} then update to {key}
    }
}
