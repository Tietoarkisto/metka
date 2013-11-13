package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.MaterialDataEntityKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:39 AM
 */
@Entity
@Table(name="MATERIAL_DATA")
public class MaterialDataEntity {
    @EmbeddedId
    private MaterialDataEntityKey key;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_ID", insertable = false, updatable = false)
    private MaterialEntity material;

    @Column(name = "TITLE")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private MaterialState state;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.DATE)
    private Date lastModified;

    @ManyToOne
    @JoinColumn(name = "MODIFIED_BY")
    private PersonEntity modifiedBy;

    @ManyToOne
    @JoinColumn(name = "HANDLER")
    private PersonEntity handler;

    @Column(name = "DATA")
    @Lob
    private String data;

    public static enum MaterialState {
        DRAFT,
        APPROVED,
        PUBLISHED,
        IN_TRANSLATION
    }
}
