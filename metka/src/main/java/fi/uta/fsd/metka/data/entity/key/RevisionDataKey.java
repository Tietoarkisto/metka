package fi.uta.fsd.metka.data.entity.key;

import fi.uta.fsd.metka.data.enums.RevisionDataType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 8:46 AM
 */
@Embeddable
public class RevisionDataKey implements Serializable {
    @Column(name = "REVISION", updatable = false)
    private Integer revision;

    @Column(name = "TYPE", updatable = false, length= 30)
    @Enumerated(EnumType.STRING)
    private RevisionDataType type;

    @Column(name = "TARGET_ID", updatable = false)
    private Integer targetId;

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={revision: "+revision+", type: "+type+", targetId: "+targetId+"}]";
    }
}
