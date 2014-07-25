package fi.uta.fsd.metka.mvc.search;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Transactional(readOnly = true)
public interface GeneralSearch {
    /**
     * Return relevant revision number for requested revisionable.
     * If the found revisionable has an approved revision then the latest approved revision number is returned, otherwise return
     * the draft revision number (if there is no approved or draft revision then something is horribly wrong in the database).
     *
     * @param id Id of the requested revisionable.
     * @return Revision number of either a draft or the latest approved revision.
     */
    public Integer findSingleRevisionNo(Long id);

    /**
     * Return a specific revision.
     * @param id Id of the revisionable object
     * @param revision Revision number of requested revision.
     * @return RevisionData of the requested revision and null if revision was not found.
     * @throws java.io.IOException
     */
    public RevisionData findSingleRevision(Long id, Integer revision, ConfigurationType type) throws IOException;

    // TODO: Remove this
    public List<RevisionDataRemovedContainer> tempFindAllStudies() throws IOException;
}
