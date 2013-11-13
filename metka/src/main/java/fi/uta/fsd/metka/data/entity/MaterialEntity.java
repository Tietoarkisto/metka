package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MATERIAL")
public class MaterialEntity {

    @Id
    @Column(name="MATERIAL_ID", updatable = false, length = 30)
    private String id;

    @Column(name = "ARCHIVED")
    private Boolean archived;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    private List<MaterialDataEntity> materialDataEntityList;

    @ManyToMany
    @JoinTable(
            name = "BINDER_MATERIAL",
            joinColumns = {@JoinColumn(name = "MATERIAL_ID", referencedColumnName = "MATERIAL_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BINDER_ID", referencedColumnName = "BINDER_ID")}
    )
    private List<BinderEntity> binderList;

    @OneToMany(mappedBy = "targetMaterial")
    private List<MaterialErrorEntity> errorList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
