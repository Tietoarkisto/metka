package fi.uta.fsd.metkaExternal.batch;

import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MassIndexRequest {
    private final Map<Language, List<IndexTarget>> targets = new HashMap<>();

    public Map<Language, List<IndexTarget>> getTargets() {
        return targets;
    }
}
