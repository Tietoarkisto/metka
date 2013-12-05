package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.RevisionDataType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/22/13
 * Time: 1:02 PM
 */
@Entity
@DiscriminatorValue("STUDY")
public class StudyRevisionEntity extends RevisionDataEntity {
}
