package fi.uta.fsd.metka.mvc.domain.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RevisionKey {
    @XmlElement private Integer versionableId;
    @XmlElement private Integer revisionNo;

    public RevisionKey() {
    }

    public RevisionKey(Integer versionableId, Integer revisionNo) {
        this.versionableId = versionableId;
        this.revisionNo = revisionNo;
    }

    public Integer getVersionableId() {
        return versionableId;
    }

    public void setVersionableId(Integer versionableId) {
        this.versionableId = versionableId;
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
        if (!versionableId.equals(that.versionableId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = versionableId.hashCode();
        result = 31 * result + revisionNo.hashCode();
        return result;
    }
}
