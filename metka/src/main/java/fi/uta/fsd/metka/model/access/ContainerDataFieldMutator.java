package fi.uta.fsd.metka.model.access;

import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

import static fi.uta.fsd.metka.model.access.ContainerDataFieldAccessor.getContainerDataField;

final class ContainerDataFieldMutator {
    // Disable instantiation
    private ContainerDataFieldMutator() {}

    public static Pair<StatusCode, ContainerDataField> setContainerDataField(Map<String, DataField> fieldMap, String key, Map<String, Change> changeMap, Configuration config,
                                                                             ConfigCheck[] configChecks) {
        if(fieldMap == null || StringUtils.isEmpty(key) || changeMap == null) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }

        Pair<StatusCode, ContainerDataField> pair = getContainerDataField(fieldMap, key, config, configChecks);
        if(pair.getRight() != null || pair.getLeft() != StatusCode.FIELD_MISSING) {
            return pair;
        }

        ContainerDataField field = new ContainerDataField(key, 1);
        fieldMap.put(key, field);
        // We can just put a change into the change map. We are creating a new object here. If there was something previously in the map it was obviously incorrect.
        changeMap.put(key, new ContainerChange(key));
        return new ImmutablePair<>(StatusCode.FIELD_INSERT, field);
    }
}
