package fi.uta.fsd.metka.data.entity.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity class for study variables revisionable objects.
 * Unlike most of the revisionable entities this contains additional information namely the id of a
 * study revisionable object this variables object belongs to. This is exclusively used to speed up
 * data manipulation operations.
 * STUDY_ID is not a key field (primary or foreign) and it is assumed to be filled when a new
 * study variables object is created.
 */
@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY_VARIABLES)
public class StudyVariablesEntity extends RevisionableEntity {
    @Column(name = "STUDY_ID")
    private Long studyId;

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
}
