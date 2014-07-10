package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.enums.VariableDataType;

import javax.persistence.*;

/**
 * Entity for study attachment queue table.
 * Contains new study attachment operations. These should be checked always when study is processed in some way
 * (saved or viewed) so that information is always up to date.
 * TODO: Should missing data be added to approved studies or should a new draft be created and all links moved to that one instead?
 */
@Entity
@Table(name = "STUDY_ATTACHMENT_QUEUE")
public class StudyAttachmentQueueEntity {

    @Id
    @SequenceGenerator(name="STUDY_ATTACHMENT_QUEUE_ID_SEQ", sequenceName="STUDY_ATTACHMENT_QUEUE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STUDY_ATTACHMENT_QUEUE_ID_SEQ")
    @Column(name = "STUDY_ATTACHMENT_QUEUE_ID", updatable = false)
    private Long id;

    @Column(name = "TARGET_STUDY")
    private Long targetStudy;

    /**
     * Strictly here to facilitate multiple configurations and to not require
     * changes to code pertaining to this queue if field name changes.
     */
    @Column(name = "STUDY_ATTACHMENT_FIELD")
    private String studyAttachmentField;

    @Column(name = "STUDY_ATTACHMENT_ID")
    private Long studyAttachmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "VARIABLE_TYPE")
    private VariableDataType type;

    @Column(name = "PATH")
    private String path;

    @Version
    @Column(name = "ROW_VERSION")
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetStudy() {
        return targetStudy;
    }

    public void setTargetStudy(Long targetStudy) {
        this.targetStudy = targetStudy;
    }

    public String getStudyAttachmentField() {
        return studyAttachmentField;
    }

    public void setStudyAttachmentField(String studyAttachmentField) {
        this.studyAttachmentField = studyAttachmentField;
    }

    public Long getStudyAttachmentId() {
        return studyAttachmentId;
    }

    public void setStudyAttachmentId(Long studyAttachmentId) {
        this.studyAttachmentId = studyAttachmentId;
    }

    public VariableDataType getType() {
        return type;
    }

    public void setType(VariableDataType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
