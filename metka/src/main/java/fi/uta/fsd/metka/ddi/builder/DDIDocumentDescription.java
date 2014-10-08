package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.fillTextType;
import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.getXmlLang;
import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.hasValue;

class DDIDocumentDescription {
    private static final String BIBL_CIT_FORMAT = "MRDF";

    private static final Map<Language, String> DDI_TITLE_PREFIXES = new HashMap<>();
    private static final Map<Language, String> PRODUCER = new HashMap<>();
    private static final Map<Language, String> COPYRIGHT = new HashMap<>();
    private static final Map<Language, String> PRODPLAC = new HashMap<>();
    private static final Map<Language, String> PRODPLAC_ADDRESS = new HashMap<>();
    private static final Map<Language, String> HOLDINGS = new HashMap<>();
    private static final Map<Language, String> HOLDINGS_LOCATION = new HashMap<>();
    private static final Map<Language, String> HOLDINGS_URI_BASE = new HashMap<>();
    private static final Map<Language, String> NOTES = new HashMap<>();
    private static final Map<Language, String> BIBL_CIT_POST = new HashMap<>();

    static {
        DDI_TITLE_PREFIXES.put(Language.DEFAULT, "DDI-kuvailu: ");
        DDI_TITLE_PREFIXES.put(Language.EN, "DDI-description: ");
        DDI_TITLE_PREFIXES.put(Language.SV, "DDI-beskrivning: ");

        PRODUCER.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto");
        PRODUCER.put(Language.EN, "Finnish Social Science Data Archive");
        PRODUCER.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv");

        COPYRIGHT.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto");
        COPYRIGHT.put(Language.EN, "Finnish Social Science Data Archive");
        COPYRIGHT.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv");

        PRODPLAC.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto");
        PRODPLAC.put(Language.EN, "Finnish Social Science Data Archive");
        PRODPLAC.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv");

        PRODPLAC_ADDRESS.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto <br>33014 TAMPEREEN YLIOPISTO <br>+358 40 190 1432 <br>");
        PRODPLAC_ADDRESS.put(Language.EN, "Finnish Social Science Data Archive (FSD) <br>FI-33014 University of Tampere <br>FINLAND <br>+358 40 190 1432 <br>");
        PRODPLAC_ADDRESS.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv (FSD) <br>FI-33014 Tammerfors universitet <br>+358 40 190 1432 <br>");

        HOLDINGS.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto FSD");
        HOLDINGS.put(Language.EN, "Finnish Social Science Data Archive FSD");
        HOLDINGS.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv FSD");

        HOLDINGS_LOCATION.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto FSD");
        HOLDINGS_LOCATION.put(Language.EN, "Finnish Social Science Data Archive FSD");
        HOLDINGS_LOCATION.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv FSD");

        HOLDINGS_URI_BASE.put(Language.DEFAULT, "http://www.fsd.uta.fi/aineistot/luettelo/");
        HOLDINGS_URI_BASE.put(Language.EN, "http://www.fsd.uta.fi/english/data/catalogue/");
        HOLDINGS_URI_BASE.put(Language.SV, "http://www.fsd.uta.fi/svenska/data/katalog/");

        NOTES.put(Language.DEFAULT, "FSD:n aineistokuvailut (FSD metadata records), jonka tekijä on Suomen yhteiskuntatieteellinen tietoarkisto (Finnish Social Science Data Archive), on lisensoitu Creative Commons Nimeä 4.0 Kansainvälinen (CC BY 4.0) -lisenssillä.");
        NOTES.put(Language.EN, "FSD:n aineistokuvailut (FSD metadata records) by Suomen yhteiskuntatieteellinen tietoarkisto (Finnish Social Science Data Archive) is licensed under a Creative Commons Attribution 4.0 International (CC BY 4.0) license.");
        NOTES.put(Language.SV, "FSD:n aineistokuvailut (FSD metadata records) av Suomen yhteiskuntatieteellinen tietoarkisto (Finlands samhällsvetenskapliga dataarkiv) är licensierad under en Creative Commons Erkännande 4.0 Internationell (CC BY 4.0) licens.");

        BIBL_CIT_POST.put(Language.DEFAULT, " [koodikirja]. Yhteiskuntatieteellinen tietoarkisto [tuottaja ja jakaja].");
        BIBL_CIT_POST.put(Language.EN, " [codebook]. Finnish Social Science Data Archive [producer and distributor].");
        BIBL_CIT_POST.put(Language.SV, " [kodbok]. Finlands samhällsvetenskapliga dataarkiv [producent och distributör].");
    }

    static void addDocumentDescription(RevisionData revisionData, Language language, Configuration configuration, CodeBookType codeBookType) {
        // Add document description
        DocDscrType docDscrType = codeBookType.addNewDocDscr();

        // Add citation information
        addCitationType(revisionData, language, configuration, docDscrType);

        /*// TODO: Controlled vocab used
        // Back to doc description
        // Add controlled vocabulary used, repeatable, excel row #31 - #39
        ControlledVocabUsedType controlledVocabUsedType = docDscrType.addNewControlledVocabUsed();
        // Add code list id ?
        StringType st = controlledVocabUsedType.addNewCodeListID();
        xmlCursor = st.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Add code list name ?
        st = controlledVocabUsedType.addNewCodeListName();
        xmlCursor = st.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Add code list agency name ?
        st = controlledVocabUsedType.addNewCodeListAgencyName();
        xmlCursor = st.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Add code list version id ?
        st = controlledVocabUsedType.addNewCodeListVersionID();
        xmlCursor = st.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Add code list urn ?
        st = controlledVocabUsedType.addNewCodeListURN();
        xmlCursor = st.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Add code list scheme urn TODO: Is this right ?
        CodeListSchemeURNDocument codeListSchemeURNDocument = CodeListSchemeURNDocument.Factory.newInstance();
        xmlCursor = codeListSchemeURNDocument.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        controlledVocabUsedType.setCodeListSchemeURN(codeListSchemeURNDocument);

        // Add usage, repeatable
        UsageType usageType = controlledVocabUsedType.addNewUsage();
        // Set selector
        usageType.setSelector("placeholder");*/

        addNotes(docDscrType);
    }

    private static void addNotes(DocDscrType docDscrType) {
        // Back to doc description
        // Add notes, repeatable, excel row #40 - #41
        for(Language l : Language.values()) {
            NotesType notesType = fillTextType(docDscrType.addNewNotes(), NOTES.get(l));
            notesType.setXmlLang(DDIBuilder.getXmlLang(l));
        }
    }

    private static void addCitationType(RevisionData revisionData, Language language, Configuration configuration, DocDscrType docDscrType) {
        // Add citation
        CitationType citationType = docDscrType.addNewCitation();
        // Add title statement (?)
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();

        // Add titles

            // Add content of title field
        addTitleField(revisionData, language, titlStmtType);

        // Add ID no
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        String agency = null;
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            agency = addIDNo(titlStmtType, valueFieldPair.getRight(), configuration.getRootSelectionList(Lists.ID_PREFIX_LIST));
        }

        // Add Producer information
        addProducerInfo(revisionData, language, citationType, agency);

        // Add container version
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            addContainerVersion(language, citationType, containerPair.getRight());
        }

        valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            BiblCitType biblCitType = fillTextType(citationType.addNewBiblCit(), valueFieldPair.getRight().getActualValueFor(language)+BIBL_CIT_POST.get(language));
            biblCitType.setFormat(BIBL_CIT_FORMAT);
        }

        // Add holdings
        addHoldingsInfo(revisionData, language, citationType);
    }

    private static void addHoldingsInfo(RevisionData revisionData, Language language, CitationType citationType) {
        HoldingsType holdingsType = fillTextType(citationType.addNewHoldings(), HOLDINGS.get(language));
        holdingsType.setLocation(HOLDINGS_LOCATION.get(language));

        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            holdingsType.setURI(HOLDINGS_URI_BASE.get(language)+valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private static void addContainerVersion(Language language, CitationType citationType, ContainerDataField container) {
        Pair<StatusCode, ValueDataField> valueFieldPair;List<DataRow> rows = container.getRowsFor(language);
        DataRow row = rows.get(rows.size()-1);

        valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
        if(hasValue(valueFieldPair, language)) {
            VerStmtType verStmt = citationType.addNewVerStmt();
            VersionType ver = fillTextType(verStmt.addNewVersion(), valueFieldPair, language);
            valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSIONDATE));
            if(hasValue(valueFieldPair, language)) {
                LocalDate localDate = LocalDate.parse(valueFieldPair.getRight().getActualValueFor(language));
                ver.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(localDate));
            }
        }
    }

    private static void addProducerInfo(RevisionData revisionData, Language language, CitationType citationType, String agency) {
        // Add producer statement
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        // Add producer, repeatable
        ProducerType producerType = fillTextType(prodStmtType.addNewProducer(), PRODUCER.get(language));

        // Set ID, repeatable
        // TODO: What is the value for this
        producerType.setID("");

        // Set abbreviation
        if(agency != null) producerType.setAbbr(agency);

        // Set type
        // TODO: What is the value for this
        producerType.setRole("");

        // Add copyright
        fillTextType(prodStmtType.addNewCopyright(), COPYRIGHT.get(language));

        // Add production date
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            ContainerDataField container = containerPair.getRight();
            for (DataRow dataRow : container.getRowsFor(language)) {
                if(dataRow.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = dataRow.dataField( ValueDataFieldCall.get(Fields.VERSIONDATE));
                if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().valueForEquals(language, "1.0")) {
                    LocalDate localDate = LocalDate.parse(valueFieldPair.getRight().getActualValueFor(language));
                    SimpleTextAndDateType stadt = prodStmtType.addNewProdDate();
                    stadt.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(localDate));
                }
            }
        }

        // Add production place (?)
        SimpleTextType stt = fillTextType(prodStmtType.addNewProdPlac(), PRODPLAC.get(language));
        fillTextType(stt.addNewAddress(), PRODPLAC_ADDRESS.get(language));
    }

    private static String addIDNo(TitlStmtType titlStmtType, ValueDataField idField, SelectionList prefixList) {
        if(prefixList != null) {
            for(Option option : prefixList.getOptions()) {
                if(idField.getActualValueFor(Language.DEFAULT).indexOf(option.getValue()) == 0) {
                    // Add id number
                    IDNoType idNoType = fillTextType(titlStmtType.addNewIDNo(), idField.getActualValueFor(Language.DEFAULT).substring(option.getValue().length()));
                    idNoType.setAgency(option.getValue());
                    return option.getValue();
                }
            }
        }
        return null;
    }

    private static void addTitleField(RevisionData revision, Language language, TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(titlStmtType.addNewTitl(), DDI_TITLE_PREFIXES.get(language)+valueFieldPair.getRight().getActualValueFor(language));
        }

        for(Language altLang : Language.values()) {
            if(altLang == language) {
                continue;
            }
            if(hasValue(valueFieldPair, altLang)) {
                SimpleTextType stt = fillTextType(titlStmtType.addNewParTitl(), DDI_TITLE_PREFIXES.get(language)+valueFieldPair.getRight().getActualValueFor(altLang));
                stt.setXmlLang(getXmlLang(altLang));
            }
        }
    }
}
