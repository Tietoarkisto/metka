package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.storage.repository.APIRepository;
import org.joda.time.LocalDateTime;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExternalUtil {
    public static final short FLAG_STUDY_CREATE     = 0b00000001;
    public static final short FLAG_SEARCH           = 0b00000010;
    public static final short FLAG_READ             = 0b00000100;
    public static final short FLAG_EDIT             = 0b00001000;

    public static String makeUrl(HttpServletRequest request) {
        // For now we don't need to return parameters since none should be in use with our REST interface
        return request.getRequestURI();
    }

    public static boolean authenticate(APIRepository repository, APISignature signature, short permission) {
        if(isBlank(signature.getKey()) || isBlank(signature.getAccessTime()) || isBlank(signature.getSignature())) {
            return false;
        }

        APIRepository.APIUser user = repository.getAPIUser(signature.getKey());
        if(user == null) {
            return false;
        }

        if(user.getLastAccess().plusSeconds(1).compareTo(new LocalDateTime()) >= 0) {
            return false;
        }

        if((permission & user.getPermissions()) != permission) {
            return false;
        }

        // Get url of current request
        String url = makeUrl(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());

        // Create signature:
        //   Concatenate users secret key with access time provided in api signature object, users public key and the url that was requested.
        //   If required we can salt this even more but this should be enough for now
        String sig = user.getSecret()+signature.getAccessTime()+signature.getKey()+url;
        // We could hash the sig here if we wanted to but atm. it's not really productive

        // Hash the signature using base64
        String hash = new String(Base64.encode(sig.getBytes()));

        // If generated signature doesn't match the provided signature then don't authenticate user
        if(!hash.equals(signature.getSignature())) {
            return false;
        }

        repository.updateAPIAccess(signature.getKey());
        return true;
    }
}
