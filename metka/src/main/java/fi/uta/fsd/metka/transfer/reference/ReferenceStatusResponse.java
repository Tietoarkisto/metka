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

package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.Map;

public class ReferenceStatusResponse {
    public static ReferenceStatusResponse returnResultResponse(ReturnResult result) {
        return new ReferenceStatusResponse(result.name(), false, null, null, null, null, null);
    }

    private final String result;
    private final boolean exists;
    private final ConfigurationType type;
    private final DateTimeUserPair removed;
    private final DateTimeUserPair saved;
    private final Map<Language, ApproveInfo> approved;
    private final UIRevisionState state;

    public ReferenceStatusResponse(String result, boolean exists, ConfigurationType type, DateTimeUserPair removed, DateTimeUserPair saved, Map<Language, ApproveInfo> approved, UIRevisionState state) {
        this.result = result;
        this.exists = exists;
        this.type = type;
        this.removed = removed;
        this.saved = saved;
        this.approved = approved;
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public boolean isExists() {
        return exists;
    }

    public ConfigurationType getType() {
        return type;
    }

    public DateTimeUserPair getRemoved() {
        return removed;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public Map<Language, ApproveInfo> getApproved() {
        return approved;
    }

    public UIRevisionState getState() {
        return state;
    }
}
