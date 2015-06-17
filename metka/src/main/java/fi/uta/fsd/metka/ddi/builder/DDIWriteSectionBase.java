package fi.uta.fsd.metka.ddi.builder;

import codebook25.CodeBookType;
import codebook25.SimpleTextAndDateType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueContainer;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

abstract class DDIWriteSectionBase {
    private static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    protected final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_PATTERN);

    protected final RevisionData revision;
    protected final Language language;
    protected final CodeBookType codeBook;
    protected final Configuration configuration;
    protected final RevisionRepository revisions;
    protected final ReferenceService references;

    protected DDIWriteSectionBase(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration,
                                  RevisionRepository revisions, ReferenceService references) {
        this.revision = revision;
        this.language = language;
        this.codeBook = codeBook;
        this.configuration = configuration;
        this.revisions = revisions;
        this.references = references;
    }

    abstract void write();

    protected String getXmlLang() {
        return getXmlLang(language);
    }

    protected String getXmlLang(Language language) {
        return (language == Language.DEFAULT) ? "fi" : language.toValue();
    }

    protected boolean hasValue(Pair<StatusCode, ValueDataField> pair, Language language) {
        return pair.getLeft() == StatusCode.FIELD_FOUND && pair.getRight().hasValueFor(language);
    }

    /**
     * Gather a list of fields of default language from rows of given language in container
     * @param revision
     * @param container
     * @param field
     * @return
     */
    protected List<ValueDataField> gatherFields(RevisionData revision, String container, String field) {
        return gatherFields(revision, container, field, Language.DEFAULT, Language.DEFAULT);
    }

    /**
     * Gather a list of fields of given language from rows of given language in container
     * @param revision
     * @param container
     * @param field
     * @param rowLang
     * @param fieldLang
     * @return
     */
    protected List<ValueDataField> gatherFields(RevisionData revision, String container, String field, Language rowLang, Language fieldLang) {
        List<ValueDataField> fields = new ArrayList<>();
        Pair<StatusCode, ValueDataField> valueFieldPair;Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(container));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(rowLang)) {
            for(DataRow row : containerPair.getRight().getRowsFor(rowLang)) {
                if(row.getRemoved()) {
                    continue;
                }
                valueFieldPair = row.dataField(ValueDataFieldCall.get(field));
                if(hasValue(valueFieldPair, fieldLang)) {
                    fields.add(valueFieldPair.getRight());
                }
            }
        }
        return fields;
    }

    /**
     * If field pair doesn't contain a value for given language inserts an empty string instead and doesn't insert a date
     * @param stdt
     * @param fieldPair
     * @param language
     * @param <T>
     * @return
     */
    protected <T extends SimpleTextAndDateType> T fillTextAndDateType(T stdt, Pair<StatusCode, ValueDataField> fieldPair, Language language) {
        if(hasValue(fieldPair, language)) {
            return fillTextAndDateType(stdt, fieldPair.getRight(), language);
        } else {
            return fillTextType(stdt, "");
        }
    }

    /**
     * If field doesn't have value for given language inserts an empty string instead and doesn't set date
     * @param stdt
     * @param field
     * @param language
     * @param <T>
     * @return
     */
    protected <T extends SimpleTextAndDateType> T fillTextAndDateType(T stdt, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        if(value != null) {
            stdt.setDate(DATE_TIME_FORMATTER.print(value.getSaved().getTime()));
            return fillTextType(stdt, value.getActualValue());
        } else {
            return fillTextType(stdt, "");
        }
    }


    /**
     * If field pair doesn't contain a value inserts an empty string instead
     * @param att
     * @param fieldPair
     * @param language
     * @param <T>
     * @return
     */
    protected <T extends XmlObject> T fillTextType(T att, Pair<StatusCode, ValueDataField> fieldPair, Language language) {
        if(hasValue(fieldPair, language)) {
            return fillTextType(att, fieldPair.getRight(), language);
        } else {
            return fillTextType(att, "");
        }
    }

    /**
     * If field doesn't have value for given language inserts an empty string instead
     * @param att
     * @param field
     * @param language
     * @param <T>
     * @return
     */
    protected <T extends XmlObject> T fillTextType(T att, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        return fillTextType(att, value != null ? value.getActualValue() : "");
    }

    protected <T extends XmlObject> T fillTextType(T att, String value) {
        XmlCursor xmlCursor = att.newCursor();
        xmlCursor.setTextValue(value);
        xmlCursor.dispose();
        return att;
    }

    /*protected String getDDIText(String value) {
        return getDDIText(Language.DEFAULT, value);
    }*/

    protected String getDDIText(Language language, String value) {
        Reference reference = new Reference("", ReferenceType.JSON, "ddi_texts", "key");
        reference.setTitlePath("text");
        ReferencePath root = new ReferencePath(reference, value);
        return getReferenceTitle(language, root);
    }

    /*protected String getDDIRestriction(String value) {
        return getDDIRestriction(Language.DEFAULT, value);
    }*/

    protected String getDDIRestriction(Language language, String value) {
        Reference rest_reference = new Reference("", ReferenceType.JSON, "ddi_texts", "key");
        Reference rest_value_reference = new Reference("", ReferenceType.DEPENDENCY, "", "values.value");
        rest_value_reference.setTitlePath("text");
        ReferencePath root = new ReferencePath(rest_reference, "RESTRICTION");
        root.setNext(new ReferencePath(rest_value_reference, value));
        return getReferenceTitle(language, root);
    }

    protected String getReferenceTitle(String path) {
        return getReferenceTitle(language, revision, path);
    }

    protected String getReferenceTitle(Language language, RevisionData revision, String path) {
        ReferenceOption option = references.getCurrentFieldOption(language, revision, configuration, path);
        if(option != null) {
            return option.getTitle().getValue();
        } else return null;
    }

    private String getReferenceTitle(Language language, ReferencePath root) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setRoot(root);
        request.setLanguage(language);
        ReferenceOption option = references.getCurrentFieldOption(request);
        return option.getTitle().getValue();
    }
}
