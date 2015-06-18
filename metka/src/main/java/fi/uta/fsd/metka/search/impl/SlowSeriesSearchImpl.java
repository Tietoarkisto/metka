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

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.search.SeriesSearch;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public List<String> findAbbreviations() {
        List<String> list = new ArrayList<>();

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for series "+entity.toString());
                continue;
            }
            RevisionData revision = pair.getRight();
            // Use the method with less sanity checks since there's no point in getting configuration here.
            Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("seriesabbr"));
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                list.add(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List<RevisionSearchResult> findNames() {
        List<RevisionSearchResult> results = new ArrayList<>();

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(entity.latestRevisionKey(), ConfigurationType.SERIES);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for series " + entity.toString());
                continue;
            }
            RevisionSearchResult result = new RevisionSearchResult();
            RevisionData revision = pair.getRight();
            result.setId(revision.getKey().getId());
            Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("seriesname"));
            result.getValues().put("seriesname",
                    fieldPair.getLeft() == StatusCode.FIELD_FOUND
                            ? fieldPair.getRight().getActualValueFor(Language.DEFAULT)
                            : "");
            results.add(result);
        }
        Collections.sort(results, new Comparator<RevisionSearchResult>() {
            @Override
            public int compare(RevisionSearchResult o1, RevisionSearchResult o2) {
                int result = o1.getValues().get("seriesname").compareTo(o2.getValues().get("seriesname"));
                return result != 0 ? result : o1.getId().compareTo(o2.getId());
            }
        });
        return results;
    }


}
