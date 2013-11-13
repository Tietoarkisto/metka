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
    @Column(name = "PERSON_ID")
    private String personId;

    @OneToMany(mappedBy = "modifiedBy")
    private List<MaterialDataEntity> modifiedList;

    @OneToMany(mappedBy = "handler")
    private List<MaterialDataEntity> handlingList;
}
