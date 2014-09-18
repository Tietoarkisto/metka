package fi.uta.fsd.metka.storage.collecting;


import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceTitleType;
import fi.uta.fsd.metka.enums.SelectionListType;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Searches given JsonNode for various interpretations of given path.
 */
class DataFieldPathParser {
    private final Map<String, DataField> initialFields;
    private final String[] path;
    private final Configuration configuration;
    private final Language language;

    /**
     * Construct a data field path parser.
     * This object can collect mainly data field maps from given initial data field map.
     * @param initialFields    Map containing a set of data fields from where the parsing starts
     * @param path             Path used to parse things
     * @param configuration    Configuration that should contain the fields in the data field map
     * @param language         Language for which options are searched
     */
    DataFieldPathParser(Map<String, DataField> initialFields, String[] path, Configuration configuration, Language language) {
        this.initialFields = initialFields;
        this.path = path;
        this.configuration = configuration;
        this.language = language;
    }

    /**
     * Finds all terminal DataField maps containing the terminating path field.
     * Partial answers are not accepted so if the current path step terminates but there's still more path left then
     * that specific object is not included in the set.
     * @return
     */
    List<Map<String, DataField>> findTermini() {
        List<Map<String, DataField>> termini = new ArrayList<>();

        findTerminiPathStep(0, initialFields, path.clone(), termini);

        return termini;
    }

    private void findTerminiPathStep(int level, Map<String, DataField> fieldMap, String[] path, List<Map<String, DataField>> termini) {
        if(level >= path.length) {
            // No more path, terminate
            return;
        }

        if(fieldMap == null) {
            // Current node was null, can't continue
            return;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return;
        }

        DataField dataField = fieldMap.get(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return;
        }

        // Checks that path should terminate at this value. If so then add the current field map to termini
        if(level == path.length-1) {
            // Value is the terminating value of path,
            termini.add(fieldMap);
            return;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    findTerminiPathStep(level+1, row.getFields(), path, termini);
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    findTerminiPathStep(level+1, row.getFields(), path, termini);
                }
            }
        }
    }

    /**
     * Finds first terminal object to path.
     * Partial answers are not accepted so if the current path step terminates but there's still more path left then
     * that specific object is not included in the set.
     * @return
     */
    /*JsonNode findFirstTerminus() {
        List<JsonNode> termini = new ArrayList<>();

        findFirstTerminusPathStep(getInitialLevel(), initialNode, path.clone(), termini);

        return (termini.size() > 0) ? termini.get(0) : null;
    }

    private boolean findFirstTerminusPathStep(int level, JsonNode node, String[] path, List<JsonNode> termini) {
        if(termini.size() > 0) {
            // First terminus has been found, return false since parsing doesn't have to continue.
            return false;
        }

        if(level >= path.length) {
            // No more path, terminate
            return false;
        }

        if(node == null) {
            // Current node was null, can't continue
            return false;
        }

        switch(node.getNodeType()) {
            case ARRAY: // Iterate over array and recursively call this method.
                ArrayNode array = (ArrayNode)node;
                // Assume array contains objects. Doesn't return the result since returned result should always be false.
                for(JsonNode nextNode : array) {
                    findFirstTerminusPathStep(level+1, nextNode, path, termini);
                }
                break;
            case OBJECT: // Recursively call this method for the next path step. Only add this node if true is returned from the next recursion.
                String step = path[level];
                if(findFirstTerminusPathStep(level, node.get(step), path, termini)) {
                    termini.add(node);
                }
                break;
            case STRING:
            case BOOLEAN:
            case NUMBER: // Checks that path should terminate at this value and return true if OK. This should cause previous iteration to add its node.
                if(level == path.length-1) {
                    // Value is the terminating value of path, add
                    return true;
                }
                break;
            default:
                // Don't know how to parse, can't continue
                break;
        }
        // Default is to return false and assume that recursion has ended and no addition takes place.
        return false;
    }*/

    /**
     * Finds first terminating object matching path given to this parser.
     * If initialNode is an Object node then initialNode is returned if it contains at least one terminating path.
     * @return
     */
    /*JsonNode findFirstTerminatingMatch() {
        return findFirstTerminatingMatchPathStep(getInitialLevel(), initialNode, path.clone());
    }

    private JsonNode findFirstTerminatingMatchPathStep(int level, JsonNode node, String[] path) {
        if(level >= path.length) {
            // No more path, terminate
            return null;
        }

        if(node == null) {
            // Current node was null, can't continue
            return null;
        }

        switch(node.getNodeType()) {
            case ARRAY: // Iterate over array and recursively call this method.
                ArrayNode array = (ArrayNode)node;
                // Assume array contains objects. If non null result is found then return that result which terminates the iteration.
                for(JsonNode nextNode : array) {
                    JsonNode result = findFirstTerminatingMatchPathStep(level+1, nextNode, path);
                    if(result != null) {
                        return result;
                    }
                }
                break;
            case OBJECT: // Recursively call this method for the next path step. Only return this node if non null value is returned from the next recursion.
                String step = path[level];
                JsonNode result = findFirstTerminatingMatchPathStep(level, node.get(step), path);
                if(result != null) {
                    return node;
                }
                break;
            case STRING:
            case BOOLEAN:
            case NUMBER: // Checks that path should terminate at this value and return true if OK. This should cause previous iteration to return its node.
                if(level == path.length-1) {
                    // Value is the terminating value of path, add
                    return node;
                }
                break;
            default:
                // Don't know how to parse, can't continue
                break;
        }
        // Default is to return null and assume that recursion has ended.
        return null;
    }*/

    /**
     * Returns the first DataField object that terminates a path
     * @return
     */
    DataField findFirstTerminatingValue() {
        return findFirstTerminatingValuePathStep(0, initialFields, path.clone());
    }

    private DataField findFirstTerminatingValuePathStep(int level, Map<String, DataField> fieldMap, String[] path) {
        if(level >= path.length) {
            // No more path, terminate
            return null;
        }

        if(fieldMap == null) {
            // Current node was null, can't continue
            return null;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return null;
        }

        DataField dataField = fieldMap.get(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return null;
        }

        // Checks that path should terminate at this value. If so then return the current data field
        if(level == path.length-1) {
            // Value is the terminating value of path,
            return dataField;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return null;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    DataField result = findFirstTerminatingValuePathStep(level+1, row.getFields(), path);
                    if(result != null) {
                        return result;
                    }
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    DataField result = findFirstTerminatingValuePathStep(level+1, row.getFields(), path);
                    if(result != null) {
                        return result;
                    }
                }
            }
        }

        // Default is to return null and assume that recursion has ended.
        return null;
    }

    /**
     * Almost the same as first terminating object but instead of only a matching path the value at terminating path
     * has to equal given value.
     *
     * @param terminatingValue
     * @return
     */
    Map<String, DataField> findRootObjectWithTerminatingValue(String terminatingValue) {
        return findRootObjectWithTerminatingValueStep(0, initialFields, path.clone(), terminatingValue);
    }

    private Map<String, DataField> findRootObjectWithTerminatingValueStep(int level, Map<String, DataField> fieldMap, String[] path, String terminatingValue) {
        if(level >= path.length) {
            // No more path, terminate
            return null;
        }

        if(fieldMap == null) {
            // Current node was null, can't continue
            return null;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return null;
        }

        DataField dataField = fieldMap.get(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return null;
        }

        // Checks that path should terminate at this value. If so then return the current data field
        if(level == path.length-1) {
            if(field.getType().isContainer() || !(dataField instanceof ValueDataField)) {
                // Don't accept container types for value checking
                return null;
            }
            ValueDataField valueField = (ValueDataField)dataField;
            // Value is the terminating value of path,
            Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
            if(valueField.valueForEquals(optionLang, terminatingValue)) {
                return fieldMap;
            }

            return null;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return null;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    Map<String, DataField> result = findRootObjectWithTerminatingValueStep(level + 1, row.getFields(), path, terminatingValue);
                    if(result != null) {
                        return result;
                    }
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    Map<String, DataField> result = findRootObjectWithTerminatingValueStep(level + 1, row.getFields(), path, terminatingValue);
                    if(result != null) {
                        return result;
                    }
                }
            }
        }

        // Default is to return null and assume that recursion has ended.
        return null;
    }

    /**
     * Returns ReferenceOption from given revision data
     * @param revision Revision data from which to return reference option
     * @param reference Reference that is used to extract value and title from given JsonNode
     * @return Single ReferenceOption containing value and title as per specification
     */
    ReferenceOption getOption(RevisionData revision, Reference reference) {

        if(revision == null) {
            // Needs a non empty data field map as field map
            return null;
        }
        // This path should be relative to the given field map
        String[] path = reference.getTitlePathParts();

        String valueStr = revision.getKey().getId().toString();
        if(!StringUtils.hasText(valueStr)) {
            return null;
        }
        String titleStr = getTitleString(revision.getFields(), path);
        if(titleStr == null) {
            titleStr = valueStr;
        }

        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, titleStr));
        return option;
    }

    /**
     * Returns ReferenceOption from given data field map
     * @param fieldMap Map containing value field. Title fields are relative to this map
     * @param reference Reference that is used to extract value and title from given JsonNode
     * @return Single ReferenceOption containing value and title as per specification
     */
    ReferenceOption getOption(Map<String, DataField> fieldMap, Reference reference) {

        if(fieldMap == null || fieldMap.isEmpty()) {
            // Needs a non empty data field map as field map
            return null;
        }
        String[] path = reference.getValuePathParts();
        // This should be the field key found from the field map
        String valueKey = path[path.length-1];

        // This path should be relative to the given field map
        path = reference.getTitlePathParts();

        DataField dataField = fieldMap.get(valueKey);
        if(dataField == null) {
            // No value to get
            return null;
        }
        if(dataField instanceof ContainerDataField || dataField instanceof ReferenceContainerDataField) {
            // Can't return a single option
            // TODO: It should be possible to basically make a read only copy of a container with references, but not yet.
            return null;
        }

        Field field = configuration.getField(dataField.getKey());

        Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;

        ValueDataField valueField = (ValueDataField)dataField;
        if(!valueField.hasValueFor(optionLang)) {
            // Field does not have a value for the needed language
            return null;
        }

        String valueStr = valueField.getActualValueFor(optionLang);
        if(!StringUtils.hasText(valueStr)) {
            return null;
        }
        String titleStr = getTitleString(fieldMap, path);
        if(titleStr != null && field.getType() == FieldType.SELECTION) {
            titleStr = getSelectionTitle(field, titleStr);
        }
        if(titleStr == null) {
            titleStr = valueStr;
        }

        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, titleStr));
        return option;
    }

    private String getTitleString(Map<String, DataField> fieldMap, String[] path) {
        String titleStr = null;

        if(path != null) {
            // Let's try to find first terminating data field matching the title path
            DataFieldPathParser titleParser = new DataFieldPathParser(fieldMap, path, configuration, language);
            DataField titleField = titleParser.findFirstTerminatingValue();
            if(titleField != null && titleField instanceof ValueDataField) {
                ValueDataField titleValueField = (ValueDataField)titleField;
                Field field = configuration.getField(titleField.getKey());
                Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
                if(titleValueField.hasValueFor(optionLang)) {
                    // Field does not have a value for the needed language
                    titleStr = titleValueField.getActualValueFor(optionLang);
                }
                if(titleStr != null && field.getType() == FieldType.SELECTION) {
                    titleStr = getSelectionTitle(field, titleStr);
                }
            }
        }

        return titleStr;
    }

    private String getSelectionTitle(Field field, String value) {
        String titleStr = null;

        Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
        SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
        if(list.getType() == SelectionListType.LITERAL || list.getType() == SelectionListType.VALUE) {
            Option option = list.getOptionWithValue(value);
            if(option != null) {
                titleStr = option.getTitleFor(optionLang);
            }
        } else {
            // TODO: Possibly follow reference
        }

        return titleStr;
    }
}
