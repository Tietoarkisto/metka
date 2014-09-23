package fi.uta.fsd.metka.ddi;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import codebook25.SimpleTextAndDateType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueContainer;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DDIBuilder {
    private static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_PATTERN);

    @Autowired
    private RevisionRepository revisions;
    @Autowired
    private ReferenceService references;

    public Pair<ReturnResult, CodeBookDocument> buildDDIDocument(Language language, RevisionData revisionData, Configuration configuration) {
        // Create the codebook xml document
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();

        // Add content to codebook document
        fillCodeBook(language, revisionData, configuration, codeBookDocument);

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, codeBookDocument);
    }

    private void fillCodeBook(Language language, RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        // Add new codebook
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();

        DDIHeader.fillDDIHeader(codeBookType, language);

        DDIDocumentDescription.addDocumentDescription(revisionData, language, configuration, codeBookType);
        DDIStudyDescription.addStudyDescription(revisionData, language, configuration, codeBookType, revisions, references);
        DDIFileDescription.addfileDescription(revisionData, language, configuration, codeBookType, revisions);
        DDIDataDescription.addDataDescription(revisionData, language, configuration, codeBookType, revisions);
        DDIOtherMaterialDescription.addOtherMaterialDescription(revisionData, language, configuration, codeBookType);
    }

    static String getXmlLang(Language language) {
        return (language == Language.DEFAULT) ? "fi" : language.toValue();
    }

    static boolean hasValue(Pair<StatusCode, ValueDataField> pair, Language language) {
        return pair.getLeft() == StatusCode.FIELD_FOUND && pair.getRight().hasValueFor(language);
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
    static List<ValueDataField> gatherFields(RevisionData revision, String container, String field, Language rowLang, Language fieldLang) {
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
    static <T extends SimpleTextAndDateType> T fillTextAndDateType(T stdt, Pair<StatusCode, ValueDataField> fieldPair, Language language) {
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
    static <T extends SimpleTextAndDateType> T fillTextAndDateType(T stdt, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        if(value != null) {
            stdt.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(value.getSaved().getTime()));
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
    static <T extends XmlObject> T fillTextType(T att, Pair<StatusCode, ValueDataField> fieldPair, Language language) {
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
    static <T extends XmlObject> T fillTextType(T att, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        return fillTextType(att, value != null ? value.getActualValue() : "");
    }

    static <T extends XmlObject> T fillTextType(T att, String value) {
        XmlCursor xmlCursor = att.newCursor();
        xmlCursor.setTextValue(value);
        xmlCursor.dispose();
        return att;
    }
}
