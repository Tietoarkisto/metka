package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:49 AM
 */
@Entity
@Table(name = "PERSON")
public class PersonEntity {

    // eduPersonPrincipalName (eppn, yksilöivä tunniste, esim. matti.heinonen@fsd.uta.fi)
    @Id
    @Column(name = "PERSON_ID")
    private String id;

    // cn eli commonName (koko nimi, esim. Matti Heinonen)
    @Column(name = "COMMON_NAME")
    private String commonName;

    // sn eli surname (sukunimi, esim. Heinonen)
    @Column(name = "SURNAME")
    private String surname;

    // displayName (kutsumanimi, esim. Matti)
    @Column(name = "DISPLAY_NAME")
    private String displayName;

    // schacHomeOrganization eli (kotiorganisaatio, aina fsd.uta.fi)
    @Column(name = "SCHAC_HOME_ORGANIZATION")
    private String schacHomeOrganization;

    // group membership (ryhmäjäsenyydet, esim. metka:admin;metka:user;aila:operator)
    @Column(name = "GROUP_MEMBERSHIP")
    private String groupMembership;

    @OneToMany(mappedBy = "modifiedBy")
    private List<RevisionDataEntity> modifiedList;

    @OneToMany(mappedBy = "handler")
    private List<RevisionDataEntity> handlingList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
