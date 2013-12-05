package fi.uta.fsd.metka.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/26/13
 * Time: 9:05 AM
 */
@Entity
@DiscriminatorValue("VOCABULARY")
public class VocabularyRevisionEntity extends RevisionDataEntity {
}
