package fi.uta.fsd.metka.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 1:38 PM
 */
@Entity
@DiscriminatorValue("BINDER")
public class BinderRevisionEntity extends RevisionDataEntity {
}
