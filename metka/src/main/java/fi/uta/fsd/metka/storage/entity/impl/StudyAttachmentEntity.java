package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY_ATTACHMENT)
public class StudyAttachmentEntity extends RevisionableEntity{
    /**
     * Links study attachment to its parent study.
     * Since this information should be immutable on the data it's set as non updatable here and the values should always match.
     *
     */
    @Column(name = "STUDY", updatable = false)
    private Long study;

    public Long getStudy() {
        return study;
    }

    public void setStudy(Long study) {
        this.study = study;
    }
}
