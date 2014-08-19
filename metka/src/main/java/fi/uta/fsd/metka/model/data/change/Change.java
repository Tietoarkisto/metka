package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.container.DataField;

import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Change.class, name = DataField.DataFieldType.Types.VALUE),
        @JsonSubTypes.Type(value = ContainerChange.class, name = DataField.DataFieldType.Types.CONTAINER)
})
public class Change {
    private final ChangeType type;
    private final String key;
    private final Set<Language> changeIn = new HashSet<>();

    @JsonCreator
    public Change(@JsonProperty("key") String key, @JsonProperty("type") ChangeType type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public ChangeType getType() {
        return type;
    }

    public Set<Language> getChangeIn() {
        return changeIn;
    }

    @JsonIgnore
    public void setChangeIn(Language language) {
        changeIn.add(language);
    }

    public static enum ChangeType {
        VALUE(Types.VALUE),
        CONTAINER(Types.CONTAINER);

        private String value;
        private ChangeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ChangeType fromValue(String value) {
            switch(value) {
                case Types.VALUE:
                    return VALUE;
                case Types.CONTAINER:
                    return CONTAINER;
            }
            throw new UnsupportedOperationException(value + " is not a valid ChangeType");
        }

        public static class Types {
            public static final String VALUE = "VALUE";
            public static final String CONTAINER = "CONTAINER";
        }
    }
}
