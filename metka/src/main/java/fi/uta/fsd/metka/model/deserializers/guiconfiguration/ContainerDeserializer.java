package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.data.enums.ContainerType;
import fi.uta.fsd.metka.data.enums.SectionState;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.Container;
import fi.uta.fsd.metka.model.guiconfiguration.FieldDescription;

import java.io.IOException;

public class ContainerDeserializer extends JsonDeserializer<Container> {

    @Override
    public Container deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        Container con = new Container();

        // set type
        JsonNode type = node.get("type");
        con.setType(ContainerType.valueOf(type.textValue()));

        // Set dialogTitle
        JsonNode title = node.get("title");
        if(title == null) {
            title = node.get("&title");
        }

        TranslationObject loc = null;
        if(title != null) {
            loc = oc.treeToValue(title, TranslationObject.class);
        }
        con.setTitle(loc);

        // Set hidden
        JsonNode hidden = node.get("hidden");
        if(hidden != null && hidden.getNodeType() == JsonNodeType.BOOLEAN) {
            con.setHidden(hidden.booleanValue());
        }

        // Set readOnly
        JsonNode readOnly = node.get("readOnly");
        if(readOnly != null && readOnly.getNodeType() == JsonNodeType.BOOLEAN) {
            con.setReadOnly(readOnly.booleanValue());
        }

        // set important
        if(con.getType() == ContainerType.SECTION || con.getType() == ContainerType.CELL) {
            JsonNode important = node.get("important");
            if(important != null && important.getNodeType() == JsonNodeType.BOOLEAN) {
                con.setImportant(important.booleanValue());
            }
        }

        // set defaultState
        if(con.getType() == ContainerType.SECTION) {
            JsonNode defaultState = node.get("defaultState");
            if(defaultState != null && defaultState.getNodeType() == JsonNodeType.STRING) {
                con.setDefaultState(SectionState.valueOf(defaultState.textValue()));
            }
        }

        // set columns
        if(con.getType() == ContainerType.COLUMN) {
            JsonNode columns = node.get("columns");
            if(columns != null && columns.getNodeType() == JsonNodeType.NUMBER) {
                con.setColspan(columns.intValue());
            }
        }

        // set content
        if(con.getType() == ContainerType.TAB || con.getType() == ContainerType.SECTION) {
            JsonNode content = node.get("content");
            if(content != null && content.getNodeType() == JsonNodeType.ARRAY) {
                for(JsonNode container : content) {
                    con.getContent().add(oc.treeToValue(container, Container.class));
                }
            }
        }
        // set rows
        if(con.getType() == ContainerType.COLUMN) {
            JsonNode rows = node.get("rows");
            if(rows != null && rows.getNodeType() == JsonNodeType.ARRAY) {
                for(JsonNode row : rows) {
                    con.getRows().add(oc.treeToValue(row, Container.class));
                }
            }
        }
        // set cells
        if(con.getType() == ContainerType.ROW) {
            JsonNode cells = node.get("cells");
            if(cells != null && cells.getNodeType() == JsonNodeType.ARRAY) {
                for(JsonNode cell : cells) {
                    con.getCells().add(oc.treeToValue(cell, Container.class));
                }
            }
        }

        // set required
        if(con.getType() == ContainerType.CELL) {
            JsonNode required = node.get("required");
            if(required != null && required.getNodeType() == JsonNodeType.BOOLEAN) {
                con.setRequired(required.booleanValue());
            }
        }

        // set colspan
        if(con.getType() == ContainerType.CELL) {
            JsonNode colspan = node.get("colspan");
            if(colspan != null && colspan.getNodeType() == JsonNodeType.NUMBER) {
                con.setColspan(colspan.intValue());
            }
        }

        // set field
        if(con.getType() == ContainerType.CELL) {
            JsonNode field = node.get("field");
            if(field != null && field.getNodeType() == JsonNodeType.OBJECT) {
                con.setField(oc.treeToValue(field, FieldDescription.class));
            }
        }

        return con;
    }
}
