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

package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.storage.repository.APIRepository;
import fi.uta.fsd.metka.transfer.settings.APIUserEntry;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaAuthentication.MetkaAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExternalUtil {

    public static String makeUrl(HttpServletRequest request) {
        // For now we don't need to return parameters since none should be in use with our REST interface
        return request.getRequestURI();
    }

    public static boolean authenticate(APIRepository repository, String authentication) {
        if(isBlank(authentication)) {
            return false;
        }

        APIUserEntry user = repository.getAPIUser(authentication);
        if(user == null) {
            return false;
        }

        // This can be used to check call frequency
        /*if(user.getLastAccess().plusSeconds(1).compareTo(new LocalDateTime()) >= 0) {
            return false;
        }*/

        /*Base64 b64 = new Base64(false);

        // Get url of current request
        String url = makeUrl(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());

        // Create signature:
        //   Concatenate users secret key with access time provided in api signature object, users public key and the url that was requested.
        //   If required we can salt this even more but this should be enough for now
        String sig = user.getSecret()+signature.getAccessTime()+url;
        String sigCrypt = Sha2Crypt.sha512Crypt(sig.getBytes(), "$6$0$");
        sigCrypt = sigCrypt.substring(5);
        // Hash the signature using base64
        String temp = new String(b64.decodeBase64(signature.getSignature()));
        String hash = new String(b64.encodeBase64(sigCrypt.getBytes()));
        //String hash = new String(b64.encodeBase64(sig.getBytes()));

        // If generated signature doesn't match the provided signature then don't authenticate user
        if(!hash.equals(signature.getSignature())) {
            return false;
        }*/

        repository.updateAPIAccess(authentication);

        MetkaAuthenticationDetails details = new MetkaAuthenticationDetails((new Random()).nextLong()+"", "api:"+user.getName(), user.getName(), user.getRole());
        AuthenticationUtil.authenticate(details);
        return true;
    }
}
