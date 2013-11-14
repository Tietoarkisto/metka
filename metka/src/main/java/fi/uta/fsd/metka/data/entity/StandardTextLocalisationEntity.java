package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 2:29 PM
 */
@Entity
@Table(name = "STANDARD_TEXT_LOCALISATION")
public class StandardTextLocalisationEntity {
    @Id
    @SequenceGenerator(name="STANDARD_TEXT_LOCALISATION_ID_SEQ", sequenceName="STANDARD_TEXT_LOCALISATION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STANDARD_TEXT_LOCALISATION_ID_SEQ")
    @Column(name = "STANDARD_TEXT_LOCALISATION_ID", updatable = false)
    private Integer id;

    @Column(name = "LOCALE")
    private Locale locale;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "VALUE", length = 1000)
    private String value;

    @ManyToOne
    @JoinColumn(name = "TARGET_STANDARD_TEXT")
    private StandardTextEntity targetStandardText;
}
