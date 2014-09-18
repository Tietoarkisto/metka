package fi.uta.fsd.metka.storage.collecting_old;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches given JsonNode for various interpretations of given path.
 */
@Deprecated
class JsonPathParser {
    private final JsonNode initialNode;
    private final String[] path;
    JsonPathParser(JsonNode initialNode, String[] path) {
        this.initialNode = initialNode;
        this.path = path;
    }

    /**
     * Returns initial level for all path parsing operations.
     * If initialNode is an array then parsing has to be started from level -1 to include all objects in that array.
     * @return
     */
    private int getInitialLevel() {
        return (initialNode.getNodeType() == JsonNodeType.ARRAY) ? -1 : 0;
    }

    /**
     * Finds all terminal objects to path.
     * Partial answers are not accepted so if the current path step terminates but there's still more path left then
     * that specific object is not included in the set.
     * @return
     */
    List<JsonNode> findTermini() {
        List<JsonNode> termini = new ArrayList<>();

        findTerminiPathStep(getInitialLevel(), initialNode, path.clone(), termini);

        return termini;
    }

    private boolean findTerminiPathStep(int level, JsonNode node, String[] path, List<JsonNode> termini) {
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
                    findTerminiPathStep(level+1, nextNode, path, termini);
                }
                break;
            case OBJECT: // Recursively call this method for the next path step. Only add this node if true is returned from the next recursion.
                String step = path[level];
                if(findTerminiPathStep(level, node.get(step), path, termini)) {
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
    }

    /**
     * Finds first terminal object to path.
     * Partial answers are not accepted so if the current path step terminates but there's still more path left then
     * that specific object is not included in the set.
     * @return
     */
    JsonNode findFirstTerminus() {
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
    }

    /**
     * Finds first terminating object matching path given to this parser.
     * If initialNode is an Object node then initialNode is returned if it contains at least one terminating path.
     * @return
     */
    JsonNode findFirstTerminatingMatch() {
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
    }

    JsonNode findFirstTerminatingValue() {
        return findFirstTerminatingValuePathStep(getInitialLevel(), initialNode, path.clone());
    }

    private JsonNode findFirstTerminatingValuePathStep(int level, JsonNode node, String[] path) {
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
                    JsonNode result = findFirstTerminatingValuePathStep(level+1, nextNode, path);
                    if(result != null) {
                        return result;
                    }
                }
                break;
            case OBJECT: // Recursively call this method for the next path step. Return result.
                String step = path[level];
                JsonNode result = findFirstTerminatingValuePathStep(level, node.get(step), path);
                return result;
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
    }

    /**
     * Almost the same as first terminating object but instead of only a matching path the value at terminating path
     * has to equal given value.
     *
     * @param terminatingValue
     * @return
     */
    JsonNode findRootObjectWithTerminatingValue(String terminatingValue) {
        return findRootObjectWithTerminatingValueStep(getInitialLevel(), initialNode, path.clone(), terminatingValue);
    }

    private JsonNode findRootObjectWithTerminatingValueStep(int level, JsonNode node, String[] path, String terminatingValue) {
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
                    JsonNode result = findRootObjectWithTerminatingValueStep(level+1, nextNode, path, terminatingValue);
                    if(result != null) {
                        return result;
                    }
                }
                break;
            case OBJECT: // Recursively call this method for the next path step. Only return this node if non null value is returned from the next recursion.
                String step = path[level];
                JsonNode result = findRootObjectWithTerminatingValueStep(level, node.get(step), path, terminatingValue);
                if(result != null) {
                    return node;
                }
                break;
            case STRING:
            case BOOLEAN:
            case NUMBER: // Checks that path should terminate at this value and return true if OK. This should cause previous iteration to return its node.
                if(level == path.length-1 && node.asText().equals(terminatingValue)) {
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
    }
}
