package fi.uta.fsd.metka.transfer.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.uta.fsd.metka.enums.Language;
import org.springframework.util.StringUtils;

/**
 * Single reference value/title pair.
 * This is always the result of one match of a reference path.
 * If multiple matches are found for a path then a List of ReferenceOptions should be used.
 */
public class ReferenceOption {
    private final String value;
    private final ReferenceOptionTitle title;

    public ReferenceOption(String value, ReferenceOptionTitle title) {
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public ReferenceOptionTitle getTitle() {
        return title;
    }

    /**
     * Return information if title for given language contains text.
     * @param language
     * @return
     */
    @JsonIgnore public boolean hasTitleFor(Language language) {
        return StringUtils.hasText(title.getValue().getTexts().get(language.toValue()));
    }

    /**
     * Returns title for given language or for default if title for given language
     * contains no text.
     * Use hasTitleFor to check which one is returned.
     * @param language
     * @return
     */
    @JsonIgnore public String getTitleFor(Language language) {
        if(hasTitleFor(language)) {
            return title.getValue().getTexts().get(language.toValue());
        } else {
            return title.getValue().getTexts().get(Language.DEFAULT.toValue());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOption that = (ReferenceOption) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
