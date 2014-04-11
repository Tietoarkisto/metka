package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.VariableDataType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import org.joda.time.LocalDateTime;
import spssio.por.PORFile;
import spssio.por.PORValue;
import spssio.por.PORValueLabels;
import spssio.por.input.PORReader;

import java.io.IOException;
import java.util.*;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Used exclusively to parse study variable data.
 * Uses different package private utility classes to do the parsing.
 * Merges parsed data with given RevisionData object
 */
public class StudyVariablesParser {
    public static boolean merge(RevisionData data, VariableDataType type, String path, Configuration config) throws IOException {
        if(type == null) {
            return false;
        }
        switch(type) {
            case POR:
                // Read POR file
                PORReader reader = new PORReader();
                PORFile por = reader.parse(path);

                // Group variables to list
                List<PORUtil.PORVariableHolder> variables = new ArrayList<>();
                PORUtil.groupVariables(variables, por.variables, por.labels);

                // Group answers under variables list
                PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
                por.data.accept(visitor);

                // Make VariablesHandler
                VariablesHandler handler = new VariablesHandler(data, config);

                // Iterate through variables merging data to variables container
                for(PORUtil.PORVariableHolder variable : variables) {
                    handler.mergeToData(variable);
                }
                return true;
            default:
                return false;
        }
    }

    private static class VariablesHandler {
        private ContainerDataField container = null;
        private RevisionData data;
        private Configuration config;

        private LocalDateTime time;
        private DataRow currentRow = null;

        VariablesHandler(RevisionData data, Configuration config) {

            this.data = data;
            this.config = config;
            this.time = new LocalDateTime();

            // Get variables container
            // TODO: Either this has to be known and hardcoded or else we need some kind of standard mapping for where these values are found on a STUDY
            container = getContainerDataFieldFromRevisionData(data, "variables", this.config);
            // If container wasn't already present make it now, since we are parsing a por-file we can assume that there will be content
            // Since we are returning true from merge-method the new container will get saved to database
            if(container == null) {
                container = new ContainerDataField("variables");
                data.putField(container);
            }
        }

        /**
         * Merge given variable to the data in this handler.
         * @param variable To be merged
         */
        void mergeToData(PORUtil.PORVariableHolder variable) {
            // Start merge process
            start(variable.asVariable().getName());
            // Set varid field using setRowField method
            setRowField(currentRow, "varid", variable.asVariable().getName());
            // Set label field using setRowField method
            setRowField(currentRow, "varlabel", variable.asVariable().label);
            // Set categories CONTAINER
            setCategories(variable);
            // Set statistics CONTAINER
            setStatistics(variable);
        }

        /**
         * Makes this handler ready for merging variable with the given name.
         * Gets a row from ContainerDataField for given variable name or if the row doesn't exist creates it.
         * @param name Variable name
         */
        private void start(String name) {
            currentRow = findRowWithFieldValue(container.getRows(), "varname", name);

            // Variable doesn't exist in container, create row
            if(currentRow == null) {
                currentRow = new DataRow(container.getKey(), data.getNewRowId());
                container.getRows().add(currentRow);
                // Set varname using setRowField
                setRowField(currentRow, "varname", name);
            }
        }

        /**
         * Merge categories container to current row.
         * Creates all missing fields that are needed but uses existing ones if present.
         *
         * @param variable Current variable
         */
        private void setCategories(PORUtil.PORVariableHolder variable) {
            ContainerDataField categories = (ContainerDataField)currentRow.getField("categories");
            if(variable.getLabels().size() == 0) {
                if(categories != null) {
                    // Remove all category fields if present by setting their modified value's value to null and adding change
                }
                return;
            }
            // Check to see if we need to initialise categories container
            if(categories == null) {
                categories = new ContainerDataField("categories");
                currentRow.putField(categories);
            }

            List<PORUtil.PORVariableData> variableData = variable.getData();
            Map<String, Integer> map = new HashMap<>();
            groupAnswers(variableData, map);
            for(PORValueLabels vls : variable.getLabels()) {
                for(PORValue v : vls.mappings.keySet()) {
                    DataRow row = findRowWithFieldValue(categories.getRows(), "categoryvalue", v.value);
                    if(row == null) {
                        row = new DataRow(categories.getKey(), data.getNewRowId());
                        categories.getRows().add(row);
                    }
                    setRowField(row, "categoryvalue", v.value);
                    setRowField(row, "categorylabel", vls.mappings.get(v));
                    setRowField(row, "categorystat", (map.containsKey(v.value) ? map.get(v.value).toString() : "0"));
                }
            }
        }

        /**
         * Group answers in data based on their value.
         * @param variableData Data to be grouped
         * @param map Target map for grouping
         */
        private void groupAnswers(List<PORUtil.PORVariableData> variableData, Map<String, Integer> map) {
            for(PORUtil.PORVariableData d : variableData) {
                String key = d.toString();

                if(!map.containsKey(key)) {
                    map.put(key, 0);
                }
                map.put(key, map.get(key)+1);
            }
        }

        private void setStatistics(PORUtil.PORVariableHolder variable) {
            // Get statistics container
            ContainerDataField statistics = (ContainerDataField)currentRow.getField("statistics");
            if(statistics == null) {
                statistics = new ContainerDataField("statistics");
                currentRow.putField(statistics);
            }

            List<PORUtil.PORVariableData> variableData = variable.getNumericalDataWithoutMissing();
            DataRow row;
            String type;
            // Set vald
            Integer variables = variableData.size();
            type = "vald";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", variables.toString());

            // Set min
            if(variables != null && variables > 0) {
                String min = Collections.min(variableData, new PORUtil.PORVariableDataComparator()).toString();
                type = "min";
                row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
                if(row == null) {
                    row = new DataRow(statistics.getKey(), data.getNewRowId());
                    statistics.getRows().add(row);
                    setRowField(row, "statisticstype", type);
                }
                setRowField(row, "statisticsvalue", min);
            }

            // Set max
            if(variables != null && variables > 0) {
                String max = Collections.max(variableData, new PORUtil.PORVariableDataComparator()).toString();
                type = "max";
                row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
                if(row == null) {
                    row = new DataRow(statistics.getKey(), data.getNewRowId());
                    statistics.getRows().add(row);
                    setRowField(row, "statisticstype", type);
                }
                setRowField(row, "statisticsvalue", max);
            }

            // Set mean
            Double mean = 0D;
            Integer meanDenom = 0;
            for(PORUtil.PORVariableData varD : variableData) {
                if(varD instanceof PORUtil.PORVariableDataDouble) {
                    mean += ((PORUtil.PORVariableDataDouble)varD).getValue();
                    meanDenom++;
                } else if(varD instanceof PORUtil.PORVariableDataInt) {
                    mean += ((PORUtil.PORVariableDataInt)varD).getValue();
                    meanDenom++;
                } else if(varD instanceof PORUtil.PORVariableDataString) {
                    mean += Double.parseDouble(((PORUtil.PORVariableDataString)varD).getValue());
                    meanDenom++;
                }
            }
            mean = mean / meanDenom;
            type = "mean";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", mean.toString());

            // Set stdev
            Double deviation = 0D;
            for(PORUtil.PORVariableData varD : variableData) {
                Double value = null;
                if(varD instanceof PORUtil.PORVariableDataDouble) {
                    value = ((PORUtil.PORVariableDataDouble)varD).getValue();
                } else if(varD instanceof PORUtil.PORVariableDataInt) {
                    value = ((PORUtil.PORVariableDataInt)varD).getValue()*1.0;
                } else if(varD instanceof PORUtil.PORVariableDataString) {
                    value = Double.parseDouble(((PORUtil.PORVariableDataString)varD).getValue());
                }
                if(value != null) {
                    deviation += Math.pow((value - mean), 2);
                } else {
                    meanDenom--;
                }
            }
            deviation = Math.sqrt(deviation/meanDenom);

            type = "stdev";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", deviation.toString());
        }

        /**
         * Searches through a list of rows for a row containing given value in a field with given id.
         *
         * @param rows List of rows to search through
         * @param key Field key of field where value should be found
         * @param value Value that is searched for
         * @return DataRow that contains given value in requested field, or null if not found.
         */
        private DataRow findRowWithFieldValue(List<DataRow> rows, String key, String value) {
            for(DataRow row : rows) {
                SavedDataField field = (SavedDataField)row.getField(key);
                if(field != null && field.hasValue() && field.valueEquals(value)) {
                    return row;
                }
            }
            return null;
        }

        /**
         * Sets a DataRow field with given key to value provided.
         * If field does not exist then creates the field.
         *
         * TODO: Provide with ChangeContainer
         *
         * @param row Row to be modified
         * @param key Field key
         * @param value Value to be inserted in field
         */
        private void setRowField(DataRow row, String key, String value) {
            SavedDataField field = (SavedDataField)row.getField(key);
            if(field == null) {
                field = new SavedDataField(key);
                row.putField(field);
            }
            if(!field.hasValue() || !field.valueEquals(value)) {
                field.setModifiedValue(setSimpleValue(createSavedValue(time), value));
                row.setSavedAt(time);
                // TODO: set savedBy
                // TODO: set change
            }
        }
    }
}