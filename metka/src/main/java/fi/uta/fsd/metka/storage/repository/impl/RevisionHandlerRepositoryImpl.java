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

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionHandlerRepositoryImpl implements RevisionHandlerRepository {

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, TransferData> changeHandler(RevisionKey key, boolean clear) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }

        RevisionData data = pair.getRight();
        boolean update = false;
        if(clear) {

            if(data.getHandler() != null) {
                data.setHandler(null);
                update = true;
            }
        } else {
            if(!AuthenticationUtil.isHandler(data)) {
                data.setHandler(AuthenticationUtil.getUserName());
                update = true;
            }
        }

        if(update) {
            ReturnResult result = revisions.updateRevisionData(data);
            if (result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, null);
            }
        }

        // For now let's assume that these just work
        finalizeChange(data, clear);

        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(data.getKey().getId());
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(info.getLeft(), null);
        }

        revisions.indexRevision(data.getKey());

        return new ImmutablePair<>(ReturnResult.REVISION_UPDATE_SUCCESSFUL, TransferData.buildFromRevisionData(data, info.getRight()));
    }

    private void finalizeChange(RevisionData revision, boolean clear) {
        switch(revision.getConfiguration().getType()) {
            case STUDY:
                finalizeStudy(revision, clear);
                break;
            case STUDY_VARIABLES:
                finalizeStudyVariables(revision, clear);
                break;
            default:
                break;
        }
    }

    private void finalizeStudy(RevisionData revision, boolean clear) {
        Pair<StatusCode, ReferenceContainerDataField> referenceContainer = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
        if(referenceContainer.getLeft() == StatusCode.FIELD_FOUND && !referenceContainer.getRight().getReferences().isEmpty()) {
            changeStudyAttachmentHandlers(referenceContainer.getRight(), clear);
        }

        Pair<StatusCode, ValueDataField> value = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(value.getLeft() == StatusCode.FIELD_FOUND && value.getRight().hasValueFor(Language.DEFAULT)) {
           Pair<ReturnResult, RevisionData> variables = revisions.getLatestRevisionForIdAndType(value.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
           if(variables.getLeft() == ReturnResult.REVISION_FOUND) {
               changeStudyVariablesHandlers(variables.getRight(), clear);
           }
        }
    }

    private void changeStudyVariablesHandlers(RevisionData variables, boolean clear) {
        changeHandler(RevisionKey.fromModelKey(variables.getKey()), clear);
    }

    private void changeStudyAttachmentHandlers(ReferenceContainerDataField references, boolean clear) {
        for(ReferenceRow row : references.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                changeHandler(RevisionKey.fromModelKey(pair.getRight().getKey()), clear);
            }
        }
    }

    private void finalizeStudyVariables(RevisionData variables, boolean clear) {
        Pair<StatusCode, ReferenceContainerDataField> referenceContainer = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(referenceContainer.getLeft() == StatusCode.FIELD_FOUND && !referenceContainer.getRight().getReferences().isEmpty()) {
            changeStudyVariableHandlers(referenceContainer.getRight(), clear);
        }
    }

    private void changeStudyVariableHandlers(ReferenceContainerDataField references, boolean clear) {
        for(ReferenceRow row : references.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                changeHandler(RevisionKey.fromModelKey(pair.getRight().getKey()), clear);
            }
        }
    }
}
