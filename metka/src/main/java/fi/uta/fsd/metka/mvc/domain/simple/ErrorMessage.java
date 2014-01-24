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
}
