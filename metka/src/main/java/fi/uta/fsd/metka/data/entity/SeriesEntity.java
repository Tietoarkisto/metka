package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 10:47 AM
 */
@Entity
@Table(name = "SERIES")
public class SeriesEntity {
    @Id
    @SequenceGenerator(name="SERIES_ID_SEQ", sequenceName="SERIES_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SERIES_ID_SEQ")
    @Column(name = "SERIES_ID", updatable = false)
    private Integer id;

    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "SERIES_PUBLICATION",
            joinColumns = {@JoinColumn(name = "SERIES_ID", referencedColumnName = "SERIES_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PUBLICATION_ID", referencedColumnName = "PUBLICATION_ID")}
    )
    private List<PublicationEntity> publicationList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
