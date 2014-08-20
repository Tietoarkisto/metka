package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudyPair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class StudyService {

    @Autowired
    private StudySearch search;

    public StudyVariablesStudiesResponse collectStudiesWithVariables() {
        Pair<ReturnResult, List<RevisionSearchResult>> result = search.getStudiesWithVariables();
        StudyVariablesStudiesResponse response = new StudyVariablesStudiesResponse();
        if(result.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            response.setResult(result.getLeft());
        }
        for(RevisionSearchResult sr : result.getRight()) {
            StudyVariablesStudyPair pair = new StudyVariablesStudyPair();
            pair.setId(sr.getId());
            pair.setTitle(sr.getValues().get("title"));
            response.getStudies().add(pair);
        }
        Collections.sort(response.getStudies(), new Comparator<StudyVariablesStudyPair>() {
            @Override
            public int compare(StudyVariablesStudyPair o1, StudyVariablesStudyPair o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return response;
    }
}
