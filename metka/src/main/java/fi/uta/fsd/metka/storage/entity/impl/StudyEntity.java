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
    @Column(name = "STUDY_ID", unique = true)
    private String studyId;

    public String getStudyId() {
        return studyId;
    }

    // StudyId has to be immutable
    public void setStudyId(String studyId) {
        if(this.studyId == null) this.studyId = studyId;
    }
}
