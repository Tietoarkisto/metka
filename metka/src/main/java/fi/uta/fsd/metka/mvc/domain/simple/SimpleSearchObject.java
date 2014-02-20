package fi.uta.fsd.metka.mvc.domain.simple;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/21/14
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleSearchObject extends SimpleObject {
    /**
     * Used to export search results as csv-files.
     * @param keys - Order of the values in the row. Must contain only configuration keys found in this SimpleSearchObject
     *             otherwise an IllegalArgumentException is thrown.
     * @return
     * @throws IllegalArgumentException - If keys contains a key not found in this object
     */
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
}
