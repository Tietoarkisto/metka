package fi.uta.fsd.metka.xml.codebook;

import codebook25.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import org.apache.xmlbeans.XmlCursor;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Transform given data to codebook document.
 */
public class CodebookTransformer {

    private static final String DDI_TITLE_PREFIX_FI = "DDI-kuvailu: ";
    private static final String DDI_TITLE_PREFIX_EN = "DDI-description: ";
    private static final String DDI_TITLE_PREFIX_SV = "DDI-beskrivning: ";
    private static final String AGENCY = "FSD";
    private static final String FSD_NAME_FI = "Yhteiskuntatieteellinen " +
            "tietoarkisto";
    private static final String MRDF = "MRDF";
    private static final String BIBL_CITATION_TEXT = " [koodikirja]. Tampere: " +
            "Yhteiskuntatieteellinen tietoarkisto [tuottaja ja jakaja], 2014.";
    private static final String HOLDINGS_BASE_URI = "http://www.fsd.uta.fi/" +
            "aineistot/luettelo/";
    private static final String FSD_NAME_FULL_FI = "Yhteiskuntatieteellinen " +
            "tietoarkisto FSD";
    private static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    private static final String COPYRIGHT_FSD_AND_MATERIAL_SUPPLIER = "FSD:n ja" +
            " aineiston luovuttajan tekemän sopimuksen mukaisesti.";
    private static final String FSD_DISTRIBUTOR_BASE_URI = "http://" +
            "www.fsd.uta.fi/";
    private static final String SERIES_BASE_URI = "http://www.fsd.uta.fi/fi/" +
            "aineistot/luettelo/sarjat.html#";
    private static final String NOTES_LICENSING_FI = "FSD:n aineistokuvailut " +
            "(FSD metadata" +
            " records), jonka tekijä on Suomen yhteiskuntatieteellinen " +
            "tietoarkisto (Finnish Social Science Data Archive), on " +
            "lisensoitu Creative Commons Nimeä 4.0 Kansainvälinen " +
            "(CC BY 4.0) -lisenssillä.";
    private static final String NOTES_LICENSING_EN = "FSD:n aineistokuvailut " +
            "(FSD metadata" +
            " records) by Suomen yhteiskuntatieteellinen tietoarkisto " +
            "(Finnish Social Science Data Archive) is licensed under a " +
            "Creative Commons Attribution 4.0 International (CC BY 4.0) " +
            "license.";
    private static final String NOTES_LICENSING_SV = "FSD:n aineistokuvailut " +
            "(FSD metadata" +
            " records) av Suomen yhteiskuntatieteellinen tietoarkisto " +
            "(Finlands samhällsvetenskapliga dataarkiv) är licensierad under" +
            " en Creative Commons Erkännande 4.0 Internationell (CC BY 4.0)" +
            " licens.";
    private static final String ACCS_PLAC_URI = "http://www.fsd.uta.fi/";
    private static final String ACCS_PLAC_CONTENT = "Yhteiskuntatieteellinen " +
            "tietoarkisto";
    private static final String CIT_REQUIRED_CONTENT = "Aineistoon ja sen " +
            "tekijöihin tulee viitata asianmukaisesti kaikissa julkaisuissa ja " +
            "esityksissä, joissa aineistoa käytetään. Tietoarkiston antaman " +
            "malliviittaustiedon voi merkitä lähdeluetteloon sellaisenaan tai " +
            "sitä voi muokata julkaisun käytäntöjen mukaisesti.";
    private static final String DEPOS_REQUIRED_CONTENT = "Tietoarkistoon on " +
            "lähetettävä viitetiedot kaikista julkaisuista, joissa käyttäjä " +
            "hyödyntää aineistoa.";
    private static final String DISCLAIMER_CONTENT = "Aineiston alkuperäiset " +
            "tekijät ja tietoarkisto eivät ole vastuussa aineiston jatkokäytössä" +
            " tuotetuista tuloksista ja tulkinnoista.";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_PATTERN);

    public CodeBookDocument createCodebook(RevisionData revisionData, Configuration configuration) throws Exception {
        // New codebook document
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();

        // Add new codebook
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();
        // Set namespaces. Get cursor
        XmlCursor xmlCursor = codeBookType.newCursor();
        // Move cursor to last attribute
        xmlCursor.toLastAttribute();
        // Create new qualified name
        QName qName = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        // Location string
        String location = "ddi:codebook:2_5 http://www.ddialliance.org/" + "Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd";
        // Set attribute
        xmlCursor.setAttributeText(qName, location);
        // Move cursor to last attribute
        xmlCursor.toLastAttribute();
        // Set version
        xmlCursor.insertAttributeWithValue("version", "2.5");
        // Dispose cursor
        xmlCursor.dispose();
        // Sets xml:lang attribute TODO: Get language (fi,sv,en) ?
        String languageCode = "fi";
        codeBookType.setLang(languageCode);

        addDocumentDescription(revisionData, configuration, codeBookDocument);
        addStudyDescription(revisionData, configuration, codeBookDocument);
        addfileDescription(revisionData, configuration, codeBookDocument);
        addDataDescription(revisionData, configuration, codeBookDocument);
        addOtherMaterialDescription(revisionData, configuration, codeBookDocument);

        return codeBookDocument;
    }

    /**
     * Create document description to codebook document
     *
     * @param revisionData revision data
     * @param configuration configuration
     * @param codeBookDocument codebook document
     */
    private void addDocumentDescription(RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        // Get codebook
        CodeBookType codeBookType = codeBookDocument.getCodeBook();
        String languageCode = codeBookType.getLang();
        XmlCursor xmlCursor;

        // Add document description
        DocDscrType docDscrType = codeBookType.addNewDocDscr();
        // Add citation
        CitationType citationType = docDscrType.addNewCitation();
        // Add title statement (?)
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();
        // Add title
        SimpleTextType stt = titlStmtType.addNewTitl();
        // TODO: value based on language, prepended to title is either: DDI-kuvailu, DDI-description or DDI-beskrivning
        String title, titleValue;

        // Create title text
        switch (languageCode) {
            case "sv":
                titleValue = revisionData.dataField(SavedDataFieldCall.get("title")).getValue().getActualValue();
                title = DDI_TITLE_PREFIX_SV + titleValue;
                break;
            case "en":
                // English title is a field called 'entitle'
                titleValue = revisionData.dataField(SavedDataFieldCall.get("entitle")).getValue().getActualValue();
                title = DDI_TITLE_PREFIX_EN + titleValue;
                break;
            default:
                // Default to finnish
                titleValue = revisionData.dataField(SavedDataFieldCall.get("title")).getValue().getActualValue();
                title = DDI_TITLE_PREFIX_FI + titleValue;
                break;
        }

        // Insert title value
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(title);
        xmlCursor.dispose();

        // Add partitle, repeatable
        ContainerDataField containerDataField = revisionData.dataField(ContainerDataFieldCall.get("partitles") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            stt = titlStmtType.addNewParTitl();
            String s = dataRow.dataField( SavedDataFieldCall.get("partitlelang") ).getValue().getActualValue();

            switch (s) {
                case "en":
                    // TODO: Is this 'partitle' or 'entitle' ?
                    title = DDI_TITLE_PREFIX_EN + dataRow.dataField(SavedDataFieldCall.get("partitle")).getValue().getActualValue();
                    break;
                case "sv":
                    title = DDI_TITLE_PREFIX_SV + dataRow.dataField(SavedDataFieldCall.get("partitle")).getValue().getActualValue();
                    break;
                default:
                    // Default to finnish
                    title = DDI_TITLE_PREFIX_FI + dataRow.dataField(SavedDataFieldCall.get("partitle")).getValue().getActualValue();
                    break;
            }

            stt.setLang(s);
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(title);
            xmlCursor.dispose();
        }

        // Add id number, repeatable TODO: How is this repeatable ?
        IDNoType idNoType = titlStmtType.addNewIDNo();
        idNoType.setAgency(AGENCY);
        // Set study id number
        xmlCursor = idNoType.newCursor();
        xmlCursor.setTextValue( revisionData.dataField( SavedDataFieldCall.get("studyid_number") ).getValue().getActualValue() );
        xmlCursor.dispose();

        // Add producer statement
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        // Add producer, repeatable
        ProducerType producerType = prodStmtType.addNewProducer();
        // Set abbreviation
        producerType.setAbbr(AGENCY);
        // Set ID, repeatable TODO: What is this?
        producerType.setID("");
        // Set type TODO: producer type has no type only role
        producerType.setRole("");
        // Set producer content
        xmlCursor = producerType.newCursor();
        xmlCursor.setTextValue(FSD_NAME_FI);
        xmlCursor.dispose();

        // Add copyright
        stt = prodStmtType.addNewCopyright();
        // Set copyright content
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(FSD_NAME_FI);
        xmlCursor.dispose();

        // Add production date
        // TODO: In excel path is descversions.versiondate of version 1.0 ?
        containerDataField = revisionData.dataField(ContainerDataFieldCall.get("descversions") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String versionDate = dataRow.dataField( SavedDataFieldCall.get("versiondate") ).getValue().getActualValue();
            LocalDate localDate = LocalDate.parse(versionDate);
            SimpleTextAndDateType stadt = prodStmtType.addNewProdDate();
            stadt.setDate( DATE_TIME_FORMATTER.print(localDate) );
        }

        // Add production place (?)
        stt = prodStmtType.addNewProdPlac();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(FSD_NAME_FI);
        xmlCursor.dispose();
        // TODO: Address gets xht namespace ?
        /*xmlCursor = stt.addNewAddress().newCursor();
        xmlCursor.setTextValue("");
        xmlCursor.dispose();*/

        /* TODO: Excel incorrect for rows 25 and 26. ProdStmt type has no verStmt type */

        // Add bibl (?) citation
        BiblCitType biblCitType = citationType.addNewBiblCit();
        // Set format
        biblCitType.setFormat(MRDF);
        // Set bibl (?) citation content, TODO: Concatenated value. Where to get all values
        String biblCitation = title + BIBL_CITATION_TEXT;
        xmlCursor = biblCitType.newCursor();
        xmlCursor.setTextValue(biblCitation);
        xmlCursor.dispose();

        // Add holdings
        HoldingsType holdingsType = citationType.addNewHoldings();
        // Set location
        holdingsType.setLocation(FSD_NAME_FULL_FI);
        // Set URI
        String holdingsURIString = HOLDINGS_BASE_URI + revisionData.dataField( SavedDataFieldCall.get("studyid") ).getValue().getActualValue();
        holdingsType.setURI(holdingsURIString);
        // Set holdings element content
        xmlCursor = holdingsType.newCursor();
        xmlCursor.setTextValue(FSD_NAME_FULL_FI);
        xmlCursor.dispose();

        // Back to doc description
        // Add controlled vocabulary used, repeatable, excel row #31 - #39 TODO: Where to get values ?
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
        usageType.setSelector("placeholder");

        // Back to doc description
        // Add notes, repeatable, excel row #40 - #41
        NotesType notesType = docDscrType.addNewNotes();
        notesType.setLang("fi");
        // Set finnish notes type content
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue(NOTES_LICENSING_FI);
        xmlCursor.dispose();
        // Set english notes type content
        notesType = docDscrType.addNewNotes();
        notesType.setLang("en");
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue(NOTES_LICENSING_EN);
        xmlCursor.dispose();
        // Set swedish notes type content
        notesType = docDscrType.addNewNotes();
        notesType.setLang("sv");
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue(NOTES_LICENSING_SV);
        xmlCursor.dispose();
        // Exit doc description
    }

    /**
     * Create study description to codebook document
     *
     * @param revisionData revision data
     * @param configuration configuration
     * @param codeBookDocument codebook document
     */
    private void addStudyDescription(RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        CodeBookType codeBookType = codeBookDocument.getCodeBook();

        // Add study description to codebook
        StdyDscrType stdyDscrType = codeBookType.addNewStdyDscr();

        // Add citation
        CitationType citationType = stdyDscrType.addNewCitation();

        // Add title statement
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();
        // Set title value TODO: Did this depend on current language ?
        SimpleTextType stt = titlStmtType.addNewTitl();
        XmlCursor xmlCursor = stt.newCursor();
        xmlCursor.setTextValue( revisionData.dataField( SavedDataFieldCall.get("title") ).getValue().getActualValue() );
        xmlCursor.dispose();

        // Add alternative title, repeatable, excel row #46
        ContainerDataField containerDataField = revisionData.dataField( ContainerDataFieldCall.get("alttitles") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String alternativeTitle = dataRow.dataField( SavedDataFieldCall.get("alttitle") ).getValue().getActualValue();
            stt = titlStmtType.addNewAltTitl();
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(alternativeTitle);
            xmlCursor.dispose();
        }

        // Add partitle, repeatable, excel row #47 - #50
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("partitles") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            stt = titlStmtType.addNewParTitl();
            String lang = dataRow.dataField( SavedDataFieldCall.get("partitlelang") ).getValue().getActualValue();
            String partitle = dataRow.dataField(SavedDataFieldCall.get("partitle")).getValue().getActualValue();

            stt.setLang(lang);
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(partitle);
            xmlCursor.dispose();
        }

        // Add id number, repeatable TODO: How is this repeatable ?
        IDNoType idNoType = titlStmtType.addNewIDNo();
        idNoType.setAgency(AGENCY);
        // Set study id number
        xmlCursor = idNoType.newCursor();
        xmlCursor.setTextValue( revisionData.dataField( SavedDataFieldCall.get("studyid_number") ).getValue().getActualValue() );
        xmlCursor.dispose();

        // Back to citation
        // Add rsp (?)
        RspStmtType rspStmtType = citationType.addNewRspStmt();

        // Add author entity, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("authors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: authortype is SELECTION type with value of the selection as it's value.
            // TODO: authortype_list is REFERENCE type. Need to get the reference from it
            // TODO: Depending on the authortype excel would indicate that the affiliation is concatenated differently
            // String authorType = dataRow.dataField( SavedDataFieldCall.get("authortype") ).getValue().getActualValue();
            String author = dataRow.dataField( SavedDataFieldCall.get("author") ).getValue().getActualValue();
            String affiliation = dataRow.dataField( SavedDataFieldCall.get("affiliation") ).getValue().getActualValue();

            AuthEntyType authEntyType = rspStmtType.addNewAuthEnty();
            // Set author content
            xmlCursor = authEntyType.newCursor();
            xmlCursor.setTextValue(author);
            xmlCursor.dispose();
            // Set affiliation
            authEntyType.setAffiliation(affiliation);
        }

        // Add other author, repeatable, excel row #56 - #59
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("otherauthors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: authortype is SELECTION type with value of the selection as it's value.
            // TODO: authortype_list is REFERENCE type. Need to get the reference from it
            // TODO: Depending on the authortype excel would indicate that the affiliation is concatenated differently
            // String authorType = dataRow.dataField( SavedDataFieldCall.get("authortype") ).getValue().getActualValue();
            String otherAuthor = dataRow.dataField( SavedDataFieldCall.get("otherauthor") ).getValue().getActualValue();
            String otherAuthorAffiliation = dataRow.dataField( SavedDataFieldCall.get("otherauthoraffiliation") ).getValue().getActualValue();

            OthIdType othIdType = rspStmtType.addNewOthId();
            // Set other author content
            xmlCursor = othIdType.newCursor();
            xmlCursor.setTextValue(otherAuthor);
            xmlCursor.dispose();
            // Set affiliation
            othIdType.setAffiliation(otherAuthorAffiliation);
        }

        // Back to citation
        // Add producers , excel row #60
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        // Add new producer, repeatable, excel row #61 - #64
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("producers") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Excel indicates fields like produceragency, producersetction,producerorganization.
            // TODO: Not implemented yet / missing ?
            // TODO: Need to recheck excel and fields for correct values set into fields
            String producer = dataRow.dataField( SavedDataFieldCall.get("producer") ).getValue().getActualValue();
            /*String producerId = dataRow.dataField( SavedDataFieldCall.get("producerid") ).getValue().getActualValue();*/
            /*String producerIdType = dataRow.dataField( SavedDataFieldCall.get("produceridtype") ).getValue().getActualValue();*/
            String producerRole = dataRow.dataField( SavedDataFieldCall.get("producerrole") ).getValue().getActualValue();
            /*String projectNr = dataRow.dataField( SavedDataFieldCall.get("projectnr") ).getValue().getActualValue();*/
            String producerAbbr = dataRow.dataField( SavedDataFieldCall.get("producerabbr") ).getValue().getActualValue();

            ProducerType producerType = prodStmtType.addNewProducer();
            xmlCursor = producerType.newCursor();
            xmlCursor.setTextValue(producer);
            xmlCursor.dispose();
            // Set role
            producerType.setRole(producerRole);
            // Set abbreviation
            producerType.setAbbr(producerAbbr);
            // Set affiliation TODO: Where to get affiliation ?
            producerType.setAffiliation("");

        }

        // Add copyright, excel file row #65
        stt = prodStmtType.addNewCopyright();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(COPYRIGHT_FSD_AND_MATERIAL_SUPPLIER);
        xmlCursor.dispose();

        // Back to citation
        // Add distribution
        DistStmtType distStmtType = citationType.addNewDistStmt();

        // Add distributor
        DistrbtrType distrbtrType = distStmtType.addNewDistrbtr();
        xmlCursor = distrbtrType.newCursor();
        xmlCursor.setTextValue(FSD_NAME_FI);
        xmlCursor.dispose();
        // Set abbreviation
        distrbtrType.setAbbr(AGENCY);
        // Set URI
        distrbtrType.setURI(FSD_DISTRIBUTOR_BASE_URI);

        // TODO: Example XMLs have depositr and depDate elements inside distStmt. Not mentioned in excel ?

        // Back to citation
        // Add series statement, excel row #70
        SerStmtType serStmtType = citationType.addNewSerStmt();
        // series id is SELECTION type and series_list is REFERENCE type
        // TODO: Get series and data from it.
        String seriesId = revisionData.dataField( SavedDataFieldCall.get("seriesid") ).getValue().getActualValue();
        // Set study series URI TODO: Get correct field, should be series abbreviation like 'ess' or 'yks' etc.
        serStmtType.setURI(SERIES_BASE_URI + "placeholder");

        // Add new series name, excel row #72
        SerNameType serNameType = serStmtType.addNewSerName();
        // Set series name content TODO: Get series name
        xmlCursor = serNameType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Set series name abbreviation TODO: Get series abbreviation
        serNameType.setAbbr("placeholder");

        // Add new series info
        stt = serStmtType.addNewSerInfo();
        xmlCursor = stt.newCursor();
        // TODO: Get series info. This is contained in p-tags already ?
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Back to citation
        // Add version statement
        VerStmtType verStmtType = citationType.addNewVerStmt();

        // Add version, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("dataversions") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String version = dataRow.dataField( SavedDataFieldCall.get("version") ).getValue().getActualValue();
            // versiondate is DATE type TODO: Is the format it is in parseable with defaults ?
            String versionDate = dataRow.dataField( SavedDataFieldCall.get("versiondate") ).getValue().getActualValue();
            // String versionPro = dataRow.dataField( SavedDataFieldCall.get("versionpro") ).getValue().getActualValue();
            // String versionLabel = dataRow.dataField( SavedDataFieldCall.get("versionlabel") ).getValue().getActualValue();
            // String versionText = dataRow.dataField( SavedDataFieldCall.get("versiontext") ).getValue().getActualValue();
            // String versionNotes = dataRow.dataField( SavedDataFieldCall.get("versionnotes") ).getValue().getActualValue();

            VersionType versionType = verStmtType.addNewVersion();
            // Set version content
            xmlCursor = versionType.newCursor();
            xmlCursor.setTextValue(version);
            xmlCursor.dispose();
            // Set date
            LocalDate localDate = LocalDate.parse(versionDate);
            versionType.setDate( DATE_TIME_FORMATTER.print(localDate) );
        }

        // Back to citation
        // Add bibl (?) citation, excel row #78
        BiblCitType biblCitType = citationType.addNewBiblCit();
        String biblCitation = revisionData.dataField( SavedDataFieldCall.get("biblcit") ).getValue().getActualValue();
        xmlCursor = biblCitType.newCursor();
        xmlCursor.setTextValue( biblCitation );
        xmlCursor.dispose();

        // TODO: Example XMLs neither had studyAuthorization element or children. Check if excel is correct ?
        // Back to study description
        // Add study authorization
        StudyAuthorizationType studyAuthorizationType = stdyDscrType.addNewStudyAuthorization();

        // Add authorizing agency TODO: Certain cases depending on authortype, see excel row #80 - #82
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("authors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // authortype is SELECTION type for authortype_list which is a REFERENCE type
            // String authorType = dataRow.dataField( SavedDataFieldCall.get("authortype") ).getValue().getActualValue();
            String author = dataRow.dataField( SavedDataFieldCall.get("author") ).getValue().getActualValue();
            String affiliation = dataRow.dataField( SavedDataFieldCall.get("affiliation") ).getValue().getActualValue();
            AuthorizingAgencyType authorizingAgencyType = studyAuthorizationType.addNewAuthorizingAgency();
            xmlCursor = authorizingAgencyType.newCursor();
            xmlCursor.setTextValue(author);
            xmlCursor.dispose();
            // Set affiliation
            authorizingAgencyType.setAffiliation(affiliation);
            // Set abbreviation TODO: Where to get this ?
            authorizingAgencyType.setAbbr("placeholder");
        }

        // Back to study description
        // Add study info
        StdyInfoType stdyInfoType = stdyDscrType.addNewStdyInfo();

        // Add subject, excel row #84
        SubjectType subjectType= stdyInfoType.addNewSubject();

        // Add keyword, repeatable TODO: Keyword has vocab or does not and values depend on that see excel row #85 - #89
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("keywords") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String keywordVocab = dataRow.dataField( SavedDataFieldCall.get("keywordvocab") ).getValue().getActualValue();
            String keyword = dataRow.dataField( SavedDataFieldCall.get("keyword") ).getValue().getActualValue();
            // String keywordNoVocab = dataRow.dataField( SavedDataFieldCall.get("keywordnovocab") ).getValue().getActualValue();
            String keywordVocabURI = dataRow.dataField( SavedDataFieldCall.get("keywordvocaburi") ).getValue().getActualValue();
            // String keywordURI = dataRow.dataField( SavedDataFieldCall.get("keyworduri") ).getValue().getActualValue();

            KeywordType keywordType = subjectType.addNewKeyword();
            xmlCursor = keywordType.newCursor();
            xmlCursor.setTextValue(keyword);
            xmlCursor.dispose();
            // Set vocabulary
            keywordType.setVocab(keywordVocab);
            // Set vocabulary URI
            keywordType.setVocabURI(keywordVocabURI);
            // Set source, excel row #89 TODO: What is the value stored, number or text ?
            // 1 archive, 2 producer
            keywordType.setSource(BaseElementType.Source.Enum.forInt(1));
        }

        // Add topic class, repeatable, excel row #90 - #93
        // TODO: topicvocab is SELECTION type for topicvocab_list which is reference
        // TODO: topic is SELECTION type for topic_list which is reference
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("topics") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Get references and data from them and insert correct values
            String topicVocab = dataRow.dataField( SavedDataFieldCall.get("topicvocab") ).getValue().getActualValue();
            String topic = dataRow.dataField( SavedDataFieldCall.get("topic") ).getValue().getActualValue();

            TopcClasType topcClasType = subjectType.addNewTopcClas();
            xmlCursor = topcClasType.newCursor();
            xmlCursor.setTextValue("placeholder");
            xmlCursor.dispose();
            // Set topic class vocabulary
            topcClasType.setVocab("placeholder");
            // Set topic class vocabulary URI
            topcClasType.setVocabURI("placeholder");
            // Set topic class source TODO: What is the value stored, number or text?
            // 1 archive, 2 producer
            topcClasType.setSource(BaseElementType.Source.Enum.forInt(2));
        }

        // Back to study info
        // Add abstract, excel row #94 TODO: This is contained in p-tags already ?
        AbstractType abstractType = stdyInfoType.addNewAbstract();
        String abs = revisionData.dataField( SavedDataFieldCall.get("abstract") ).getValue().getActualValue();
        xmlCursor = abstractType.newCursor();
        xmlCursor.setTextValue(abs);
        xmlCursor.dispose();

        // Add summary description
        SumDscrType sumDscrType = stdyInfoType.addNewSumDscr();

        // Add time period, repeatable, excel row #96 - #98
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("timeperiods") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // timeperiod is DATE type
            String timePeriod = dataRow.dataField( SavedDataFieldCall.get("timeperiod") ).getValue().getActualValue();
            LocalDate localDate = LocalDate.parse(timePeriod);
            String timePeriodText = dataRow.dataField( SavedDataFieldCall.get("timeperiodtext") ).getValue().getActualValue();
            // timeperiodevent is SELECTION type for timeperiodevent_list which is SUBLIST type for sublistKey start_end_single
            String timePeriodEvent = dataRow.dataField( SavedDataFieldCall.get("timeperiodevent") ).getValue().getActualValue();

            TimePrdType timePrdType = sumDscrType.addNewTimePrd();
            xmlCursor = timePrdType.newCursor();
            xmlCursor.setTextValue(timePeriodText);
            xmlCursor.dispose();
            // Set date
            timePrdType.setDate( DATE_TIME_FORMATTER.print(localDate) );
            // Set event
            // values 1/2/3 -> start/end/single TODO: What is the saved value, number or text ?
            timePrdType.setEvent( TimePrdType.Event.Enum.forString(timePeriodEvent) );
        }

        // Add collection time, repeatable, excel row #99 - #101
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("colltime") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // colldate is DATE type
            String collDate = dataRow.dataField( SavedDataFieldCall.get("colldate") ).getValue().getActualValue();
            LocalDate localDate = LocalDate.parse(collDate);
            String collDateText = dataRow.dataField( SavedDataFieldCall.get("colldatetext") ).getValue().getActualValue();
            // colldateevent is SELECTION type for colldateevent_list
            String collDateEvent = dataRow.dataField( SavedDataFieldCall.get("colldateevent") ).getValue().getActualValue();

            CollDateType collDateType = sumDscrType.addNewCollDate();
            xmlCursor = collDateType.newCursor();
            xmlCursor.setTextValue(collDateText);
            xmlCursor.dispose();
            // Set date
            collDateType.setDate( DATE_TIME_FORMATTER.print(localDate) );
            // Set event
            // values 1/2/3 start/end/single TODO: What is the saved value, number or text ?
            collDateType.setEvent( CollDateType.Event.Enum.forString(collDateEvent) );
        }

        // Add nation, repeatable, excel row #102 - #105
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("countries") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String country = dataRow.dataField( SavedDataFieldCall.get("country") ).getValue().getActualValue();
            String countryAbbr = dataRow.dataField( SavedDataFieldCall.get("countryabbr") ).getValue().getActualValue();

            NationType nationType = sumDscrType.addNewNation();
            xmlCursor = nationType.newCursor();
            xmlCursor.setTextValue(country);
            xmlCursor.dispose();
            // Set nation abbreviation
            nationType.setAbbr(countryAbbr);
            // TODO: countries.countryfinland=true missing from data ?
        }

        // Add geographical coverage, repeatable, excel row #106
        // TODO: Excel says in export content of nations field are copied to geogcovers ?
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("geogcovers") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String geographicalCover = dataRow.dataField( SavedDataFieldCall.get("geogcover") ).getValue().getActualValue();

            ConceptualTextType conceptualTextType = sumDscrType.addNewGeogCover();
            xmlCursor = conceptualTextType.newCursor();
            xmlCursor.setTextValue(geographicalCover);
            xmlCursor.dispose();
        }

        // Add analyzing unit, repeatable, row #107 - #112
        // TODO: Analysis is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("analysis") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String analysisUnit = dataRow.dataField( SavedDataFieldCall.get("analysisunit") ).getValue().getActualValue();
            String analysisUnitVocab = dataRow.dataField( SavedDataFieldCall.get("analysisunitvocab") ).getValue().getActualValue();
            String analysisUnitVocabURI = dataRow.dataField( SavedDataFieldCall.get("analysisunitvocaburi") ).getValue().getActualValue();
            String analysisUnitURI = dataRow.dataField( SavedDataFieldCall.get("analysisunituri") ).getValue().getActualValue();
            String analysisUnitOther = dataRow.dataField( SavedDataFieldCall.get("analysisunitother") ).getValue().getActualValue();

            AnlyUnitType anlyUnitType = sumDscrType.addNewAnlyUnit();
            // TODO: Some 2.1 examples have f.ex. "Henkilö" inside anlyUnit then also concept element inside it.
            // TODO: In excel it's top element so shouldn't have content inside it ?
            // Add concept type
            ConceptType conceptType = anlyUnitType.addNewConcept();
            xmlCursor = conceptType.newCursor();
            xmlCursor.setTextValue(analysisUnit);
            xmlCursor.dispose();
            // Set vocabulary
            conceptType.setVocab(analysisUnitVocab);
            // Set vocabulary URI
            conceptType.setVocabURI(analysisUnitVocabURI);
            // Set source, 1 archive and 2 producer TODO: What value is saved, number or text ?
            conceptType.setSource(BaseElementType.Source.Enum.forString(analysisUnitURI));

            /*
            // Add new text (only if anlyUnit/concept=Muu havaintoyksikkö TAI Maantieteellinen alue)
            // TODO: How to check ? What path? Special handling for cases see excel row #112
            TxtType txtType = anlyUnitType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue(analysisUnitOther);
            xmlCursor.dispose();
            */
        }

        // Add universe, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("universes")).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String universe = dataRow.dataField( SavedDataFieldCall.get("universe") ).getValue().getActualValue();
            // universeclusion is SELECTION type for universeclusion_list which is VALUE type
            String universeClusion = dataRow.dataField( SavedDataFieldCall.get("universeclusion") ).getValue().getActualValue();

            UniverseType universeType = sumDscrType.addNewUniverse();
            xmlCursor = universeType.newCursor();
            xmlCursor.setTextValue(universe);
            xmlCursor.dispose();
            // Add (I/E)clusion, 1 I, 2 E
            universeType.setClusion(UniverseType.Clusion.Enum.forInt( Integer.parseInt(universeClusion) ));
        }

        // Add data kind
        // datakind is SELECTION type for datakind_list which is VALUE type
        String dataKind = revisionData.dataField( SavedDataFieldCall.get("datakind") ).getValue().getActualValue();
        Field field = configuration.getField("datakind");
        String dataKindSelectionListKey = field.getSelectionList();
        SelectionList selectionList = configuration.getSelectionList(dataKindSelectionListKey);
        Option option = selectionList.getOptionWithValue(dataKind);
        // TODO: Default title for now, later on replace with the correct from language code ?
        String dataKindSelectedOptionValueTranslatedTitle = option.getDefaultTitle();
        DataKindType dataKindType = sumDscrType.addNewDataKind();
        xmlCursor = dataKindType.newCursor();
        xmlCursor.setTextValue(dataKindSelectedOptionValueTranslatedTitle);
        xmlCursor.dispose();

        // Back to study description
        // Add method, row #116
        MethodType methodType = stdyDscrType.addNewMethod();

        // Add data column
        DataCollType dataCollType = methodType.addNewDataColl();

        // Add time method, repeatable
        // TODO: Timemethods is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("timemethods") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String timeMethodTopic = dataRow.dataField( SavedDataFieldCall.get("topic") ).getValue().getActualValue();
            String timeMethodTopicVocab = dataRow.dataField( SavedDataFieldCall.get("topicvocab") ).getValue().getActualValue();

            TimeMethType timeMethType = dataCollType.addNewTimeMeth();
            xmlCursor = timeMethType.newCursor();
            // TODO: Excel #118 says this is "upper" element so it should not contain text (?)
            // TODO: But in example XMLs it has actual text value in it plus the concept element
            // TODO: Excel does not mention path for the value of this either
            xmlCursor.setTextValue("");
            xmlCursor.dispose();
            // Add new concept
            ConceptType conceptType = timeMethType.addNewConcept();
            // Set vocabulary
            conceptType.setVocab("");
            // Set vocabulary URI
            conceptType.setVocabURI("");
            // TODO: Typo in excel row #122 path ?
            // 1 archive, 2 producer
            conceptType.setSource(BaseElementType.Source.Enum.forString(""));

            // TODO: Only if condition is met, see excel row #123
            TxtType txtType = timeMethType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue("");
            xmlCursor.dispose();
        }

        // Add data collector, repeatable, excel row #124 - #127
        // TODO: Conditions apply for this see excel
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collectors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: authortype is SELECTION type for authortype_list which is REFERENCE type
            String authorType = dataRow.dataField( SavedDataFieldCall.get("authortype") ).getValue().getActualValue();
            String collector = dataRow.dataField( SavedDataFieldCall.get("collector") ).getValue().getActualValue();
            String collectorAffiliation = dataRow.dataField( SavedDataFieldCall.get("collectoraffiliation") ).getValue().getActualValue();

            // TODO: Conditional values depending on collectortype (authortype ?)
            DataCollectorType dataCollectorType = dataCollType.addNewDataCollector();
            xmlCursor = dataCollectorType.newCursor();
            xmlCursor.setTextValue(collector);
            xmlCursor.dispose();
            // Set data collector abbreviation
            dataCollectorType.setAbbr("");
            // Set data collector affiliation
            dataCollectorType.setAffiliation(collectorAffiliation);
        }

        // Add sample procurement, repeatable, row #128 - #134
        // TODO: Get correct values from reference and dependency
        // TODO: Is this supposed to be a vocabulary implementation similiar to analysis ?
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("sampprocs") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // sampproc is SELECTION type for sampproc_list which is REFERENCE type
            String sampProc = dataRow.dataField( SavedDataFieldCall.get("sampproc") ).getValue().getActualValue();
            // sampprocdesc is REFERENCE type for sampprocdesc_ref which is DEPENDENCY type
            String sampProcDesc = dataRow.dataField( SavedDataFieldCall.get("sampprocdesc") ).getValue().getActualValue();

            ConceptualTextType conceptualTextType = dataCollType.addNewSampProc();
            // Add concept
            ConceptType conceptType = conceptualTextType.addNewConcept();
            xmlCursor = conceptType.newCursor();
            xmlCursor.setTextValue("placeholder");
            xmlCursor.dispose();
            // Set vocabulary
            conceptType.setVocab("placeholder");
            // Set vocabulary URI
            conceptType.setVocabURI("placeholder");
            // Set source
            conceptType.setSource(BaseElementType.Source.Enum.forInt(2));
            // Add new text TODO: Conditional value based on sampproc
            TxtType txtType = conceptualTextType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue("");
            xmlCursor.dispose();
        }

        // Add collection mode, excel row #135 - #140
        // TODO: Collmodes is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collmodes") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String collModesTopic = dataRow.dataField( SavedDataFieldCall.get("topic") ).getValue().getActualValue();
            String collModesTopicVocab = dataRow.dataField( SavedDataFieldCall.get("topicvocab") ).getValue().getActualValue();

            ConceptualTextType conceptualTextType = dataCollType.addNewCollMode();
            // Add concept
            ConceptType conceptType = conceptualTextType.addNewConcept();
            xmlCursor = conceptType.newCursor();
            xmlCursor.setTextValue("placeholder");
            xmlCursor.dispose();
            // Set vocabulary
            conceptType.setVocab("placeholder");
            // Set vocabulary URI
            conceptType.setVocabURI("placeholder");
            // Set source
            conceptType.setSource(BaseElementType.Source.Enum.forInt(2));
            // Add new text TODO: Conditional value based on concept
            TxtType txtType = conceptualTextType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue("");
            xmlCursor.dispose();
        }

        // Add resource instrumentation, excel row #141 - #146
        // TODO: Instruments is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collmodes") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String instrumentsTopic = dataRow.dataField( SavedDataFieldCall.get("topic") ).getValue().getActualValue();
            String instrumentsTopicVocab = dataRow.dataField( SavedDataFieldCall.get("topicvocab") ).getValue().getActualValue();

            ResInstruType resInstruType = dataCollType.addNewResInstru();
            // Add concept
            ConceptType conceptType = resInstruType.addNewConcept();
            xmlCursor = conceptType.newCursor();
            xmlCursor.setTextValue("placeholder");
            xmlCursor.dispose();
            // Set vocabulary
            conceptType.setVocab("placeholder");
            // Set vocabulary URI
            conceptType.setVocabURI("placeholder");
            // Set source
            conceptType.setSource(BaseElementType.Source.Enum.forInt(2));
            // Add new text TODO: Conditional value based on concept
            TxtType txtType = resInstruType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue("");
            xmlCursor.dispose();
        }

        // Add sources, excel row #147 - #148
        SourcesType sourcesType = dataCollType.addNewSources();
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("datasources") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String datasource = dataRow.dataField( SavedDataFieldCall.get("datasource") ).getValue().getActualValue();

            // Add new data source
            stt = sourcesType.addNewDataSrc();
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(datasource);
            xmlCursor.dispose();
        }

        // Add weight, repeatable TODO: Repeatable for STRING and BOOLEAN type ?
        // TODO: Excel row #149 - #150 slightly unclear about this
        String weightYesNo = revisionData.dataField( SavedDataFieldCall.get("weightyesno") ).getValue().getActualValue();
        stt = dataCollType.addNewWeight();
        if (Boolean.parseBoolean(weightYesNo)) {
            String weight = revisionData.dataField( SavedDataFieldCall.get("weight") ).getValue().getActualValue();
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(weight);
            xmlCursor.dispose();
        } else {
            // TODO: Excel says it should be standard text, where to get it?
            xmlCursor = stt.newCursor();
            xmlCursor.setTextValue("placeholder");
            xmlCursor.dispose();
        }

        // Back to method
        // Add notes, excel row #151
        String dataProcessing = revisionData.dataField( SavedDataFieldCall.get("dataprosessing") ).getValue().getActualValue();
        NotesType notesType = methodType.addNewNotes();
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue(dataProcessing);
        xmlCursor.dispose();

        // Add analyze info
        AnlyInfoType anlyInfoType = methodType.addNewAnlyInfo();

        // Add response rate
        String respRate = revisionData.dataField( SavedDataFieldCall.get("resprate") ).getValue().getActualValue();
        stt = anlyInfoType.addNewRespRate();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(respRate);
        xmlCursor.dispose();

        // Add data appraisal, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("appraisals") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String appraisal = dataRow.dataField( SavedDataFieldCall.get("appraisal") ).getValue().getActualValue();
            DataApprType dataApprType = anlyInfoType.addNewDataAppr();
            xmlCursor = dataApprType.newCursor();
            xmlCursor.setTextValue(appraisal);
            xmlCursor.dispose();
        }

        // Add data access, row #155 - #168
        DataAccsType dataAccsType = stdyDscrType.addNewDataAccs();

        // Add set availability
        SetAvailType setAvailType = dataAccsType.addNewSetAvail();

        // Add access place
        AccsPlacType accsPlacType = setAvailType.addNewAccsPlac();
        xmlCursor = accsPlacType.newCursor();
        xmlCursor.setTextValue(ACCS_PLAC_CONTENT);
        xmlCursor.dispose();
        // Set URI
        accsPlacType.setURI(ACCS_PLAC_URI);

        // Add original archive
        String originalLocation = revisionData.dataField( SavedDataFieldCall.get("originallocation") ).getValue().getActualValue();
        stt = setAvailType.addNewOrigArch();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(originalLocation);
        xmlCursor.dispose();

        // Add collection size
        String collSize = revisionData.dataField( SavedDataFieldCall.get("collsize") ).getValue().getActualValue();
        stt = setAvailType.addNewCollSize();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(collSize);
        xmlCursor.dispose();

        // Add complete
        String complete = revisionData.dataField( SavedDataFieldCall.get("complete") ).getValue().getActualValue();
        stt = setAvailType.addNewComplete();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(complete);
        xmlCursor.dispose();

        // Add use statement, excel row #162 - #167
        UseStmtType useStmtType = dataAccsType.addNewUseStmt();

        // Add special permissions
        String specialTOS = revisionData.dataField( SavedDataFieldCall.get("specialtermsofuse") ).getValue().getActualValue();
        SpecPermType specPermType = useStmtType.addNewSpecPerm();
        xmlCursor = specPermType.newCursor();
        xmlCursor.setTextValue(specialTOS);
        xmlCursor.dispose();

        // Add restrictions, excel row #164
        // TODO: Standard text value depends on 'termsofuse'
        // TODO: Does it mean the SELECTION type title or some other value ?
        String tos = revisionData.dataField( SavedDataFieldCall.get("termsofuse") ).getValue().getActualValue();
        field = configuration.getField("termsofuse");
        String tosSelectionListKey = field.getSelectionList();
        selectionList = configuration.getSelectionList(tosSelectionListKey);
        option = selectionList.getOptionWithValue(tos);
        // TODO: Default title for now, later on replace with the correct from language code ?
        String tosSelectedOptionValueTranslatedTitle = option.getDefaultTitle();
        stt = useStmtType.addNewRestrctn();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(tosSelectedOptionValueTranslatedTitle);
        xmlCursor.dispose();

        // Add citation required
        stt = useStmtType.addNewCitReq();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(CIT_REQUIRED_CONTENT);
        xmlCursor.dispose();
        // Add deposition required
        stt = useStmtType.addNewDeposReq();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(DEPOS_REQUIRED_CONTENT);
        xmlCursor.dispose();
        // Add disclaimer required
        stt = useStmtType.addNewDisclaimer();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue(DISCLAIMER_CONTENT);
        xmlCursor.dispose();

        // Add notes, excel row #168
        String notes = revisionData.dataField( SavedDataFieldCall.get("datasetnotes") ).getValue().getActualValue();
        notesType = dataAccsType.addNewNotes();
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue(notes);
        xmlCursor.dispose();

        // Back to study description
        // Add other study material, excel row #169 - #173
        OthrStdyMatType othrStdyMatType = stdyDscrType.addNewOthrStdyMat();

        // Add related materials, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("relatedmaterials") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String relatedMaterial = dataRow.dataField( SavedDataFieldCall.get("relatedmaterial") ).getValue().getActualValue();
            RelMatType relMatType = othrStdyMatType.addNewRelMat();
            xmlCursor = relMatType.newCursor();
            xmlCursor.setTextValue(relatedMaterial);
            xmlCursor.dispose();
        }

        // Add related studies, repeatable
        // TODO: No such fields as mentioned in excel #171 path
        // TODO: Concatenated value of relatedstudies.id and relatedstudies.title
        MaterialReferenceType materialReferenceType = othrStdyMatType.addNewRelStdy();
        xmlCursor = materialReferenceType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add related publications, repeatable
        // TODO: No such fields as mentioned in excel #172
        // TODO: Should be publications marked for this study
        materialReferenceType = othrStdyMatType.addNewRelPubl();
        xmlCursor = materialReferenceType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add other references, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("publicationcomments") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String publicationComment = dataRow.dataField( SavedDataFieldCall.get("publicationcomment") ).getValue().getActualValue();
            OthRefsType othRefsType = othrStdyMatType.addNewOthRefs();
            xmlCursor = othRefsType.newCursor();
            xmlCursor.setTextValue(publicationComment);
            xmlCursor.dispose();
        }
    }

    /**
     * Create file description to codebook document
     *
     * @param revisionData revision data
     * @param configuration configuration
     * @param codeBookDocument codebook document
     */
    private void addfileDescription(RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        //TODO: Not implemented yet, excel path references variables not found in configuration
        // Get codebook
        CodeBookType codeBookType = codeBookDocument.getCodeBook();

        // Add file description, excel row #174
        FileDscrType fileDscrType = codeBookType.addNewFileDscr();

        // Add file text, excel row #175 - #183
        FileTxtType fileTxtType = fileDscrType.addNewFileTxt();

        // Add  file name
        SimpleTextType stt = fileTxtType.addNewFileName();
        XmlCursor xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
        // Set ID
        stt.setID("placeholder");

        // Add software
        SoftwareType softwareType = fileTxtType.addNewSoftware();
        // Set version
        softwareType.setVersion("placeholder");

        // Add dimensions
        DimensnsType dimensnsType = fileTxtType.addNewDimensns();

        // Add case quantity
        stt = dimensnsType.addNewCaseQnty();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add variable quantity
        stt = dimensnsType.addNewVarQnty();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add file type
        FileTypeType fileTypeType = fileTxtType.addNewFileType();
        xmlCursor = fileTypeType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
    }

    /**
     * Create data description to codebook document
     *
     * @param revisionData revision data
     * @param configuration configuration
     * @param codeBookDocument codebook document
     */
    private void addDataDescription(RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        // Get codebook
        CodeBookType codeBookType = codeBookDocument.getCodeBook();

        // Add data description, excel row #184
        DataDscrType dataDscrType = codeBookType.addNewDataDscr();

        // Add variable group, repeatable TODO: Implement
        VarGrpType varGrpType = dataDscrType.addNewVarGrp();

        // Set variable TODO: Implement
        List<String> list = new ArrayList<>();
        list.add("placeholder");
        varGrpType.setVar(list);

        // Add variable group text, repeatable TODO: Implement
        TxtType txtType = varGrpType.addNewTxt();
        XmlCursor xmlCursor = txtType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add variable, repeatable, excel row #188 - #208
        // TODO: Not implemented, excel path references variables not found in configuration
        // TODO: See example XMLs as to what it should look like
        VarType varType = dataDscrType.addNewVar();
        // Set ID
        varType.setID("placeholder");
        // Set name
        varType.setName("placeholder");
        // Set files
        list = new ArrayList<>();
        list.add("placeholder");
        varType.setFiles(list);
        // Set interval, 1 contin 2 discrete
        varType.setIntrvl(VarType.Intrvl.Enum.forInt(2));

        // Add label
        LablType lablType = varType.addNewLabl();
        xmlCursor = lablType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add security
        SimpleTextAndDateType stadt = varType.addNewSecurity();
        xmlCursor = stadt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add question, excel row #195
        QstnType qstnType = varType.addNewQstn();

        // Add prequel text for question
        SimpleTextType stt = qstnType.addNewPreQTxt();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add question lit
        QstnLitType qstnLitType = qstnType.addNewQstnLit();
        xmlCursor = qstnLitType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add postquel text for question
        stt = qstnType.addNewPostQTxt();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add interviewer instructions
        stt = qstnType.addNewIvuInstr();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Back to var
        // Add summary statistics, excel row #200
        SumStatType sumStatType = varType.addNewSumStat();

        // Set type, values 1-9 see the enum for meaning
        sumStatType.setType(SumStatType.Type.Enum.forInt(1));

        // Add text element
        txtType = varType.addNewTxt();
        xmlCursor = txtType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add category element, row #203
        CatgryType catgryType = varType.addNewCatgry();
        // Set missing, 1 Y and 2 N
        catgryType.setMissing(CatgryType.Missing.Enum.forInt(2));

        // Add category value
        stt = catgryType.addNewCatValu();
        xmlCursor = stt.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add label
        lablType = catgryType.addNewLabl();
        xmlCursor = lablType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Add category statistics
        CatStatType catStatType = catgryType.addNewCatStat();
        xmlCursor = catStatType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();

        // Back to var
        // Add notes, excel row #208
        NotesType notesType = varType.addNewNotes();
        xmlCursor = notesType.newCursor();
        xmlCursor.setTextValue("placeholder");
        xmlCursor.dispose();
    }

    /**
     * Create other material description to codebook document
     *
     * @param revisionData revision data
     * @param configuration configuration
     * @param codeBookDocument codebook document
     */
    private void addOtherMaterialDescription(RevisionData revisionData, Configuration configuration, CodeBookDocument codeBookDocument) {
        // Get codebook
        CodeBookType codeBookType = codeBookDocument.getCodeBook();

        // Add other material, repeatable, row #209
        ContainerDataField containerDataField = revisionData.dataField( ContainerDataFieldCall.get("othermaterials") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String otherMaterialURI = dataRow.dataField( SavedDataFieldCall.get("othermaterialuri") ).getValue().getActualValue();
            String otherMaterialLabel = dataRow.dataField( SavedDataFieldCall.get("othermateriallabel") ).getValue().getActualValue();
            String otherMaterialText = dataRow.dataField( SavedDataFieldCall.get("othermaterialtext") ).getValue().getActualValue();

            OtherMatType otherMatType = codeBookType.addNewOtherMat();
            // Set URI
            otherMatType.setURI(otherMaterialURI);

            // Add label
            LablType lablType = otherMatType.addNewLabl();
            XmlCursor xmlCursor = lablType.newCursor();
            xmlCursor.setTextValue(otherMaterialLabel);
            xmlCursor.dispose();

            // Add text
            TxtType txtType = otherMatType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue(otherMaterialText);
            xmlCursor.dispose();
        }
    }

}
