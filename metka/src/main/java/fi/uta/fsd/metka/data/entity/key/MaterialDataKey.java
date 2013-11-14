package fi.uta.fsd.metka.data.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:39 AM
 */
@Embeddable
public class MaterialDataKey implements Serializable {
    static final long serialVersionUID = 1L;

    @Column(name = "MATERIAL_ID", nullable = false, length = 30)
    private String materialId;

    @Column(name = "REVISION", nullable = false)
    private Integer revision;

    public MaterialDataKey() {
    }

    public MaterialDataKey(String materialId, Integer revision) {
        this.materialId = materialId;
        this.revision = revision;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={materialId: "+materialId+", revision]";
    }
}
