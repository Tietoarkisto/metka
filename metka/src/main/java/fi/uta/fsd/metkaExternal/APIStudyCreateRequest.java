package fi.uta.fsd.metkaExternal;

import java.util.HashMap;
import java.util.Map;

public class APIStudyCreateRequest extends APIRequest {

    private final Map<String, String> parameters = new HashMap<>();

    public Map<String, String> getParameters() {
        return parameters;
    }
}
