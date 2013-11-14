package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 1:36 PM
 */
@Entity
@Table(name = "MATERIAL_ERROR")
public class MaterialErrorEntity{
    @Id
    @SequenceGenerator(name="MATERIAL_ERROR_ID_SEQ", sequenceName="MATERIAL_ERROR_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MATERIAL_ERROR_ID_SEQ")
    @Column(name = "MATERIAL_ERROR_ID", updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "TARGET_MATERIAL_ID")
    private MaterialEntity targetMaterial;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "NOTED_BY")
    private PersonEntity notedBy;

    // TODO: Lisää 'aineiston osa' kun termistö on toimitettu

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "SCORE")
    private Short score;

    // TODO: Lisää heräte tiedot

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MaterialEntity getTargetMaterial() {
        return targetMaterial;
    }

    public void setTargetMaterial(MaterialEntity targetMaterial) {
        this.targetMaterial = targetMaterial;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PersonEntity getNotedBy() {
        return notedBy;
    }

    public void setNotedBy(PersonEntity notedBy) {
        this.notedBy = notedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getScore() {
        return score;
    }

    public void setScore(Short score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
