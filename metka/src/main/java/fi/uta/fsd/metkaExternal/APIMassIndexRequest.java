package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.enums.Language;

import java.util.*;

public class APIMassIndexRequest extends APIRequest {
    private final List<IndexTarget> targets = new ArrayList<>();

    public List<IndexTarget> getTargets() {
        return targets;
    }
}
