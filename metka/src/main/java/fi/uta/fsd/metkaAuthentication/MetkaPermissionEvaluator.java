package fi.uta.fsd.metkaAuthentication;

import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public class MetkaPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private SavedSearchRepository savedSearches;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object check) {
        if(check == null || !(check instanceof String) || !PermissionCheck.isValid((String)check)) {
            throw new RuntimeException("Given permission is not valid permission type");
        }
        if(targetDomainObject == null || (targetDomainObject instanceof String && !StringUtils.hasText((String)targetDomainObject))) {
            throw new RuntimeException("Domain object is either null or is an empty string. Domain object always has to either be a permission name or a value related to given permission");
        }

        MetkaAuthenticationDetails details = AuthenticationUtil.getAuthenticationDetails();

        return evaluatePermission(details, targetDomainObject, PermissionCheck.valueOf((String)check));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("This type of permission checking is not supported");
    }

    private boolean evaluatePermission(MetkaAuthenticationDetails details, Object target, PermissionCheck permission) {
        boolean hasPermission = false;

        switch(permission) {
            case PERMISSION:
            default:
                if(!(target instanceof String)) {
                    throw new RuntimeException("Target has to be a non empty string for permission checking.");
                }
                hasPermission = details.getRole().hasPermission((String)target);
                break;
            case REMOVE_SEARCH:
                if(!(target instanceof Long)) {
                    throw new RuntimeException("Target has to be a Long for search removal checking");
                }
                hasPermission = details.getRole().hasPermission(Permission.Values.CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH) || checkSavedSearchOwner(details, (Long)target);
                break;
            // TODO: add special case checking for cases where we have target object
        }

        return hasPermission;
    }

    private boolean checkSavedSearchOwner(MetkaAuthenticationDetails details, Long target) {
        Pair<ReturnResult, SavedExpertSearchItem> item = savedSearches.getSavedExpertSearch(target);
        return item.getLeft() == ReturnResult.NO_RESULTS || item.getRight().getSavedBy().equals(details.getUserName());
    }
}
