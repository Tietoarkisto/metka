package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.enums.ConfigurationType;

import javax.persistence.*;

/**
 * Entity class for Study type revisionable objects.
 */
@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY)
public class StudyEntity extends RevisionableEntity {
    @Column(name = "STUDYID", updatable = false, unique = true)
    private String studyId;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }
}
