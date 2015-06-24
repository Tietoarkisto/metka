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

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;

class FieldTargetCascader {
    static boolean cascade(CascadeInstruction instruction, Target target, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        Field field = configuration.getField(target.getContent());
        if(field == null) {
            // Configuration error, no field with provided name
            return false;
        }
        if(field.getSubfield() && target.getParent() == null) {
            // Something is wrong. Field is marked as a subfield but target doesn't have a parent
            return false;
        }

        DataField d;
        // Can't cascade to regular containers, those should only be used with CHILDREN target to get to references in fields inside their rows
        switch (field.getType()) {
            case REFERENCECONTAINER:
                d = context.dataField(ReferenceContainerDataFieldCall.get(target.getContent())).getRight();
                break;
            default:
                d = context.dataField(ValueDataFieldCall.get(target.getContent())).getRight();
                break;
        }
        // TODO: Allow restrictions in cascade operations, this gives more control and is easy to do
        /*for(Check check : target.getChecks()) {
            // Check is enabled
            if(cascader.cascade(check.getRestrictors(), context, configuration)) {
                if(!checkConditionForField(field, d, check.getCondition(), context, configuration, searcher)) {
                    return false;
                }
            }
        }*/
        boolean result = true;
        if(d != null) {
            if(!cascadeOperation(instruction, d, field, configuration, repositories)) {
                result = false;
            }
        }

        if(!DataFieldCascader.cascade(instruction, target.getTargets(), context, configuration, repositories)) {
            result = false;
        }

        return result;
    }

    // CONDITION HANDLERS
    // *************************
    private static boolean cascadeOperation(CascadeInstruction instruction, DataField d, Field field, Configuration configuration, Cascader.RepositoryHolder repositories) {
        switch(field.getType()) {
            case REFERENCECONTAINER:
                return cascadeOperationToReferenceContainer(instruction, (ReferenceContainerDataField) d, field, configuration, repositories);
            default:
                return cascadeOperationToValue(instruction, (ValueDataField) d, field, configuration, repositories);
        }
    }

    private static boolean cascadeOperationToReferenceContainer(CascadeInstruction instruction, ReferenceContainerDataField d, Field field,
            Configuration configuration, Cascader.RepositoryHolder repositories) {
        boolean result = true;
        if(d.hasValidRows()) {
            for(ReferenceRow row : d.getReferences()) {
                if(row.getRemoved()) {
                    continue;
                }

                if(row.hasValue()) {
                    if(!cascadeReference(instruction, row.getReference(), configuration.getReference(field.getReference()), repositories)) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param d            ValueDataField
     * @return boolean telling if validation is successful
     */
    private static boolean cascadeOperationToValue(CascadeInstruction instruction, ValueDataField d, Field field, Configuration configuration, Cascader.RepositoryHolder repositories) {
        // Lets check that the field actually has somewhere to cascade to
        if(!(field.getType() == FieldType.REFERENCE || field.getType() == FieldType.SELECTION)) {
            // Configuration error, no cascade possible
            return false;
        }
        Reference reference;
        // If selection is
        if(field.getType() == FieldType.SELECTION) {
            SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
            if(list == null || list.getType() != SelectionListType.REFERENCE) {
                // Configuration error, no cascade possible
                return false;
            }
            reference = configuration.getReference(list.getReference());
        } else {
            reference = configuration.getReference(field.getReference());
        }

        if(reference == null) {
            // Configuration error, no cascade possible
            return false;
        }

        // Only revision references can cascade
        if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
            // Configuration error, no cascade possible
            return false;
        }

        // References can't be translatable, get default language value
        if(d.hasValueFor(Language.DEFAULT) && d.getValueFor(Language.DEFAULT).hasValue()) {
            return cascadeReference(instruction, d.getValueFor(Language.DEFAULT).getValue(), configuration.getReference(field.getReference()), repositories);
        }

        return true;
    }

    private static boolean cascadeReference(CascadeInstruction instruction, Value value, Reference reference, Cascader.RepositoryHolder repositories) {
        if(reference == null) {
            // Configuration error, no cascade possible
            return false;
        }

        // Only revision references can cascade
        if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
            // Configuration error, no cascade possible
            return false;
        }

        RevisionData data;
        if(reference.getType() == ReferenceType.REVISIONABLE) {
            data = repositories.getRevisions().getLatestRevisionForIdAndType(value.asInteger(), false, null).getRight();
        } else {
            String[] splits = value.getValue().split("-");
            if(splits.length != 2) {
                return false;
            }
            data = repositories.getRevisions().getRevisionData(Long.parseLong(splits[0]), Integer.parseInt(splits[1])).getRight();
        }

        if(data == null) {
            return true;
        }

        TransferData transferData = TransferData.buildFromRevisionData(data, RevisionableInfo.FALSE);
        // TODO: Restore, Claim, Release
        switch(instruction.getOperation()) {
            case SAVE: {
                // Cascading save doesn't really make any sense at the moment since the user can only edit one form at a time
                return true;
            } case APPROVE: {
                OperationResponse result = repositories.getApprove().approve(transferData, instruction.getInfo()).getLeft();
                ReturnResult rr = ReturnResult.valueOf(result.getResult());
                return rr == ReturnResult.OPERATION_SUCCESSFUL || rr == ReturnResult.REVISION_NOT_A_DRAFT || rr == ReturnResult.NO_CHANGES;
            } case REMOVE: {
                OperationResponse result;
                if(instruction.getDraft() == null) {
                    result = repositories.getRemove().remove(transferData, instruction.getInfo());
                } else if(instruction.getDraft()) {
                    result = repositories.getRemove().removeDraft(transferData, instruction.getInfo());
                } else {
                    result = repositories.getRemove().removeLogical(transferData, instruction.getInfo());
                }
                RemoveResult rr = RemoveResult.valueOf(result.getResult());
                return rr == RemoveResult.SUCCESS_LOGICAL
                        || rr == RemoveResult.ALREADY_REMOVED
                        || rr == RemoveResult.SUCCESS_REVISIONABLE
                        || rr == RemoveResult.SUCCESS_DRAFT
                        || rr == RemoveResult.NOT_DRAFT;
            } case REMOVE_REVISIONABLE: {
                // Cascade results don't matter for REMOVE_REVISIONABLE so we can just return true no matter the result
                RevisionableInfo revInfo = repositories.getRevisions().getRevisionableInfo(transferData.getKey().getId()).getRight();
                if(instruction.getDraft() && revInfo != null && revInfo.getApproved() == null) {
                    repositories.getRemove().removeDraft(transferData, instruction.getInfo());
                }
                return true;
            } case EDIT: {
                OperationResponse result = repositories.getEdit().edit(transferData, instruction.getInfo()).getLeft();
                ReturnResult rr = ReturnResult.valueOf(result.getResult());
                return rr == ReturnResult.REVISION_FOUND || rr == ReturnResult.REVISION_CREATED;
            }
        }

        return true;
    }
}
