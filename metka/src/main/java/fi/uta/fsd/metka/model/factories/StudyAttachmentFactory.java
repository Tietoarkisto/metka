package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Contains functionality related to File-revisions
 */
@Service
public class StudyAttachmentFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(StudyAttachmentFactory.class);

    /**
     * Construct a new RevisionData for STUDY_ATTACHMENT
     * Sets the following fields:
     *   study - RevisionableId of the study this attachment is linked to, provided by studyId parameter
     * @param id
     * @param no
     * @param configuration
     * @param studyId
     * @return
     */
    public Pair<ReturnResult, RevisionData> newData(Long id, Integer no, Configuration configuration, String studyId) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_ATTACHMENT) {
            logger.error("Called StudyAttachmentFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        data.dataField(SavedDataFieldCall.set("study").setValue(studyId).setConfiguration(configuration).setTime(time));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
