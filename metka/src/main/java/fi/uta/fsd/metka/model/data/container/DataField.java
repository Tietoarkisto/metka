package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.*;

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
public class DataField {
    private final String key;
    private DataFieldType type;

    public DataField(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    // Immutable
    /*public void setType(DataFieldType type) {
        if(this.type == null) {
            this.type = type;
        }
    }*/

    public DataFieldType getType() {
        return type;
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
    public DataField copy() {throw new UnsupportedOperationException();}

    /**
     * Normalizes this data field for new revision.
     * Operations depend on the actual type of field.
     */
    @JsonIgnore
    public void normalize() {throw new UnsupportedOperationException();}

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
