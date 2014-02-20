package fi.uta.fsd.metka.mvc.domain.simple.study;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/6/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesReference {
    private Integer id;
    private String name;

    public SeriesReference(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeriesReference that = (SeriesReference) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
