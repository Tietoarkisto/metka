package fi.uta.fsd.metka.storage.collecting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceTitleType;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.ReferenceRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public abstract class ReferenceHandler {
    @Autowired
    protected ReferenceRepository repository;

    @Autowired
    protected ConfigurationRepository configurations;

    @Autowired
    protected JSONUtil json;

    /**
     * Returns ReferenceOption from given JsonObject
     * @param root JsonNode containing actual value node as one of its nodes and functioning as a root for titlePath
     * @param reference Reference that is used to extract value and title from given JsonNode
     * @return Single ReferenceOption containing value and title as per specification
     */
    protected ReferenceOption getOption(JsonNode root, Reference reference) {
        if(root.getNodeType() != JsonNodeType.OBJECT) {
            // Needs an Object as its root
            return null;
        }
        String[] path = reference.getValuePathParts();
        //String[] path = (reference.getValuePath()).split("\\.");
        String valueKey = path[path.length-1];

        path = reference.getTitlePathParts();

        JsonNode value = root.get(valueKey);
        String valueStr = (value != null) ? value.asText() : null;
        TranslationObject to = null;
        if(path != null) {
            JsonPathParser titleParser = new JsonPathParser(root, path);
            // This should return either a string node or an object that represents a translation object
            // We try to detect translation object by checking if the object contains parameter 'default' that has content
            JsonNode title = titleParser.findFirstTerminatingValue();
            if(title.getNodeType() == JsonNodeType.OBJECT) {
                JsonNode def = title.get("default");
                // If this node is a translation object then it has to contain non null default parameter.
                if(def != null && def.getNodeType() != JsonNodeType.NULL) {
                    to = new TranslationObject();
                    for(Language language : Language.values()) {
                        JsonNode langField = def.get(language.toValue());
                        if(langField != null && langField.getNodeType() != JsonNodeType.NULL) {
                            to.getTexts().put(language.toValue(), langField.textValue());
                        }
                    }
                }
            } else {
                to = new TranslationObject();
                // If we have some text in title parameter then put it inside the default in translation object
                if(StringUtils.hasText(title.textValue())) {
                    to.getTexts().put(Language.DEFAULT.toValue(), title.textValue());
                }
            }
        } else {
            to = new TranslationObject();
            to.getTexts().put(Language.DEFAULT.toValue(), valueStr);
        }
        if(to == null) {
            // Don't return option since we don't have a value
            return null;
        }
        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to));
        return option;
    }
}
