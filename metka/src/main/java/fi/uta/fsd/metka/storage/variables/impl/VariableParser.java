package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.*;

import static fi.uta.fsd.metka.storage.variables.impl.StudyVariablesParserImpl.checkResultForUpdate;
import static fi.uta.fsd.metka.storage.variables.impl.StudyVariablesParserImpl.resultCheck;

/**
 * Builds variables.
 * Doesn't require transaction as such but should be used within one and with EntityManager
 * so that possible changes can be persisted after they are made.
 * Should only work with RevisionData for StudyVariableEntity revisions.
 *
 * Since there is no configuration for variable parsing we don't require STUDY_VARIABLE configuration
 * here. If something changes in the configuration then manual changes need to be made here in any case.
 */
class VariableParser {
    private final DateTimeUserPair info;
    private final Language language;

    private static final Map<Language, String> missingLabel = new HashMap<>();
    static {
        missingLabel.put(Language.DEFAULT, "[Muuttujalta puuttuu LABEL tieto]");
        missingLabel.put(Language.EN, "[Variable is missing LABEL]");
        missingLabel.put(Language.SV, "[Variable is missing LABEL]");
    }

    VariableParser(DateTimeUserPair info, Language language) {
        this.info = info;
        this.language = language;
    }

    String getVarName(PORUtil.PORVariableHolder variable) {
        if(variable == null) {
            return null;
        }
        return variable.asVariable().getName();
    }

    /**
     * Handles one PORVariableHolder inserting all relevant information into given variable revision.
     * Handles only fields that are not user editable and so doesn't need to care about not overwriting
     * existing data.
     *
     * @param variableRevision Variable revision where data is inserted
     * @param variable PORVariableHolder containing singe variable data to be parsed
     */
    ParseResult mergeToData(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
        // Sanity check
        if(variableRevision == null || variable == null) {
            return ParseResult.NO_CHANGES;
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // Set varlabel field
        String label = !StringUtils.hasText(variable.asVariable().label)
                ? missingLabel.get(language)
                : variable.asVariable().label;
        Pair<StatusCode, ValueDataField> fieldPair = variableRevision.dataField(ValueDataFieldCall.set(Fields.VARLABEL, new Value(label), language).setInfo(info));
        result = checkResultForUpdate(fieldPair, result);

        // TODO: Copy varlabel content to a row in qustnlits if there's not already rows in there
        /*Pair<StatusCode, ContainerDataField> qstns = variableRevision.dataField(ContainerDataFieldCall.set("qstnlits"));
        checkResultForUpdate(qstns, result);

        if(!qstns.getRight().hasRowsFor(DEFAULT)) {
            Pair<StatusCode, DataRow> row = qstns.getRight().insertNewDataRow(DEFAULT, variable);
        }*/

        // Set valuelabels CONTAINER
        ParseResult operationResult = setValueLabels(variableRevision, variable);
        result = resultCheck(result, operationResult);
        // Set categories CONTAINER
        operationResult = setCategories(variableRevision, variable);
        result = resultCheck(result, operationResult);
        // Set interval
        operationResult = setInterval(variableRevision, variable);
        result = resultCheck(result, operationResult);
        // Set statistics CONTAINER
        operationResult = setStatistics(variableRevision, variable);
        result = resultCheck(result, operationResult);

        if(result == ParseResult.REVISION_CHANGES) {
            // We can do this as a set operation since container field set returns the existing container if one is present and doesn't do anything else in that case
            Pair<StatusCode, ContainerDataField> containerPair = variableRevision.dataField(ContainerDataFieldCall.set(Fields.TRANSLATIONS, variableRevision));
            ContainerDataField container = containerPair.getRight();
            Pair<StatusCode, DataRow> pair = container.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.TRANSLATION, new Value(language.toValue()), variableRevision.getChanges(), info);
        }

        return result;
    }

    /**
     * Merge value labels data to given variable revision.
     * Creates missing fields as needed, uses existing ones if present.
     *
     * @param variableRevision Variable revision to merge variable data to.
     * @param variable Current variable
     */
    private ParseResult setValueLabels(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
        ContainerDataField valueLabels = variableRevision.dataField(ContainerDataFieldCall.get(Fields.VALUELABELS)).getRight();

        if(variable.getLabelsSize() == 0 && valueLabels == null) {
            // No labels and no old container. Nothing needs to be done
            return ParseResult.NO_CHANGES;
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // Check to see if we need to initialise valueLabels container
        // There has to be labels to warrant a valueLabels container creation if it's not present already
        if(variable.getLabelsSize() > 0 && valueLabels == null) {
            valueLabels = variableRevision.dataField(ContainerDataFieldCall.set(Fields.VALUELABELS)).getRight();
            result = ParseResult.REVISION_CHANGES;
        }

        // Gather existing value labels to a separate list to allow for obsolete row checking and preserving por-file defined order
        List<DataRow> rows = gatherAndClear(valueLabels, Language.DEFAULT);

        // Add container rows
        for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
            DataRow row = popOrCreateAndInsertRowTo(Language.DEFAULT, valueLabels, rows, Fields.VALUE, label.getValue(), variableRevision.getChanges(), Language.DEFAULT);
            result = resultCheck(result, setValueLabelRow(row, label, variableRevision.getChanges()));
        }

        result = resultCheck(result, removeObsoleteRowsFrom(Language.DEFAULT, rows, valueLabels, variableRevision.getChanges(), info));

        return result;
    }

    private ParseResult setValueLabelRow(DataRow row, PORUtil.PORVariableValueLabel label, Map<String, Change> changeMap) {
        ParseResult result = ParseResult.NO_CHANGES;

        // We know that this row is needed and so we can set it to not removed state no matter if it was removed previously or not
        StatusCode restoreResult = row.restore(changeMap, language, info);
        if(restoreResult == StatusCode.ROW_CHANGE) {
            result = ParseResult.REVISION_CHANGES;
        }

        Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                ValueDataFieldCall.set(Fields.VALUE, new Value(label.getValue()), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        fieldPair = row.dataField(ValueDataFieldCall.set(Fields.LABEL, new Value(label.getLabel()), language).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        fieldPair = row.dataField(ValueDataFieldCall.set(Fields.MISSING, new Value(label.isMissing() ? "Y" : null), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        return result;
    }

    /**
     * Merge categories container to given variable revision.
     * Creates all missing fields that are needed but uses existing ones if present.
     * Checks the need of frequency statistics and calculates them as needed based on multiple criteria.
     * Frequency statistics and value labels are not directly linked but instead if a value happens to match a value label
     * then the label is printed as part of the statistics.
     *
     * @param variableRevision Variable revision to merge variable data to.
     * @param variable Current variable
     */
    private ParseResult setCategories(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
        boolean getValidFreq = false;
        for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
            if(!label.isMissing()) {
                getValidFreq = true;
                break;
            }
        }

        // Calculate frequencies
        Map<PORUtil.PORVariableData, Integer> valid = new HashMap<>();
        Map<PORUtil.PORVariableData, Integer> missing = new HashMap<>();
        Integer sysmiss = 0;

        for(PORUtil.PORVariableData value : variable.getData()) {
            switch(value.getType()) {
                case SYSMISS:
                    sysmiss++;
                    break;
                case STRING:
                    // For string checking the variable must be string typed and have labels. Strings without labels won't be frequency checked
                    if(variable.isNumeric() || variable.getLabelsSize() == 0) {
                        break;
                    }

                    PORUtil.PORVariableDataString s = (PORUtil.PORVariableDataString)value;
                    // Check frequency only for string value that matches a label
                    if(variable.getLabel(s.getValue()) == null) {
                        break;
                    }

                    // If value is an user missing value add it to missing values frequency map, otherwise add it to valid frequencies map if valid frequencies are needed
                    if(variable.isUserMissing(s.getValue())) {
                        increaseFrequencyValue(value, missing);
                    } else if(getValidFreq) {
                        increaseFrequencyValue(value, valid);
                    }
                    break;
                case NUMERIC:
                    // For numeric frequency checking the variable must be numeric and contain either labels or/and missing values
                    if(!variable.isNumeric() || (variable.getLabelsSize() == 0 && variable.getMissingSize() == 0)) {
                        break;
                    }
                    PORUtil.PORVariableDataNumeric n = (PORUtil.PORVariableDataNumeric)value;
                    // If value is an user missing value add it to missing values frequency map, otherwise add it to valid frequencies map if valid frequencies are needed
                    if(variable.isUserMissing(n.getValue())) {
                        increaseFrequencyValue(value, missing);
                    } else if(getValidFreq) {
                        increaseFrequencyValue(value, valid);
                    }
                    break;
            }
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // Get categories container
        ContainerDataField categories = variableRevision.dataField(ContainerDataFieldCall.get(Fields.CATEGORIES)).getRight();
        // Get changeMap reference since it's used multiple times
        Map<String, Change> changeMap = variableRevision.getChanges();

        if(valid.size() == 0 && missing.size() == 0) {
            // No frequencies, remove all frequency rows and return
            if(categories != null) {
                for(Integer id : categories.getRowIdsFor(Language.DEFAULT)) {
                    StatusCode removeResult = categories.removeRow(id, changeMap, info).getLeft();
                    if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                        result = ParseResult.REVISION_CHANGES;
                    }
                }
            }
            return result;
        }

        // At least some frequencies, check that we have a container
        if(categories == null) {
            categories = variableRevision.dataField(ContainerDataFieldCall.set(Fields.CATEGORIES)).getRight();
            result = ParseResult.REVISION_CHANGES;
        }

        // Gather all old rows
        List<DataRow> rows = gatherAndClear(categories, Language.DEFAULT);

        // Add valid frequencies
        result = resultCheck(result, setFrequencies(variableRevision, variable, categories, valid, changeMap, rows, false));

        // Add missing frequencies
        result = resultCheck(result, setFrequencies(variableRevision, variable, categories, missing, changeMap, rows, true));

        // Add SYSMISS if they exist and there are other frequencies, otherwise remove possible existing SYSMISS related inserts
        DataRow sysmissRow = popRowWithFieldValue(rows, Fields.VALUE, "SYSMISS", Language.DEFAULT);
        if((valid.size() > 0 || missing.size() > 0) && sysmiss > 0) {
            if(sysmissRow == null) {
                sysmissRow = DataRow.build(categories);
            }
            categories.addRow(Language.DEFAULT, sysmissRow);

            result = resultCheck(result, setCategoryRow(sysmissRow, "SYSMISS", "SYSMISS", sysmiss, true, changeMap));
        } else if(sysmissRow != null) {
            // SYSMISS row is not needed but existed previously, mark as removed
            categories.addRow(Language.DEFAULT, sysmissRow); // Insert it back to to categories container before removal or the change doesn't make any sense
            StatusCode removeResult = categories.removeRow(sysmissRow.getRowId(), changeMap, info).getLeft();
            if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                result = resultCheck(result, ParseResult.REVISION_CHANGES);
            }
        }

        return result;
    }

    /**
     * Helper function for category checking.
     * Increases an integer value in a map based on a given key. If key doesn't exist previously then puts it to map with value 1
     * @param key Frequency map key
     * @param map Frequency map
     */
    private void increaseFrequencyValue(PORUtil.PORVariableData key, Map<PORUtil.PORVariableData, Integer> map) {
        if(map.containsKey(key)) {
            map.put(key, map.get(key)+1);
        } else {
            map.put(key, 1);
        }
    }

    /**
     * Helper method for grouping and processing value frequencies.
     *
     * @param revision RevisionData of variableRevision, needed for row index
     * @param variable Current variable, needed for value labels
     * @param target Container where rows are inserted
     * @param frequencies New frequency values
     * @param changeMap Change map that changes to target are under.
     * @param rows List of DataRows containing old frequencies
     * @param missing Are values in rows to be considered missing values
     */
    private ParseResult setFrequencies(RevisionData revision, PORUtil.PORVariableHolder variable, ContainerDataField target,
                                Map<PORUtil.PORVariableData, Integer> frequencies, Map<String, Change> changeMap,
                                List<DataRow> rows, boolean missing) {
        // Add frequencies to target, frequencies map will be empty if frequencies of this type are not required so this step can be passed in that case.
        if(frequencies.size() == 0) {
            return ParseResult.NO_CHANGES;
        }

        ParseResult result = ParseResult.NO_CHANGES;

        List<PORUtil.PORVariableData> sortedKeys = new ArrayList<>(frequencies.keySet());
        Collections.sort(sortedKeys, new PORUtil.PORVariableDataComparator());
        for(PORUtil.PORVariableData value : sortedKeys) {
            DataRow row = popOrCreateAndInsertRowTo(Language.DEFAULT, target, rows, Fields.VALUE, value.toString(), revision.getChanges(), Language.DEFAULT);
            PORUtil.PORVariableValueLabel label = variable.getLabel(value.toString());
            Integer freq = frequencies.get(value);

            result = resultCheck(result, setCategoryRow(row, value.toString(), (label != null) ? label.getLabel() : null, freq, missing, changeMap));
        }
        return result;
    }

    /**
     * Helper function for setting category row values
     * @param row Row where the values are set
     * @param value Category value for the row
     * @param label Possible label for the row, can be null
     * @param stat Frequency statistic for the row
     * @param missing Is the category a missing category or not
     * @param changeMap Where changes are logged
     */
    private ParseResult setCategoryRow(DataRow row, String value, String label, Integer stat, boolean missing, Map<String, Change> changeMap) {
        ParseResult result = ParseResult.NO_CHANGES;

        StatusCode restoreResult = row.restore(changeMap, Language.DEFAULT, info);
        if(restoreResult == StatusCode.ROW_CHANGE) {
            result = ParseResult.REVISION_CHANGES;
        }
        Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                ValueDataFieldCall.set(Fields.VALUE, new Value(value), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        fieldPair = row.dataField(ValueDataFieldCall.set(Fields.LABEL, new Value(label), language).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        fieldPair = row.dataField(ValueDataFieldCall.set(Fields.STAT, new Value(stat.toString()), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        fieldPair = row.dataField(ValueDataFieldCall.set(Fields.MISSING, new Value(missing ? "Y" : null), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        return result;
    }

    /**
     * Set varinterval attribute for given variable revision.
     *
     * @param variableRevision Variable revision requiring varinterval information
     * @param variable Variable used to determine varinterval value
     */
    private ParseResult setInterval(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
        // Start from the assumption that the variable is continuous and work from there
        boolean continuous = true;

        if(!variable.isNumeric()) {
            continuous = false;
        }

        if(continuous && variable.getLabelsSize() > 0) {
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                if(!label.isMissing()) {
                    continuous = false;
                    break;
                }
            }
        }

        return checkResultForUpdate(
                variableRevision.dataField(ValueDataFieldCall
                        .set(Fields.VARINTERVAL, new Value(continuous ? "contin" : "discrete"), Language.DEFAULT)
                        .setInfo(info)),
                ParseResult.NO_CHANGES);
    }

    /**
     * Sets statistics for single variable (min, max etc.)
     * Statistics are calculated only for numerical variables and all statistics are calculated for all numerical variables.
     * SYSMISS and user missing values are not valid values for statistics
     *
     * @param variableRevision Variable revision to merge variable data to.
     * @param variable Current variable
     */
    private ParseResult setStatistics(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
        // Get statistics container
        ContainerDataField statistics = variableRevision.dataField(ContainerDataFieldCall.get("statistics")).getRight();
        // Get changeMap
        Map<String, Change> changeMap = variableRevision.getChanges();

        ParseResult result = ParseResult.NO_CHANGES;

        if(!variable.isNumeric()) {
            // Variable is not numeric and should not contain statistics.
            if(statistics != null) {
                for(Integer id : statistics.getRowIdsFor(Language.DEFAULT)) {
                    StatusCode removeResult = statistics.removeRow(id, changeMap, info).getLeft();
                    if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                        result = ParseResult.REVISION_CHANGES;
                    }
                }
            }
            return result;
        }

        // Get valid numerical data as base for calculating statistics
        List<PORUtil.PORVariableDataNumeric> data = variable.getValidNumericalData();

        // This variable should have statistics, create if missing
        if(statistics == null) {
            statistics = variableRevision.dataField(ContainerDataFieldCall.set("statistics")).getRight();
            result = ParseResult.REVISION_CHANGES;
        }

        List<DataRow> rows = gatherAndClear(statistics, Language.DEFAULT);

        // These variables are needed multiple times so define them separately here
        DataRow row;
        String type;
        // amount of valid values is used multiple times so it makes sense to lift it as a separate value
        Integer values = data.size();

        // Set vald
        type = "vald"; // Valid values statistic
        row = popOrCreateAndInsertRowTo(Language.DEFAULT, statistics, rows, Fields.STATISTICSTYPE, type, variableRevision.getChanges(), Language.DEFAULT);
        Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                ValueDataFieldCall.set(Fields.STATISTICSVALUE, new Value(values.toString()), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
        checkResultForUpdate(fieldPair, result);

        // Set min
        type = "min";
        if(values > 0) {
            row = popOrCreateAndInsertRowTo(Language.DEFAULT, statistics, rows, Fields.STATISTICSTYPE, type, variableRevision.getChanges(), Language.DEFAULT);
            String min = Collections.min(data, new PORUtil.PORNumericVariableDataComparator()).toString();
            fieldPair = row.dataField(ValueDataFieldCall.set(Fields.STATISTICSVALUE, new Value(min), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);
        }

        // Set max
        type = "max";
        if(values > 0) {
            row = popOrCreateAndInsertRowTo(Language.DEFAULT, statistics, rows, Fields.STATISTICSTYPE, type, variableRevision.getChanges(), Language.DEFAULT);
            String max = Collections.max(data, new PORUtil.PORNumericVariableDataComparator()).toString();
            fieldPair = row.dataField(ValueDataFieldCall.set(Fields.STATISTICSVALUE, new Value(max), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);
        }

        // Set mean
        type = "mean";
        Double mean = 0D;
        // If there are no values or variable is continuous don't add mean
        if(values > 0) {
            row = popOrCreateAndInsertRowTo(Language.DEFAULT, statistics, rows, Fields.STATISTICSTYPE, type, variableRevision.getChanges(), Language.DEFAULT);
            Integer denom = 0;
            for(PORUtil.PORVariableDataNumeric varD : data) {
                mean += varD.getValue();
                denom++;
            }
            mean = mean / denom;
            fieldPair = row.dataField(ValueDataFieldCall.set(Fields.STATISTICSVALUE, new Value(mean.toString()), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);
        }

        // Set stdev
        type = "stdev";
        // If there are no values or variable is continuous don't add deviation
        if(values > 0) {
            row = popOrCreateAndInsertRowTo(Language.DEFAULT, statistics, rows, Fields.STATISTICSTYPE, type, variableRevision.getChanges(), Language.DEFAULT);
            Double deviation = 0D;
            Integer denom = 0;
            for(PORUtil.PORVariableDataNumeric varD : data) {
                Double value = varD.getValue();
                if(value != null) {
                    deviation += Math.pow((value - mean), 2);
                    denom++;
                }
            }
            deviation = Math.sqrt(deviation/denom);
            fieldPair = row.dataField(ValueDataFieldCall.set(Fields.STATISTICSVALUE, new Value(deviation.toString()), Language.DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);
        }

        result = resultCheck(result, removeObsoleteRowsFrom(Language.DEFAULT, rows, statistics, changeMap, info));

        return result;
    }

    /**
     * Helper method for handling and organising container rows.
     * Takes a collection of rows, finds a row based on a field and removes it from the given
     * collection.
     * Assumption is that the collection is not the actual rows list of a ContainerDataField
     * but some other collection used for organising rows during operations.
     *
     * @param rows Collection of rows to search through
     * @param key Field key of the field where the value should be found
     * @param value Value to be searched for, should be non empty string
     * @return First DataRow to match the given value, null if no row was found
     */
    private DataRow popRowWithFieldValue(Collection<DataRow> rows, String key, String value, Language fieldLangage) {
        for(Iterator<DataRow> i = rows.iterator(); i.hasNext(); ) {
            DataRow row = i.next();
            Pair<StatusCode, ValueDataField> field = row.dataField(ValueDataFieldCall.get(key));
            if(field.getLeft() == StatusCode.FIELD_FOUND && field.getRight().valueForEquals(fieldLangage, value)) {
                i.remove();
                return row;
            }
        }
        return null;
    }

    /**
     * Helper method for handling and organising container rows.
     * Searches given collection for a row with given value in given field.
     * If row was not found then creates a new row and inserts it into provided container.
     * No change handling is necessary since some set operation should follow always after
     * calling this method.
     *
     * @param target Target container where the row will be set
     * @param rows Collection of rows to search through for correct existing row
     * @param key Field key of the field where given value should be
     * @param value Value to search for
     * @param changeMap Map where this rows containers should reside
     * @return Either an existing or newly created DataRow that has been inserted to the given container already
     */
    private DataRow popOrCreateAndInsertRowTo(Language rowLanguage, ContainerDataField target, Collection<DataRow> rows, String key, String value, Map<String, Change> changeMap, Language fieldLanguage) {
        DataRow row = popRowWithFieldValue(rows, key, value, fieldLanguage);
        if(row == null) {
            row = DataRow.build(target);
            row.dataField(ValueDataFieldCall.set(key, new Value(value), fieldLanguage).setChangeMap(changeMap));
        }
        target.addRow(rowLanguage, row);
        return row;
    }

    /**
     * Removes obsolete rows by placing them in given container and then running them through remove method
     * @param rows Collection of rows that are obsolete
     * @param target Container where removed rows are added
     * @param changeMap Change map where target containers changes should be
     */
    private static ParseResult removeObsoleteRowsFrom(Language language, Collection<DataRow> rows, ContainerDataField target, Map<String, Change> changeMap, DateTimeUserPair info) {
        ParseResult result = ParseResult.NO_CHANGES;
        for(DataRow row : rows) {
            target.addRow(language, row);
            StatusCode status = target.removeRow(row.getRowId(), changeMap, info).getLeft();
            if(status == StatusCode.ROW_CHANGE || status == StatusCode.ROW_REMOVED) {
                result = ParseResult.REVISION_CHANGES;
            }
        }
        return result;
    }

    private static List<DataRow> gatherAndClear(ContainerDataField field, Language language) {
        if(field.getRowsFor(language) == null) {
            return new ArrayList<>();
        }
        List<DataRow> rows = new ArrayList<>(field.getRowsFor(language));
        field.getRowsFor(language).clear();
        return rows;
    }
}
