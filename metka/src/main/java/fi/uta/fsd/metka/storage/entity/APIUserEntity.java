package fi.uta.fsd.metka.storage.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "API_USER")
public class APIUserEntity {
    @Id
    @SequenceGenerator(name="API_USER_ID_SEQ", sequenceName="API_USER_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="API_USER_ID_SEQ")
    @Column(name = "API_USER_ID", updatable = false, insertable = false, unique = true)
    private Long apiUserId;

    @Column(name = "PUBLIC_KEY", unique = true)
    private String publicKey;

    @Column(name = "SECRET", unique = true)
    private String secret;

    @Column(name = "PERMISSIONS", updatable = false, insertable = false)
    private short permissions;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LAST_ACCESS")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastAccess;

    @Column(name = "CREATED_BY", updatable = false, insertable = false)
    private String createdBy;

    public Long getApiUserId() {
        return apiUserId;
    }

    public void setApiUserId(Long apiUserId) {
        this.apiUserId = apiUserId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        if(this.publicKey == null) this.publicKey = publicKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        if(this.secret == null) this.secret = secret;
    }

    public short getPermissions() {
        return permissions;
    }

    public void setPermissions(short permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
