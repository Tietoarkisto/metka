package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.repository.StudyAttachmentRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Service
public class FileService {
    @Autowired
    private StudyAttachmentRepository repository;
    @Autowired
    private ConfigurationService configService;

    public String saveFile(MultipartFile file, String fileName, Integer id) throws IOException {
        File dir = new File("/usr/share/metka/files/study/"+id);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File multipartFile = new File("/usr/share/metka/files/study/"+id+"/"+fileName);
        file.transferTo(multipartFile);
        return multipartFile.getCanonicalPath();
    }

    /**
     * Request adding a file with given path to database.
     * Calls for adding a new file to database which will create a new Revisionable entry and a revision for that entry.
     * After this method returns there should be a new File-revisionable with a revision pointing to given path with state DRAFT.
     * Returns a representation of a row ready to be inserted into a CONTAINER json on client.
     * @param path Path to a newly added file
     * @param targetId Id of the revisionable that should refer this file.
     * @return
     */
    public String initNewFile(String path, Integer targetId, String key) throws IOException {
        // TODO: Form file row
        Configuration config = configService.findLatestByRevisionableId(targetId);

        Field field = config.getField(key);
        if(field == null || field.getType() != FieldType.REFERENCECONTAINER) {
            // No target field found or target not container
            return null;
        }

        RevisionData revision = repository.newFileRevisionable(path);

        repository.addFileLinkEvent(targetId, revision.getKey().getId(), key, path);

        JSONObject json = new JSONObject();
        json.put("type", "reference");
        json.put("key", key);
        json.put("value", getSavedDataFieldFromRevisionData(revision, "fileno").getActualValue());

        /*for(DataField field : container.getFields().values()) {
            if(field instanceof ContainerDataField) {
                JSONObject ct = ContainerTransfer.buildJSONObject((ContainerDataField) field);
                if(ct != null) {
                    values.put(field.getKey(), ct);
                }
            } else {
                SavedDataField saved = (SavedDataField)field;
                JSONObject value = new JSONObject();
                value.put("type", "value");
                value.put("value", ((SimpleValue)saved.getActualValue());
                // TODO: Handle derived values
                values.put(saved.getKey(), value);
            }
        }*/

        return json.toString();
    }

    public RevisionViewDataContainer findLatestStudyAttachmentRevisionForEdit(Integer id) {
        RevisionData revision = null;
        try {
            revision = repository.getEditableStudyAttachmentRevision(id);
        } catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
        if(revision == null) {
            return null;
        }
        TransferObject to = TransferObject.buildTransferObjectFromRevisionData(revision);
        Configuration config = configService.findLatestByType(ConfigurationType.STUDY_ATTACHMENT);
        if(to != null && config != null) {
            RevisionViewDataContainer container = new RevisionViewDataContainer(to, config);
            return container;
        } else {
            return null;
        }
    }

    public ErrorMessage studyAttachmentSaveAndApprove(TransferObject to) {
        try {
            repository.studyAttachmentSaveAndApprove(to);
            return ErrorMessage.studyAttachmentSaveAndApproveSuccesss();
        } catch(Exception ex) {
            // TODO: Log error and notify user that there was a problem with saving file
            return ErrorMessage.studyAttachmentSaveAndApproveFail(ex);
        }
    }
}
