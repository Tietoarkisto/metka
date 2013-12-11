package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.VersionableType;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/10/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "VERSIONABLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public class VersionableEntity {
    @Id
    @SequenceGenerator(name="VERSIONABLE_ID_SEQ", sequenceName="VERSIONABLE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VERSIONABLE_ID_SEQ")
    @Column(name = "VERSIONABLE_ID", updatable = false)
    private Integer id;

    @Column(name = "TYPE", updatable = false, length= 30)
    @Enumerated(EnumType.STRING)
    private VersionableType type;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
