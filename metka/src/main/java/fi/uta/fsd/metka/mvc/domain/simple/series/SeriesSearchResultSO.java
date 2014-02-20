package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleSearchObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/30/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSearchResultSO extends SimpleSearchObject {
    private Integer revision;
    private String state;
    private Integer seriesno;
    private String seriesname;
    private String seriesabb;


    // External values
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

    // Field values


    public Integer getSeriesno() {
        return seriesno;
    }

    public void setSeriesno(Integer seriesno) {
        this.seriesno = seriesno;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getSeriesabb() {
        return seriesabb;
    }

    public void setSeriesabb(String seriesabb) {
        this.seriesabb = seriesabb;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series search result object: [");
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
        switch(SeriesValues.fromString(key)) {
            case SERIESNO:
                return seriesno;
            case SERIESABB:
                return seriesabb;
            case SERIESNAME:
                return seriesname;
        }
        return null;
    }
}
