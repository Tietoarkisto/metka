package fi.uta.fsd.metka.mvc.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.storage.repository.APIUserRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.ReportRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    @Autowired
    private APIUserRepository api;

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

    @Override public ReturnResult removeAPIUser(String publicKey) {
        return api.removeAPIUser(publicKey);
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
