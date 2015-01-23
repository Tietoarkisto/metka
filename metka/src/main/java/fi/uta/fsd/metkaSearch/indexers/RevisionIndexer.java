package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
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

public class RevisionIndexer extends Indexer {
    public static RevisionIndexer build(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                                        RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references,
                                        StudyErrorsRepository studyErrors, BinderRepository binders) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        if(manager == null) {
            throw new UnsupportedOperationException("Needs a directory manager");
        }
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
        if(revisions == null || configurations == null) {
            throw new UnsupportedOperationException("Revision indexer needs access to revision and configuration repositories");
        }

        return new RevisionIndexer(manager, path, commands, revisions, configurations, references, studyErrors, binders);
    }

    private RevisionRepository revisions;
    private ConfigurationRepository configurations;
    private ReferenceService references;
    private StudyErrorsRepository studyErrors;
    private BinderRepository binders;

    private RevisionIndexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                            RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references,
                            StudyErrorsRepository studyErrors, BinderRepository binders) throws UnsupportedOperationException {
        super(manager, path, commands);
        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
        this.studyErrors = studyErrors;
        this.binders = binders;
    }

    protected void handleCommand(IndexerCommand command) {
        Logger.debug(RevisionIndexer.class, "Starting handling or revision command");
        // This is a safe type conversion since Indexers add command only accepts commands of correct type
        RevisionIndexerCommand rCom = (RevisionIndexerCommand) command;

        switch(rCom.getAction()) {
            case REMOVE:
                Logger.debug(RevisionIndexer.class, "Performing REMOVE action on revision");
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
                Logger.debug(RevisionIndexer.class, "Performing INDEX action on revision");
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
        Logger.debug(RevisionIndexer.class, "Trying to build revision handler");
        RevisionHandler handler = HandlerFactory.buildRevisionHandler(this, revisions, configurations, references, studyErrors, binders);
        try {
            Logger.debug(RevisionIndexer.class, "Trying to handle revision command");
            handler.handle(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
