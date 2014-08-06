package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.SeriesAbbreviationsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService {

    @Autowired
    private SeriesSearch search;

    public SeriesAbbreviationsResponse findAbbreviations() {
        SeriesAbbreviationsResponse response = new SeriesAbbreviationsResponse();
        try {
            List<String> list = search.findAbbreviations();
            response.setResult(ReturnResult.SEARCH_SUCCESS);
            for(String string : list) {
                response.getAbbreviations().add(string);
            }
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            response.setResult(ReturnResult.SEARCH_FAILED);
        }
        return response;
    }

    /*public List<SearchResult> searchForSeries(SeriesSearchSO query) {
        List<SearchResult> seriesList = new ArrayList<>();
        List<RevisionDataRemovedContainer> datas = null;
        try {
            datas = search.findSeries(query);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return seriesList;
        }

        for(RevisionDataRemovedContainer container : datas) {
            SearchResult series = resultSOFromRevisionData(container.getData());
            if(series != null) {
                if(container.isRemoved()) {
                    series.setState(UIRevisionState.REMOVED);
                }
                seriesList.add(series);
            }
        }
        return seriesList;
    }*/


    /*public RevisionViewDataContainer newSeries() {
        RevisionData revision = null;
        try {
            revision = repository.getNew();
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        // Creating new series was successful, index series
        indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), revision.getKey().getId(), revision.getKey().getNo()));

        Configuration config = configService.findByTypeAndVersion(revision.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision);

        return new RevisionViewDataContainer(single, config);
    }*/

    // TODO: Add information of if new revision was created or not so it can be indexed as necessary
    /*public RevisionViewDataContainer editSeries(Long seriesno) {
        try {
            RevisionData data = repository.editSeries(seriesno);
            Configuration config = configService.findByTypeAndVersion(data.getConfiguration()).getRight();
            TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);
            return new RevisionViewDataContainer(single, config);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
    }*/

    // TODO: Add information of whether changes were found or not so that unnecessary indexing can be avoided
    /*public boolean saveSeries(TransferObject to) {
        try {
            boolean result = repository.saveSeries(to);
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }*/

    /*public boolean approveSeries(TransferObject to) {
        try {
            boolean result = repository.approveSeries(to.getId());
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }*/
}
