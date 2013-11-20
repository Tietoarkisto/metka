package fi.uta.fsd.metka.data.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 9:03 AM
 */
@Embeddable
public class PublicationPidKey implements Serializable {
    static final long serialVersionUID = 1L;

    @Column(name = "PUBLICATION_ID", nullable = false, length = 30)
    private Integer publicationId;

    @Column(name = "TERM_ID", nullable = false)
    private Integer termId;

    public PublicationPidKey() {
    }

    public PublicationPidKey(Integer publicationId, Integer termId) {
        this.publicationId = publicationId;
        this.termId = termId;
    }

    public Integer getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Integer publicationId) {
        this.publicationId = publicationId;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={publicationId: "+publicationId+", termId: "+termId+"}]";
    }
}
