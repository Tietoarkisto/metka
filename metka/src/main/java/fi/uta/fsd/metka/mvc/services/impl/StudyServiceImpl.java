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

package fi.uta.fsd.metka.mvc.services.impl;

import codebook25.CodeBookDocument;
import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.ddi.DDIBuilderService;
import fi.uta.fsd.metka.ddi.DDIReaderService;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metka.transfer.settings.JSONListEntry;
import fi.uta.fsd.metka.transfer.study.StudyErrorsResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudyPair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class StudyServiceImpl implements StudyService {

    @Autowired
    private StudySearch search;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    @Autowired
    private DDIBuilderService ddiBuilderService;

    @Autowired
    private DDIReaderService ddiReaderService;

    @Override public RevisionSearchResponse collectAttachmentHistory(TransferData transferData) {
        RevisionSearchResponse response = new RevisionSearchResponse();
        if(transferData.getConfiguration().getType() != ConfigurationType.STUDY_ATTACHMENT) {
            response.setResult(ReturnResult.INCORRECT_TYPE_FOR_OPERATION);
            return response;
        }

        Pair<ReturnResult, List<RevisionSearchResult>> results = search.collectAttachmentHistory(transferData.getKey().getId());
        response.setResult(results.getLeft());
        if(results.getLeft() == ReturnResult.OPERATION_SUCCESSFUL) {
            response.getRows().addAll(results.getRight());
        }
        return response;
    }

    @Override
    public Pair<ReturnResult, CodeBookDocument> exportDDI(Long id, Integer no, Language language) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(id, no);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Return error to user
            return new ImmutablePair<>(pair.getLeft(), null);
        } else if(pair.getRight().getConfiguration().getType() != ConfigurationType.STUDY) {
            // Only applicaple to studies
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        } else {
            RevisionData revision = pair.getRight();
            Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(revision.getConfiguration());
            if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                return new ImmutablePair<>(configurationPair.getLeft(), null);
            }
            Pair<ReturnResult, CodeBookDocument> cb = ddiBuilderService.buildDDIDocument(language, revision, configurationPair.getRight());
            return cb;
        }
    }

    @Override
    public ReturnResult importDDI(TransferData transferData, String path) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return pair.getLeft();
        }
        return ddiReaderService.readDDIDocument(path, pair.getRight());
    }

    @Override
    public String getOrganizations() {
        Pair<ReturnResult, String> result =  miscJSONRepository.findStringByKey("Organizations");

        return result == null || result.getRight() == null ? "" : result.getRight();
    }

    @Override
    public ReturnResult uploadOrganizations(JsonNode misc) {
        //backupAndCopy(file, "misc");

        ReturnResult result = miscJSONRepository.insert(misc);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }
}
