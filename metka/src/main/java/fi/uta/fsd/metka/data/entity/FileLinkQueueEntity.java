package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "FILE_LINK_QUEUE")
public class FileLinkQueueEntity {

    @Id
    @SequenceGenerator(name="FILE_LINK_QUEUE_ID_SEQ", sequenceName="FILE_LINK_QUEUE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FILE_LINK_QUEUE_ID_SEQ")
    @Column(name = "FILE_LINK_QUEUE_ID", updatable = false)
    private Integer id;

    @Column(name = "TARGET_ID")
    private Integer targetId;

    @Column(name = "TARGET_FIELD")
    private String targetField;

    @Column(name = "FILE_ID")
    private Integer fileId;

    @Column(name = "POR_FILE")
    private Boolean porFile;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Boolean getPorFile() {
        return (porFile == null) ? false : porFile;
    }

    public void setPorFile(Boolean porFile) {
        this.porFile = (porFile == null) ? false : porFile;
    }
}
