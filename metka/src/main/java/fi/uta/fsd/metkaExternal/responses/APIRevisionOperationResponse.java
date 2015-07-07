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

package fi.uta.fsd.metkaExternal.responses;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionDataResponse;

public class APIRevisionOperationResponse extends APIResponse {
    public static APIRevisionOperationResponse authFail() {
        return new APIRevisionOperationResponse(false, ReturnResult.API_AUTHENTICATION_FAILED, null);
    }

    public static APIRevisionOperationResponse caughtException(Exception e) {
        APIRevisionOperationResponse response = new APIRevisionOperationResponse(true, ReturnResult.EXCEPTION_DURING_API_CALL, null);
        response.setException(e);
        return response;
    }

    public static APIRevisionOperationResponse success(ReturnResult result, RevisionDataResponse response) {
        return new APIRevisionOperationResponse(true, result, response);
    }

    public static APIRevisionOperationResponse success(String result, RevisionDataResponse response) {
        return new APIRevisionOperationResponse(true, result, response);
    }

    private final RevisionDataResponse response;

    public APIRevisionOperationResponse(boolean authenticated, ReturnResult result, RevisionDataResponse response) {
        super(authenticated, result);
        this.response = response;
    }

    public APIRevisionOperationResponse(boolean authenticated, String result, RevisionDataResponse response) {
        super(authenticated, result);
        this.response = response;
    }

    public RevisionDataResponse getResponse() {
        return response;
    }
}
