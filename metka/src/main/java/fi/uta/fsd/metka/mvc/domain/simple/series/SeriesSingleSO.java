package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.data.enums.RevisionState;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSingleSO {
    private Integer id;
    private String abbreviation;
    private String name;
    private String description;
    private RevisionState state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }
}
