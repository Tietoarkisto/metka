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
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.response.UploadResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Autowired
    private IndexerCommandRepository indexCommands;

    @Autowired
    private ExpertSearchService expertSearch;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionHandlerRepository claim;

    @Autowired
    private RevisionSaveRepository save;

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
        Collections.sort(entries, new Comparator<JSONListEntry>() {
            @Override
            public int compare(JSONListEntry l, JSONListEntry r) {
                return l.getTitle().compareTo(r.getTitle());
            }
        });
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
        Pair<ReturnResult, Integer> pair = indexCommands.getOpenIndexCommands();
        response.setResult(pair.getLeft());
        response.setOpenCommands(pair.getRight().longValue());
        return response;
    }

    @Override
    public OpenIndexCommandsResponse getRevisionsWaitingIndexing() {
        OpenIndexCommandsResponse response = new OpenIndexCommandsResponse();
        Pair<ReturnResult, Long> pair = revisions.getRevisionsWaitingIndexing();
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
    public UploadResponse uploadConfiguration(Configuration configuration, Boolean newVersion) {
        //backupAndCopy(file, "configuration");

        ReturnResult result = configurations.insert(configuration);
        UploadResponse response = new UploadResponse(ReturnResult.OPERATION_SUCCESSFUL);
        if (configuration.getKey().getType().equals(ConfigurationType.STUDY) && newVersion && result.equals(ReturnResult.DATABASE_INSERT_SUCCESS)) {
            try {
                response = finalizeStudyConfigurationUpload(configuration);
            } catch (Exception e) {
                e.printStackTrace();
                return new UploadResponse(ReturnResult.CONFIG_UPDATE_PARTIAL_FAILURE);
            }
        }
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? response : new UploadResponse(result);
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
        JsonNode key = misc.get("key");
        if(key == null || key.getNodeType() != JsonNodeType.STRING) {
            return ReturnResult.OPERATION_FAIL;
        }

        ReturnResult result = miscJSONRepository.insert(key.textValue(), misc);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    private UploadResponse finalizeStudyConfigurationUpload(Configuration configuration) throws Exception {
        String expertSearchString = "+key.configuration.type:" + configuration.getKey().getType() + " ";
        expertSearchString += "+(state.latest:approved OR state.latest:draft) ";
        expertSearchString += "-state.removed:true";
        ExpertSearchQueryRequest request = new ExpertSearchQueryRequest();
        request.setQuery(expertSearchString);

        ExpertSearchQueryResponse results = expertSearch.performQuery(request);
        List<String> changedFields = getChangedFields(configuration);
        List<Long> handled = new ArrayList<>();
        results.getResults().sort(new Comparator<RevisionResult>() {
            @Override
            public int compare(RevisionResult o1, RevisionResult o2) {
                if(o1.getId().compareTo(o2.getId()) == 0) {
                    return o2.getNo().compareTo(o1.getNo());
                } else {
                    return o1.getId().compareTo(o2.getId());
                }
            }
        });
        UploadResponse response = new UploadResponse(ReturnResult.OPERATION_SUCCESSFUL);
        DateTimeUserPair dateTimeUserPair = DateTimeUserPair.build();
        for (RevisionResult result : results.getResults().getResults()){
            if (handled.contains(result.getId())){
                continue;
            }
            RevisionData revisionData = null;
            if (!result.isDraft()){
                Pair<OperationResponse, RevisionData> editPair = edit.edit(new RevisionKey(result.getId(), result.getNo()), dateTimeUserPair);
                if(!editPair.getLeft().equals(ReturnResult.OPERATION_SUCCESSFUL)){
                    continue;
                }
                revisionData = editPair.getRight();
            }
            if (revisionData == null){
                Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(result.getId(), result.getNo());
                if (!revisionPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                    continue;
                }
                revisionData = revisionPair.getRight();
            }
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(result.getId());
            if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                continue;
            }
            TransferData saveData = TransferData.buildFromRevisionData(revisionData, infoPair.getRight());
            for (String changed : changedFields) {
                if (configuration.getField(changed) != null && !configuration.getField(changed).getEditable()){
                    continue;
                }
                if (saveData.getField(changed) == null){
                    continue;
                }
                if (configuration.getField(changed) != null && configuration.getField(changed).getType().equals(FieldType.SELECTION)){
                    saveData.getField(changed).getValueFor(Language.DEFAULT).setCurrent("0");
                } else if (configuration.getField(changed) != null && (configuration.getField(changed).getType().equals(FieldType.REFERENCECONTAINER) ||configuration.getField(changed).getType().equals(FieldType.CONTAINER))) {
                    for (List<TransferRow> langRows : saveData.getField(changed).getRows().values()){
                        for (TransferRow row : langRows){
                            row.setRemoved(true);
                        }
                    }
                } else {
                    saveData.getField(changed).getValueFor(Language.DEFAULT).setCurrent("");
                }
            }
            if (!AuthenticationUtil.isHandler(revisionData)){
                claim.changeHandler(revisionData.getKey(), false);
            }
            Pair<ReturnResult, TransferData> savePair = save.saveRevision(saveData, dateTimeUserPair);
            if (!savePair.getLeft().equals(ReturnResult.OPERATION_SUCCESSFUL)){
                response.getFailedRevisions().add(savePair.getRight().getField("studyid").getValueFor(Language.DEFAULT).getValue());
            } else {
                response.getSuccesfulRevisions().add(savePair.getRight().getField("studyid").getValueFor(Language.DEFAULT).getValue());
            }
            handled.add(result.getId());
        }
        if (response.getFailedRevisions().size() > 0){
            response.setResult(ReturnResult.CONFIG_UPDATE_PARTIAL_FAILURE);
        }
        return response;
    }

    private List<String> getChangedFields(Configuration configuration) {
        List<String> fields = new ArrayList<>();
        Pair<ReturnResult, Configuration> oldConf = configurations.findConfiguration(configuration.getKey().getType(), configuration.getKey().getVersion() - 1);
        if (!oldConf.getLeft().equals(ReturnResult.CONFIGURATION_FOUND)){
            return fields;
        }
        for (Field oldField: oldConf.getRight().getFields().values()){
            Field newField = configuration.getField(oldField.getKey());
            if (newField != null && newField.getSubfield()){
                continue;
            }
            if (newField == null ||!oldField.equals(newField)){
                fields.add(oldField.getKey());
                continue;
            }
            // Selection lists remain the same, check if the contents (in case selection list is chosen) remain the same
            if (StringUtils.hasText(newField.getSelectionList()) && !configuration.getSelectionList(newField.getSelectionList()).equals(oldConf.getRight().getSelectionList(oldField.getSelectionList()))){
                fields.add(oldField.getKey());
                continue;
            }
            // Check references
            if (StringUtils.hasText(newField.getReference()) && !configuration.getReference(newField.getReference()).equals(oldConf.getRight().getReference(oldField.getReference()))){
                fields.add(oldField.getKey());
                continue;
            }
            if (newField.getSubfields().size() > 0){
                for (String subFieldKey : newField.getSubfields()){
                    if (!oldConf.getRight().getField(subFieldKey).equals(configuration.getField(subFieldKey))){
                        fields.add(oldField.getKey());
                    }
                }
            }
        }
        return fields;
    }

}
