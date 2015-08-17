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

package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;

class OperationCascader {
    static boolean cascade(RevisionData data, CascadeInstruction instruction, Cascader.RepositoryHolder repositories) {
        TransferData transferData = TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE);
        switch(instruction.getOperation()) {
            case SAVE: {
                // Cascading save doesn't really make any sense at the moment since the user can only edit one form at a time
                return true;
            } case APPROVE: {
                OperationResponse result = repositories.getApprove().approve(transferData, instruction.getInfo()).getLeft();
                ReturnResult rr = ReturnResult.valueOf(result.getResult());
                return rr == ReturnResult.OPERATION_SUCCESSFUL || rr == ReturnResult.REVISION_NOT_A_DRAFT || rr == ReturnResult.NO_CHANGES;
            } case REMOVE: {
                //just cascading with "REMOVE"-operation should never happen
                RemoveResult rr = RemoveResult.valueOf(repositories.getRemove().remove(transferData.getKey(), instruction.getInfo()).getResult());
                return rr == RemoveResult.SUCCESS_LOGICAL
                        || rr == RemoveResult.ALREADY_REMOVED
                        || rr == RemoveResult.SUCCESS_REVISIONABLE
                        || rr == RemoveResult.SUCCESS_DRAFT
                        || rr == RemoveResult.NOT_DRAFT;
            } case REMOVE_DRAFT: {
                RemoveResult rr = RemoveResult.valueOf(repositories.getRemove().removeDraft(transferData.getKey(), instruction.getInfo()).getResult());
                return rr == RemoveResult.ALREADY_REMOVED || rr == RemoveResult.SUCCESS_REVISIONABLE || rr == RemoveResult.SUCCESS_DRAFT || rr == RemoveResult.NOT_DRAFT;
            } case REMOVE_LOGICAL: {
                RemoveResult rr = RemoveResult.valueOf(repositories.getRemove().removeLogical(transferData.getKey(), instruction.getInfo(), true).getResult());
                return rr  == RemoveResult.ALREADY_REMOVED || rr == RemoveResult.SUCCESS_LOGICAL ;
            }
            case REMOVE_REVISIONABLE: {
                // Cascade results don't matter for REMOVE_REVISIONABLE so we can just return true no matter the result
                RevisionableInfo revInfo = repositories.getRevisions().getRevisionableInfo(transferData.getKey().getId()).getRight();
                if(instruction.getOperation() == OperationType.REMOVE_REVISIONABLE && revInfo != null && revInfo.getApproved() == null) {
                    repositories.getRemove().removeDraft(transferData.getKey(), instruction.getInfo());
                }
                return true;
            } case EDIT: {
                OperationResponse result = repositories.getEdit().edit(transferData.getKey(), instruction.getInfo()).getLeft();
                ReturnResult rr = ReturnResult.valueOf(result.getResult());
                return rr == ReturnResult.REVISION_FOUND || rr == ReturnResult.REVISION_CREATED;
            } case RESTORE: {
               RemoveResult result = repositories.getRestore().restore(transferData.getKey().getId(), instruction.getInfo().getTime());
                return result == RemoveResult.SUCCESS_RESTORE;
            } case BEGIN_EDIT: {
                ReturnResult rr = repositories.getHandler().beginEditing(RevisionKey.fromModelKey(transferData.getKey())).getLeft();
                return rr == ReturnResult.REVISION_UPDATE_SUCCESSFUL;
            } case CLAIM: {
                ReturnResult rr = repositories.getHandler().changeHandler(RevisionKey.fromModelKey(transferData.getKey()), false).getLeft();
                return rr == ReturnResult.REVISION_UPDATE_SUCCESSFUL;
            } case RELEASE: {
                ReturnResult rr = repositories.getHandler().changeHandler(RevisionKey.fromModelKey(transferData.getKey()), true).getLeft();
                return rr == ReturnResult.REVISION_UPDATE_SUCCESSFUL;
            }
        }

        return true;
    }
}
