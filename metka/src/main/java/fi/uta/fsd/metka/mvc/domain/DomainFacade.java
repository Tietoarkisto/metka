package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.deprecated.StudyEntity;
import fi.uta.fsd.metka.data.deprecated.SeriesEntity;
import fi.uta.fsd.metka.data.deprecated.VocabularyEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 10:07 AM
 */
@Component("domainFacade")
public class DomainFacade {

    @Autowired
    private CRUDRepository<StudyEntity, String> studyRepository;

    @Autowired
    private CRUDRepository<VocabularyEntity, String> vocabularyRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    public List<StudyEntity> listAllStudies() {
        return studyRepository.listAll();
    }

    public SeriesEntity createSeries(SeriesEntity entity) {
        return seriesRepository.create(entity);
    }

    public List<String> listAllSeriesAbbreviations() {
        return seriesRepository.listAllAbbreviations();
    }

    public List<SeriesEntity> listAllSeries() {
        return seriesRepository.listAll();
    }

    public void removeSeries(Integer seriesId) {
        seriesRepository.delete(seriesId);
    }

    public VocabularyEntity createVocabulary(VocabularyEntity vocabulary) {
        return vocabularyRepository.create(vocabulary);
    }

    public void removeVocabulary(String vocabularyId) {
        vocabularyRepository.delete(vocabularyId);
    }

}
