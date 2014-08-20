package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConfigurationType.Values.PUBLICATION)
public class PublicationEntity extends RevisionableEntity {
}
