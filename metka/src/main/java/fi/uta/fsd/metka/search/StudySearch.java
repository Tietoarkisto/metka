package fi.uta.fsd.metka.search;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudySearch {

    /**
     * Return relevant revision number for requested study.
     * If the found series has an approved revision then the latest approved revision number is returned, otherwise return
     * the draft revision number (if there is no approved or draft revision then something is horribly wrong in the database).
     *
     * @param id Id of the requested series.
     * @return Revision number of either a draft or the latest approved revision.
     */
    public Integer findSingleStudyRevisionNo(Long id);
}
