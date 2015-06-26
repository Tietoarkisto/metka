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

package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
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
                                        StudyErrorsRepository studyErrors) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        if(manager == null) {
            throw new UnsupportedOperationException("Needs a directory manager");
        }
        // Check that additional parameters matches requirements
        if(path.getAdditionalParameters().length > 0) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too many additional parameters");
        }
        if(revisions == null || configurations == null) {
            throw new UnsupportedOperationException("Revision indexer needs access to revision and configuration repositories");
        }

        return new RevisionIndexer(manager, path, commands, revisions, configurations, references, studyErrors);
    }

    private RevisionRepository revisions;
    private ConfigurationRepository configurations;
    private ReferenceService references;
    private StudyErrorsRepository studyErrors;

    private RevisionIndexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                            RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references,
                            StudyErrorsRepository studyErrors) throws UnsupportedOperationException {
        super(manager, path, commands);
        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
        this.studyErrors = studyErrors;
    }

    protected void handleCommand(IndexerCommand command) {
        Logger.debug(getClass(), "Starting handling or revision command");
        // This is a safe type conversion since Indexers add command only accepts commands of correct type
        RevisionIndexerCommand rCom = (RevisionIndexerCommand) command;

        switch(rCom.getAction()) {
            case REMOVE:
                Logger.debug(getClass(), "Performing REMOVE action on revision");
                // Create term for identification
                if(rCom.getId() == null || rCom.getNo() == null) {
                    break;
                }
                BooleanQuery query = new BooleanQuery();
                query.add(NumericRangeQuery.newLongRange("key.id", 1, rCom.getId(), rCom.getId(), true, true), BooleanClause.Occur.MUST);
                query.add(NumericRangeQuery.newIntRange("key.no", 1, rCom.getNo(), rCom.getNo(), true, true), BooleanClause.Occur.MUST);

                removeDocument(query);
                break;
            case INDEX:
                Logger.debug(getClass(), "Performing INDEX action on revision");
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
        Logger.debug(getClass(), "Trying to build revision handler");
        RevisionHandler handler = HandlerFactory.buildRevisionHandler(this, revisions, configurations, references, studyErrors);
        try {
            Logger.debug(getClass(), "Trying to handle revision command");
            handler.handle(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
