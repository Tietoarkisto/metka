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

import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class RevisionHandlerRepositoryImpl implements RevisionHandlerRepository {

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private Messenger messenger;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private Cascader cascader;

    @Override
    public Pair<ReturnResult, TransferData> beginEditing(RevisionKey key) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }

        RevisionData data = pair.getRight();
        if(StringUtils.hasText(data.getHandler())) {
            return new ImmutablePair<>(ReturnResult.HAS_HANDLER, null);
        }

        data.setHandler(AuthenticationUtil.getUserName());
        ReturnResult result = revisions.updateRevisionData(data);
        if (result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
            return new ImmutablePair<>(result, null);
        }

        // For now let's assume that these just work
        return finalizeChange(data, OperationType.BEGIN_EDIT);
    }

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

        return finalizeChange(data, clear ? OperationType.RELEASE : OperationType.CLAIM);
    }

    private Pair<ReturnResult, TransferData> finalizeChange(RevisionData data, OperationType opType) {
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        for(Operation operation : configPair.getRight().getCascade()) {
            if(!(operation.getType() == opType || operation.getType() == OperationType.ALL)) {
                continue;
            }
            cascader.cascade(CascadeInstruction.build(opType, DateTimeUserPair.build()), data, operation.getTargets(), configPair.getRight());
        }

        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(data.getKey().getId());
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(info.getLeft(), null);
        }

        messenger.sendAmqpMessage(StringUtils.hasText(data.getHandler()) ? messenger.FD_CLAIM : messenger.FD_RELEASE, new RevisionPayload(data));
        //revisions.indexRevision(data.getKey());

        return new ImmutablePair<>(ReturnResult.REVISION_UPDATE_SUCCESSFUL, TransferData.buildFromRevisionData(data, info.getRight()));
    }
}
