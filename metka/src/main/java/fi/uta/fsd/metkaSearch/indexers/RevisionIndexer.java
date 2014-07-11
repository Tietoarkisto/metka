package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.handlers.HandlerFactory;
import fi.uta.fsd.metkaSearch.handlers.RevisionHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.joda.time.LocalDateTime;

import java.io.IOException;

public class RevisionIndexer extends Indexer {
    private GeneralRepository general;
    private ConfigurationRepository configurations;

    public static RevisionIndexer build(DirectoryManager.DirectoryPath path, GeneralRepository general, ConfigurationRepository configurations) throws IOException, UnsupportedOperationException {
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

        return new RevisionIndexer(path, general, configurations);
    }

    private RevisionIndexer(DirectoryManager.DirectoryPath path, GeneralRepository general, ConfigurationRepository configurations) throws IOException {
        super(path);
        this.general = general;
        this.configurations = configurations;
    }

    protected void handleCommand(IndexerCommand command) throws IOException {
        // This is a safe type conversion since Indexers add command only accepts commands of correct type
        RevisionIndexerCommand rCom = (RevisionIndexerCommand) command;

        switch(rCom.getAction()) {
            case REMOVE:
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
                indexCommand(rCom);
                break;
            case STOP:
                // This is here to remove compiler warning, actual STOP command is handled earlier
                break;
        }
    }

    /**
     * Create Document out of an XML file and add it to the writer.
     *
     * @param command
     */
    private void indexCommand(RevisionIndexerCommand command) throws IOException {
        RevisionData data = general.getRevision(command.getRevisionable(), command.getRevision());
        if(data == null) {
            // Nothing to index, possibly make a log event
            return;
        }
        Pair<Boolean, LocalDateTime> removalInfo = general.getRevisionableRemovedInfo(data.getKey().getId());
        if(removalInfo == null) {
            // If removal info is null then that means that the actual revisionable object was not found and so there's something deeper wrong
            return;
        }
        Configuration config = configurations.findConfiguration(data.getConfiguration());
        if(config == null) {
            // Can't index without configuration, make log event
            return;
        }
        RevisionHandler handler = HandlerFactory.buildRevisionHandler(getIndexer(), data, config, removalInfo);
        try {
            handler.handle();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
