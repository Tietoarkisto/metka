package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.handlers.HandlerFactory;
import fi.uta.fsd.metkaSearch.handlers.RevisionHandler;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevisionIndexer extends Indexer {
    private static final Logger logger = LoggerFactory.getLogger(RevisionIndexer.class);
    public static RevisionIndexer build(DirectoryManager.DirectoryPath path, IndexerCommandRepository commands, GeneralRepository general, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        // Check that additional parameters matches requirements
        if(path.getAdditionalParameters() == null || path.getAdditionalParameters().length == 0) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too few additional parameters");
        }
        if(path.getAdditionalParameters().length > 1) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too many additional parameters");
        }
        if(!ConfigurationType.isValue(path.getAdditionalParameters()[0])) {
            // Additional parameter must be a String representation of one of the ConfigurationTypes
            throw new UnsupportedOperationException("Additional parameter doesn't match any configuration type");
        }
        if(general == null || configurations == null) {
            throw new UnsupportedOperationException("Revision indexer needs access to general and configuration repositories");
        }

        return new RevisionIndexer(path, commands, general, configurations, references);
    }

    private GeneralRepository general;
    private ConfigurationRepository configurations;
    private ReferenceService references;

    private RevisionIndexer(DirectoryManager.DirectoryPath path, IndexerCommandRepository commands, GeneralRepository general, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        super(path, commands);
        this.general = general;
        this.configurations = configurations;
        this.references = references;
    }

    protected void handleCommand(IndexerCommand command) {
        logger.info("Starting handling or revision command");
        // This is a safe type conversion since Indexers add command only accepts commands of correct type
        RevisionIndexerCommand rCom = (RevisionIndexerCommand) command;

        switch(rCom.getAction()) {
            case REMOVE:
                logger.info("Performing REMOVE action on revision");
                // Create term for identification
                if(rCom.getRevisionable() == null || rCom.getRevision() == null) {
                    break;
                }
                BooleanQuery query = new BooleanQuery();
                query.add(NumericRangeQuery.newLongRange("key.id", 1, rCom.getRevisionable(), rCom.getRevisionable(), true, true), BooleanClause.Occur.MUST);
                query.add(NumericRangeQuery.newIntRange("key.no", 1, rCom.getRevision(), rCom.getRevision(), true, true), BooleanClause.Occur.MUST);

                removeDocument(query);
                break;
            case INDEX:
                logger.info("Performing INDEX action on revision");
                indexCommand(rCom);
                break;
            case STOP:
                // This is here to remove compiler warning, actual STOP command is handled earlier
                break;
        }
    }

    /**
     * Create Document out of a revision and add it to the writer.
     *
     * @param command
     */
    private void indexCommand(RevisionIndexerCommand command) {
        logger.info("Trying to build revision handler");
        RevisionHandler handler = HandlerFactory.buildRevisionHandler(this, general, configurations, references);
        try {
            logger.info("Trying to handle revision command");
            handler.handle(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
