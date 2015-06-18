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

package fi.uta.fsd.metkaSearch.results;

import java.util.Comparator;
import java.util.List;

/**
 * Common interface for all search results in Metka software.
 * Doesn't really define that much functionality since there's a great deal of variation between
 * the actual data contained in different result lists.
 *
 * TODO: Possibly extend List so Result Lists can be iterated over but more likely provide a wrapper for contained list
 */
public interface ResultList<T extends SearchResult> {
    public static enum ResultType {
        BOOLEAN, // Simple true|false result. Most often used to check restrictions for validity
        REVISION // Just a plain old revision result, should contain revision key and nothing else
        // ... Add more as needed. It might be useful to have the Search handler fetch some predefined data from the revision in some cases
    }

    /**
     * Provide the type of this Result Set.
     * This should be set internally by different set constructors.
     * @return
     */
    public ResultType getType();

    /**
     * Adds search result to some form of internal container.
     * Most ResultLists will check that the type of SearchResult is the same as the type of ResultList before adding
     * @param result Search Result to be added to ResultList
     * @return Was adding successful. Most common failure is trying to add a SearchResult with type that differs from ResultLists type.
     */
    public boolean addResult(T result);

    /**
     * Returns results in a list ready to be iterated over.
     * It doesn't matter how the implementation collects the results, they should always be
     * returned in an ordered list since they are always displayed that way.
     * Client can do reordering later but that is not the problem of the search system.
     * @return
     */
    public List<T> getResults();

    /**
     * Sorts the contained list using given comparator.
     * @param comparator
     */
    public void sort(Comparator<T> comparator);
}
