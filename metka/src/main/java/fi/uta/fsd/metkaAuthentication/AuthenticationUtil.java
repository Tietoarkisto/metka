package fi.uta.fsd.metkaAuthentication;

import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);

    // Disable instantiation
    private AuthenticationUtil() {}

    public static boolean authenticate(MetkaAuthenticationDetails details) {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            logger.error("Authentication was requested but no SecurityContext was found");
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
            logger.error("User "+getUserName()+" didn't have minimum permission. Forwarded to AUTH_ERROR page.");
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
            logger.error("User name was requested but no SecurityContext was found");
            throw new AuthenticationCredentialsNotFoundException("Couldn't find security context");
        }
        Authentication authentication = context.getAuthentication();
        if(authentication == null) {
            logger.error("SecurityContext was found but no authentication details were set");
            throw new AuthenticationCredentialsNotFoundException("Couldn't find Authentication information");
        }
        if(authentication.getDetails() == null || !(authentication.getDetails() instanceof MetkaAuthenticationDetails)) {
            logger.error("Authentication details are null or don't match expected format");
            throw new AuthenticationCredentialsNotFoundException("Authentication details are null or not in expected format");
        }
        return (MetkaAuthenticationDetails)authentication.getDetails();
    }
}
