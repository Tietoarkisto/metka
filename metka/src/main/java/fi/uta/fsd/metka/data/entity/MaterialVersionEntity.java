package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 9:26 AM
 */
@Entity
@Table(name = "STUDY_LEVEL_VERSION")
public class MaterialVersionEntity {
    @Id
    @SequenceGenerator(name="STUDY_LEVEL_VERSION_ID_SEQ", sequenceName="STUDY_LEVEL_VERSION_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STUDY_LEVEL_VERSION_ID_SEQ")
    @Column(name = "STUDY_LEVEL_VERSION_ID", updatable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "VERSION_TYPE")
    private VersionType versionType;

    @Column(name = "VERSION")
    private String version;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE")
    private Date date;

    @ManyToOne
    @Column(name = "SAVED_BY")
    private PersonEntity savedBy;

    // TODO: If this differs between version types then this class needs to be split after all. If not, then enumerate.
    @Column(name = "DESCRIPTION_TYPE")
    private String descriptionType;

    @Column(name = "PUBLIC_DESCRIPTION")
    private String publicDescription;

    @Column(name = "PRIVATE_DESCRIPTION")
    private String privateDescription;

    @ManyToOne
    @JoinColumn(name = "TARGET_MATERIAL_ID")
    private MaterialEntity targetMaterial;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }

    public static enum VersionType {
        DATA_VERSION,
        STUDY_LEVEL_VERSION
    }
}
