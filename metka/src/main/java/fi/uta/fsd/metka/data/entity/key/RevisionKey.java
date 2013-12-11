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
    @Column(name = "VERSIONABLE_ID", updatable = false)
    private Integer versionableId;

    @Column(name = "REVISION_NO", updatable = false)
    private Integer revisionNo;

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={versionableId: "+versionableId+", revisionNo: "+revisionNo+"}]";
    }
}
