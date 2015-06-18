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

package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;

/**
 * Base interface for all Indexer commands.
 * Defines getters for two attributes required of all Indexer Commands:
 *     path - DirectoryInformation path to which this command is linked. This informs the system about to which indexer
 *            the command should go to.
 *     action - Action enum value that lets the indexer know what it should do.
 *
 * It is not possible to form any documents based on these parameters alone so all implementing classes should contain
 * type specific additional information that allows for actual document creation and indexing or document identification
 * from index in case of remove actions.
 */
public interface IndexerCommand {
    public DirectoryManager.DirectoryPath getPath();

    public Action getAction();

    public Long getQueueId();
    public void setQueueId(Long queueId);

    /**
     * Defines what indexer should do with given command.
     * Current actions are index, remove and stop. Remove clears the document from index, stop requests the indexer to stop
     * and index adds the document to index if it's not present or creates the document, then removes the old one from index
     * and adds the new one in its stead if already present.
     * If more actions are needed they are added here.
     * Each action should correspond to a single set of instructions followed by the indexer. In this light it might make
     * sense to have INDEX only produce a document and then add one or two new commands to the queue in the form of REMOVE
     * if there was a previous version of the document in the index (basically if we are reindexing a document) and ADD
     * which contains the indexed document. This might be overkill however and will not be implemented until the other
     * parts of the program are more clear.
     */
    public static enum Action {
        INDEX,
        REMOVE,
        STOP
        // ...
    }

    /**
     * Returns commands custom parameters, such as Revisions id and no, as a string.
     * This is used to save the command to queue in database so that no IndexerCommands are missed
     * in the case of server restart.
     * Implementation differs on command and action basis but will always produce a single String
     * that can be saved to database.
     * There should be a corresponding static factory method called fromParameterString in every
     * implementation that accepts a Path, an Action and the parameter string produced by this
     * method and returns correctly initialized IndexerCommand ready for execution.
     * @return
     */
    public String toParameterString();
}
