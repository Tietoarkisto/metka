package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSingleSO extends SimpleObject {
    private Integer revision;
    private ConfigurationKey configuration;
    private RevisionState state;

    /*private Integer seriesno;
    private String seriesabb;
    private String seriesname;
    private String seriesdesc;
    private String seriesnotes;*/

    private final Map<String, Object> values = new HashMap<>();

    // External values
    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationKey configuration) {
        this.configuration = configuration;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series simple object: [");
        for(int i = 0; i < SeriesValues.values().length; i++) {
            SeriesValues v = SeriesValues.values()[i];
            sb.append(v.getKey()+": ");
            sb.append(getByKey(v.getKey()));
            if(i < SeriesValues.values().length-1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object getByKey(String key) throws IllegalArgumentException {
        return values.get(key);
        /*switch(SeriesValues.fromString(key)) {
            case SERIESNO:
                return seriesno;
            case SERIESABB:
                return seriesabb;
            case SERIESNAME:
                return seriesname;
            case SERIESDESC:
                return seriesdesc;
            case SERIESNOTES:
                return seriesnotes;
        }
        return null;*/
    }

    public void setByKey(String key, Object value) {
        values.put(key, value);
    }
}
