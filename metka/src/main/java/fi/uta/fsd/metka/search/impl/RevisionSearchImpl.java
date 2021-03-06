/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.*;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class RevisionSearchImpl implements RevisionSearch {

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private ConfigurationRepository configurations;


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
                Logger.error(getClass(), "Could not find revision with [ID " + request.getId() + "|NO " + no + "]");
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

        return new ImmutablePair<>(results.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, results);
    }

    @Override
    public List<RevisionCompareResponseRow> compareRevisions(RevisionCompareRequest request) {
        List<Integer> nos = revisions.getAllRevisionNumbers(request.getId());
        RevisionData original = null;
        for(Iterator<Integer> i = nos.iterator(); i.hasNext(); ) {
            Integer no = i.next();
            if(no.compareTo(request.getBegin()) < 0) {
                i.remove();
                continue;
            }
            if(no.compareTo(request.getBegin()) == 0) {
                Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(request.getId(), no);
                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    original = pair.getRight();
                }
                continue;
            }
            if(no.compareTo(request.getEnd()) > 0) {
                i.remove();
                continue;
            }
        }

        if(nos.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, MutablePair<String, String>> changes = new HashMap<>();

        Pair<ReturnResult, Configuration> configPair = configurations.findLatestConfiguration(ConfigurationType.fromValue(request.getType()));
        if (!configPair.getLeft().equals(ReturnResult.CONFIGURATION_FOUND)) {
            return new ArrayList<>();
        }

        Pair<ReturnResult, GUIConfiguration> GUIconfigPair = configurations.findLatestGUIConfiguration(ConfigurationType.fromValue(request.getType()));
        if (!GUIconfigPair.getLeft().equals(ReturnResult.CONFIGURATION_FOUND)) {
            return new ArrayList<>();
        }


        for(Integer no : nos) {
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(request.getId(), no);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            gatherChanges("", pair.getRight().getChanges(), pair.getRight(), changes, original, configPair.getRight(), GUIconfigPair.getRight());
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

    private void gatherChanges(String root, Map<String, Change> changeMap, DataFieldContainer container, Map<String, MutablePair<String, String>> changes, DataFieldContainer originalFields, Configuration configuration, GUIConfiguration guiConfiguration) {
        for(String key : changeMap.keySet()) {
            Change change = changeMap.get(key);
            if(change.getType() == Change.ChangeType.CONTAINER) {
                gatherContainerChanges(root, (ContainerChange)change, container, changes, originalFields, configuration, guiConfiguration);
            } else {
                gatherValueChanges(root, (ValueChange) change, container, changes, originalFields, configuration, guiConfiguration);
            }
        }
    }

    private void gatherContainerChanges(String root, ContainerChange change, DataFieldContainer container, Map<String, MutablePair<String, String>> changes, DataFieldContainer originalFields, Configuration configuration, GUIConfiguration guiConfiguration) {
        Pair<StatusCode, ContainerDataField> containerPair = container.dataField(ContainerDataFieldCall.get(change.getKey()));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND) {
            ContainerDataField originalContainer = originalFields.dataField(ContainerDataFieldCall.get(change.getKey())).getRight();
            if(StringUtils.hasText(root)) {
                root = root + " / ";
            }
            for(Language l : Language.values()) {

                String langKey = root + (guiConfiguration.getFieldTitles().get(change.getKey()) != null ? guiConfiguration.getFieldTitles().get(change.getKey()).getTitle().getTitleFor(l) : change.getKey()) +" ["+l.toValue()+"] ";
                for(RowChange rowChange : change.getRows().values()) {
                    DataRow row = containerPair.getRight().getRowWithIdFrom(l, rowChange.getRowId()).getRight();
                    if(row == null) {
                        continue;
                    }
                    DataRow origRow = (originalContainer != null) ? originalContainer.getRowWithId(row.getRowId()).getRight() : null;
                    // Keys in brackets will be supplanted in javascript with the correct translations
                    String rowKey = langKey + "{rowNumber} " + rowChange.getRowId();
                    if((origRow == null || origRow.getRemoved()) && row.getRemoved()) {
                        // If we don't have original row and the current row is removed then nothing of note has happened
                        if(changes.containsKey(rowKey)) {
                            // There's no difference between original row and current row
                            // Clearing all changes from inside the container row
                            clearContainerRowChanges(changes, rowKey);
                        }
                        continue;
                    } else if((origRow != null && !origRow.getRemoved()) && !row.getRemoved()) {
                        if(changes.containsKey(rowKey)) {
                            // There's no difference between original row and current row
                            changes.remove(rowKey);
                        }
                    } else {
                        // Keys in brackets will be supplanted in javascript with the correct translations
                        changes.put(rowKey, new MutablePair<>(origRow != null && !origRow.getRemoved() ? origRow.getRowId().toString() : "-", !row.getRemoved() ? "{newRow}" : "{removedRow}"));
                    }
                    if(!row.getRemoved()) {
                        gatherChanges(rowKey, rowChange.getChanges(), row, changes, origRow, configuration, guiConfiguration);
                    }
                }
            }
        } else {
            Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = container.dataField(ReferenceContainerDataFieldCall.get(change.getKey()));
            if(referenceContainerPair.getLeft() != StatusCode.FIELD_FOUND) {
                // We have no field matching the change
                return;
            }
            ReferenceContainerDataField originalContainer = originalFields.dataField(ReferenceContainerDataFieldCall.get(change.getKey())).getRight();
            if(StringUtils.hasText(root)) {
                root = root+" / ";
            }

            root = root + (guiConfiguration.getFieldTitles().get(change.getKey()) != null ? guiConfiguration.getFieldTitles().get(change.getKey()).getTitle().getTitleFor(Language.DEFAULT) : change.getKey()) + " ";
            for(RowChange rowChange : change.getRows().values()) {
                ReferenceRow row = referenceContainerPair.getRight().getReferenceWithId(rowChange.getRowId()).getRight();
                if(row == null) {
                    continue;
                }
                ReferenceRow origRow = (originalContainer != null) ? originalContainer.getReferenceWithId(row.getRowId()).getRight() : null;

                // Keys in brackets will be supplanted in javascript with the correct translations
                String rowKey = root + "{rowNumber} " + rowChange.getRowId();

                if((origRow == null || origRow.getRemoved()) && row.getRemoved()) {
                    // If we don't have original row and the current row is removed then nothing of note has happened
                    if(changes.containsKey(rowKey)) {
                        // There's no difference between original row and current row
                        changes.remove(rowKey);
                    }
                    continue;
                } else if((origRow != null && !origRow.getRemoved()) && !row.getRemoved()) {
                    if(changes.containsKey(rowKey)) {
                        // There's no difference between original row and current row
                        changes.remove(rowKey);
                    }
                } else {
                    // Keys in brackets will be supplanted in javascript with the correct translations
                    changes.put(rowKey, new MutablePair<>(origRow != null && !origRow.getRemoved() ? origRow.getRowId().toString() : "-", !row.getRemoved() ? "{newRow}" : "{removedRow}"));
                }
            }
        }
    }

    // Should terminate
    private void gatherValueChanges(String root, ValueChange change, DataFieldContainer container, Map<String, MutablePair<String, String>> changes, DataFieldContainer originalFields, Configuration configuration, GUIConfiguration guiConfiguration) {
        Pair<StatusCode, ValueDataField> pair = container.dataField(ValueDataFieldCall.get(change.getKey()));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            return;
        }

        if(StringUtils.hasText(root)) {
            root = root + " / ";
        }

        String changeType = configuration.getField(pair.getRight().getKey()).getType().toString();

        if (changeType.equals("SELECTION")){
            changeType += "_" + configuration.getSelectionList(configuration.getField(pair.getRight().getKey()).getSelectionList()).getType().toString();
        }

        ValueDataField field = pair.getRight();

        ValueDataField original = originalFields != null ? originalFields.dataField(ValueDataFieldCall.get(change.getKey())).getRight() : null;

        for(Language l : Language.values()) {
            String langKey = root + (guiConfiguration.getFieldTitles().get(change.getKey()) != null ? guiConfiguration.getFieldTitles().get(change.getKey()).getTitle().getTitleFor(l) : change.getKey()) +" ["+l.toValue()+"]";
            if(changes.containsKey(root)) {
                if (changeType.equals("SELECTION_VALUE")){
                    SelectionList list = configuration.getSelectionList(configuration.getField(field.getKey()).getSelectionList());
                    if (list != null) {
                        changes.get(langKey).setRight(field.hasCurrentFor(l) ? list.getOptionWithValue(field.getCurrentFor(l).getActualValue()).getTitleFor(l) : "-");
                    }
                } else {
                    changes.get(langKey).setRight(field.hasCurrentFor(l) ? field.getCurrentFor(l).getActualValue() : "-");
                }
            } else {
                if(field.hasValueFor(l)) {
                    if (changeType.equals("SELECTION_VALUE")) {
                        SelectionList list = configuration.getSelectionList(configuration.getField(field.getKey()).getSelectionList());
                        if (list != null) {
                            changes.put(langKey, new MutablePair<>(original != null && original.hasOriginalFor(l) ? list.getOptionWithValue(original.getOriginalFor(l).getActualValue()).getTitleFor(l) : "-"
                                    , field.hasCurrentFor(l) ? list.getOptionWithValue(field.getCurrentFor(l).getActualValue()).getTitleFor(l) : (field.hasOriginalFor(l) ? list.getOptionWithValue(field.getOriginalFor(l).getActualValue()).getTitleFor(l) : "-")));
                        }
                    } else {
                        changes.put(langKey, new MutablePair<>(original != null && original.hasOriginalFor(l) ? original.getOriginalFor(l).getActualValue() : "-"
                                , field.hasCurrentFor(l) ? field.getCurrentFor(l).getActualValue() : (field.hasOriginalFor(l) ? field.getOriginalFor(l).getActualValue() : "-")));
                    }
                }
            }
        }
    }
    private void clearContainerRowChanges(Map<String, MutablePair<String, String>> changes, String rowKey){
        List<String> cleared = new ArrayList<>();
        for (String key : changes.keySet()){
            if (key.contains(rowKey)){
                cleared.add(key);
            }
        }
        for (String key : cleared){
            changes.remove(key);
        }
    }

    private ResultList<RevisionResult> collectResults(ResultList<RevisionResult> resultList) {
        if(resultList == null) {
            return resultList;
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

        List<RevisionResult> removeList = new ArrayList<>();

        RevisionResult prev = null;
        for(Iterator<RevisionResult> i = resultList.getResults().listIterator() ; i.hasNext(); ) {
            RevisionResult current = i.next();
            if(prev != null && prev.getId().equals(current.getId())) {
                if((current.isApproved() && prev.isApproved()) || (current.isRemoved() && prev.isRemoved())) {
                    removeList.add(prev);
                }
            }
            prev = current;
        }

        resultList.getResults().removeAll(removeList);

        return resultList;
    }/*

    private List<RevisionSearchResult> collectResults(ResultList<RevisionResult> resultList, RevisionSearchRequest request) {
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

        for(RevisionResult result : resultList.getResults()) {
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(result.getId());
            if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                Logger.error(getClass(), "Couldn't find info for revisionable "+result.getId());
                continue;
            }
            RevisionableInfo info = infoPair.getRight();
            // NOTICE: Try to remove the need to do this, although granted this isn't exactly heavy
            if(info.getApproved() != null && result.getNo() < info.getApproved()) {
                continue;
            }

            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(result.getId(), result.getNo());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Failed to find revision for id: "+result.getId()+" and no: "+result.getNo());
                continue;
            }
            RevisionData data = pair.getRight();
            RevisionSearchResult searchResult = RevisionSearchResult.build(data, info);
            if(request == null) {
                continue;
            }
            searchResult.setType(info.getType());
            // Add type specific search result values
            switch(info.getType()) {
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

    private boolean hasValue(Pair<StatusCode, ValueDataField> pair, Language language) {
        return pair.getLeft() == StatusCode.FIELD_FOUND && pair.getRight().hasValueFor(Language.DEFAULT);
    }

    private void addSeriesSearchResults(RevisionSearchResult searchResult, RevisionData data) {
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("seriesname"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("seriesname", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
        fieldPair = data.dataField(ValueDataFieldCall.get("seriesabbr"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("seriesabbr", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private void addStudySearchResults(RevisionSearchResult searchResult, RevisionData data) {
        // aineistonumero
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("studyid"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("studyid", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }

        // nimi
        fieldPair = data.dataField(ValueDataFieldCall.get("title"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("title", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }

        // tekijät
        Pair<StatusCode, ContainerDataField> containerPair = data.dataField(ContainerDataFieldCall.get("authors"));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            List<String> authors = new ArrayList<>();
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                fieldPair = row.dataField(ValueDataFieldCall.get("author"));
                if(hasValue(fieldPair, Language.DEFAULT)) {
                    authors.add(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
                }
            }
            if(!authors.isEmpty()) {
                searchResult.getValues().put("authors", StringUtils.collectionToDelimitedString(authors, ", "));
            }
        }

        // sarja
        fieldPair = data.dataField(ValueDataFieldCall.get(Fields.SERIES));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put(Fields.SERIES, fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }

        // laatu
        fieldPair = data.dataField(ValueDataFieldCall.get("datakind"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("datakind", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }

        // käyttöoikeus
        fieldPair = data.dataField(ValueDataFieldCall.get("termsofuse"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("termsofuse", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private void addPublicationSearchResults(RevisionSearchResult searchResult, RevisionData data) {
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("publicationid"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("publicationid", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }

        // nimi
        fieldPair = data.dataField(ValueDataFieldCall.get("publicationtitle"));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            searchResult.getValues().put("publicationtitle", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }*/
}
