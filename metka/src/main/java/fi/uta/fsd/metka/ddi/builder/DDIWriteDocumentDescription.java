package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.*;

class DDIWriteDocumentDescription extends DDIWriteSectionBase {
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

    DDIWriteDocumentDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        // Add document description
        DocDscrType docDscrType = codeBook.addNewDocDscr();

        // Add citation information
        addCitationType(docDscrType);

        // Collect references by hand
	    Set<String> usedVocabs = new HashSet<>();
        // anlyUnit
        usedVocabs.add(configuration.getReference("analysisunitvocab_ref").getTarget());
        // collMode
        usedVocabs.add(configuration.getReference("collmodevocab_ref").getTarget());
        // resInstru
        usedVocabs.add(configuration.getReference("instrumentvocab_ref").getTarget());
        // sampProc
        usedVocabs.add(configuration.getReference("sampprocvocab_ref").getTarget());
        // timeMethod
        usedVocabs.add(configuration.getReference("timemethodvocab_ref").getTarget());
        // topClass
        usedVocabs.add(configuration.getReference("topicvocab_ref").getTarget());

        for(String target : usedVocabs) {
            Reference usedVocabRef = new Reference("temp", ReferenceType.JSON, target, "codeListID");
            ReferencePath usedVocabRoot = new ReferencePath(usedVocabRef, null);
            ReferencePathRequest request = new ReferencePathRequest();
            request.setRoot(usedVocabRoot);
            request.setLanguage(Language.DEFAULT);
            request.setKey("temp");
            request.setContainer(null);
            List<ReferenceOption> vocabIds = references.collectReferenceOptions(request);
            for(ReferenceOption vocabId : vocabIds) {
                ControlledVocabUsedType type = docDscrType.addNewControlledVocabUsed();
                fillTextType(type.addNewCodeListID(), vocabId.getValue());
                Reference singleValueRef = new Reference("single", ReferenceType.JSON, target, "codeListID");

                ReferencePath singleValuePath = new ReferencePath(singleValueRef, vocabId.getValue());
                request.setLanguage(language);
                request.setRoot(singleValuePath);

                singleValueRef.setTitlePath("codeListName");
                List<ReferenceOption> singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListName(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListAgencyName");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListAgencyName(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListVersionID");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListVersionID(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListURN");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListURN(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListSchemeURN");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListSchemeURN(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("selector");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    UsageType usage = type.addNewUsage();
                    usage.setSelector(singleValue.get(0).getTitle().getValue());
                }
            }
        }

        addNotes(docDscrType);
    }

    private void addNotes(DocDscrType docDscrType) {
        // Back to doc description
        // Add notes, repeatable, excel row #40 - #41
        for(Language l : Language.values()) {
            NotesType notesType = fillTextType(docDscrType.addNewNotes(), NOTES.get(l));
            notesType.setXmlLang(getXmlLang(l));
        }
    }

    private void addCitationType(DocDscrType docDscrType) {
        // Add citation
        CitationType citationType = docDscrType.addNewCitation();
        // Add title statement (?)
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();

        // Add titles

            // Add content of title field
        addTitleField(titlStmtType);

        // Add ID no
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        String agency = null;
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            agency = addIDNo(titlStmtType, valueFieldPair.getRight(), configuration.getRootSelectionList(Lists.ID_PREFIX_LIST));
        }

        // Add Producer information
        addProducerInfo(citationType, agency);

        // Add container version
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            addContainerVersion(citationType, containerPair.getRight());
        }

        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            BiblCitType biblCitType = fillTextType(citationType.addNewBiblCit(), valueFieldPair.getRight().getActualValueFor(language)+BIBL_CIT_POST.get(language));
            biblCitType.setFormat(BIBL_CIT_FORMAT);
        }

        // Add holdings
        addHoldingsInfo(citationType);
    }

    private void addHoldingsInfo(CitationType citationType) {
        HoldingsType holdingsType = fillTextType(citationType.addNewHoldings(), HOLDINGS.get(language));
        holdingsType.setLocation(HOLDINGS_LOCATION.get(language));

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            holdingsType.setURI(HOLDINGS_URI_BASE.get(language)+valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private void addContainerVersion(CitationType citationType, ContainerDataField container) {
        Pair<StatusCode, ValueDataField> valueFieldPair;List<DataRow> rows = container.getRowsFor(language);
        DataRow row = rows.get(rows.size()-1);

        valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
        if(hasValue(valueFieldPair, language)) {
            VerStmtType verStmt = citationType.addNewVerStmt();
            VersionType ver = fillTextType(verStmt.addNewVersion(), valueFieldPair, language);
            valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSIONDATE));
            if(hasValue(valueFieldPair, language)) {
                LocalDateTime versiondate = new LocalDateTime(valueFieldPair.getRight().getActualValueFor(language));
                LocalDate localDate = versiondate.toLocalDate();
                ver.setDate(DATE_TIME_FORMATTER.print(localDate));
            }
        }
    }

    private void addProducerInfo(CitationType citationType, String agency) {
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
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
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
                    stadt.setDate(DATE_TIME_FORMATTER.print(localDate));
                }
            }
        }

        // Add production place (?)
        SimpleTextType stt = fillTextType(prodStmtType.addNewProdPlac(), PRODPLAC.get(language));
        fillTextType(stt.addNewAddress(), PRODPLAC_ADDRESS.get(language));
    }

    private String addIDNo(TitlStmtType titlStmtType, ValueDataField idField, SelectionList prefixList) {
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

    private void addTitleField(TitlStmtType titlStmtType) {
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
