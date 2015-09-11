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

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;

import java.util.concurrent.Callable;

/**
 * Base class for actual search query execution.
 * Most of the time search queries are executed synchronously but it's implemented as a Thread to allow
 * for asynchronous execution of queries.
 * SearcherComponent should decide between synchronous and asynchronous implementation
 */
public abstract class Searcher<T extends SearchResult> implements Callable<ResultList<T>> {

    /**
     * Indexer path where this search should be performed
     */
    private final DirectoryManager.DirectoryPath path;
    /**
     * Command that should be executed
     */
    private final SearchCommand<T> command;

    private final DirectoryInformation indexer;

    protected Searcher(DirectoryManager manager, SearchCommand<T> command) throws UnsupportedOperationException {
        this.path = command.getPath();
        this.command = command;
        indexer = manager.getIndexDirectory(path, false);
        if(indexer == null) {
            throw new UnsupportedOperationException("Couldn't get an indexer for Searcher with path "+path);
        }
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    public DirectoryInformation getIndexer() {
        return indexer;
    }

    public SearchCommand<T> getCommand() {
        return command;
    }

    /**
     * Callable interface implementation.
     * For now search doesn't need any common functions since there's
     * one Searcher per query
     * @return
     * @throws Exception
     */
    @Override
    public abstract ResultList<T> call() throws Exception;
}
