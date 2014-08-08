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
    private final String firstName;
    private final String lastName;
    private final String roles;

    public MetkaAuthenticationDetails(String sessionId, String userName, String firstName, String lastName, String roles) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public Set<? extends GrantedAuthority> getGrantedAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        for(String role : StringUtils.commaDelimitedListToStringArray(roles)){
            role = role.toUpperCase();
            if (!role.startsWith("ROLE_")){
                role = "ROLE_" + role;
            }
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
