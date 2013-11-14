package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 1:59 PM
 */
// TODO: Use Locale as key, change to embeddable and use http://hwellmann.blogspot.ie/2010/07/jpa-20-mapping-map.html
@Entity
@Table(name = "TERM_LOCALISATION")
public class TermLocalisationEntity {
    @Id
    @SequenceGenerator(name="TERM_LOCALISATION_ID_SEQ", sequenceName="TERM_LOCALISATION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TERM_LOCALISATION_ID_SEQ")
    @Column(name = "TERM_LOCALISATION_ID", updatable = false)
    private Integer id;

    @Column(name = "LOCALE")
    private String locale;

    /**
     * Localised version of defaultValue from TermEntity
     */
    @Column(name = "VALUE", length = 1000)
    private String value;

    @ManyToOne
    @JoinColumn(name = "TARGET_TERM")
    private TermEntity targetTerm;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
