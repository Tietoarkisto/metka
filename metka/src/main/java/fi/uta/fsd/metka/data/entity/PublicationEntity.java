package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 10:52 AM
 */
@Entity
@Table(name = "PUBLICATION")
public class PublicationEntity {
    @Id
    @SequenceGenerator(name="PUBLICATION_ID_SEQ", sequenceName="PUBLICATION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PUBLICATION_ID_SEQ")
    @Column(name = "PUBLICATION_ID", updatable = false)
    private Integer id;

    @OneToMany
    @JoinColumn(name = "PUBLICATION_ID", referencedColumnName = "TARGET_ID", insertable = false, updatable = false)
    private List<PublicationRevisionEntity> revisionDataList;

    /*@Column(name = "RELEASE_YEAR")
    private Integer releaseYear;

    @Column(name = "CAN_BE_PUBLISHED")
    private String canBePublished;

    @Temporal(TemporalType.DATE)
    @Column(name = "FIRST_SAVED")
    private Date firstSaved;

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_MODIFIED")
    private Date lastModified;

    @ManyToOne
    @JoinColumn(name = "LAST_MODIFIED_BY")
    private PersonEntity lastModifiedBy;

    // TODO: Julkaisun ilmoitustapa

    @Column(name = "PUBLICATION_LANGUAGE")
    private String publicationLanguage;

    @Column(name = "PUBLICATION_TITLE", length = 1000)
    private String publicationTitle;

    @Column(name = "REL_PUBL", length = 1000)
    private String relPubl;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @ManyToMany(mappedBy = "publicationList")
    private List<SeriesEntity> seriesList;*/

    /**
     * XML containing all persons relating to this publication.
     * The names don't have any connection in the database and so they can be saved as an xml.
     *
     * <pre>
     * {@code
     * <Persons>
     *     <Person>
     *         <FirstName>Firstname</FirstName>
     *         <LastName>LastName</LastName>
     *     </Person>
     * </Persons>
     * }
     * </pre>
     */
    /*@Column(name = "RELATED_PERSONS", length = 1000)
    private String relatedPersons;

    // TODO: Pysyvät tunnisteet todennäköisesti
    @OneToMany(mappedBy = "targetPublication")
    private List<PublicationPidEntity> identifierList;*/

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
