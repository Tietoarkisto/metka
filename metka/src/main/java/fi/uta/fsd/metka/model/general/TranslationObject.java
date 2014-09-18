package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.Language;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a json translation text object.
 * Should never be serialized or deserialized as is but always through custom components
 */
@JsonIgnoreProperties("_comment")
public class TranslationObject {
    private final Map<String, String> texts = new HashMap<>();

    public Map<String, String> getTexts() {
        return texts;
    }

    @JsonIgnore
    public String getTitleFor(Language language) {
        if(!StringUtils.hasText(texts.get(language))) {
            return texts.get(language);
        } else {
            return texts.get("default");
        }
    }
}
