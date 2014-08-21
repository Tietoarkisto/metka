package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.ConfigurationType;

public class ReferenceRowRequest {
    private ConfigurationType type;
    private Long id;
    private Integer no;
    private String path;
    private String reference;

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
