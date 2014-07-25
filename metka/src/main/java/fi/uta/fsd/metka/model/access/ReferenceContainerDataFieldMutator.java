package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.ReferenceContainerDataFieldAccessor.getReferenceContainerDataField;

final class ReferenceContainerDataFieldMutator {
    // Disable instantiation
    private ReferenceContainerDataFieldMutator() {}

    public static Pair<StatusCode, ReferenceContainerDataField> setReferenceContainerDataField(Map<String, DataField> fieldMap, String key, Map<String, Change> changeMap, Configuration config,
                                                                             ConfigCheck[] configChecks) {
        if(fieldMap == null || StringUtils.isEmpty(key) || changeMap == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, ReferenceContainerDataField> pair = getReferenceContainerDataField(fieldMap, key, config, configChecks);
        if(pair.getRight() != null || pair.getLeft() != StatusCode.FIELD_MISSING) {
            return pair;
        }

        ReferenceContainerDataField field = new ReferenceContainerDataField(key, 1);
        fieldMap.put(key, field);
        // We can just put a change into the change map. We are creating a new object here. If there was something previously in the map it was obviously incorrect.
        changeMap.put(key, new ContainerChange(key));
        return new ImmutablePair<>(StatusCode.FIELD_INSERT, field);
    }
}
