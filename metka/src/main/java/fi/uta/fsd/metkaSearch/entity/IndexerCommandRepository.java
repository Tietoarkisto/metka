package fi.uta.fsd.metkaSearch.entity;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public interface IndexerCommandRepository {
    /**
     * Adds new IndexerCommand to the database queue.
     * @param command
     */
    public void addIndexerCommand(IndexerCommand command);

    /**
     * Sets handled date time to current date time.
     * @param id
     */
    public void markCommandAsHandled(Long id);

    /**
     * Returns the next command of given type (ordered by created time) that has not yet
     * been requested or handled. Sets requested to current date time.
     * @param type IndexerConfigurationType of the command that is returned
     * @return
     */
    public IndexerCommand getNextCommand(IndexerConfigurationType type);

    /**
     * Returns the next command that has not been requested yet irregardless of type.
     * Does not mark the command as requested.
     * This is mostly used to check that indexers are running and handling commands
     * @return
     */
    public IndexerCommand getNextCommandWithoutChange();

    /**
     * Sets requested value to null in all commands that have not been handled yet.
     * Used at server restart where obviously all non handled requested commands need to be requested again.
     */
    public void clearAllRequests();

    /**
     * Removes all commands that have been handled.
     * Used at server restart to clear command queue of already handled commands. Some better logging for commands could be implemented.
     */
    public void removeAllHandled();
}
