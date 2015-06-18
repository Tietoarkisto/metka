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

import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface StudyAttachmentRepository {
    public RevisionData studyAttachmentForPath(String path, Long studyId);

    /**
     * Creates a new study attachment with initial revision in DRAFT state
     *
     * @param studyId Id of the study this study attachment is attached to.
     * @returnś
     */
    public RevisionData newStudyAttachment(Long studyId);

    /**
     * Return a DRAFT revision for given STUDY_ATTACHMENT.
     * If there's no existing DRAFT then one is created, otherwise returns an existing draft
     * @param id Revisionable id
     * @return RevisionData for DRAFT revision for given file, if none exists one is created.
     */
    public RevisionData getEditableStudyAttachmentRevision(Long id);

    /**
     * Adds a row to FILE_LINK_QUEUE for future checking that a reference actually exists in target revisionable.
     * Will also make a note if the file is a por file in need of parsing and adding to a STUDY.
     * It is assumed that this is handled before a DRAFT is approved so there's only need to consider current
     * latest revision (that should be a draft)
     * @param studyId RevisionableId from where the file should be found.
     * @param attachmentId RevisionableId of the File that should be linked
     * @param key Field key of the REFERENCECONTAINER where the reference should be found
     * @param path File path, used to detect a por file
     */
    public void addFileLinkEvent(Long studyId, Long attachmentId, String key, String path);
}
