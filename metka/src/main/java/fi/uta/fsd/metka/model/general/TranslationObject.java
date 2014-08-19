package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
}
