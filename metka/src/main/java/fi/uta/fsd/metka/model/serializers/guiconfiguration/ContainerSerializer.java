package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.data.enums.ContainerType;
import fi.uta.fsd.metka.model.guiconfiguration.Container;

import java.io.IOException;

public class ContainerSerializer extends JsonSerializer<Container> {
    @Override
    public void serialize(Container value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("type", value.getType().name());

        if(value.getType() != ContainerType.EMPTYCELL) {
            jgen.writeStringField("title", value.getTitle()); // TODO: Change to translation text instead
            jgen.writeBooleanField("hidden", value.getHidden());
            if(value.getReadOnly() == null) {
                jgen.writeNullField("readOnly");
            } else {
                jgen.writeBooleanField("readOnly", value.getReadOnly());
            }

            if(value.getType() == ContainerType.CELL) {
                jgen.writeBooleanField("required", value.getRequired());
            }

            if(value.getType() == ContainerType.CELL || value.getType() == ContainerType.SECTION) {
                jgen.writeBooleanField("important", value.getImportant());
            }

            if(value.getType() == ContainerType.SECTION) {
                jgen.writeStringField("defaultState", value.getDefaultState().name());
            }

            if(value.getType() == ContainerType.COLUMN) {
                jgen.writeNumberField("columns", value.getColumns());
            }

            if(value.getType() == ContainerType.TAB || value.getType() == ContainerType.SECTION) {
                jgen.writeArrayFieldStart("content");

                for(Container child : value.getContent()) {
                    if(checkValidContainer(value.getType(), child.getType())) {
                        jgen.writeObject(child);
                    }
                }

                jgen.writeEndArray();
            }

            if(value.getType() == ContainerType.COLUMN) {
                jgen.writeArrayFieldStart("rows");

                for(Container child : value.getRows()) {
                    if(checkValidContainer(value.getType(), child.getType())) {
                        jgen.writeObject(child);
                    }
                }

                jgen.writeEndArray();
            }

            if(value.getType() == ContainerType.ROW) {
                jgen.writeArrayFieldStart("cells");

                for(Container child : value.getCells()) {
                    if(checkValidContainer(value.getType(), child.getType())) {
                        jgen.writeObject(child);
                    }
                }

                jgen.writeEndArray();
            }

            if(value.getType() == ContainerType.CELL) {
                jgen.writeObjectField("field", value.getField());
            }
        }

        if(value.getType() == ContainerType.CELL || value.getType() == ContainerType.EMPTYCELL) {
            jgen.writeNumberField("colspan", value.getColspan());
        }

        jgen.writeEndObject();
    }

    private boolean checkValidContainer(ContainerType parent, ContainerType child) {
        // Parent type can never contain the same type
        if(parent == child) {
            return false;
        }
        // Cell or empty cell can never contain anything
        if(parent == ContainerType.CELL || parent == ContainerType.EMPTYCELL) {
            return false;
        }
        // Cells and empty cells must always be within a row
        if((child == ContainerType.CELL || child == ContainerType.EMPTYCELL) && parent != ContainerType.ROW) {
            return false;
        }
        // Rows must always be within a column container
        if(child == ContainerType.ROW && parent != ContainerType.COLUMN) {
            return false;
        }
        // Column container must always be within a section or a tab
        if(child == ContainerType.COLUMN && !(parent == ContainerType.SECTION || parent == ContainerType.TAB)) {
            return false;
        }
        // Section must always be within a tab if it's inside something
        if(child == ContainerType.SECTION && parent != ContainerType.TAB) {
            return false;
        }
        return true;
    }
}
