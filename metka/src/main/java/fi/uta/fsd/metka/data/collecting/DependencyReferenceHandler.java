package fi.uta.fsd.metka.data.collecting;


import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.data.entity.MiscJSONEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ChoicelistType;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.ReferenceTitleType;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Service
class DependencyReferenceHandler extends ReferenceHandler {
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
                                            String dependencyValue, List<ReferenceOption> options)
            throws IOException {
        Field dependencyField = config.getField(reference.getTarget());
        // TODO: All possible permutations as per specification. Implement in priority order. When more permutations start to be added then reorganize for clarity.
        switch(dependencyField.getType()) {
            case CHOICE:
                Choicelist dependencyList = config.getRootChoicelist(dependencyField.getChoicelist());
                if(dependencyList == null) {
                    // We can't do anything, dependency list is missing from configuration.
                    return;
                }
                if(dependencyList.getType() == ChoicelistType.REFERENCE) {
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
                                           List<ReferenceOption> options)
            throws IOException {
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
                                                     String dependencyValue, List<ReferenceOption> options)
            throws IOException {
        RevisionEntity revision = repository.getRevisionForReferencedRevisionable(reference, dependencyValue);
        if(revision == null || StringUtils.isEmpty(revision.getData())) {
            // No data, can't continue
            return;
        }

        RevisionData data = json.readRevisionDataFromString(revision.getData());
        if(data == null) {
            return;
        }

        // TODO: fields that are not top level value fields, for now assume this is the case.

        // TODO: If field type is CHOICE or some other type that requires multiple options and valuePath points to something that allows multiple options then add all of them.

        String value;
        ReferenceOptionTitle title = null;
        SavedDataField sf = getSavedDataFieldFromRevisionData(data, reference.getValuePath());
        if(sf == null || !sf.hasValue()) {
            // No value to save
            return;
        }

        value = sf.getActualValue();
        if(!StringUtils.isEmpty(reference.getTitlePath())) {
            sf = getSavedDataFieldFromRevisionData(data, reference.getTitlePath());
            Configuration config = configurations.findConfiguration(data.getConfiguration());
            if(sf != null && sf.hasValue()) {
                if(config.getField(reference.getTitlePath()).getType() == FieldType.CHOICE) {
                    title = new ReferenceOptionTitle(ReferenceTitleType.VALUE, sf.getActualValue());
                } else {
                    title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, sf.getActualValue());
                }
            }
        }

        if(title == null) {
            title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, value);
        }

        // Add option to options list.
        // TODO: Other possibilities
        ReferenceOption option = new ReferenceOption(value, title);
        options.add(option);
    }

    private void collectJsonDependencyValues(Field field, Reference reference, Configuration config,
                                             String dependencyValue, Reference dependencyReference,
                                             List<ReferenceOption> options)
                throws IOException {
        MiscJSONEntity misc = repository.getMiscJsonForReference(dependencyReference);
        if(misc == null || StringUtils.isEmpty(misc.getData())) {
            // No data, can't continue
            return;
        }

        JsonNode root = json.readJsonTree(misc.getData());
        JsonPathParser parser = new JsonPathParser(root.get("data"), dependencyReference.getValuePath().split("\\."));
        root = parser.findRootObjectWithTerminatingValue(dependencyValue);
        if(root == null) {
            // No containing object found, can't continue;
            return;
        }
        parser = new JsonPathParser(root, reference.getValuePath().split("\\."));

        // If field requires only a single answer then return only first option, otherwise return all
        switch(field.getType()) {
            case CHOICE:
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