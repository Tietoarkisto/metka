package fi.uta.fsd.metka.data.entity.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY_ATTACHMENT)
public class StudyAttachmentEntity extends RevisionableEntity{
    // Records current path of this StudyAttachment.
    // Should strictly be used to speed up operations and actual path should always be checked from current revision to make sure.
    // Path should also be updated during those operations if it's not up to date
    @Column(name = "FILE_PATH")
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
