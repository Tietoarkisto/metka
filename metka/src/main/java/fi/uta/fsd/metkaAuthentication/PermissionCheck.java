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

package fi.uta.fsd.metkaAuthentication;

public enum PermissionCheck {
    PERMISSION(Values.PERMISSION),
    REMOVE_SEARCH(Values.REMOVE_SEARCH),
    RELEASE_REVISION(Values.RELEASE_REVISION),
    CLAIM_REVISION(Values.CLAIM_REVISION),
    IS_HANDLER(Values.IS_HANDLER);

    private final String check;

    private PermissionCheck(String check) {
        this.check = check;
    }

    public String getCheck() {
        return check;
    }

    public static boolean isValid(String check) {
        for(PermissionCheck c : values()) {
            if(c.check.equals(check)) {
                return true;
            }
        }
        return false;
    }

    public static PermissionCheck fromCheck(String check) {
        for(PermissionCheck c : values()) {
            if(c.check.equals(check)) {
                return c;
            }
        }
        throw new UnsupportedOperationException("Provided String is not a valid PermissionCheck value");
    }

    public static class Values {
        public static final String PERMISSION = "PERMISSION";
        public static final String REMOVE_SEARCH = "REMOVE_SEARCH";
        public static final String RELEASE_REVISION = "RELEASE_REVISION";
        public static final String CLAIM_REVISION = "CLAIM_REVISION";
        public static final String IS_HANDLER = "IS_HANDLER";
    }
}
