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

package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import fi.uta.fsd.metkaSearch.searchers.RevisionSearcher;
import fi.uta.fsd.metkaSearch.searchers.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.*;

@Service
public class SearcherComponent {

    @Autowired
    private DirectoryManager manager;

    // Pool for searcher threads.
    private ExecutorService indexerPool = Executors.newCachedThreadPool();

    /**
     * This is a blocking implementation of search execution (search is executed synchronously).
     *
     * @param command
     * @return
     */
    public <T extends SearchResult> ResultList<T> executeSearch(SearchCommand<T> command) {
        ResultList<T> results = null;
        try {
            Searcher<T> searcher = build(command);
            Future<ResultList<T>> operation = indexerPool.submit(searcher);
            results = operation.get();
        } catch(Exception e) {
            Logger.error(getClass(), "Exception while executing search command.", e);
        }
        return results;
    }

    /**
     * Factory method to build a new Searcher for executing given command
     * @param command
     * @return
     */
    public <T extends SearchResult> Searcher<T> build(SearchCommand<T> command) throws IOException, UnsupportedOperationException {
        Searcher<T> searcher = null;
        switch(command.getPath().getType()) {
            case REVISION:
                searcher = RevisionSearcher.build(manager, command);
                break;
            default:
                searcher = null;
                break;
        }
        return searcher;
    }
}
