package fi.uta.fsd.metka.data.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RevisionKey implements Serializable {
    public static final long serialVersionUID = 1L;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionKey that = (RevisionKey) o;

        if (!revisionNo.equals(that.revisionNo)) return false;
        if (!revisionableId.equals(that.revisionableId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = revisionableId.hashCode();
        result = 31 * result + revisionNo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={revisionableId: "+revisionableId+", revisionNo: "+revisionNo+"}]";
    }
}
