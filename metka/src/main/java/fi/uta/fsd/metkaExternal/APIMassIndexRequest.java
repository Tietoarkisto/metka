package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIMassIndexRequest extends APIRequest {
    private final Map<Language, List<IndexTarget>> targets = new HashMap<>();

    public Map<Language, List<IndexTarget>> getTargets() {
        return targets;
    }
}
