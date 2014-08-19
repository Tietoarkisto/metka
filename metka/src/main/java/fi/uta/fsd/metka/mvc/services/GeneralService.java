package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralService {

    @Autowired
    private GeneralRepository repository;

    @Autowired
    private IndexerComponent indexer;

    /**
     * Return the id of next or previous revisionable of the same type as the current revisionable the user is looking at.
     * Used to navigate to previous or next object.
     *
     * @param currentId Id of the revisionable the user is looking at at the moment
     * @param type What type of revisionable is required (series, publication etc.)
     * @param forward do we want next or previous revisionable
     * @return Id of the adjanced revisionable object. If not found then error is thrown instead.
     */
    public Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, String type, boolean forward) {
        return repository.getAdjacentRevisionableId(currentId, type, forward);
    }

    /**
     * Removes the draft revision from given revisionable object. There has to be a draft, otherwise error should be returned.
     * To remove a draft the user requesting the removal has to be the same as the handler of that draft.
     * Notice that revision number is not required since all revisionable objects can have at most one draft open at a time.
     *
     * @param type - Type of the revisionable object.
     * @param id - Id of the revisionable object.
     * @return DraftRemoveResponse enum returned by repository. Success or failure of the operation can be determined from this.
     */
    public DraftRemoveResponse removeDraft(String type, Long id) {
        DraftRemoveResponse response = repository.removeDraft(type, id);
        if(response.getResponse() == DraftRemoveResponse.Response.FINAL_REVISION || response.getResponse() == DraftRemoveResponse.Response.SUCCESS) {
            for(Language language : Language.values()) {
                indexer.addCommand(RevisionIndexerCommand.remove(ConfigurationType.fromValue(type.toUpperCase()), language, response.getId(), response.getNo()));
            }
        }
        return response;
    }

    /**
     * Performs a logical remove on revisionable entity with given type and id.
     * To logically remove a revisionable entity it has to have at least one approved revision and can not have open drafts,
     * If successful then the revisionable has a value 'true' in its 'removed' column in database.
     *
     * @param type - Type of the revisionable object.
     * @param id - Id of the revisionable object.
     * @return LogicalRemoveResponse enum returned by repository. Success or failure of the operation can be determined from this.
     */
    public LogicalRemoveResponse removeLogical(String type, Long id) {
        LogicalRemoveResponse response = repository.removeLogical(type, id);
        if(response == LogicalRemoveResponse.SUCCESS) {
            List<Integer> revisions = repository.getAllRevisionNumbers(id);
            for(Integer revision : revisions) {
                for(Language language : Language.values()) {
                    indexer.addCommand(RevisionIndexerCommand.index(ConfigurationType.fromValue(type), language, id, revision));
                }
            }
        }
        return response;
    }

    /**
     * Returns the latest revision for requested revisionable id if one is found.
     * Doesn't check the type of found revisionable
     * @param id Revisionable id of the requested revision
     * @return
     */
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id) {
        return getRevisionData(id, (ConfigurationType)null);
    }

    /**
     * Returns the latest revision for requested revisionable id if one is found.
     * Checks the revision against provided type and doesn't return a revision if type doesn't match.
     * @param id
     * @param type
     * @return
     */
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id, ConfigurationType type) {
        return repository.getLatestRevisionForIdAndType(id, false, type);
    }

    /**
     * Returns a RevisionData for a specific revision id and number.
     * Doesn't check the returned revision for type.
     * @param id
     * @param revision
     * @return
     */
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision) {
        return repository.getRevisionData(id, revision);
    }

    /**
     * Returns a RevisionData for a specific revision id and number and checks to see that it matches the provided type.
     * @param id
     * @param revision
     * @param type
     * @return
     */
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision, ConfigurationType type) {
        return repository.getRevisionDataOfType(id, revision, type);
    }
}
