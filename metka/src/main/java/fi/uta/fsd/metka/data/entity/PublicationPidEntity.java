package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.PublicationPidKey;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 9:02 AM
 */
@Entity
@Table(name = "PUBLICATION_PID")
public class PublicationPidEntity {
    @EmbeddedId
    private PublicationPidKey key;

    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    @MapsId("publicationId")
    private PublicationEntity targetPublication;

    @ManyToOne
    @MapsId("termId")
    private TermEntity targetTerm;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
