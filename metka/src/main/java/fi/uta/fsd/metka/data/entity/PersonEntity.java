package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:49 AM
 */
@Entity
@Table(name = "PERSON")
public class PersonEntity {

    @Id
    @Column(name = "PERSON_ID", length = 10)
    private String id;

    // TODO: Person name from LDAP either as one field or as separate fields.

    // TODO: Person role from LDAP

    @OneToMany(mappedBy = "modifiedBy")
    private List<MaterialDataEntity> modifiedList;

    @OneToMany(mappedBy = "handler")
    private List<MaterialDataEntity> handlingList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
