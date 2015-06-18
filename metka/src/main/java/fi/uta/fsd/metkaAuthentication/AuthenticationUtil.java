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

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.ui.Model;

/**
 * Grants easy access to authentication details of current user
 */
public final class AuthenticationUtil {

    // Disable instantiation
    private AuthenticationUtil() {}

    public static boolean authenticate(MetkaAuthenticationDetails details) {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            Logger.error(AuthenticationUtil.class, "Authentication was requested but no SecurityContext was found");
            throw new AuthenticationCredentialsNotFoundException("Couldn't find security context");
        }
        /*Authentication authentication = context.getAuthentication();
        if(authentication != null && authentication.getDetails() != null) {
            logger.error("Authentication details already set");
            throw new AuthenticationCredentialsNotFoundException("Authentication details already set");
        }*/
        PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(details.getUserName(), "credentials", details.getGrantedAuthorities());
        auth.setDetails(details);
        context.setAuthentication(auth);
        return true;
    }

    public static String getModelName(String destination, Model model) {
        MetkaAuthenticationDetails details = getDetails();
        if(details == null || !details.getRole().hasPermission(Permission.HAS_MINIMUM_PERMISSION)) {
            Logger.error(AuthenticationUtil.class, "User "+getUserName()+" didn't have minimum permission. Forwarded to AUTH_ERROR page.");
            model.asMap().put("configurationType", "AUTH_ERROR");
            return "authError";
        }
        return destination;
    }

    public static String getUserName() throws AuthenticationCredentialsNotFoundException {
        MetkaAuthenticationDetails details = getDetails();
        return details.getUserName();
    }

    public static MetkaAuthenticationDetails getAuthenticationDetails() throws AuthenticationCredentialsNotFoundException {
        return getDetails();
    }

    public static void clearAuthenticationDetails() throws AuthenticationCredentialsNotFoundException {
        MetkaAuthenticationDetails details = getAuthenticationDetails();
        if(details == null) {
            return;
        }

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
    }

    public static boolean isHandler(RevisionData revision) {
        return revision.getState() == RevisionState.DRAFT && getUserName().equals(revision.getHandler());
    }

    private static MetkaAuthenticationDetails getDetails() throws AuthenticationCredentialsNotFoundException {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            Logger.error(AuthenticationUtil.class, "User name was requested but no SecurityContext was found");
            throw new AuthenticationCredentialsNotFoundException("Couldn't find security context");
        }
        Authentication authentication = context.getAuthentication();
        if(authentication == null) {
            Logger.error(AuthenticationUtil.class, "SecurityContext was found but no authentication details were set");
            throw new AuthenticationCredentialsNotFoundException("Couldn't find Authentication information");
        }
        if(authentication.getDetails() == null || !(authentication.getDetails() instanceof MetkaAuthenticationDetails)) {
            Logger.error(AuthenticationUtil.class, "Authentication details are null or don't match expected format");
            throw new AuthenticationCredentialsNotFoundException("Authentication details are null or not in expected format");
        }
        return (MetkaAuthenticationDetails)authentication.getDetails();
    }
}
