package fi.uta.fsd.metka.mvc.domain.simple.series;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSearchSO {
    private Integer id;
    private String name;
    private String abbreviation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return "Series search simple object: [id: "+id+", name: "+name+", abbreviation: "+abbreviation+"]";
    }
}
