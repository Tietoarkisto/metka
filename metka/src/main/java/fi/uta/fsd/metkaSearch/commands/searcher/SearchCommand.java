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

package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.search.Query;

/**
 * This is the base interface for all search commands that are targeted at Lucene in Metka software.
 * It defines the most basic SearchCommand that can function providing the requested index location
 * as well as a method for getting a query that can be executed. More complex cases however might
 * require additional information.
 */
public interface SearchCommand<T extends SearchResult> {
    /**
     * To what index should this command be directed to
     * @return
     */
    public DirectoryManager.DirectoryPath getPath();

    /**
     * Query to be executed based on the properties given to this command.
     * This is the most challenging part of the search engine since some circumstances might require multiple
     * queries to be executed in sequence to get the correct result. In those cases additional commands should
     * most likely be created out of the initial query request.
     * @return Query to be executed
     */
    public Query getQuery();

    /**
     * What sort of result can you expect from this search command.
     * This should be set by the constructor of each respective SearchCommand implementation.
     * @return
     */
    public ResultList.ResultType getResultType();

    /**
     * This will return a result handler appropriate for the search command.
     * The result handler takes in TopDocs returned by the search and forms
     * a sensible ResultList from them.
     * @return
     */
    public ResultHandler<T> getResultHandler();
}
