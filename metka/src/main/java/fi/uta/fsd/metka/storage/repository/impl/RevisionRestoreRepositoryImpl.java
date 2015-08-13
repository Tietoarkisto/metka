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
import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRestoreRepository;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Restores removed revisionables back to usage.
 * Needs special permissions to use but those are checked on the interface
 * Successful result is SUCCESS_RESTORE, all other results are failures of some sort
 */
@Repository
public class RevisionRestoreRepositoryImpl implements RevisionRestoreRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private Messenger messenger;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private Cascader cascader;

    @Override
    public RemoveResult restore(Long id) {
        return restore(id,null);
    }

    public RemoveResult restore(Long id, LocalDateTime dt) {
        Pair<ReturnResult, RevisionableInfo> pair = revisions.getRevisionableInfo(id);
        if(pair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return RemoveResult.NOT_FOUND;
        }
        if(!pair.getRight().getRemoved()) {
            return RemoveResult.NOT_REMOVED;
        }
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);

        if (dt != null) {
            if (!dt.equals(entity.getRemovalDate())) {
                return RemoveResult.NOT_REMOVED;
            }
        } else {
            dt = entity.getRemovalDate();
        }

        entity.setRemoved(false);
        entity.setRemovedBy(null);
        entity.setRemovalDate(null);

        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(entity.getId(), false, ConfigurationType.fromValue(entity.getType()));
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        RevisionData data = dataPair.getRight();

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        for(Operation operation : configPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.RESTORE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            cascader.cascade(CascadeInstruction.build(OperationType.RESTORE, DateTimeUserPair.build(dt)), data, operation.getTargets(), configPair.getRight());
        }

        List<Integer> nos = revisions.getAllRevisionNumbers(id);
        for(Integer no : nos) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(id, no);
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                revisions.indexRevision(revPair.getRight().getKey());
            }
        }

        messenger.sendAmqpMessage(messenger.FD_RESTORE, new RevisionPayload(data));

        return RemoveResult.SUCCESS_RESTORE;
    }
}
