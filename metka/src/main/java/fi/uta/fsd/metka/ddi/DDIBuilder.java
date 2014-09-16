package fi.uta.fsd.metka.ddi;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueContainer;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DDIBuilder {
    private static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_PATTERN);


    private static final String MRDF = "MRDF";
    private static final String BIBL_CITATION_TEXT = " [koodikirja]. Tampere: Yhteiskuntatieteellinen tietoarkisto [tuottaja ja jakaja], 2014.";
    private static final String COPYRIGHT_FSD_AND_MATERIAL_SUPPLIER = "FSD:n ja aineiston luovuttajan tekemän sopimuksen mukaisesti.";
    private static final String FSD_DISTRIBUTOR_BASE_URI = "http://www.fsd.uta.fi/";
    private static final String SERIES_BASE_URI = "http://www.fsd.uta.fi/fi/aineistot/luettelo/sarjat.html#";
    private static final String ACCS_PLAC_URI = "http://www.fsd.uta.fi/";
    private static final String ACCS_PLAC_CONTENT = "Yhteiskuntatieteellinen tietoarkisto";
    private static final String CIT_REQUIRED_CONTENT = "Aineistoon ja sen tekijöihin tulee viitata asianmukaisesti kaikissa julkaisuissa ja esityksissä, joissa aineistoa käytetään. Tietoarkiston antaman malliviittaustiedon voi merkitä lähdeluetteloon sellaisenaan tai sitä voi muokata julkaisun käytäntöjen mukaisesti.";
    private static final String DEPOS_REQUIRED_CONTENT = "Tietoarkistoon on lähetettävä viitetiedot kaikista julkaisuista, joissa käyttäjä hyödyntää aineistoa.";
    private static final String DISCLAIMER_CONTENT = "Aineiston alkuperäiset tekijät ja tietoarkisto eivät ole vastuussa aineiston jatkokäytössä tuotetuista tuloksista ja tulkinnoista.";


    @Autowired
    private RevisionRepository revisions;

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
        DDIStudyDescription.addStudyDescription(revisionData, language, configuration, codeBookType, revisions);
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

    static <T extends SimpleTextAndDateType> T fillTextAndDateType(T stdt, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        stdt.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(value.getSaved().getTime()));
        return fillTextType(stdt, value.getActualValue());
    }

    static <T extends AbstractTextType> T fillTextType(T att, ValueDataField field, Language language) {
        ValueContainer value = field.getValueFor(language);
        return fillTextType(att, value.getActualValue());
    }

    static <T extends AbstractTextType> T fillTextType(T att, String value) {
        XmlCursor xmlCursor = att.newCursor();
        xmlCursor.setTextValue(value);
        xmlCursor.dispose();
        return att;
    }
}
