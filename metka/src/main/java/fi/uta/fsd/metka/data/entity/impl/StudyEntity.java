package fi.uta.fsd.metka.data.entity.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.persistence.*;

/**
 * Entity class for Study type revisionable objects.
 * Contains a separate sequence for study number.
 * Globally unique revisionable id is still part of the revision key and determines for example references
 * but studies contain a separate sequence for displayable study numbers that is used to form the studyno
 * information (FSD+number with leading zeros).
 *
 * IMPORTANT: studyNumber is not used for references in any way, the only meaning it has is to provide a sequence of numbers
 *            to provide a new study with a study number that's not affected by other revisionable objects.
 *            Since sequences are not supported with annotations and non ID fields manual sequencing is required instead.
 *            This should be implemented using SequenceEntity table and using ConfigurationType value as a key
 */
@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY)
public class StudyEntity extends RevisionableEntity {
    @Column(name = "STUDY_NUMBER", updatable = false, unique = true)
    private Integer studyNumber;

    public Integer getStudyNumber() {
        return studyNumber;
    }

    public void setStudyNumber(Integer studyNumber) {
        this.studyNumber = studyNumber;
    }
}
