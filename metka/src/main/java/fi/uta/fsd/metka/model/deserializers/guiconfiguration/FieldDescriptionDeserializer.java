package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.FieldDescription;

import java.io.IOException;

public class FieldDescriptionDeserializer extends ObjectDeserializer<FieldDescription> {

    @Override
    public FieldDescription doDeserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode key = node.get("key");
        FieldDescription fd = new FieldDescription(key.asText());

        // NOTICE: displayType and multichoice are defined attributes in the specification but their implementation is not an immediate concerne so they can be skipped

        // Set multiline
        JsonNode multiline = node.get("multiline");
        if(multiline != null && multiline.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setMultiline(multiline.booleanValue());
        }

        // Set user groups
        JsonNode columns = node.get("columnFields");
        if(columns != null && columns.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode column : columns) {
                if(column.getNodeType() == JsonNodeType.STRING) {
                    fd.getColumnFields().add(column.textValue());
                }
            }
        }

        // Set showSaveInfo
        JsonNode showSave = node.get("showSaveInfo");
        if(showSave != null && showSave.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowSaveInfo(showSave.booleanValue());
        }

        // Set showReferenceValue
        JsonNode showRef = node.get("showReferenceValue");
        if(showRef != null && showRef.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowReferenceValue(showRef.booleanValue());
        }

        // Set handlerName
        JsonNode handlerName = node.get("handlerName");
        if(handlerName != null && handlerName.getNodeType() == JsonNodeType.STRING) {
            fd.setHandlerName(handlerName.textValue());
        }

        // Set dialogTitle
        JsonNode dialogTitle = node.get("dialogTitle");
        if(dialogTitle == null) {
            dialogTitle = node.get("&dialogTitle");
        }

        // Set displayHeader
        JsonNode displayHeader = node.get("displayHeader");
        if(showRef != null && showRef.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setDisplayHeader(displayHeader.booleanValue());
        }

        TranslationObject loc = null;
        if(dialogTitle != null) {
            loc = oc.treeToValue(dialogTitle, TranslationObject.class);
        }
        fd.setDialogTitle(loc);

        return fd;
    }
}
