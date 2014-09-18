package fi.uta.fsd.metka.storage.collecting_old;


import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.enums.SelectionListType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

// TODO: Actual handling of language, for now always fetches values from default
@Deprecated
@Service
class DependencyReferenceHandler extends ReferenceHandler {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    /**
     * Analyses a dependency reference and collects needed values defined by that reference.
     * Configuration is required since content of options depends on another field inside the same configuration,
     * which may lead to other revisionables, json-files or fields inside the same revision data.
     * Field is required since depending on field type a single option or multiple options is returned
     *
     * TODO: Referencing a value from inside the currently being displayed revision data.
     *      In the ideal case this would be made from within the client since all required data is already there
     *      (might even be only there since there might be unsaved changes).
     * TODO: Recursive dependencies cause a problem since all required dependency values have to be brought from client.
     *
     * @param field Field currently being analysed
     * @param reference Reference of the field currently being analysed
     * @param config Configuration from where the field originates
     * @param dependencyValue Current value of the field this dependency reference depends on
     * @param options List where found values are placed as ReferenceOption objects
     */
    void collectOptions(Field field, Reference reference, Configuration config,
                                            String dependencyValue, List<ReferenceOption> options) {
        Field dependencyField = config.getField(reference.getTarget());
        // TODO: All possible permutations as per specification. Implement in priority order. When more permutations start to be added then reorganize for clarity.
        switch(dependencyField.getType()) {
            case SELECTION:
                SelectionList dependencyList = config.getRootSelectionList(dependencyField.getSelectionList());
                if(dependencyList == null) {
                    // We can't do anything, dependency list is missing from configuration.
                    return;
                }
                if(dependencyList.getType() == SelectionListType.REFERENCE) {
                    Reference dependencyReference = config.getReference(dependencyList.getReference());
                    if(dependencyReference == null) {
                        // We can't do anything, dependency reference is missing from configuration.
                        return;
                    }
                    handleReferenceDependency(field, reference, config, dependencyValue, dependencyReference, options);
                } else {
                    // TODO: Handle non reference list dependency
                }
                break;
            case REFERENCECONTAINER:
                Reference dependencyReference = config.getReference(dependencyField.getReference());
                if(dependencyReference == null) {
                    // We can't do anything, dependency reference is missing from configuration.
                    return;
                }
                handleReferenceDependency(field, reference, config, dependencyValue, dependencyReference, options);
                break;
            default:
                // Functionality has not been implemented yet for the given case.
                break;
        }
    }

    /**
     * Handle dependency reference that is dependent of a reference.
     * @param field Field currently being analysed. Used to determine how many options are needed.
     * @param reference Reference of the field currently being analysed
     * @param config Configuration from where the field originates
     * @param dependencyValue Current value of the field this dependency reference depends on
     * @param dependencyReference Reference that the current dependency reference field is dependent on
     * @param options List where found values are placed as ReferenceOption objects
     */
    private void handleReferenceDependency(Field field, Reference reference, Configuration config,
                                           String dependencyValue, Reference dependencyReference,
                                           List<ReferenceOption> options) {
        switch(dependencyReference.getType()) {
            case REVISIONABLE:
                collectRevisionableDependencyValues(field, reference, dependencyValue, options);
                break;
            case JSON:
                collectJsonDependencyValues(field, reference, config, dependencyValue, dependencyReference, options);
                break;
            case DEPENDENCY:
                // TODO: Recursive dependencies...
                break;
        }
    }

    /**
     * Collects reference values from Revisionable reference.
     * Field determines if multiple values are collected but this relates mostly to containers and those are not handled.
     * @param field
     * @param reference
     * @param dependencyValue
     * @param options
     */
    private void collectRevisionableDependencyValues(Field field, Reference reference,
                                                     String dependencyValue, List<ReferenceOption> options) {
        /*Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(Long.parseLong(dependencyValue), false, null);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return;
        }
        RevisionData data = dataPair.getRight();

        // TODO: fields that are not top level value fields, for now assume this is the case.

        // TODO: If field type is SELECTION or some other type that requires multiple options and valuePath points to something that allows multiple options then add all of them.

        ReferenceOptionTitle title = null;
        Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(reference.getValuePath()));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // No value to save
            return;
        }

        String value = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
        if(StringUtils.hasText(reference.getTitlePath())) {
            fieldPair = data.dataField(ValueDataFieldCall.get(reference.getTitlePath()));
            Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(data.getConfiguration());
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                TranslationObject to = new TranslationObject();
                to.getTexts().put(Language.DEFAULT.toValue(), fieldPair.getRight().getActualValueFor(Language.DEFAULT));
                if(confPair.getLeft() == ReturnResult.CONFIGURATION_FOUND && confPair.getRight().getField(reference.getTitlePath()).getType() == FieldType.SELECTION) {
                    title = new ReferenceOptionTitle(ReferenceTitleType.VALUE, to);
                } else {
                    title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to);
                }
            }
        }

        if(title == null) {
            Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
            Field targetField = null;
            if(configPair.getLeft() == ReturnResult.CONFIGURATION_FOUND) {
                targetField = configPair.getRight().getField(reference.getValuePath());
            }
            if(targetField != null && targetField.getType() == FieldType.SELECTION) {
                SelectionList list = configPair.getRight().getRootSelectionList(targetField.getSelectionList());
                if(list != null) {
                    Option option = list.getOptionWithValue(value);
                    if(option != null) {
                        TranslationObject to = new TranslationObject();
                        to.getTexts().put(Language.DEFAULT.toValue(), option.getDefaultTitle());
                        title = new ReferenceOptionTitle(list.getType() == SelectionListType.LITERAL ? ReferenceTitleType.LITERAL : ReferenceTitleType.VALUE, to);
                    }
                }
            }
            // TODO: Can we resolve title from behind reference?
        }

        if(title == null) {
            TranslationObject to = new TranslationObject();
            to.getTexts().put(Language.DEFAULT.toValue(), value);
            title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to);
        }

        // Add option to options list.
        // TODO: Other possibilities
        ReferenceOption option = new ReferenceOption(value, title);
        options.add(option);*/
    }

    private void collectJsonDependencyValues(Field field, Reference reference, Configuration config,
                                             String dependencyValue, Reference dependencyReference,
                                             List<ReferenceOption> options) {
        MiscJSONEntity misc = repository.getMiscJsonForReference(dependencyReference);
        if(misc == null || !StringUtils.hasText(misc.getData())) {
            // No data, can't continue
            return;
        }

        Pair<SerializationResults, JsonNode> pair = json.deserializeToJsonTree(misc.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            // Deserialization failure
            return;
        }
        JsonNode root = pair.getRight();
        JsonPathParser parser = new JsonPathParser(root.get("data"), dependencyReference.getValuePathParts());
        root = parser.findRootObjectWithTerminatingValue(dependencyValue);
        if(root == null) {
            // No containing object found, can't continue;
            return;
        }
        parser = new JsonPathParser(root, reference.getValuePathParts());

        // If field requires only a single answer then return only first option, otherwise return all
        switch(field.getType()) {
            case SELECTION:
                List<JsonNode> termini = parser.findTermini();
                for(JsonNode node : termini) {
                    // Get node containing value, Has to be ValueNode due to JsonParser only returning objects containing terminating value node.

                    ReferenceOption option = getOption(node, reference);
                    if(option != null) {
                        options.add(option);
                    }
                }
                break;
            default:
                // Default is to return one option
                JsonNode terminus = parser.findFirstTerminus();
                ReferenceOption option = getOption(terminus, reference);

                if(option != null) {
                    options.add(option);
                }
                break;
        }
    }
}