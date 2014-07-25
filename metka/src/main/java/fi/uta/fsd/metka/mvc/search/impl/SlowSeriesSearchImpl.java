package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.mvc.services.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.series.SeriesBasicSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.*;

@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private SearcherComponent searcher;

    @Override
    public List<String> findAbbreviations() throws IOException {
        List<String> list = new ArrayList<>();
        list.add("");

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            String data;
            if(entity.getCurApprovedNo() != null) {
                //data = entity.getCurApprovedRev().getData();
                data = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo())).getData();
            } else {
                //data = entity.getLatestRevision().getData();
                data = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo())).getData();
            }

            RevisionData revData = json.readRevisionDataFromString(data);
            // Use the method with less sanity checks since there's no point in getting configuration here.
            SavedDataField field = revData.dataField(SavedDataFieldCall.get("seriesabbr")).getRight();
            if(field != null && !StringUtils.isEmpty(field.getActualValue())) list.add(field.getActualValue());
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List<RevisionDataRemovedContainer> findSeries(SeriesSearchSO query) throws IOException {
        // TODO: Make path management sensible, get path language from some common source that knows which language we should search.
        SeriesBasicSearchCommand command;
        try {
            command = SeriesBasicSearchCommand.build("fi", query.isSearchApproved(), query.isSearchDraft(), query.isSearchRemoved(),
                    stringToLong(query.getByKey("seriesno")), (String)query.getByKey("seriesabbr"), (String)query.getByKey("seriesname"));
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            return collectResults(results);
        } catch(QueryNodeException qne) {
            // Couldn't form query command
            qne.printStackTrace();
            return null;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private List<RevisionDataRemovedContainer> collectResults(ResultList<RevisionResult> resultList) throws IOException {
        List<RevisionDataRemovedContainer> results = new ArrayList<>();

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
        RevisionableEntity entity = null;
        for(RevisionResult result : resultList.getResults()) {
            if(entity == null || !entity.getId().equals(result.getId())) {
                entity = em.find(RevisionableEntity.class, result.getId());
            }
            // TODO: Try to remove the need to do this, although granted this isn't exactly heavy
            if(entity.getCurApprovedNo() != null && result.getNo() < entity.getCurApprovedNo()) {
                continue;
            }

            RevisionEntity revision = em.find(RevisionEntity.class, new RevisionKey(result.getId(), result.getNo().intValue()));
            if(revision != null && !StringUtils.isEmpty(revision.getData())) {
                RevisionData data = json.readRevisionDataFromString(revision.getData());
                results.add(new RevisionDataRemovedContainer(data, entity.getRemoved()));
            }
        }

        return results;
    }
}
