package fi.uta.fsd.metka.mvc.domain.simple.transfer;

public class SearchResult extends TransferObject {

    /**
     * Used to export search results as csv-files.
     * @param keys - Order of the values in the row. Must contain only configuration keys found in this SimpleSearchObject
     *             otherwise an IllegalArgumentException is thrown.
     * @return
     * @throws IllegalArgumentException - If keys contains a key not found in this object
     */
    // TODO: Remake using current model
    public String toCSVRow(String[] keys) throws IllegalArgumentException {
        StringBuilder csvBuilder = new StringBuilder();
        boolean first = true;
        for(String key : keys) {
            csvBuilder.append(getByKey(key));
            if(first) {
                first = false;
            } else {
                csvBuilder.append(";"); // TODO: configure the separator character for csv somewhere.
            }
        }
        return csvBuilder.toString();
    }

    // TODO: Make enum containing only valid fields for each type of search result and override setByKey and getByKey to check that given value is in that enum
}
