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

package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.AdjacentRevisionRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Contains methods for general operations that don't require their own repositories.
 */
@Transactional(readOnly = true)
public interface RevisionRepository {

    /**
     * Returns information on if revisionable object with given id has been set to removed state.
     * @param id Id of the revisionable object to be checked
     * @return Pair with left value being the removal state of the revisionable (true if removed) and right value being the removal time of said object.
     */
    Pair<ReturnResult, RevisionableInfo> getRevisionableInfo(Long id);

    /**
     * Returns the revision data with given id and number.
     * Forwards the call to revision key variant with null type.
     * @param id RevisionableId of the requested revision
     * @param no Revision number of the requested revision
     * @return Pair with ReturnResult in the left value and returned RevisionData in the right value, or null if no RevisionData is returned.
     */
    Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer no);

    /**
     * Returns the revision data based on String form of revision key.
     * If it's detected that the key does not contain the revision number then latest revision is fetched.
     * In that case the second form with boolean parameter should be used if drafts need to be excluded
     * @param key
     * @return
     */
    Pair<ReturnResult, RevisionData> getRevisionData(String key);
    Pair<ReturnResult, RevisionData> getRevisionData(String key, boolean approveOnly);
    Pair<ReturnResult, RevisionData> getRevisionData(RevisionKey key);

    /*
     * Returns a revision number for given id.
     * If approvedOnly is true then returns the current approved number, otherwise returns latest revision number.
     * If type is present then checks to see that type of revisionable matches given type and if not a correct error value is returned
     * @param id Requested revisionable id
     * @param approvedOnly Disregards DRAFT revisions if true
     * @param type Requested type of revisionable, can be null in which case this check is omitted
     * @return
     */
    /*Pair<ReturnResult, Integer> getLatestRevisionNoForIdAndType(Long id, boolean approvedOnly, ConfigurationType type);*/

    List<Integer> getAllRevisionNumbers(Long id);

    /**
     * Takes provided RevisionData, serializes it and tries to insert it into database
     * @param revision RevisionData to be serialized and updated to database
     * @return ReturnResult informing if the operation was successful or not
     */
    @Transactional(readOnly = false) ReturnResult updateRevisionData(RevisionData revision);

    @Transactional(readOnly = false) Pair<ReturnResult, RevisionKey> createNewRevision(RevisionData revision);

    /**
     * Returns file directory path for given study based on property value and study id.
     * @param study    Revisionable id of study
     * @return ReturnResult, String pair pointing to root directory of study files if entity was found
     */
    Pair<ReturnResult, String> getStudyFileDirectory(long study);

    Pair<ReturnResult, String> getStudyId(Long id);

    List<RevisionData> getVariableRevisionsOfVariables(Long id);

    Pair<ReturnResult,RevisionData> getAdjacentRevision(AdjacentRevisionRequest request);

    @Transactional(readOnly = false) void indexRevision(RevisionKey key);
    @Transactional(readOnly = false) void indexRevision(fi.uta.fsd.metka.model.general.RevisionKey key);
    @Transactional(readOnly = false) void indexRevisions(RevisionKey key);
    @Transactional(readOnly = false) void indexRevisions(fi.uta.fsd.metka.model.general.RevisionKey key);
    @Transactional(readOnly = false) void removeRevision(RevisionKey key);
    @Transactional(readOnly = false) void removeRevision(fi.uta.fsd.metka.model.general.RevisionKey key);

    void sendStudyErrorMessageIfNeeded(RevisionData revision, Configuration configuration);

    /**
     * Fetched the key of next revision that needs indexing and sets the indexing requested value
     * @return RevisionKey of the revision that needs indexing
     */
    @Transactional(readOnly = false) fi.uta.fsd.metka.model.general.RevisionKey getNextForIndexing();

    /**
     * Marks given revision as being indexed and sets the indexing handled value
     * @param key    RevisionKey of the revision that needs operations to be performed
     */
    @Transactional(readOnly = false) void markAsIndexed(RevisionKey key);

    /**
     * Clears the given revision of indexing values, sets index status, indexing requested and indexing handled to null
     * @param key    RevisionKey of the revision that needs operations to be performed
     */
    @Transactional(readOnly = false) void clearIndexing(RevisionKey key);

    /**
     * Clears indexing from all revisions.
     */
    @Transactional(readOnly = false) void clearAll();

    /**
     * Clears revisions that have for some reason been requested but have not been handled
     */
    @Transactional(readOnly = false) void clearPartlyIndexed();

    /**
     * Returns the number of revisions still left to be indexed
     * @return
     */
    Pair<ReturnResult, Long> getRevisionsWaitingIndexing();
}
