package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConfigurationType.Values.PUBLICATION)
public class PublicationEntity extends RevisionableEntity {
    @Column(name = "PUBLICATION_ID", updatable = false, unique = true)
    private Long publicationId;

    public Long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Long publicationId) {
        this.publicationId = publicationId;
    }
}
