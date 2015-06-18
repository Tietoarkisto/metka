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

package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 * For now this knows how to execute a search command aimed at revision.
 *
 * TODO: Split to single-index and multi-index variants.
 */
public class RevisionSearcher<T extends SearchResult> extends Searcher<T> {

    public static <T extends SearchResult> RevisionSearcher<T> build(DirectoryManager manager, SearchCommand<T> command) throws UnsupportedOperationException {
        if(manager == null) {
            throw new UnsupportedOperationException("DirectoryManager must be provided");
        }
        if(command.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Path is not for a REVISION");
        }
        if(command.getPath().getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("There are too many additional parameters");
        }
        Logger.debug(RevisionSearcher.class, "Building new RevisionSearcher for path " + command.getPath().toString());
        return new RevisionSearcher<T>(manager, command);
    }

    private RevisionSearcher(DirectoryManager manager, SearchCommand<T> command) throws UnsupportedOperationException {
        super(manager, command);
    }

    @Override
    public ResultList<T> call() throws Exception {
        ResultHandler<T> handler = getCommand().getResultHandler();
        if(!getIndexer().exists()) {
            return handler.handle(null, null);
        }
        Logger.debug(getClass(), "RevisionSearcher is acquiring an IndexReader");
        IndexReader reader = getIndexer().getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        Logger.debug(getClass(), "RevisionSearcher is performing the following query: " + getCommand().getQuery().toString());
        // TODO: Tarvitaan parempi ratkaisu tulosten määrien rajaamiseen
        TopDocs results = searcher.search(getCommand().getQuery(), Integer.MAX_VALUE);
        return handler.handle(searcher, results);
    }
}
