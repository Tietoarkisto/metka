package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 12:47 PM
 */
@Entity
@Table(name = "BINDER")
public class BinderEntity {

    @Id
    @SequenceGenerator(name="BINDER_ID_SEQ", sequenceName="BINDER_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BINDER_ID_SEQ")
    @Column(name = "BINDER_ID", updatable = false)
    private Integer id;

    @Column(name = "BINDER_NUM")
    private String binderNum;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(mappedBy = "binderList")
    private List<MaterialEntity> materialList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBinderNum() {
        return binderNum;
    }

    public void setBinderNum(String binderNum) {
        this.binderNum = binderNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
