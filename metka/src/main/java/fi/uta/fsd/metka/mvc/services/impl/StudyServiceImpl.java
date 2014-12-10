package fi.uta.fsd.metka.mvc.services.impl;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.ddi.DDIBuilderService;
import fi.uta.fsd.metka.ddi.DDIReaderService;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import fi.uta.fsd.metka.transfer.study.StudyErrorsResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudyPair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class StudyServiceImpl implements StudyService {

    @Autowired
    private StudySearch search;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private DDIBuilderService ddiBuilderService;

    @Autowired
    private DDIReaderService ddiReaderService;

    @Override public StudyVariablesStudiesResponse collectStudiesWithVariables() {
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

    @Override public RevisionSearchResponse collectAttachmentHistory(TransferData transferData) {
        RevisionSearchResponse response = new RevisionSearchResponse();
        if(transferData.getConfiguration().getType() != ConfigurationType.STUDY_ATTACHMENT) {
            response.setResult(ReturnResult.INCORRECT_TYPE_FOR_OPERATION);
            return response;
        }

        Pair<ReturnResult, List<RevisionSearchResult>> results = search.collectAttachmentHistory(transferData.getKey().getId());
        response.setResult(results.getLeft());
        if(results.getLeft() == ReturnResult.SEARCH_SUCCESS) {
            response.getRows().addAll(results.getRight());
        }
        return response;
    }

    @Override
    public StudyErrorsResponse getStudiesWithErrors() {
        Pair<ReturnResult, List<RevisionSearchResult>> results = search.getStudiesWithErrors();
        StudyErrorsResponse response = new StudyErrorsResponse(results.getLeft());
        response.getRows().addAll(results.getRight());
        return response;
    }

    @Override
    public Pair<ReturnResult, CodeBookDocument> exportDDI(Long id, Integer no, Language language) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(id, no);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Return error to user
            return new ImmutablePair<>(pair.getLeft(), null);
        } else if(pair.getRight().getConfiguration().getType() != ConfigurationType.STUDY) {
            // Only applicaple to studies
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        } else {
            RevisionData revision = pair.getRight();
            Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(revision.getConfiguration());
            if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                return new ImmutablePair<>(configurationPair.getLeft(), null);
            }
            Pair<ReturnResult, CodeBookDocument> cb = ddiBuilderService.buildDDIDocument(language, revision, configurationPair.getRight());
            return cb;
        }
    }

    @Override
    public ReturnResult importDDI(TransferData transferData, String path) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return pair.getLeft();
        }
        return ddiReaderService.readDDIDocument(path, pair.getRight());
    }
}
