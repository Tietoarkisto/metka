package fi.uta.fsd.metka.ddi.reader;

import codebook25.AbstractTextType;
import codebook25.CodeBookType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

abstract class DDISectionBase {
    protected final RevisionData revision;
    protected final Language language;
    protected final CodeBookType codeBook;
    protected final DateTimeUserPair info;
    protected final Configuration configuration;

    protected DDISectionBase(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        this.revision = revision;
        this.language = language;
        this.codeBook = codeBook;
        this.info = info;
        this.configuration = configuration;
    }

    abstract ReturnResult read();

    protected StatusCode valueSet(String key, String value) {
        return valueSet(revision, key, value);
    }

    protected StatusCode valueSet(DataFieldContainer dataFields, String key, String value) {
        return valueSet(dataFields, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(String key, String value, Language language) {
        return valueSet(revision, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(DataFieldContainer dataFields, String key, String value, Language language, Map<String, Change> changes) {
        return dataFields.dataField(ValueDataFieldCall.set(key, new Value(value), language).setInfo(info).setChangeMap(changes)).getLeft();
    }

    protected <T> boolean hasContent(T[] array) {
        return array != null && array.length > 0;
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> getContainer(String key) {
        return getContainer(key, revision, revision.getChanges());
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> getContainer(String key, RevisionData data) {
        return getContainer(key, data, data.getChanges());
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> getContainer(String key, DataFieldContainer fieldContainer, Map<String, Change> changeMap) {
        Pair<StatusCode, ContainerDataField> container = fieldContainer.dataField(ContainerDataFieldCall.set(key));
        if(!(container.getLeft() == StatusCode.FIELD_FOUND || container.getLeft() == StatusCode.FIELD_INSERT)) {
            // No need to continue insert, we have a problem
            return new ImmutablePair<>(ReturnResult.OPERATION_FAIL, null);
        }
        ContainerChange change = (ContainerChange)changeMap.get(key);
        if(change == null) {
            change = new ContainerChange(key);
            changeMap.put(key, change);
        }
        Pair<ContainerDataField, ContainerChange> pair = new ImmutablePair<>(container.getRight(), change);
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, pair);
    }

    protected ReturnResult fillSingleValueContainer(DataFieldContainer data, Map<String, Change> changeMap, String containerKey, String fieldKey, AbstractTextType[] tts) {
        if(!hasContent(tts)) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(containerKey, data, changeMap);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        for(AbstractTextType tt : tts) {
            if(!StringUtils.hasText(tt.xmlText())) {
                continue;
            }
            container.getOrCreateRowWithFieldValue(language, fieldKey, new Value(tt.xmlText()), changeMap, info);
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
