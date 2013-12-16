package fi.uta.fsd.metka.data.entity.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/11/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("STUDY")
public class StudyEntity extends RevisionableEntity {
}
