package fi.uta.fsd.metka.data.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 8:46 AM
 */
@Embeddable
public class RevisionKey implements Serializable {
    @Column(name = "REVISIONABLE_ID", updatable = false)
    private Integer revisionableId;

    @Column(name = "REVISION_NO", updatable = false)
    private Integer revisionNo;

    public RevisionKey() {
    }

    public RevisionKey(Integer versionableId, Integer revisionNo) {
        this.revisionableId = versionableId;
        this.revisionNo = revisionNo;
    }

    public Integer getRevisionableId() {
        return revisionableId;
    }

    public void setRevisionableId(Integer revisionableId) {
        this.revisionableId = revisionableId;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={revisionableId: "+revisionableId+", revisionNo: "+revisionNo+"}]";
    }
}
