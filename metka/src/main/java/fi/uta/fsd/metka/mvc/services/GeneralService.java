package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import javassist.NotFoundException;
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
    public Long getAdjancedRevisionableId(Long currentId, String type, boolean forward) throws NotFoundException {
        return repository.getAdjancedRevisionableId(currentId, type, forward);
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
            indexer.addCommand(RevisionIndexerCommand.remove(DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, "fi", ConfigurationType.valueOf(type.toUpperCase()).toValue()), response.getId(), response.getNo()));
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
            DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, "fi", ConfigurationType.fromValue(type.toUpperCase()).name());
            List<Integer> revisions = repository.getAllRevisionNumbers(id);
            for(Integer revision : revisions) {
                indexer.addCommand(RevisionIndexerCommand.index(path, id, revision));
            }
        }
        return response;
    }

    public RevisionData getRevision(Long id, Integer revision) {
        return repository.getRevision(id, revision);
    }

    public String getRevisionData(Long id, Integer revision) {
        return repository.getRevisionData(id, revision);
    }
}
