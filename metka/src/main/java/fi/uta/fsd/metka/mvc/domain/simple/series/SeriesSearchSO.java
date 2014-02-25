package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleSearchObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSearchSO extends SimpleSearchObject {
    private boolean searchApproved = true;
    private boolean searchDraft = true;
    private boolean searchRemoved;

    private final Map<String, Object> values = new HashMap<>();

   // External values
    public boolean isSearchApproved() {
        return searchApproved;
    }

    public void setSearchApproved(boolean searchApproved) {
        this.searchApproved = searchApproved;
    }

    public boolean isSearchDraft() {
        return searchDraft;
    }

    public void setSearchDraft(boolean searchDraft) {
        this.searchDraft = searchDraft;
    }

    public boolean isSearchRemoved() {
        return searchRemoved;
    }

    public void setSearchRemoved(boolean searchRemoved) {
        this.searchRemoved = searchRemoved;
    }

    // Field values


    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series search object: [");
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
        }
        return null;*/
    }
}
