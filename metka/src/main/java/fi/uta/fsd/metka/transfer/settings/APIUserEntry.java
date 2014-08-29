package fi.uta.fsd.metka.transfer.settings;

import org.joda.time.LocalDateTime;

public class APIUserEntry {
    private String name;
    private String publicKey;
    private String secret;
    private boolean hasStudyCreatePermission;
    private boolean hasSearchPermission;
    private boolean hasReadPermission;
    private boolean hasEditPermission;
    private LocalDateTime lastAccess;
    private String createBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isHasStudyCreatePermission() {
        return hasStudyCreatePermission;
    }

    public void setHasStudyCreatePermission(boolean hasStudyCreatePermission) {
        this.hasStudyCreatePermission = hasStudyCreatePermission;
    }

    public boolean isHasSearchPermission() {
        return hasSearchPermission;
    }

    public void setHasSearchPermission(boolean hasSearchPermission) {
        this.hasSearchPermission = hasSearchPermission;
    }

    public boolean isHasReadPermission() {
        return hasReadPermission;
    }

    public void setHasReadPermission(boolean hasReadPermission) {
        this.hasReadPermission = hasReadPermission;
    }

    public boolean isHasEditPermission() {
        return hasEditPermission;
    }

    public void setHasEditPermission(boolean hasEditPermission) {
        this.hasEditPermission = hasEditPermission;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}
