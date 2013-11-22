package fi.uta.fsd.metka.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 1:18 PM
 */
@Entity
@DiscriminatorValue("PUBLICATION")
public class PublicationRevisionEntity extends RevisionDataEntity {
}
