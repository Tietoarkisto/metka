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
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.AlreadyClosedException;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Base class for all Indexers in the software.
 * Implements basic functionality common for all indexers but all specialized stuff needs to be handled elsewhere.
 */
public abstract class Indexer implements Callable<IndexerStatusMessage>/*, IndexerCommandHandler*/ {

    protected static void checkPathType(DirectoryManager.DirectoryPath path, IndexerConfigurationType type) throws UnsupportedOperationException {
        if(path.getType() != type) {
            throw new UnsupportedOperationException("Path is for a different type");
        }
    }

    // Counter for processed commands. Used to detect when to force flush the index if that setting is true.

    private final DirectoryManager manager;

    protected final DirectoryInformation indexer;

    protected final IndexerCommandRepository commands;

    protected final IndexWriter indexWriter;

    private final DirectoryManager.DirectoryPath path;

    private IndexerStatusMessage status;

    private Long idleStart;

    //protected BlockingQueue<IndexerCommand> commandQueue = new LinkedBlockingQueue<>();

    protected Indexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws UnsupportedOperationException {
        this.path = path;
        this.manager = manager;
        indexer = manager.getIndexDirectory(path, true);
        if(indexer == null) {
            throw new UnsupportedOperationException("Couldn't get an index directory for indexer with path "+path);
        }
        indexWriter = indexer.getIndexWriter();
        if(indexWriter == null) {
            throw new UnsupportedOperationException("Can't create Indexer, no indexWriter created for Indexer with path "+path);
        }
        this.commands = commands;
    }

    protected DirectoryInformation getIndexer() {
        return indexer;
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    public IndexerStatusMessage getStatus() {
        return status;
    }

    synchronized public void setStatus(IndexerStatusMessage status) {
        if(status != IndexerStatusMessage.IDLING && status != IndexerStatusMessage.FLUSHING) {
            idleStart = System.currentTimeMillis();
        }
        if(this.status == status) {
            return;
        }
        if(this.status != IndexerStatusMessage.FLUSHING && this.status != IndexerStatusMessage.IDLING && status == IndexerStatusMessage.IDLING) {
            idleStart = System.currentTimeMillis();
        }
        this.status = status;
    }

    public Long getIdleStart() {
        return idleStart;
    }

    @Override
    public abstract IndexerStatusMessage call() throws Exception;

    protected boolean checkInterval(long timeHandlingCommands, long previousTime, long extraTime, long interval) {
        return (previousTime+extraTime) % interval > (timeHandlingCommands+extraTime) % interval;
    }

    synchronized public void removeDocument(Term term) throws Exception {
        indexWriter.deleteDocuments(term);
    }

    public void removeDocument(Query query) throws Exception {
        indexWriter.deleteDocuments(query);
    }

    public void addDocument(Document document, Analyzer analyzer) throws Exception {
        indexWriter.addDocument(document, analyzer);
    }

    protected void flushIndex() {
        Logger.debug(getClass(), "Preparing to flush index " + indexer.getPath().toString());
        try {
            // Try to commit the writer
            Logger.debug(getClass(), "Trying index writer commit.");
            indexWriter.commit();

            // Set indexer to dirty state so that searchers know to update their index
            // Not needed anymore sine searches always reopen the index anyway
            //indexer.setDirty(true);
        } catch (OutOfMemoryError er) {
            setStatus(IndexerStatusMessage.STOP);
            er.printStackTrace();
            // If we get an OutOfMemoryError then close the writer immediately
            try {
                // Try closing the writer
                indexWriter.close();
            } catch(OutOfMemoryError erc) {
                // As I understand it we should get another OutOfMemoryError, close the writer again
                try {
                    // Try closing the writer
                    indexWriter.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    Logger.error(getClass(), "IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
                Logger.error(getClass(), "IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
            } finally {
                // Interrupt current Thread since we can't continue indexing
                Thread.currentThread().interrupt();
            }
        } catch(IOException ioe) {
            setStatus(IndexerStatusMessage.STOP);
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
        } catch(AlreadyClosedException ace) {
            setStatus(IndexerStatusMessage.STOP);
        }
    }
}
