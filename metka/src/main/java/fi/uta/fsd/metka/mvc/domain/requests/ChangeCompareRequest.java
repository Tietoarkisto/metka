package fi.uta.fsd.metka.mvc.domain.requests;

import fi.uta.fsd.metka.data.enums.ConfigurationType;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/17/14
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeCompareRequest {
    private Integer id;
    private Integer begin;
    private Integer end;
    private ConfigurationType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeCompareRequest that = (ChangeCompareRequest) o;

        if (!begin.equals(that.begin)) return false;
        if (!end.equals(that.end)) return false;
        if (!id.equals(that.id)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + begin.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
