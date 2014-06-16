package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.entity.SequenceEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.data.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.model.data.RevisionData;
import javassist.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Contains methods for general operations that don't require their own repositories.
 */
@Transactional(readOnly = true, noRollbackFor = {NotFoundException.class, MissingResourceException.class})
public interface GeneralRepository {

    public Long getAdjancedRevisionableId(Long currentId, String type, boolean forward) throws NotFoundException;
    @Transactional(readOnly = false) public DraftRemoveResponse removeDraft(String type, Long id);
    @Transactional(readOnly = false) public LogicalRemoveResponse removeLogical(String type, Long id);

    /**
     * Returns a list of revision data objects consisting of the latest revision of each revisionable of given type.
     * If onlyApproved is set to true then returns latest approved revision instead. If no approved revision
     * is found for a revisionable object then no revision of that object exists in the result list.
     * @param type ConfigurationType of the recuested revisionable objects.
     * @param approvedOnly Should draft revisions be ignored when getting latest revision.
     * @return List of RevisionData objects fitting the given parameters
     * @throws IOException If Jackson deserialization fails for some reason at any point.
     */
    public List<RevisionData> getLatestRevisionsForType(ConfigurationType type, Boolean approvedOnly) throws IOException;

    public RevisionData getRevision(Long id, Integer revision) throws IOException;

    public String getRevisionData(Long id, Integer revision);

    @Transactional(readOnly = false) public SequenceEntity getNewSequenceValue(String key);
    @Transactional(readOnly = false) public SequenceEntity getNewSequenceValue(String key, Long initialValue);
}
