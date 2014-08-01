package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Contains functionality related to File-revisions
 */
@Service
public class FileFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(FileFactory.class);
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private JSONUtil json;

    /**
     * Constructs a new dataset for a Revision entity.
     * Entity should have no previous data and its state should be DRAFT.
     * All required field values that can be inserted automatically will be added as
     * modified values containing some default value like revisionable id or selectionList default selection.
     *
     * As a result the supplied RevisionEntity will have a every required field that can be automatically set
     * initialised to its default value.
     *
     * @param entity RevisionEntity for which this revision data is created.
     */
    public RevisionData newStudyAttachmentData(RevisionEntity entity, Long studyId) {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Pair<ReturnResult, Configuration> pair = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_ATTACHMENT);

        if(pair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("No configuration found for study attachment. Halting RevisionData creation.");
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, pair.getRight(), time);

        data.dataField(SavedDataFieldCall.set("study").setValue(studyId.toString()).setTime(time));

        Pair<ReturnResult, String> string = json.serialize(data);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Failed at serializing "+data.toString());
            return null;
        } else {
            entity.setData(string.getRight());
            return data;
        }
    }
}
