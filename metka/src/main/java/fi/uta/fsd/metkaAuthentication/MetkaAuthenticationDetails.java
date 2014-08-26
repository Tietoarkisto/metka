package fi.uta.fsd.metkaAuthentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class MetkaAuthenticationDetails implements GrantedAuthoritiesContainer {

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
        String[] aRoles = StringUtils.commaDelimitedListToStringArray(roles);
        String topRole = null;
        for(String role : aRoles) {
            role = role.toUpperCase();
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
