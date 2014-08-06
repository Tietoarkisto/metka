package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudyAttachmentService {
    @Autowired
    private GeneralRepository general;



    @Autowired
    private ConfigurationService configurations;

    public Pair<ReturnResult, Integer> findLatestRevisionNumber(Long id) {
        return general.getLatestRevisionNoForIdAndType(id, false, ConfigurationType.STUDY_ATTACHMENT);
    }

    public Pair<ReturnResult, RevisionData> getStudyAttachmentRevision(Long id, Integer no) {
        return general.getRevisionDataOfType(id, no, ConfigurationType.STUDY_ATTACHMENT);
    }

    // TODO: Check that these work through the generalized service

    /**
     * Creates new study attachment DRAFT that is linked to the given study id.
     *
     * @param studyId Id of the study that should refer this file.
     * @return
     */
    // TODO: Remove this if there is no further use for this method
    /*public String newStudyAttachment(Long studyId) {
        *//*
         * Find a RevisionData matching given study or create a new one if none present
         *//*
        RevisionData revision = studyAttachments.newStudyAttachment(studyId);
        if(revision == null) {
            // TODO: There's a problem, notify user
            return null;
        }

        *//*
         * Add event to file link queue for making sure that the reference exists and if this is a variable file to parse
         * and merge it to study
         *//*
        studyAttachments.addFileLinkEvent(studyId, revision.getKey().getId(), "files", null);

        *//*
         * Create row to be sent to client
         *//*
        JsonNodeFactory nf = JsonNodeFactory.instance;

        ObjectNode node = nf.objectNode();
        node.put("type", "reference");
        node.put("key", "files");
        node.put("value", revision.getKey().getId().toString());

        return node.toString();
    }*/

    /*public RevisionViewDataContainer findLatestStudyAttachmentRevisionForEdit(Long id) {
        RevisionData revision = null;
        revision = studyAttachments.getEditableStudyAttachmentRevision(id);
        if(revision == null) {
            return null;
        }
        TransferObject to = TransferObject.buildTransferObjectFromRevisionData(revision);
        Configuration config = configurations.findLatestByType(ConfigurationType.STUDY_ATTACHMENT).getRight();
        if(to != null && config != null) {
            RevisionViewDataContainer container = new RevisionViewDataContainer(to, config);
            return container;
        } else {
            return null;
        }
    }

    public ErrorMessage saveStudyAttachment(TransferObject to) {
        // TODO: Save changes from provided TransferObject to database
        return null;
    }*/
}
