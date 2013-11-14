package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 1:50 PM
 */
@Entity
@Table(name = "TERM")
public class TermEntity {
    @Id
    @SequenceGenerator(name="TERM_ID_SEQ", sequenceName="TERM_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TERM_ID_SEQ")
    @Column(name = "TERM_ID", updatable = false)
    private Integer id;

    @Column(name = "KEY", nullable = false)
    private String termKey;

    /**
     * Finnish value for this term.
     */
    @Column(name = "DEFAULT_VALUE", length = 1000)
    private String defaultValue;

    @OneToMany(mappedBy = "targetTerm")
    private List<TermLocalisationEntity> localisationList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}