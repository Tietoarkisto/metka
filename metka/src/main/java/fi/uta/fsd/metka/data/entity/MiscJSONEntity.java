package fi.uta.fsd.metka.data.entity;


import fi.uta.fsd.metka.data.enums.MiscJSONType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "MISC_JSON")
public class MiscJSONEntity {
    @Id
    @Column(name = "key")
    private String key;

    @Lob
    @Column(name = "DATA")
    @Type(type="org.hibernate.type.StringClobType")
    private String data;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
