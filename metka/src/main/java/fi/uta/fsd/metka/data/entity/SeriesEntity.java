package fi.uta.fsd.metka.data.entity;

import org.springframework.transaction.annotation.Transactional;

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
    @Column(name = "SERIES_ID", insertable = false, updatable = false)
    private Integer id;

    @OneToMany
    @JoinColumn(name = "SERIES_ID", referencedColumnName = "TARGET_ID")
    private List<SeriesRevisionEntity> revisionDataList;

    // All versionable data is inside Revision data clob
    /*@Column(name = "ABBREVIATION")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PublicationEntity> getPublicationList() {
        return publicationList;
    }

    public void setPublicationList(List<PublicationEntity> publicationList) {
        this.publicationList = publicationList;
    }*/

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
