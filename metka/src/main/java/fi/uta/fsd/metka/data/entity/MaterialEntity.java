package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "materials")
public class MaterialEntity {

    @Id
    @SequenceGenerator(name="materials_id_seq", sequenceName="materials_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="materials_id_seq")
    @Column(name="id", updatable = false)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
