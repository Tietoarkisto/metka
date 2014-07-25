package fi.uta.fsd.metka.mvc.services.simple;

public abstract class SimpleSearchObject extends SimpleObject {

    private Long id;
    private Integer revision;
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

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
