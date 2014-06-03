package fi.uta.fsd.metka.data.entity.key;

import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class RevisionableKey implements Serializable {
    public static final long serialVersionUID = 1L;

    @Column(name = "TYPE", insertable=false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ConfigurationType type;

    @Column(name = "REVISIONABLE_ID", updatable = false)
    private Integer id;

    public RevisionableKey() {
    }

    public RevisionableKey(ConfigurationType type, Integer id) {
        this.type = type;
        this.id = id;
    }

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={type: "+type+", id: "+id+"}]";
    }
}
