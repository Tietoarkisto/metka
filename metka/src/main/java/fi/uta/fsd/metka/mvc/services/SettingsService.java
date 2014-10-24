package fi.uta.fsd.metka.mvc.services;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@PreAuthorize("hasPermission('canViewSettingsPage', 'PERMISSION')")
@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_SETTINGS_PAGE +"', 'PERMISSION')")
@Transactional
public interface SettingsService {
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    ReturnResult uploadConfiguration(Configuration configuration);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    ReturnResult uploadConfiguration(GUIConfiguration configuration);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    ReturnResult uploadJson(JsonNode misc);

    // Uses report repository to generate example report
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_GENERATE_REPORTS +"', 'PERMISSION')")
    @Transactional(readOnly = true) String generateReport();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_API_USERS +"', 'PERMISSION')")
    @Transactional(readOnly = true) APIUserListResponse listAPIUsers();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_API_USERS +"', 'PERMISSION')")
    APIUserListResponse newAPIUser(NewAPIUserRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_API_USERS +"', 'PERMISSION')")
    ReturnResult removeAPIUser(String publicKey);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION') OR " +
            "hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    List<JSONListEntry> getJsonList(UploadJsonRequest.JsonType type);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION') OR " +
            "hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    String getJsonContent(JSONListEntry entry);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_INDEX_INFO +"', 'PERMISSION')")
    OpenIndexCommandsResponse getOpenIndexCommands();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_MANUALLY_INDEX_CONTENT +"', 'PERMISSION')")
    ReturnResult indexEverything();

    /*@PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    void uploadDataConfig(MultipartFile file) throws IOException;

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    void uploadGuiConfig(MultipartFile file) throws IOException;

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    void uploadJson(MultipartFile file) throws IOException;*/
}
