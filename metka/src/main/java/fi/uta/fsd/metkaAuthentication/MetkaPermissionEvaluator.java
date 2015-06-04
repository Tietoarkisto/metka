package fi.uta.fsd.metkaAuthentication;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
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

    @Autowired
    private RevisionRepository revisions;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object check) {
        if(check == null || !(check instanceof String) || !PermissionCheck.isValid((String)check)) {
            throw new RuntimeException("Given permission is not valid permission type");
        }
        if(targetDomainObject == null || (targetDomainObject instanceof String && !StringUtils.hasText((String)targetDomainObject))) {
            throw new RuntimeException("Domain object is either null or is an empty string. Domain object always has to either be a permission name or a value related to given permission");
        }

        MetkaAuthenticationDetails details = AuthenticationUtil.getAuthenticationDetails();

        return evaluatePermission(details, targetDomainObject, PermissionCheck.fromCheck((String) check));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("This type of permission checking is not supported");
    }

    private boolean evaluatePermission(MetkaAuthenticationDetails details, Object target, PermissionCheck check) {
        boolean hasPermission = false;

        switch(check) {
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
                hasPermission = details.getRole().hasPermission(Permission.CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH) || checkSavedSearchOwner(details, (Long)target);
                break;
            case RELEASE_REVISION:
                if(!(target instanceof RevisionKey)) {
                    throw new RuntimeException("Target has to be a revision key");
                }
                hasPermission = claimReleaseCheck((RevisionKey)target, check);
                break;
            case CLAIM_REVISION:
                if(!(target instanceof RevisionKey)) {
                    throw new RuntimeException("Target has to be a revision key");
                }
                hasPermission = claimReleaseCheck((RevisionKey)target, check);
                break;
            case IS_HANDLER:
                if(!(target instanceof TransferData)) {
                    throw new RuntimeException("Target has to be TransferData");
                }
                hasPermission = checkIsHandler((TransferData)target);
                break;
            // TODO: add special case checking for cases where we have target object
        }

        return hasPermission;
    }

    private boolean checkSavedSearchOwner(MetkaAuthenticationDetails details, Long target) {
        Pair<ReturnResult, SavedExpertSearchItem> item = savedSearches.getSavedExpertSearch(target);
        return item.getLeft() == ReturnResult.NO_RESULTS || item.getRight().getSavedBy().equals(details.getUserName());
    }

    private boolean claimReleaseCheck(RevisionKey key, PermissionCheck check) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.warning(getClass(), "No revision found for key " + key.toString() + " while checking claim or release permission.");
            return false;
        }

        RevisionData data = pair.getRight();
        // Both operations can be performed if handler is null or user is the current handler
        if(data.getHandler() == null || AuthenticationUtil.isHandler(data)) {
            return true;
        }
        switch(check) {
            case RELEASE_REVISION:
                return AuthenticationUtil.getAuthenticationDetails().getRole().hasPermission(Permission.CAN_FORCE_RELEASE_REVISION);
            case CLAIM_REVISION:
                return AuthenticationUtil.getAuthenticationDetails().getRole().hasPermission(Permission.CAN_FORCE_CLAIM_REVISION);
            default:
                return false;
        }
    }

    /**
     * Checks if there's a handler and if the user is the handler of the latest revision of the revisionable with provided transfer data's id.
     * @param transferData    TransferData
     * @return boolean
     */
    private boolean checkIsHandler(TransferData transferData) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(transferData.getKey().getId(), false, null);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.warning(getClass(), "No revision found for key " + transferData.getKey().toString() + " while checking handler permission.");
            return false;
        }
        RevisionData data = pair.getRight();
        // If data is not in DRAFT state then everyone is effectively the handler
        if(data.getState() != RevisionState.DRAFT) {
            return true;
        }

        // If we have no handler then no one can be the handler
        if(data.getHandler() == null) {
            return false;
        }

        // When we have handler check that it matches current user name
        return AuthenticationUtil.isHandler(data);
    }
}
