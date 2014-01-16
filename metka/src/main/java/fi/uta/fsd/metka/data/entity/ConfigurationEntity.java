package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = {"TYPE", "VERSION"}))
public class ConfigurationEntity {
    @Id
    @SequenceGenerator(name="CONFIGURATION_ID_SEQ", sequenceName="CONFIGURATION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CONFIGURATION_ID_SEQ")
    @Column(name = "CONFIGURATION_ID", updatable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private ConfigurationType type;

    @Column(name = "VERSION")
    private Integer version;

    @Lob
    @Column(name = "DATA", length = 10000)
    @Type(type="org.hibernate.type.StringClobType")
    // Length defined because HSQL has problems for some reason, should be removed.
    private String data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
