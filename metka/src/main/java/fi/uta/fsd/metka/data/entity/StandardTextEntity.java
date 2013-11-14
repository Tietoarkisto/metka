package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 2:12 PM
 */
@Entity
@Table(name = "STANDARD_TEXT")
public class StandardTextEntity {

    @Id
    @Column(name = "STANDARD_TEXT_ID")
    private String standardTextId;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "DEFAULT_VALUE", length = 1000)
    private String defaultValue;

    // TODO: If possible try to get Map annotations to work...
    @OneToMany(mappedBy = "targetStandardText")
    private List<StandardTextLocalisationEntity> localisationList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+standardTextId+"]";
    }
}
