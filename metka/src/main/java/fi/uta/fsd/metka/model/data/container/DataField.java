package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
/**
 * Specification and documentation is found from uml/data/uml_json_data_container_row.graphml
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueDataField.class, name = DataField.DataFieldType.Types.VALUE),
        @JsonSubTypes.Type(value = ContainerDataField.class, name = DataField.DataFieldType.Types.CONTAINER),
        @JsonSubTypes.Type(value = ReferenceContainerDataField.class, name = DataField.DataFieldType.Types.REFERENCECONTAINER)
})
public abstract class DataField {
    private final String key;

    @JsonIgnore private DataFieldType type;

    @JsonIgnore private DataFieldContainer parent; // This is used during restriction validation

    public DataField(String key) {
        this.key = key;
    }

    public DataFieldType getType() {
        return type;
    }

    public void setType(DataFieldType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataField that = (DataField) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    @JsonIgnore
    public abstract DataField copy();

    /**
     * Normalizes this data field for new revision.
     * Operations depend on the actual type of field.
     */
    @JsonIgnore
    public void normalize() {throw new UnsupportedOperationException();}

    public DataFieldContainer getParent() {
        return parent;
    }

    public void setParent(DataFieldContainer parent) {
        this.parent = parent;
    }

    // There's no null version since DataFields are always under some DataFieldContainer
    public abstract void initParents(DataFieldContainer parent);

    public static enum DataFieldType {
        VALUE(Types.VALUE),
        CONTAINER(Types.CONTAINER),
        REFERENCECONTAINER(Types.REFERENCECONTAINER);

        private String value;
        private DataFieldType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static DataFieldType fromValue(String value) {
            switch(value) {
                case Types.VALUE:
                    return VALUE;
                case Types.CONTAINER:
                    return CONTAINER;
                case Types.REFERENCECONTAINER:
                    return REFERENCECONTAINER;
            }
            throw new UnsupportedOperationException(value + " is not a valid DataFieldType");
        }

        public static class Types {
            public static final String VALUE = "VALUE";
            public static final String CONTAINER = "CONTAINER";
            public static final String REFERENCECONTAINER = "REFERENCECONTAINER";
        }
    }
}
