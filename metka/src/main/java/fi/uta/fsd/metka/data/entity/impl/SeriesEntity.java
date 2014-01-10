package fi.uta.fsd.metka.data.entity.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/20/13
 * Time: 8:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("SERIES")
public class SeriesEntity extends RevisionableEntity {
}
