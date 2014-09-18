package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.study.StudyIdSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class RevisionSearchImpl implements RevisionSearch {
    private static Logger logger = LoggerFactory.getLogger(RevisionSearchImpl.class);

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private ConfigurationRepository configurations;

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> search(RevisionSearchRequest request) {
        return performBasicSearch(request);
        /*switch(request.getType()) {
            case SERIES:
                return findSeries(request);
            case STUDY:
                return findStudies(request);
            case PUBLICATION:
                return findPublications(request);
            default:
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }*/
    }

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> studyIdSearch(String studyId) {
        StudyIdSearchCommand command = StudyIdSearchCommand.build(studyId);
        ResultList<RevisionResult> results = searcher.executeSearch(command);
        return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
    }

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> collectRevisionHistory(RevisionHistoryRequest request) {
        List<RevisionSearchResult> results = new ArrayList<>();

        Pair<ReturnResult, RevisionableInfo> infoPair = this.revisions.getRevisionableInfo(request.getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(infoPair.getLeft(), results);
        }

        List<Integer> nos = revisions.getAllRevisionNumbers(request.getId());

        for(Integer no : nos) {
            Pair<ReturnResult, RevisionData> dataPair = this.revisions.getRevisionData(request.getId(), no);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Could not find revision with [ID "+request.getId()+"|NO "+ no+"]");
                continue;
            }
            RevisionData data = dataPair.getRight();
            RevisionSearchResult result = RevisionSearchResult.build(data, infoPair.getRight());
            results.add(result);
            for(String key : request.getRequestFields()) {
                Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(key));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    result.getValues().put(key, fieldPair.getRight().getActualValueFor(Language.DEFAULT));
                }
            }
        }

        return new ImmutablePair<>(results.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.SEARCH_SUCCESS, results);
    }

    @Override
    public List<RevisionCompareResponseRow> compareRevisions(RevisionCompareRequest request) {
        List<Integer> nos = revisions.getAllRevisionNumbers(request.getId());
        for(Iterator<Integer> i = nos.iterator(); i.hasNext(); ) {
            Integer no = i.next();
            if(no.compareTo(request.getBegin()) < 1) {
                i.remove();
                break;
            }
            if(no.compareTo(request.getEnd()) > 0) {
                i.remove();
                break;
            }
        }

        if(nos.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, MutablePair<String, String>> changes = new HashMap<>();

        for(Integer no : nos) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(request.getId(), nos.get(no));
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            gatherChanges("", pair.getRight().getChanges(), pair.getRight(), changes);
        }

        List<RevisionCompareResponseRow> responses = new ArrayList<>();

        List<String> keyList = new ArrayList<>(changes.keySet());
        Collections.sort(keyList);

        for(String key : keyList) {
            MutablePair<String, String> change = changes.get(key);
            if(change.getLeft().equals(change.getRight())) {
                continue;
            }
            responses.add(new RevisionCompareResponseRow(key, change.getLeft(), change.getRight()));
        }

        return responses;
    }

    private void gatherChanges(String root, Map<String, Change> changeMap, DataFieldContainer container, Map<String, MutablePair<String, String>> changes) {
        for(String key : changeMap.keySet()) {
            Change change = changeMap.get(key);
            if(change instanceof ContainerChange) {
                gatherContainerChanges(root, (ContainerChange)change, container, changes);
            } else {
                gatherValueChanges(root, change, container, changes);
            }
        }
    }

    private void gatherContainerChanges(String root, ContainerChange change, DataFieldContainer container, Map<String, MutablePair<String, String>> changes) {
        Pair<StatusCode, ContainerDataField> containerPair = container.dataField(ContainerDataFieldCall.get(change.getKey()));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND) {
            if(StringUtils.hasText(root)) {
                root = root + ".";
            }
            root = root + change.getKey();
            for(Language l : Language.values()) {
                String langKey = root+"["+l.toValue()+"].";
                for(RowChange rowChange : change.getRows().values()) {
                    DataRow row = containerPair.getRight().getRowWithId(rowChange.getRowId()).getRight();
                    if(row == null) {
                        continue;
                    }
                    String rowKey = root + rowChange.getRowId();
                    if(changes.containsKey(rowKey)) {
                        if(row.getRemoved()) {
                            changes.get(rowKey).setRight(" ");
                        } else {
                            changes.get(rowKey).setRight(row.getRowId().toString());
                        }
                    } else {
                        if(!row.getRemoved()) {
                            changes.put(rowKey, new MutablePair<String, String>(" ", row.getRowId().toString()));
                        }
                    }

                    gatherChanges(rowKey, rowChange.getChanges(), row, changes);
                }
            }
        } else {
            Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = container.dataField(ReferenceContainerDataFieldCall.get(change.getKey()));
            if(referenceContainerPair.getLeft() != StatusCode.FIELD_FOUND) {
                // We have no field matching the change
                return;
            }
            if(StringUtils.hasText(root)) {
                root = root+".";
            }
            root = root + change.getKey()+".";
            for(RowChange rowChange : change.getRows().values()) {
                String rowKey = root + rowChange.getRowId();
                ReferenceRow refRow = referenceContainerPair.getRight().getReferenceWithId(rowChange.getRowId()).getRight();
                if(refRow == null) {
                    continue;
                }
                if(changes.containsKey(rowKey)) {
                    if(refRow.getRemoved()) {
                        changes.get(rowKey).setRight(" ");
                    } else {
                        changes.get(rowKey).setRight(refRow.getActualValue());
                    }
                } else {
                    if(!refRow.getRemoved()) {
                        changes.put(rowKey, new MutablePair<String, String>(" ", refRow.getActualValue()));
                    }
                }
            }
        }
    }

    // Should terminate
    private void gatherValueChanges(String root, Change change, DataFieldContainer container, Map<String, MutablePair<String, String>> changes) {
        Pair<StatusCode, ValueDataField> pair = container.dataField(ValueDataFieldCall.get(change.getKey()));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            return;
        }

        if(StringUtils.hasText(root)) {
            root = root + ".";
        }

        root = root + change.getKey();

        ValueDataField field = pair.getRight();

        for(Language l : Language.values()) {
            String langKey = root + "["+l.toValue()+"]";
            if(changes.containsKey(root)) {
                changes.get(langKey).setRight(field.hasCurrentFor(l) ? field.getCurrentFor(l).getActualValue() : " ");
            } else {
                if(field.hasValueFor(l)) {
                    changes.put(langKey, new MutablePair<String, String>(field.hasOriginalFor(l) ? field.getOriginalFor(l).getActualValue() : " "
                            , field.hasCurrentFor(l) ? field.getCurrentFor(l).getActualValue() : " "));
                }
            }
        }
    }

    private Pair<ReturnResult, List<RevisionSearchResult>> performBasicSearch(RevisionSearchRequest request) {
        Pair<ReturnResult, Configuration> configPair = configurations.findLatestConfiguration(request.getType());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            return new ImmutablePair<>(configPair.getLeft(), null);
        }
        try {
            ExpertRevisionSearchCommand command = ExpertRevisionSearchCommand.build(request, configPair.getRight());
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return new ImmutablePair<>(ReturnResult.SEARCH_SUCCESS, collectResults(results));
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            logger.error("Exception while performing basic series search:", qne);
            return new ImmutablePair<>(ReturnResult.SEARCH_FAILED, null);
        }
    }

    private List<RevisionSearchResult> collectResults(ResultList<RevisionResult> resultList) {
        List<RevisionSearchResult> results = new ArrayList<>();
        if(resultList == null) {
            return results;
        }
        resultList.sort(new Comparator<RevisionResult>() {
            @Override
            public int compare(RevisionResult o1, RevisionResult o2) {
                if(o1.getId().compareTo(o2.getId()) == 0) {
                    return o1.getNo().compareTo(o2.getNo());
                } else {
                    return o1.getId().compareTo(o2.getId());
                }
            }
        });

        if(resultList.getType() != ResultList.ResultType.REVISION) {
            // This only knows how to handle revision results
            return results;
        }

        Pair<ReturnResult, RevisionableInfo> infoPair = null;
        for(RevisionResult result : resultList.getResults()) {
            RevisionableInfo info = null;
            if(infoPair == null || !infoPair.getRight().getId().equals(result.getId())) {
                infoPair = revisions.getRevisionableInfo(result.getId());
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                    logger.error("Couldn't find info for revisionable "+result.getId());
                    continue;
                }
                info = infoPair.getRight();
            }
            // NOTICE: Try to remove the need to do this, although granted this isn't exactly heavy
            if(info == null || (info.getApproved() != null && result.getNo() < info.getApproved())) {
                continue;
            }

            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(result.getId(), result.getNo().intValue());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Failed to find revision for id: "+result.getId()+" and no: "+result.getNo());
                continue;
            }
            RevisionData data = pair.getRight();
            RevisionSearchResult searchResult = RevisionSearchResult.build(data, info);
            // Add type specific search result values
            switch(data.getConfiguration().getType()) {
                case SERIES:
                    addSeriesSearchResults(searchResult, data);
                    break;
                case STUDY:
                    addStudySearchResults(searchResult, data);
                    break;
                case PUBLICATION:
                    addPublicationSearchResults(searchResult, data);
                    break;
            }
            results.add(searchResult);
        }

        return results;
    }

    private void addSeriesSearchResults(RevisionSearchResult searchResult, RevisionData data) {
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("seriesname"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesname", "");
        }
        fieldPair = data.dataField(ValueDataFieldCall.get("seriesabbr"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesabbr", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesabbr", "");
        }
    }

    private void addStudySearchResults(RevisionSearchResult searchResult, RevisionData data) {
        /*Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("seriesname"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().containsValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesname", "");
        }
        fieldPair = data.dataField(ValueDataFieldCall.get("seriesabbr"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().containsValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesabbr", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesabbr", "");
        }*/
    }

    private void addPublicationSearchResults(RevisionSearchResult searchResult, RevisionData data) {
        /*Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("seriesname"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().containsValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesname", "");
        }
        fieldPair = data.dataField(ValueDataFieldCall.get("seriesabbr"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().containsValueFor(Language.DEFAULT)) {
            searchResult.getValues().put("seriesabbr", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        } else {
            searchResult.getValues().put("seriesabbr", "");
        }*/
    }
}
