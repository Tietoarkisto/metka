package fi.uta.fsd.metka.mvc.domain.simple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/21/14
 * Time: 9:15 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleObject {

    private final Map<String, Object> values = new HashMap<>();

    public Map<String, Object> getValues() {
        return values;
    }

    public Object getByKey(String key) {
        return values.get(key);
    }
    public void setByKey(String key, Object value) {
        values.put(key, value);
    }


}
