package fi.uta.fsd.metka.storage.collecting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.enums.ReferenceTitleType;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.ReferenceRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Reference;
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
        String titleStr = null;
        if(path != null) {
            JsonPathParser titleParser = new JsonPathParser(root, path);
            JsonNode title = titleParser.findFirstTerminatingValue();
            titleStr = (title != null) ? title.asText() : null;
            if(titleStr == null) {
                titleStr = valueStr;
            }
        } else {
            titleStr = valueStr;
        }
        if(StringUtils.isEmpty(valueStr)) {
            // Don't return option since we don't have a value
            return null;
        }
        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, titleStr));
        return option;
    }
}
