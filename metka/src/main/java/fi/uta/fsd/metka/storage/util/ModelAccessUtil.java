package fi.uta.fsd.metka.storage.util;

import fi.uta.fsd.metka.model.data.RevisionData;

// TODO: Refactor to use ModelFieldUtil and move away from JSONObject
public final class ModelAccessUtil {
    // Private constructor to stop instantiation
    private ModelAccessUtil() {}

    public static interface PathNavigable {

    }
    /**
     * Returns a DataField using field path navigation.
     * If supplied path doesn't lead to a legal DataField then null is returned.
     * Since both Containers and Rows are also DataFields both can be returned with this method.
     *
     * Field path consist of one or more steps separated by period.
     * Each step is either a field key that should be found from given position or an integer denoting a rowId for
     * specific row.
     * @param path Path to requested DataField
     * @param data RevisionData from which the DataField should be returned.
     * @return DataField found from the specific path.
     */
    // TODO: Change to using DataFieldCall operations
    public static PathNavigable getFieldDotFormat(String path, RevisionData data) {
        /*List<String> steps = Arrays.asList(path.split("."));
        PathNavigable field = null;
        for(String step : steps) {
            if(field == null) {
                field = data.getField(step);
                if(field == null) {
                    // Path terminates here, no starting field found. Return null.
                    return null;
                }
            } else {
                if(field instanceof ContainerDataField) {
                    ContainerDataField container = (ContainerDataField)field;
                    Integer rowId = stringToInteger(step);
                    if(rowId != null) {
                        field = null;
                        for(DataRow row : container.getRows()) {
                            if(row.getRowId().equals(rowId)) {
                                // Found correct row, break.
                                field = row;
                                break;
                            }
                        }
                        if(field == null) {
                            // No correct row found, path terminates here, return null.
                            return null;
                        }
                    } else {
                        // Path is invalid, we should find a row but step is not an integer.
                        return null;
                    }
                } else if(field instanceof DataRow) {
                    DataRow row = (DataRow)field;
                    field = row.getField(step);
                    if(field == null) {
                        // Path terminates here, no matter if there are more steps. Return null.
                        return null;
                    }
                }
                // TODO: ReferenceContainerDataField
            }
        }
        return field;*/
        return null;
    }
}
