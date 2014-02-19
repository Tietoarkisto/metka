package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSingleSO extends SimpleObject {
    private Integer seriesno;
    private Integer revision;
    private String seriesabb;
    private String seriesname;
    private String seriesdesc;
    private String seriesnotes;
    private RevisionState state;

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

    // Field values


    public Integer getSeriesno() {
        return seriesno;
    }

    public void setSeriesno(Integer seriesno) {
        this.seriesno = seriesno;
    }

    public String getSeriesabb() {
        return seriesabb;
    }

    public void setSeriesabb(String seriesabb) {
        this.seriesabb = seriesabb;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getSeriesdesc() {
        return seriesdesc;
    }

    public void setSeriesdesc(String seriesdesc) {
        this.seriesdesc = seriesdesc;
    }

    public String getSeriesnotes() {
        return seriesnotes;
    }

    public void setSeriesnotes(String seriesnotes) {
        this.seriesnotes = seriesnotes;
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
        switch(SeriesValues.fromString(key)) {
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
        return null;
    }
}
