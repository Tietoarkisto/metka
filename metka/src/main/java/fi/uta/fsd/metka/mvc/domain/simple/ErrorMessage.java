package fi.uta.fsd.metka.mvc.domain.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/24/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorMessage {


    private String title;
    private String msg;
    private List<String> data = new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getData() {
        return data;
    }

    // Static factory methods
    public static ErrorMessage noResults(String type) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.search.noResult");
        error.getData().add("general.errors.search.noResult."+type);

        return error;
    }

    public static ErrorMessage saveSuccess() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.save.success");

        return error;
    }

    public static ErrorMessage saveFail() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.save.fail");

        return error;
    }

    public static ErrorMessage approveSuccess() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.approve.success");

        return error;
    }

    public static ErrorMessage approveFailSave() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.approve.fail.save");

        return error;
    }

    public static ErrorMessage approveFailValidate() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.approve.fail.validate");

        return error;
    }

    public static Object noSuchRevision(String type, Integer id, Integer revision) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.noRevision");
        error.getData().add("general.errors.revision.noRevision."+type);
        error.getData().add(id+"");
        error.getData().add(revision+"");

        return error;
    }

    public static Object noViewableRevision(String type, Integer id) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.noViewableRevision");
        error.getData().add("general.errors.revision.noViewableRevision."+type);
        error.getData().add(id+"");

        return error;
    }
}
