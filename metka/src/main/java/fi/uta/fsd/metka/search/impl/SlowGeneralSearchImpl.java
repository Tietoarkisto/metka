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

package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.search.GeneralSearch;
import fi.uta.fsd.metka.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository("generalSearch")
public class SlowGeneralSearchImpl implements GeneralSearch {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public List<RevisionDataRemovedContainer> tempFindAllStudies() {
        List<RevisionDataRemovedContainer> result = new ArrayList<>();
        List<StudyEntity> entities = em.createQuery("SELECT s FROM StudyEntity s ORDER BY s.id ASC", StudyEntity.class)
                .getResultList();
        RevisionDataRemovedContainer container;
        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(entity.getCurApprovedNo() != null) {
                    Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.currentApprovedRevisionKey(), ConfigurationType.STUDY);

                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        container = new RevisionDataRemovedContainer(pair.getRight(), false);
                        result.add(container);
                    }
                }
                if(entity.hasDraft()) {
                    Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.STUDY);

                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        container = new RevisionDataRemovedContainer(pair.getRight(), false);
                        result.add(container);
                    }
                }
            } else {
                Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.currentApprovedRevisionKey(), ConfigurationType.STUDY);

                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    container = new RevisionDataRemovedContainer(pair.getRight(), true);
                    result.add(container);
                }
            }
        }
        return result;
    }

}
