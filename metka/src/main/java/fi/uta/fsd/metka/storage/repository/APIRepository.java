package fi.uta.fsd.metka.storage.repository;

import org.joda.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface APIRepository {

    APIUser getAPIUser(String key);
    @Transactional(readOnly = false) void updateAPIAccess(String key);

    public static class APIUser {
        private String name;
        private short permissions;
        private String secret;
        private LocalDateTime lastAccess;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public short getPermissions() {
            return permissions;
        }

        public void setPermissions(short permissions) {
            this.permissions = permissions;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public LocalDateTime getLastAccess() {
            return lastAccess;
        }

        public void setLastAccess(LocalDateTime lastAccess) {
            this.lastAccess = lastAccess;
        }
    }
}
