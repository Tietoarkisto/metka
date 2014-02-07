package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.mvc.domain.simple.study.StudySearchResultSO;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySingleSO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/3/14
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StudyService {
    public Integer findSingleStudyRevisionNo(Integer id) {
        // TODO: find, convert and return requested study data.
        return null;
    }

    public List<StudySearchResultSO> searchForStudies(StudySearchSO query) {
        // TODO: Find searched studies and create search result objects for them.
        return null;
    }

    public StudySingleSO newSeries() {
        // TODO: Create new study with initial draft and return the converted StudySingleSO object.
        return null;
    }

    public StudySingleSO findSingleRevision(Integer id, Integer revision) {
        // TODO: Find requested revision, convert and return StudySingleSO object.
        return null;
    }

    public boolean saveStudy(StudySingleSO single) {
        // TODO: Send given study to repository to be validated and saved
        return false;
    }

    public boolean approveStudy(StudySingleSO single) {
        // TODO: Request validation and approval of the given study.
        return false;
    }

    public StudySingleSO editStudy(Integer id) {
        // TODO: Request an editable draft for given study, convert and return StudySingleSO object.
        return null;
    }
}
