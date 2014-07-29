package fi.uta.fsd.metka.mvc.services;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.services.simple.ErrorMessage;
import fi.uta.fsd.metka.mvc.services.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import fi.uta.fsd.metka.storage.repository.StudyAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudyAttachmentService {
    @Autowired
    private GeneralSearch generalSearch;

    @Autowired
    private GeneralService general;

    @Autowired
    private StudyAttachmentRepository studyAttachments;

    @Autowired
    private ConfigurationService configurations;

    public Integer findLatestRevisionNumber(Long id) {
        return generalSearch.findSingleRevisionNo(id);
    }

    public RevisionData getStudyAttachmentRevision(Long id, Integer revisionNo) {
        RevisionData revision = general.getRevision(id, revisionNo);
        if(revision.getConfiguration().getType() != ConfigurationType.STUDY_ATTACHMENT) {
            return null;
        }
        return revision;
    }

    /**
     * Creates new study attachment DRAFT that is linked to the given study id.
     *
     * @param studyId Id of the study that should refer this file.
     * @return
     */
    // TODO: Remove this if there is no further use for this method
    public String newStudyAttachment(Long studyId) {
        /*
         * Find a RevisionData matching given study or create a new one if none present
         */
        RevisionData revision = studyAttachments.newStudyAttachment(studyId);
        if(revision == null) {
            // TODO: There's a problem, notify user
            return null;
        }

        /*
         * Add event to file link queue for making sure that the reference exists and if this is a variable file to parse
         * and merge it to study
         */
        studyAttachments.addFileLinkEvent(studyId, revision.getKey().getId(), "files", null);

        /*
         * Create row to be sent to client
         */
        JsonNodeFactory nf = JsonNodeFactory.instance;

        ObjectNode node = nf.objectNode();
        node.put("type", "reference");
        node.put("key", "files");
        node.put("value", revision.getKey().getId().toString());

        return node.toString();
    }

    public RevisionViewDataContainer findLatestStudyAttachmentRevisionForEdit(Long id) {
        RevisionData revision = null;
        revision = studyAttachments.getEditableStudyAttachmentRevision(id);
        if(revision == null) {
            return null;
        }
        TransferObject to = TransferObject.buildTransferObjectFromRevisionData(revision);
        Configuration config = configurations.findLatestByType(ConfigurationType.STUDY_ATTACHMENT);
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
    }
}
