package fi.uta.fsd.metka.transfer.study;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;

public class DDIExportRequest {
    private final Long id;
    private final Integer no;
    private final Language language;

    @JsonCreator
    public DDIExportRequest(@JsonProperty("id") Long id, @JsonProperty("no") Integer no, @JsonProperty("language") String language) {
        this.id = id;
        this.no = no;
        this.language = Language.fromValue(language);
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public Language getLanguage() {
        return language;
    }
}
