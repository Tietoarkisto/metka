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

package fi.uta.fsd.metkaSearch.entity;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public interface IndexerCommandRepository {
    /**
     * Adds new IndexerCommand to the database queue.
     * @param command
     */
    void addIndexerCommand(IndexerCommand command);

    /**
     * Sets handled date time to current date time.
     * @param id
     */
    void markCommandAsHandled(Long id);

    /**
     * Clears command of request information so that it can be handled again
     * @param id
     */
    void clearCommandRequest(Long id);

    /**
     * Returns the next command of given type (ordered by created time) that has not yet
     * been requested or handled. Sets requested to current date time.
     * @param type IndexerConfigurationType of the command that is returned
     * @return
     */
    IndexerCommand getNextCommand(IndexerConfigurationType type, String path);

    /**
     * Returns the next command that has not been requested yet irregardless of type.
     * Does not mark the command as requested.
     * This is mostly used to check that indexers are running and handling commands
     * @return
     */
    IndexerCommand getNextCommandWithoutChange();

    /**
     * Sets requested value to null in all commands that have not been handled yet.
     * Used at server restart where obviously all non handled requested commands need to be requested again.
     */
    void clearAllRequests();

    /**
     * Removes all commands that have been handled.
     * Used at server restart to clear command queue of already handled commands. Some better logging for commands could be implemented.
     */
    void removeAllHandled();

    /**
     * Returns the number of index commands still awaiting handling
     * @return
     */
    Pair<ReturnResult, Integer> getOpenIndexCommands();
}
