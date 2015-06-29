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

package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionHandlerRepository {

    /**
     * Claims a revision without handler to the requesting user.
     * Revision must be without handler or else the operation fails.
     * In the case of two people pushing the 'start editing' button simultaneously
     * (i.e. without refreshing their browser after one of them has performed the operation)
     * the person that first pushed the button receives priority.
     * @param key      RevisionKey to find the revision
     * @return  Pair
     */
    Pair<ReturnResult, TransferData> beginEditing(RevisionKey key);

    /**
     * Changes revision handler if revision can be found and there is a need to change the handler.
     * @param key      RevisionKey to find the revision
     * @param clear    Should handler be cleared or set to current user
     * @return  Pair
     */
    Pair<ReturnResult, TransferData> changeHandler(RevisionKey key, boolean clear);
}
