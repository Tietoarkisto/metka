package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 8:57 AM
 */
@Entity
@Table(name = "SAVED_SEARCH")
public class SavedSearchEntity {
    @Id
    @SequenceGenerator(name="SAVED_SEARCH_ID_SEQ", sequenceName="SAVED_SEARCH_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAVED_SEARCH_ID_SEQ")
    @Column(name = "SAVED_SEARCH_ID", updatable = false)
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "QUERY", length = 1000)
    private String query;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
