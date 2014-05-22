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
     * Calls for adding a file to database which will either create a new Revisionable entry and a revision for that entry
     * or use an existing entry if one with the same path exists.
     * After this method returns there should be a new File-revisionable with a revision pointing to given path with state DRAFT.
     * Returns a representation of a row ready to be inserted into a CONTAINER json on client.
     * @param path Path to a newly added file
     * @param studyId Id of the study that should refer this file.
     * @param key Field key of the field that will contain a reference to this file
     * @return
     */
    public String initNewStudyAttachment(String path, Integer studyId, String key) throws IOException {
        // TODO: Form file row
        Configuration config = configService.findLatestByRevisionableId(studyId);

        Field field = config.getField(key);
        if(field == null || field.getType() != FieldType.REFERENCECONTAINER) {
            // No target field found or target not container
            return null;
        }

        /*
         * Find a RevisionData matching given study or create a new one if none present
         */
        RevisionData revision = repository.studyAttachmentForPath(path, studyId);
        if(revision == null) {
            // TODO: There's a problem, notify user
            return null;
        }

        /*
         * Add event to file link queue for making sure that the reference exists and if this is a variable file to parse
         * and merge it to study
         */
        repository.addFileLinkEvent(studyId, revision.getKey().getId(), key, path);

        /*
         * Create
         */
        JSONObject json = new JSONObject();
        json.put("type", "reference");
        json.put("key", key);
        json.put("value", revision.getKey().getId());

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
