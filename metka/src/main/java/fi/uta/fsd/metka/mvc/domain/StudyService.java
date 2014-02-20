package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySearchResultSO;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySingleSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.extractIntegerSimpleValue;
import static fi.uta.fsd.metka.data.util.ModelAccessUtil.extractStringSimpleValue;
import static fi.uta.fsd.metka.data.util.ModelAccessUtil.getValueFieldContainerFromRevisionData;

@Service
public class StudyService {
    @Autowired
    private StudyRepository repository;
    @Autowired
    private GeneralSearch generalSearch;

    public List<StudySearchResultSO> searchForStudies(StudySearchSO query) {
        // TODO: Find searched studies and create search result objects for them.
        return null;
    }

    public StudySingleSO newSeries(Integer acquisition_number) {
        RevisionData data = null;
        try {
            data = repository.getNew(acquisition_number);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
        StudySingleSO single = singleSOFromRevisionData(data);
        return single;
    }

    /**
     * Return a default revision number for requested revisionable
     * @param id Revisionable id
     * @return
     */
    public Integer findSingleRevisionNo(Integer id) {
        Integer revision = generalSearch.findSingleRevisionNo(id);
        return revision;
    }

    /**
     * Find requested revision data and convert it to SeriesSingle simple object
     * @param id Revisionable id
     * @param revision Revision number
     * @return Revision data converted to SeriesSingleSO
     */
    public StudySingleSO findSingleRevision(Integer id, Integer revision) {
        RevisionData data = null;
        try {
            data = generalSearch.findSingleRevision(id, revision, ConfigurationType.STUDY);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        if(data == null) {
            return null;
        }

        StudySingleSO series = singleSOFromRevisionData(data);

        return series;
    }

    public boolean saveStudy(StudySingleSO so) {
        try {
            return repository.saveStudy(so);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveStudy(StudySingleSO single) {
        // TODO: Request validation and approval of the given study.
        return false;
    }

    public StudySingleSO editStudy(Integer id) {
        // TODO: Request an editable draft for given study, convert and return StudySingleSO object.
        return null;
    }

    private StudySingleSO singleSOFromRevisionData(RevisionData data) {
        // check if data is for study
        if(data == null || data.getConfiguration().getType() != ConfigurationType.STUDY) {
            return null;
        }
        StudySingleSO so = new StudySingleSO();
        // Set additional information
        so.setState(data.getState());
        so.setRevision(data.getKey().getRevision());
        so.setConfiguration(data.getConfiguration());

        // Set field values
        so.setStudy_id(extractIntegerSimpleValue(getValueFieldContainerFromRevisionData(data, "study_id")));
        so.setId(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "id")));
        so.setSubmissionid(extractIntegerSimpleValue(getValueFieldContainerFromRevisionData(data, "submissionid")));
        so.setDatakind(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "datakind")));
        so.setIspublic(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "ispublic")));
        so.setTitle(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "title")));
        return so;
    }
}
