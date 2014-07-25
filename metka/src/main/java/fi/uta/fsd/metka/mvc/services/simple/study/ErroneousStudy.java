package fi.uta.fsd.metka.mvc.services.simple.study;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/7/14
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ErroneousStudy {
    private String study_number;
    private String study_name;
    private Integer point_count;

    public String getStudy_number() {
        return study_number;
    }

    public void setStudy_number(String study_number) {
        this.study_number = study_number;
    }

    public String getStudy_name() {
        return study_name;
    }

    public void setStudy_name(String study_name) {
        this.study_name = study_name;
    }

    public Integer getPoint_count() {
        return point_count;
    }

    public void setPoint_count(Integer point_count) {
        this.point_count = point_count;
    }
}
