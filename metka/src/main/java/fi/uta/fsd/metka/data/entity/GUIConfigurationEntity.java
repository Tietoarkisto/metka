package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "GUI_CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = {"TYPE", "VERSION"}))
public class GUIConfigurationEntity {
    @Id
    @SequenceGenerator(name="GUI_CONFIGURATION_ID_SEQ", sequenceName="GUI_CONFIGURATION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GUI_CONFIGURATION_ID_SEQ")
    @Column(name = "GUI_CONFIGURATION_ID", updatable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private ConfigurationType type;

    @Column(name = "VERSION")
    private Integer version;

    @Lob
    @Column(name = "DATA")
    @Type(type="org.hibernate.type.StringClobType")
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
