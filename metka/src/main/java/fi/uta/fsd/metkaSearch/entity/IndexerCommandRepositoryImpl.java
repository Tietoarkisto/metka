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

package fi.uta.fsd.metkaSearch.entity;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class IndexerCommandRepositoryImpl implements IndexerCommandRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public void addIndexerCommand(IndexerCommand command) {
        IndexerCommandEntity entity = IndexerCommandEntity.buildFromCommand(command);
        em.persist(entity);
    }

    @Override
    public void markCommandAsHandled(Long id) {
        IndexerCommandEntity entity = em.find(IndexerCommandEntity.class, id);
        if(entity != null) {
            em.remove(entity); // We don't need to save the command
            //entity.setHandled(new LocalDateTime());
        }
    }

    @Override
    public void clearCommandRequest(Long id) {
        IndexerCommandEntity entity = em.find(IndexerCommandEntity.class, id);
        if(entity != null) {
            entity.setRequested(null);
        }
    }

    @Override
    public IndexerCommand getNextCommand(IndexerConfigurationType type, String path) {
        IndexerCommandEntity entity = null;
        List<IndexerCommandEntity> entities = em.createQuery("SELECT e FROM IndexerCommandEntity e " +
                "WHERE e.requested IS NULL AND e.type=:type AND e.path=:path ORDER BY e.created ASC", IndexerCommandEntity.class)
                .setParameter("type", type)
                .setParameter("path", path)
                .setMaxResults(1)
                .getResultList();
        if(entities.size() == 1) {
            entity = entities.get(0);
        }
        if(entity != null) {
            entity.setRequested(new LocalDateTime());
            return entity.buildCommandFromEntity();
        } else {
            return null;
        }
    }

    @Override
    public IndexerCommand getNextCommandWithoutChange() {
        IndexerCommandEntity entity = null;
        List<IndexerCommandEntity> entities = em.createQuery("SELECT e FROM IndexerCommandEntity e " + "WHERE e.requested IS NULL ORDER BY e.created ASC",
                IndexerCommandEntity.class)
                .setMaxResults(1)
                .getResultList();
        if(entities.size() == 1) {
            entity = entities.get(0);
        }
        if(entity != null) {
            return entity.buildCommandFromEntity();
        } else {
            return null;
        }
    }

    @Override
    public void clearAllRequests() {
        em.createQuery("UPDATE IndexerCommandEntity e SET e.requested=NULL WHERE e.handled IS NULL").executeUpdate();
    }

    @Override
    public void removeAllHandled() {
        em.createQuery("DELETE FROM IndexerCommandEntity e WHERE e.handled IS NOT NULL").executeUpdate();
    }

    @Override
    public Pair<ReturnResult, Integer> getOpenIndexCommands() {
        List<IndexerCommandEntity> entities = em
                .createQuery("SELECT e FROM IndexerCommandEntity e WHERE e.handled IS NULL AND e.requested IS NULL", IndexerCommandEntity.class)
                .getResultList();

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, entities.size());
    }
}
