package fi.uta.fsd.metka.storage.collecting_old;


import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Deprecated
class JsonReferenceHandler extends ReferenceHandler {
    /**
     * Analyses a json reference and collects the values defined by that reference.
     *
     * @param reference Reference to be processed
     * @param options List where found values are placed as ReferenceOption objects
     */
    void collectOptions(Reference reference,  List<ReferenceOption> options) {
        if(!StringUtils.hasText(reference.getValuePath())) {
            // We have no value path, can't continue
            return;
        }

        MiscJSONEntity entity = repository.getMiscJsonForReference(reference);
        if(entity == null || !StringUtils.hasText(entity.getData())) {
            // No json or no data, can't continue
            return;
        }

        Pair<SerializationResults, JsonNode> pair = json.deserializeToJsonTree(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            // No root node, can't continue
            return;
        }

        // Form value path array.
        String[] path = reference.getValuePathParts();

        JsonPathParser pathParser = new JsonPathParser(pair.getRight().get("data"), path);
        List<JsonNode> termini = pathParser.findTermini();
        for(JsonNode node : termini) {
            // Get node containing value, Has to be ValueNode due to JsonParser only returning objects containing terminating value node.

            ReferenceOption option = getOption(node, reference);
            if(option != null) {
                options.add(option);
            }
        }
    }
}
