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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class MetkaAuthenticationDetails implements GrantedAuthoritiesContainer {
    public static final long serialVersionUID = 1;

    private final String sessionId;
    private final String userName;
    private final String displayName;
    private final String roles;
    private final MetkaRole role;

    public MetkaAuthenticationDetails(String sessionId, String userName, String displayName, String roles) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.displayName = displayName;
        this.roles = roles;
        Set<? extends GrantedAuthority> authorities = getGrantedAuthorities();
        if(authorities.isEmpty()) {
            // Should never happen but let's be careful
            this.role = MetkaRole.UNKNOWN;
        } else {
            this.role = MetkaRole.fromRoleName(MetkaRole.MetkaRoleName.fromName(authorities.iterator().next().getAuthority()));
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MetkaRole getRole() {
        return role;
    }

    @Override
    public Set<? extends GrantedAuthority> getGrantedAuthorities() {
        String[] aRoles = StringUtils.delimitedListToStringArray(roles, ";");
        String topRole = null;
        for(String role : aRoles) {
            role = StringUtils.trimAllWhitespace(role);
            role = role.toUpperCase();
            Logger.debug(getClass(), "User " + userName + " has role " + role);
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + roleFromShibbolethRole(role);
            }
            if(topRole == null) {
                topRole = role;
            } else {
                topRole = MetkaRole.compareRoles(topRole, role);
            }
        }
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        if(topRole == null) {
            topRole = "ROLE_" + MetkaRole.MetkaRoleName.UNKNOWN.name();
        }
        authorities.add(new SimpleGrantedAuthority(topRole));
        return authorities;
    }

    private String roleFromShibbolethRole(String role) {
        if(!role.substring(0, 6).equals("METKA:")) {
            return MetkaRole.MetkaRoleName.UNKNOWN.name();
        }

        switch(role) {
            case "METKA:READER":
                return MetkaRole.MetkaRoleName.READER.name();
            case "METKA:BASIC-USER":
                return MetkaRole.MetkaRoleName.USER.name();
            case "METKA:TRANSLATOR":
                return MetkaRole.MetkaRoleName.TRANSLATOR.name();
            case "METKA:DATA-ADMINISTRATOR":
                return MetkaRole.MetkaRoleName.DATA_ADMIN.name();
            case "METKA:ADMINISTRATOR":
                return MetkaRole.MetkaRoleName.ADMIN.name();
            default:
                return MetkaRole.MetkaRoleName.UNKNOWN.name();
        }
    }
}
