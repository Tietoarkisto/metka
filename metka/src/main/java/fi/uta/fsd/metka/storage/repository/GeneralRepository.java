package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.SequenceEntity;
import javassist.NotFoundException;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.MissingResourceException;

/**
 * Contains methods for general operations that don't require their own repositories.
 */
@Transactional(readOnly = true, noRollbackFor = {NotFoundException.class, MissingResourceException.class})
public interface GeneralRepository {

    public Pair<Boolean, LocalDateTime> getRevisionableRemovedInfo(Long id);

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
     */
    public List<RevisionData> getLatestRevisionsForType(ConfigurationType type, Boolean approvedOnly);

    public RevisionData getLatestRevisionForId(Long id, boolean approvedOnly);

    public RevisionData getRevision(Long id, Integer revision);

    public String getRevisionData(Long id, Integer revision);

    public List<Integer> getAllRevisionNumbers(Long id);

    @Transactional(readOnly = false) public SequenceEntity getNewSequenceValue(String key);
    @Transactional(readOnly = false) public SequenceEntity getNewSequenceValue(String key, Long initialValue);
}
