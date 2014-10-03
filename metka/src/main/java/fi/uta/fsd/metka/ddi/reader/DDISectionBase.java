package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;

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

    abstract void read();

    protected StatusCode valueSet(String key, String value) {
        return valueSet(revision, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(String key, String value, Language language) {
        return valueSet(revision, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(DataFieldContainer dataFields, String key, String value, Language language, Map<String, Change> changes) {
        return dataFields.dataField(ValueDataFieldCall.set(key, new Value(value), language).setInfo(info).setChangeMap(changes)).getLeft();
    }
}
