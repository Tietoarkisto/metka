package fi.uta.fsd.metkaAuthentication;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class ShibAuthFilter extends AbstractPreAuthenticatedProcessingFilter
        implements AuthenticationDetailsSource<HttpServletRequest,MetkaAuthenticationDetails> {

    public ShibAuthFilter() {
        setAuthenticationDetailsSource(this);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        MetkaAuthenticationDetails details = buildDetails(request);
        if(!StringUtils.hasText(details.getSessionId())) {
            throw new PreAuthenticatedCredentialsNotFoundException("Shibboleth session id not found.");
        }
        String userName = details.getUserName();
        if(!StringUtils.hasText(userName)) {
            throw new PreAuthenticatedCredentialsNotFoundException("No user name for shibboleth session.");
        }
        return userName;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "credentials";
    }

    @Override
    public MetkaAuthenticationDetails buildDetails(HttpServletRequest request) {
        String userName = (String)request.getAttribute("Shib-UserName");
        String lastName = (String)request.getAttribute("Shib-LastName");
        String firstName = (String)request.getAttribute("Shib-FirstName");
        String sessionId = (String)request.getAttribute("Shib-Session-ID");
        String roles = (String)request.getAttribute("Shib-Roles");
        return new MetkaAuthenticationDetails(sessionId, userName, firstName, lastName, roles);
    }
}
