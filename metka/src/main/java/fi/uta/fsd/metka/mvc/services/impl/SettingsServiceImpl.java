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

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    @Autowired
    private APIRepository api;

    @Autowired
    private ReportRepository reports;

    @Autowired
    private IndexerComponent indexer;

    @Value("${dir.autoload}")
    private String rootFolder;

    // User report repository to generate example report
    @Override public String generateReport() {
        // Here we could choose which report to generate based on some parameter
        return reports.gatherGeneralReport();
    }

    @Override public APIUserListResponse listAPIUsers() {
        Pair<ReturnResult, List<APIUserEntry>> result =api.listAPIUsers();
        APIUserListResponse response = new APIUserListResponse();
        response.setResult(result.getLeft());
        response.getUsers().addAll(result.getRight());
        return response;
    }

    @Override public APIUserListResponse newAPIUser(NewAPIUserRequest request) {
        Pair<ReturnResult, APIUserEntry> result = api.newAPIUser(request);
        APIUserListResponse response = new APIUserListResponse();
        response.setResult(result.getLeft());
        if(result.getRight() != null) {
            response.getUsers().add(result.getRight());
        }
        return response;
    }

    @Override public ReturnResult removeAPIUser(String userName) {
        return api.removeAPIUser(userName);
    }

    @Override
    public List<JSONListEntry> getJsonList(UploadJsonRequest.JsonType type) {
        List<JSONListEntry> entries = new ArrayList<>();
        switch(type) {
            case DATA_CONF: {
                List<ConfigurationKey> keys = configurations.getDataKeys();
                for (ConfigurationKey key : keys) {
                    JSONListEntry entry = new JSONListEntry();
                    entry.setConfigKey(key);
                    entry.setType(type);
                    entry.setTitle(key.getType().toValue() + "." + key.getVersion());
                    entries.add(entry);
                }
                break;
            }
            case GUI_CONF: {
                List<ConfigurationKey> keys = configurations.getGUIKeys();
                for (ConfigurationKey key : keys) {
                    JSONListEntry entry = new JSONListEntry();
                    entry.setConfigKey(key);
                    entry.setType(type);
                    entry.setTitle(key.getType().toValue() + "." + key.getVersion());
                    entries.add(entry);
                }
                break;
            }
            case MISC: {
                List<String> keys = miscJSONRepository.getJsonKeys();
                for (String key : keys) {
                    JSONListEntry entry = new JSONListEntry();
                    entry.setJsonKey(key);
                    entry.setType(type);
                    entry.setTitle(key);
                    entries.add(entry);
                }
                break;
            }
        }
        return entries;
    }

    @Override
    public String getJsonContent(JSONListEntry entry) {
        Pair<ReturnResult, String> result = null;
        switch(entry.getType()) {
            case DATA_CONF:
                result = configurations.getDataConfiguration(entry.getConfigKey());
                break;
            case GUI_CONF:
                result = configurations.getGUIConfiguration(entry.getConfigKey());
                break;
            case MISC:
                result = miscJSONRepository.findStringByKey(entry.getJsonKey());
                break;
        }
        return result == null || result.getRight() == null ? "" : result.getRight();
    }

    @Override
    public OpenIndexCommandsResponse getOpenIndexCommands() {
        OpenIndexCommandsResponse response = new OpenIndexCommandsResponse();
        Pair<ReturnResult, Integer> pair = indexer.getOpenIndexCommands();
        response.setResult(pair.getLeft());
        response.setOpenCommands(pair.getRight());
        return response;
    }

    @Override
    public ReturnResult indexEverything() {
        indexer.indexEverything();
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public ReturnResult indexRevision(RevisionKey key) {
        if(key.getId() == null || key.getNo() == null) {
            return ReturnResult.PARAMETERS_MISSING;
        }
        revisions.indexRevision(key);
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public ReturnResult stopIndexers() {
        indexer.stopIndexers();
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public ReturnResult uploadConfiguration(Configuration configuration) {
        //backupAndCopy(file, "configuration");

        ReturnResult result = configurations.insert(configuration);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    @Override
    public ReturnResult uploadConfiguration(GUIConfiguration configuration) {
        //backupAndCopy(file, "gui");

        ReturnResult result = configurations.insert(configuration);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    @Override
    public ReturnResult uploadJson(JsonNode misc) {
        //backupAndCopy(file, "misc");

        ReturnResult result = miscJSONRepository.insert(misc);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }
}
