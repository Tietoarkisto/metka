package fi.uta.fsd.metka.ddi;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.*;

import static fi.uta.fsd.metka.ddi.DDIBuilder.*;

class DDIStudyDescription {
    private static final Map<Language, String> ACCS_PLAC = new HashMap<>();
    private static final Map<Language, String> ACCS_PLAC_URI = new HashMap<>();
    private static final Map<String, Map<Language, String>> RESTRICTION = new HashMap<>();
    private static final Map<Language, String> CIT_REQ = new HashMap<>();
    private static final Map<Language, String> DEPOS_REQ = new HashMap<>();
    private static final Map<Language, String> DISCLAIMER = new HashMap<>();
    private static final Map<Language, String> SERIES_URI_PREFIX = new HashMap<>();
    private static final Map<Language, String> WEIGHT_NO = new HashMap<>();
    private static final Map<Language, String> COPYRIGHT = new HashMap<>();
    private static final Map<Language, String> DISTRIBUTR = new HashMap<>();
    private static final Map<Language, String> DISTRIBUTR_ABB = new HashMap<>();
    private static final Map<Language, String> DISTRIBUTR_URI = new HashMap<>();
    private static final Map<Language, String> NATION = new HashMap<>();

    static {
        ACCS_PLAC.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto");
        ACCS_PLAC.put(Language.EN, "Finnish Social Science Data Archive");
        ACCS_PLAC.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv");

        ACCS_PLAC_URI.put(Language.DEFAULT, "http://www.fsd.uta.fi");
        ACCS_PLAC_URI.put(Language.EN, "http://www.fsd.uta.fi");
        ACCS_PLAC_URI.put(Language.SV, "http://www.fsd.uta.fi");

        Map<Language, String> tempMap = new HashMap<>();
        RESTRICTION.put("1", tempMap);
        tempMap.put(Language.DEFAULT, "Aineisto on kaikkien käytettävissä.");
        tempMap.put(Language.EN, "The dataset is available for all users.");
        tempMap.put(Language.SV, "??");

        tempMap = new HashMap<>();
        RESTRICTION.put("2", tempMap);
        tempMap.put(Language.DEFAULT, "Aineisto on käytettävissä tutkimukseen, opetukseen ja opiskeluun.");
        tempMap.put(Language.EN, "The dataset is available for research, teaching and study.");
        tempMap.put(Language.SV, "??");

        tempMap = new HashMap<>();
        RESTRICTION.put("3", tempMap);
        tempMap.put(Language.DEFAULT, "Aineisto on käytettävissä vain tutkimukseen ja ylempiin opinnäytteisiin (pro gradu, lisensiaattitutkimus ja väitöstutkimus).");
        tempMap.put(Language.EN, "The dataset is available for research and for Master's, licentiate and doctoral theses.");
        tempMap.put(Language.SV, "??");

        tempMap = new HashMap<>();
        RESTRICTION.put("4", tempMap);
        tempMap.put(Language.DEFAULT, "Aineisto on käytettävissä vain luovuttajan luvalla.");
        tempMap.put(Language.EN, "The dataset is available by the permission of the depositor only.");
        tempMap.put(Language.SV, "??");

        tempMap = new HashMap<>();
        RESTRICTION.put("5", tempMap);
        tempMap.put(Language.DEFAULT, "Aineisto on jatkokäytettävissä vasta määräajan jälkeen tietystä päivämäärästä alkaen.");
        tempMap.put(Language.EN, "The dataset is available only after a specified time.");
        tempMap.put(Language.SV, "??");

        CIT_REQ.put(Language.DEFAULT, "Aineistoon ja sen tekijöihin tulee viitata asianmukaisesti kaikissa julkaisuissa ja esityksissä, joissa aineistoa käytetään. Tietoarkiston antaman malliviittaustiedon voi merkitä lähdeluetteloon sellaisenaan tai sitä voi muokata julkaisun käytäntöjen mukaisesti.");
        CIT_REQ.put(Language.EN, "The data and its creators shall be cited in all publications and presentations for which the data have been used. The bibliographic citation may be in the form suggested by the archive or in the form required by the publication.");
        CIT_REQ.put(Language.SV, "Publikationer och presentationer som helt eller delvis baseras på datamaterialet ska förses med vederbörlig hänvisning till primärforskarna och det berörda datamaterialet. Referensen kan vara i den stil som krävs av publikationen eller i den stil som rekommenderas av dataarkivet.");

        DEPOS_REQ.put(Language.DEFAULT, "Tietoarkistoon on lähetettävä viitetiedot kaikista julkaisuista, joissa käyttäjä hyödyntää aineistoa.");
        DEPOS_REQ.put(Language.EN, "The user shall notify the archive of all publications where she or he has used the data.");
        DEPOS_REQ.put(Language.SV, "Referenser till alla publikationer där användaren har utnyttjat datamaterialet ska sändas till dataarkivet.");

        DISCLAIMER.put(Language.DEFAULT, "Aineiston alkuperäiset tekijät ja tietoarkisto eivät ole vastuussa aineiston jatkokäytössä tuotetuista tuloksista ja tulkinnoista.");
        DISCLAIMER.put(Language.EN, "The original data creators and the archive bear no responsibility for any results or interpretations arising from the reuse of the data.");
        DISCLAIMER.put(Language.SV, "Varken primärforskarna (dvs. de ursprungliga rättsinnehavarna) eller dataarkivet är ansvariga för sådana analysresultat och tolkningar av datamaterialet som uppstått vid sekundäranalys.");

        SERIES_URI_PREFIX.put(Language.DEFAULT, "http://www.fsd.uta.fi/fi/aineistot/luettelo/sarjat.html#");
        SERIES_URI_PREFIX.put(Language.EN, "http://www.fsd.uta.fi/en/data/catalogue/series.html#");
        SERIES_URI_PREFIX.put(Language.SV, "http://www.fsd.uta.fi/sv/data/serier.html#");

        WEIGHT_NO.put(Language.DEFAULT, "Aineisto ei sisällä painomuuttujia.");
        WEIGHT_NO.put(Language.EN, "There are no weight variables in the data.");
        WEIGHT_NO.put(Language.SV, "Datamaterialet innehåller inga viktvariabler.");

        COPYRIGHT.put(Language.DEFAULT, "FSD:n ja aineiston luovuttajan tekemän sopimuksen mukaisesti.");
        COPYRIGHT.put(Language.EN, "According to the agreement between FSD and the depositor.");
        COPYRIGHT.put(Language.SV, "I enlighet med avtalet mellan FSD och överlåtaren av datamaterialet.");

        DISTRIBUTR.put(Language.DEFAULT, "");
        DISTRIBUTR.put(Language.EN, "");
        DISTRIBUTR.put(Language.SV, "");

        DISTRIBUTR_ABB.put(Language.DEFAULT, "FSD");
        DISTRIBUTR_ABB.put(Language.EN, "FSD");
        DISTRIBUTR_ABB.put(Language.SV, "FSD");

        DISTRIBUTR_URI.put(Language.DEFAULT, "http://www.fsd.uta.fi");
        DISTRIBUTR_URI.put(Language.EN, "http://www.fsd.uta.fi");
        DISTRIBUTR_URI.put(Language.SV, "http://www.fsd.uta.fi");

        NATION.put(Language.DEFAULT, "Suomi");
        NATION.put(Language.EN, "Finland");
        NATION.put(Language.SV, "Finland");
    }

    static void addStudyDescription(RevisionData revision, Language language, Configuration configuration, CodeBookType codeBookType, RevisionRepository revisions, ReferenceService references) {
        // Add study description to codebook
        StdyDscrType stdyDscrType = codeBookType.addNewStdyDscr();

        addCitationInfo(stdyDscrType, revision, language, configuration, revisions);

        addStudyAuthorization(revision, stdyDscrType);

        addStudyInfo(stdyDscrType, revision, language, configuration, references);

        addMethod(stdyDscrType, revision, language, references);

        addDataAccess(stdyDscrType, revision, configuration, language);

        addOtherStudyMaterial(stdyDscrType, revision, language, revisions);
    }

    private static void addCitationInfo(StdyDscrType stdyDscrType, RevisionData revisionData, Language language, Configuration configuration, RevisionRepository revisions) {
        // Add citation
        CitationType citationType = stdyDscrType.addNewCitation();

        addCitationTitle(revisionData, language, citationType, configuration);

        addCitationRspStatement(revisionData, citationType);

        addCitationProdStatement(revisionData, citationType);

        addCitationDistStatement(citationType);

        // Add SerStmt
        addCitationSerStatement(citationType, revisionData, language, revisions);

        // Add VerStmt
        addCitationVerStatement(citationType, revisionData, language);

        // Add biblcit
        // TODO: Concatenate biblcit during saving
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.BIBLCIT));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(citationType.addNewBiblCit(), valueFieldPair, Language.DEFAULT);
        }
    }

    private static void addCitationProdStatement(RevisionData revisionData, CitationType citationType) {
        /*// TODO: Add prod statement
        // Back to citation
        // Add producers , excel row #60
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        // Add new producer, repeatable, excel row #61 - #64
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("producers") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Excel indicates fields like produceragency, producersetction,producerorganization.
            // TODO: Not implemented yet / missing ?
            // TODO: Need to recheck excel and fields for correct values set into fields
            String producer = dataRow.dataField( ValueDataFieldCall.get("producer") ).getValue().getActualValue();
            String producerId = dataRow.dataField( ValueDataFieldCall.get("producerid") ).getValue().getActualValue();
            String producerIdType = dataRow.dataField( ValueDataFieldCall.get("produceridtype") ).getValue().getActualValue();
            String producerRole = dataRow.dataField( ValueDataFieldCall.get("producerrole") ).getValue().getActualValue();
            String projectNr = dataRow.dataField( ValueDataFieldCall.get("projectnr") ).getValue().getActualValue();
            String producerAbbr = dataRow.dataField( ValueDataFieldCall.get("producerabbr") ).getValue().getActualValue();

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
        xmlCursor.dispose();*/
    }

    private static void addCitationDistStatement(CitationType citationType) {
        /*// TODO: Add dist statement
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
        distrbtrType.setURI(FSD_DISTRIBUTOR_BASE_URI);*/
    }

    private static void addCitationRspStatement(RevisionData revisionData, CitationType citationType) {
        /*// TODO: ADD rsp
        // Back to citation
        // Add rsp (?)
        RspStmtType rspStmtType = citationType.addNewRspStmt();

        // Add author entity, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("authors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: authortype is SELECTION type with value of the selection as it's value.
            // TODO: authortype_list is REFERENCE type. Need to get the reference from it
            // TODO: Depending on the authortype excel would indicate that the affiliation is concatenated differently
            // String authorType = dataRow.dataField( ValueDataFieldCall.get("authortype") ).getValue().getActualValue();
            String author = dataRow.dataField( ValueDataFieldCall.get("author") ).getValue().getActualValue();
            String affiliation = dataRow.dataField( ValueDataFieldCall.get("affiliation") ).getValue().getActualValue();

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
            // String authorType = dataRow.dataField( ValueDataFieldCall.get("authortype") ).getValue().getActualValue();
            String otherAuthor = dataRow.dataField( ValueDataFieldCall.get("otherauthor") ).getValue().getActualValue();
            String otherAuthorAffiliation = dataRow.dataField( ValueDataFieldCall.get("otherauthoraffiliation") ).getValue().getActualValue();

            OthIdType othIdType = rspStmtType.addNewOthId();
            // Set other author content
            xmlCursor = othIdType.newCursor();
            xmlCursor.setTextValue(otherAuthor);
            xmlCursor.dispose();
            // Set affiliation
            othIdType.setAffiliation(otherAuthorAffiliation);
        }*/
    }

    private static void addCitationSerStatement(CitationType citationType, RevisionData revision, Language language, RevisionRepository revisions) {
        // Add series statement, excel row #70
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SERIESID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revisionPair = revisions.getLatestRevisionForIdAndType(
                    valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), true, ConfigurationType.SERIES);
            if(revisionPair.getLeft() == ReturnResult.REVISION_FOUND) {
                Logger.error(DDIStudyDescription.class, "Did not find referenced SERIES with id: "+valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
                SerStmtType serStmtType = citationType.addNewSerStmt();
                RevisionData series = revisionPair.getRight();
                valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESABBR));
                String seriesAbbr = null;
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    seriesAbbr = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                }
                if(seriesAbbr != null) {
                    serStmtType.setURI(SERIES_URI_PREFIX.get(language)+seriesAbbr);
                }
                valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESNAME));
                if(hasValue(valueFieldPair, language)) {
                    SerNameType serName = fillTextType(serStmtType.addNewSerName(), valueFieldPair, language);
                    if(seriesAbbr != null) {
                        serName.setAbbr(seriesAbbr);
                    }
                }
                valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESDESC));
                if(hasValue(valueFieldPair, language)) {
                    fillTextType(serStmtType.addNewSerInfo(), valueFieldPair, language);
                }
            }
        }
    }

    private static void addCitationVerStatement(CitationType citationType, RevisionData revisionData, Language language) {
        VerStmtType verStmtType = citationType.addNewVerStmt();

        // Add version, repeatable
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.DATAVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSION));
                if(hasValue(valueFieldPair, language)) {
                    fillTextAndDateType(verStmtType.addNewVersion(), valueFieldPair, language);
                }
            }
        }
    }

    private static void addCitationTitle(RevisionData revisionData, Language language, CitationType citationType, Configuration configuration) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.TITLE));
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();
        if(hasValue(valueFieldPair, language)) {
            // Add title of requested language
            fillTextType(titlStmtType.addNewTitl(), valueFieldPair, language);
        }

        addAltTitles(revisionData, language, titlStmtType);

        addParTitles(revisionData, language, titlStmtType);

        String agency = "";
        valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            String id = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
            // Get agency from study id
            SelectionList list = configuration.getRootSelectionList(Lists.ID_PREFIX_LIST);
            if(list != null) {
                for(Option option : list.getOptions()) {
                    if(id.indexOf(option.getValue()) == 0) {
                        agency = option.getValue();
                        break;
                    }
                }
            }
            // Add study id as id no
            IDNoType idNoType = fillTextType(titlStmtType.addNewIDNo(), valueFieldPair, Language.DEFAULT);
            idNoType.setAgency(agency);
        }

        // Add DDI pid for the current language as idNO
        valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.PIDDDI+getXmlLang(language)));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            IDNoType idNoType = fillTextType(titlStmtType.addNewIDNo(), valueFieldPair, Language.DEFAULT);
            idNoType.setAgency(agency);
        }
    }

    private static void addParTitles(RevisionData revisionData, Language language, TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.TITLE));
        Set<String> usedLanguages = new HashSet<>();
        usedLanguages.add(getXmlLang(language));
        for(Language l : Language.values()) {
            if(l == language) {
                continue;
            }
            if(hasValue(valueFieldPair, l)) {
                SimpleTextType stt = fillTextType(titlStmtType.addNewParTitl(), valueFieldPair, l);
                stt.setLang(getXmlLang(l));
                usedLanguages.add(getXmlLang(l));
            }
        }
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.PARTITLES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.PARTITLE));
                String partitle = null;
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    partitle = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                }
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.PARTITLELANG));
                String partitlelang = null;
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    partitlelang = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                }
                if(partitle != null && partitlelang != null) {
                    if(!usedLanguages.contains(partitlelang)) {
                        SimpleTextType stt = fillTextType(titlStmtType.addNewParTitl(), partitle);
                        stt.setLang(partitlelang);
                        usedLanguages.add(partitlelang);
                    }
                }
            }
        }
    }

    private static void addAltTitles(RevisionData revisionData, Language language, TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair;// Add alternative titles
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.ALTTITLES));
        // TODO: Do we translate alternate titles or do the alternate titles have translations?
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.ALTTITLE));
                if(hasValue(valueFieldPair, language)) {
                    fillTextType(titlStmtType.addNewAltTitl(), valueFieldPair, language);
                }
            }
        }
    }

    private static void addStudyAuthorization(RevisionData revisionData, StdyDscrType stdyDscrType) {
        /*// TODO: Add study authorization
        // TODO: Example XMLs neither had studyAuthorization element or children. Check if excel is correct ?
        // Back to study description
        // Add study authorization
        StudyAuthorizationType studyAuthorizationType = stdyDscrType.addNewStudyAuthorization();

        // Add authorizing agency TODO: Certain cases depending on authortype, see excel row #80 - #82
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("authors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // authortype is SELECTION type for authortype_list which is a REFERENCE type
            // String authorType = dataRow.dataField( ValueDataFieldCall.get("authortype") ).getValue().getActualValue();
            String author = dataRow.dataField( ValueDataFieldCall.get("author") ).getValue().getActualValue();
            String affiliation = dataRow.dataField( ValueDataFieldCall.get("affiliation") ).getValue().getActualValue();
            AuthorizingAgencyType authorizingAgencyType = studyAuthorizationType.addNewAuthorizingAgency();
            xmlCursor = authorizingAgencyType.newCursor();
            xmlCursor.setTextValue(author);
            xmlCursor.dispose();
            // Set affiliation
            authorizingAgencyType.setAffiliation(affiliation);
            // Set abbreviation TODO: Where to get this ?
            authorizingAgencyType.setAbbr("placeholder");
        }*/
    }

    private static void addStudyInfo(StdyDscrType stdyDscrType, RevisionData revision, Language language, Configuration configuration, ReferenceService references) {
        StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

        addStudyInfoSubject(stdyInfo, revision, language, configuration, references);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField( ValueDataFieldCall.get(Fields.ABSTRACT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(stdyInfo.addNewAbstract(), valueFieldPair, language);
        }

        addStudyInfoSumDesc(stdyInfo, revision, language, configuration, references);
    }

    private static void addStudyInfoSubject(StdyInfoType stdyInfo, RevisionData revision, Language language, Configuration configuration, ReferenceService references) {
        SubjectType subject= stdyInfo.addNewSubject();

        // Add subject
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.KEYWORDS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSubjectKeywords(subject, containerPair.getRight(), revision, language, references);
        }

        // Add topic
        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TOPICS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSubjectTopics(subject, containerPair.getRight(), revision, language, references);
        }
    }

    private static String getReferenceTitle(ReferenceService references, Language language, RevisionData revision, String path) {
        ReferenceOption option = references.getCurrentFieldOption(language, revision, path);
        if(option != null) {
            return option.getTitle().getValue();
        } else return null;
    }

    private static void addStudyInfoSubjectKeywords(SubjectType subject, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "keywords.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String keyword = null;
            String keyworduri = null;
            String keywordvocab = null;
            String keywordvocaburi = null;

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.KEYWORDNOVOCAB));
            if(hasValue(valueFieldPair, language)) {
                // Since there's nothing in this value unless user has selected a free text option we know we can use this and don't need to bother fetching the keyword
                keyword = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                // There's no URI for other
            } else {
                keywordvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.KEYWORDVOCAB);
                if(!StringUtils.hasText(keywordvocab)) {
                    continue;
                }

                keyword = getReferenceTitle(references, language, revision, rowRoot + Fields.KEYWORD);
                if(!StringUtils.hasText(keywordvocab)) {
                    continue;
                }

                keywordvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.KEYWORDVOCABURI);

                keyworduri = getReferenceTitle(references, language, revision, rowRoot + Fields.KEYWORDURI);
            }

            // Keyword should always be non null at this point
            KeywordType kwt = fillTextType(subject.addNewKeyword(), keyword);
            if(keyworduri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(keyworduri) {
                    case "archive":
                        kwt.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        kwt.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }
            if(keywordvocab != null) {
                kwt.setVocab(keywordvocab);
            }
            if(keywordvocaburi != null) {
                kwt.setVocabURI(keywordvocaburi);
            }
        }
    }

    private static void addStudyInfoSubjectTopics(SubjectType subject, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "topics.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String topic = null;
            String topicuri = null;
            String topicvocab = null;
            String topicvocaburi = null;

            topicvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.TOPICVOCAB);
            if(!StringUtils.hasText(topicvocab)) {
                continue;
            }

            topic = getReferenceTitle(references, language, revision, rowRoot + Fields.TOPIC);
            if(!StringUtils.hasText(topic)) {
                continue;
            }


            topicvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.TOPICVOCABURI);

            topicuri = getReferenceTitle(references, language, revision, rowRoot + Fields.TOPICURI);

            // Keyword should always be non null at this point
            TopcClasType tt = fillTextType(subject.addNewTopcClas(), topic);
            if(topicuri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(topicuri) {
                    case "archive":
                        tt.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        tt.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }
            if(topicvocab != null) {
                tt.setVocab(topicvocab);
            }
            if(topicvocaburi != null) {
                tt.setVocabURI(topicvocaburi);
            }
        }
    }

    private static void addStudyInfoSumDesc(StdyInfoType stdyInfo, RevisionData revision, Language language, Configuration configuration, ReferenceService references) {
        SumDscrType sumDscrType = stdyInfo.addNewSumDscr();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TIMEPERIODS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescTimePrd(sumDscrType, containerPair.getRight(), language);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLTIME));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescCollDate(sumDscrType, containerPair.getRight(), language);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COUNTRIES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescNation(sumDscrType, containerPair.getRight(), revision, language, references);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.GEOGCOVERS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                if (row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> fieldPair = row.dataField(ValueDataFieldCall.get(Fields.GEOGCOVER));
                if(hasValue(fieldPair, language)) {
                    fillTextType(sumDscrType.addNewGeogCover(), fieldPair, language);
                }
            }
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.ANALYSIS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescAnlyUnit(sumDscrType, containerPair.getRight(), revision, language, references);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.UNIVERSES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescUniverse(language, sumDscrType, containerPair);
        }

        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAKIND));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            SelectionList list = configuration.getRootSelectionList(configuration.getField(Fields.DATAKIND).getSelectionList());
            Option option = list.getOptionWithValue(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(option != null) {
                fillTextType(sumDscrType.addNewDataKind(), option.getTitleFor(Language.DEFAULT));
            }
        }
    }

    private static void addStudyInfoSumDescAnlyUnit(SumDscrType sumDscr, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "analysis.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String txt = null;
            String analysisunit = null;
            String analysisunituri = null;
            String analysisunitvocab = null;
            String analysisunitvocaburi = null;

            analysisunitvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.ANALYSISUNITVOCAB);
            if(!StringUtils.hasText(analysisunitvocab)) {
                continue;
            }

            analysisunit = getReferenceTitle(references, language, revision, rowRoot + Fields.ANALYSISUNIT);
            if(!StringUtils.hasText(analysisunit)) {
                continue;
            }

            analysisunitvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.ANALYSISUNITVOCABURI);

            analysisunituri = getReferenceTitle(references, language, revision, rowRoot + Fields.ANALYSISUNITURI);

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.ANALYSISUNITOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            AnlyUnitType t = sumDscr.addNewAnlyUnit();
            ConceptType c = fillTextType(t.addNewConcept(), analysisunit);
            if(analysisunituri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(analysisunituri) {
                    case "archive":
                        c.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        c.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }

            if(analysisunitvocab != null) {
                c.setVocab(analysisunitvocab);
            }

            if(analysisunitvocaburi != null) {
                c.setVocabURI(analysisunitvocaburi);
            }

            if(txt != null) {
                fillTextType(t.addNewTxt(), txt);
            }
        }
    }

    private static void addStudyInfoSumDescUniverse(Language language, SumDscrType sumDscrType, Pair<StatusCode, ContainerDataField> containerPair) {
        for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
            if (row.getRemoved()) {
                continue;
            }
            Pair<StatusCode, ValueDataField> fieldPair = row.dataField(ValueDataFieldCall.get(Fields.UNIVERSE));
            if(hasValue(fieldPair, language)) {
                UniverseType t = fillTextType(sumDscrType.addNewUniverse(), fieldPair, language);
                fieldPair = row.dataField(ValueDataFieldCall.get(Fields.UNIVERSECLUSION));
                if(hasValue(fieldPair, Language.DEFAULT)) {
                    switch(fieldPair.getRight().getActualValueFor(Language.DEFAULT)) {
                        case "I":
                            t.setClusion(UniverseType.Clusion.I);
                            break;
                        case "E":
                            t.setClusion(UniverseType.Clusion.E);
                            break;
                    }
                }
            }
        }
    }

    private static void addStudyInfoSumDescTimePrd(SumDscrType sumDscr, ContainerDataField container, Language language) {
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }

            Pair<StatusCode, ValueDataField> valuePair = row.dataField(ValueDataFieldCall.get(Fields.TIMEPERIODTEXT));
            String timeperiodtext = hasValue(valuePair, language) ? valuePair.getRight().getActualValueFor(language) : null;
            valuePair = row.dataField(ValueDataFieldCall.get(Fields.TIMEPERIOD));
            if(StringUtils.hasText(timeperiodtext) || hasValue(valuePair, Language.DEFAULT)) {
                TimePrdType t = sumDscr.addNewTimePrd();
                if(StringUtils.hasText(timeperiodtext)) {
                    fillTextType(t, timeperiodtext);
                }
                if(hasValue(valuePair, Language.DEFAULT)) {
                    t.setDate(valuePair.getRight().getActualValueFor(Language.DEFAULT));
                }
                valuePair = row.dataField(ValueDataFieldCall.get(Fields.TIMEPERIODEVENT));
                if(hasValue(valuePair, Language.DEFAULT)) {
                    switch(valuePair.getRight().getActualValueFor(Language.DEFAULT)) {
                        case "start":
                            t.setEvent(TimePrdType.Event.START);
                            break;
                        case "end":
                            t.setEvent(TimePrdType.Event.END);
                            break;
                        case "single":
                            t.setEvent(TimePrdType.Event.SINGLE);
                            break;
                    }
                }
            }
        }
    }

    private static void addStudyInfoSumDescCollDate(SumDscrType sumDscr, ContainerDataField container, Language language) {
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }

            Pair<StatusCode, ValueDataField> valuePair = row.dataField(ValueDataFieldCall.get(Fields.COLLDATETEXT));
            String colldatetext = hasValue(valuePair, language) ? valuePair.getRight().getActualValueFor(language) : null;
            valuePair = row.dataField(ValueDataFieldCall.get(Fields.COLLDATE));
            if(StringUtils.hasText(colldatetext) || hasValue(valuePair, Language.DEFAULT)) {
                CollDateType t = sumDscr.addNewCollDate();
                if(StringUtils.hasText(colldatetext)) {
                    fillTextType(t, colldatetext);
                }
                if(hasValue(valuePair, Language.DEFAULT)) {
                    t.setDate(valuePair.getRight().getActualValueFor(Language.DEFAULT));
                }
                valuePair = row.dataField(ValueDataFieldCall.get(Fields.COLLDATEEVENT));
                if(hasValue(valuePair, Language.DEFAULT)) {
                    switch(valuePair.getRight().getActualValueFor(Language.DEFAULT)) {
                        case "start":
                            t.setEvent(CollDateType.Event.START);
                            break;
                        case "end":
                            t.setEvent(CollDateType.Event.END);
                            break;
                        case "single":
                            t.setEvent(CollDateType.Event.SINGLE);
                            break;
                    }
                }
            }
        }
    }

    private static void addStudyInfoSumDescNation(SumDscrType sumDscr, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        String path = "countries.";
        for (DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if (row.getRemoved()) {
                continue;
            }
            String rowPath = path + row.getRowId() + ".";
            String country = getReferenceTitle(references, language, revision, rowPath+Fields.COUNTRY);
            if(!StringUtils.hasText(country)) {
                continue;
            }
            NationType n = fillTextType(sumDscr.addNewNation(), country);
            String abbr = getReferenceTitle(references, language, revision, rowPath+Fields.COUNTRYABBR);
            if(abbr != null) {
                n.setAbbr(abbr);
            }
        }
    }

    private static void addMethod(StdyDscrType stdyDscrType, RevisionData revision, Language language, ReferenceService references) {
        MethodType methodType = stdyDscrType.addNewMethod();

        addMethodDataColl(methodType, revision, language, references);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAPROSESSING));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(methodType.addNewNotes(), valueFieldPair, language);
        }

        addMethodAnalyzeInfo(methodType, revision, language);
    }

    private static void addMethodDataColl(MethodType methodType, RevisionData revision, Language language, ReferenceService references) {
        // Add data column
        DataCollType dataCollType = methodType.addNewDataColl();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TIMEMETHODS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollTimeMeth(dataCollType, containerPair.getRight(), revision, language, references);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.SAMPPROCS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollSampProc(dataCollType, containerPair.getRight(), revision, language, references);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLMODES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollCollMode(dataCollType, containerPair.getRight(), revision, language, references);
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.INSTRUMENTS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollResInstru(dataCollType, containerPair.getRight(), revision, language, references);
        }

        addMethodDataCollDataCollector(dataCollType);

        addMethodDataCollSources(dataCollType, revision, language);

        addMethodDataCollWeight(dataCollType, revision, language);
    }

    private static void addMethodDataCollTimeMeth(DataCollType dataColl, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "timemethods.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String txt = null;
            String timemethod = null;
            String timemethoduri = null;
            String timemethodvocab = null;
            String timemethodvocaburi = null;

            timemethodvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.TIMEMETHODVOCAB);
            if(!StringUtils.hasText(timemethodvocab)) {
                continue;
            }

            timemethod = getReferenceTitle(references, language, revision, rowRoot + Fields.TIMEMETHOD);
            if(!StringUtils.hasText(timemethod)) {
                continue;
            }

            timemethodvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.TIMEMETHODVOCABURI);

            timemethoduri = getReferenceTitle(references, language, revision, rowRoot + Fields.TIMEMETHODURI);

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.TIMEMETHODOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            TimeMethType t = dataColl.addNewTimeMeth();
            ConceptType c = fillTextType(t.addNewConcept(), timemethod);
            if(timemethoduri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(timemethoduri) {
                    case "archive":
                        c.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        c.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }

            if(timemethodvocab != null) {
                c.setVocab(timemethodvocab);
            }

            if(timemethodvocaburi != null) {
                c.setVocabURI(timemethodvocaburi);
            }

            if(txt != null) {
                fillTextType(t.addNewTxt(), txt);
            }
        }
    }

    private static void addMethodDataCollDataCollector(DataCollType dataCollType) {
        /*// Add data collector, repeatable, excel row #124 - #127
        // TODO: Conditions apply for this see excel
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collectors") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: authortype is SELECTION type for authortype_list which is REFERENCE type
            String authorType = dataRow.dataField( ValueDataFieldCall.get("authortype") ).getValue().getActualValue();
            String collector = dataRow.dataField( ValueDataFieldCall.get("collector") ).getValue().getActualValue();
            String collectorAffiliation = dataRow.dataField( ValueDataFieldCall.get("collectoraffiliation") ).getValue().getActualValue();

            // TODO: Conditional values depending on collectortype (authortype ?)
            DataCollectorType dataCollectorType = dataCollType.addNewDataCollector();
            xmlCursor = dataCollectorType.newCursor();
            xmlCursor.setTextValue(collector);
            xmlCursor.dispose();
            // Set data collector abbreviation
            dataCollectorType.setAbbr("");
            // Set data collector affiliation
            dataCollectorType.setAffiliation(collectorAffiliation);
        }*/
    }

    private static void addMethodDataCollSampProc(DataCollType dataColl, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "sampprocs.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String txt = null;
            String sampproc = null;
            String sampprocuri = null;
            String sampprocvocab = null;
            String sampprocvocaburi = null;

            sampprocvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.SAMPPROCVOCAB);
            if(!StringUtils.hasText(sampprocvocab)) {
                continue;
            }

            sampproc = getReferenceTitle(references, language, revision, rowRoot + Fields.SAMPPROC);
            if(!StringUtils.hasText(sampproc)) {
                continue;
            }

            sampprocvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.SAMPPROCVOCABURI);

            sampprocuri = getReferenceTitle(references, language, revision, rowRoot + Fields.SAMPPROCURI);

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.SAMPPROCOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            ConceptualTextType t = dataColl.addNewSampProc();

            // Add sampproctext if present
            valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.SAMPPROCTEXT));
            if(hasValue(valueFieldPair, language)) {
                fillTextType(t, valueFieldPair, language);
            }

            ConceptType c = fillTextType(t.addNewConcept(), sampproc);
            if(sampprocuri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(sampprocuri) {
                    case "archive":
                        c.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        c.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }

            if(sampprocvocab != null) {
                c.setVocab(sampprocvocab);
            }

            if(sampprocvocaburi != null) {
                c.setVocabURI(sampprocvocaburi);
            }

            if(txt != null) {
                fillTextType(t.addNewTxt(), txt);
            }
        }
    }

    private static void addMethodDataCollCollMode(DataCollType dataColl, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "collmodes.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String txt = null;
            String collmode = null;
            String collmodeuri = null;
            String collmodevocab = null;
            String collmodevocaburi = null;

            collmodevocab = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLMODEVOCAB);
            if(!StringUtils.hasText(collmodevocab)) {
                continue;
            }

            collmode = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLMODE);
            if(!StringUtils.hasText(collmode)) {
                continue;
            }

            collmodevocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLMODEVOCABURI);

            collmodeuri = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLMODEURI);

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.COLLMODEOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            ConceptualTextType t = dataColl.addNewCollMode();

            ConceptType c = fillTextType(t.addNewConcept(), collmode);
            if(collmodeuri != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(collmodeuri) {
                    case "archive":
                        c.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        c.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }

            if(collmodevocab != null) {
                c.setVocab(collmodevocab);
            }

            if(collmodevocaburi != null) {
                c.setVocabURI(collmodevocaburi);
            }

            if(txt != null) {
                fillTextType(t.addNewTxt(), txt);
            }
        }
    }

    private static void addMethodDataCollResInstru(DataCollType dataColl, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "instruments.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String txt = null;
            String instrument = null;
            String instrumenturi = null;
            String instrumentvocab = null;
            String instrumentvocaburi = null;

            instrumentvocab = getReferenceTitle(references, language, revision, rowRoot + Fields.INSTRUMENTVOCAB);
            if(!StringUtils.hasText(instrumentvocab)) {
                continue;
            }

            instrument = getReferenceTitle(references, language, revision, rowRoot + Fields.INSTRUMENT);
            if(!StringUtils.hasText(instrument)) {
                continue;
            }

            instrumentvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.INSTRUMENTVOCABURI);

            instrumenturi = getReferenceTitle(references, language, revision, rowRoot + Fields.INSTRUMENTURI);

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.INSTRUMENTOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            ResInstruType t = dataColl.addNewResInstru();

            ConceptType c = fillTextType(t.addNewConcept(), instrument);
            if(instrumenturi != null) {
                // TODO: This is compiled as an ENUM, is this correct?
                switch(instrumenturi) {
                    case "archive":
                        c.setSource(BaseElementType.Source.ARCHIVE);
                        break;
                    case "producer":
                        c.setSource(BaseElementType.Source.PRODUCER);
                        break;
                }
            }

            if(instrumentvocab != null) {
                c.setVocab(instrumentvocab);
            }

            if(instrumentvocaburi != null) {
                c.setVocabURI(instrumentvocaburi);
            }

            if(txt != null) {
                fillTextType(t.addNewTxt(), txt);
            }
        }
    }

    private static void addMethodDataCollSources(DataCollType dataCollType, RevisionData revision, Language language) {
        List<ValueDataField> fields = gatherFields(revision, Fields.DATASOURCES, Fields.DATASOURCE, language, language);
        SourcesType sources = dataCollType.addNewSources();
        for(ValueDataField field : fields) {
            fillTextType(sources.addNewDataSrc(), field, language);
        }
    }

    private static void addMethodDataCollWeight(DataCollType dataCollType, RevisionData revision, Language language) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHTYESNO));
        if(hasValue(valueFieldPair, Language.DEFAULT) && valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsBoolean()) {
            fillTextType(dataCollType.addNewWeight(), WEIGHT_NO.get(language));
        } else {
            valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHT));
            if(hasValue(valueFieldPair, language)) {
                fillTextType(dataCollType.addNewWeight(), valueFieldPair, language);
            }
        }
    }

    private static void addMethodAnalyzeInfo(MethodType methodType, RevisionData revision, Language language) {
        AnlyInfoType anlyInfoType = methodType.addNewAnlyInfo();

        // Add response rate
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.RESPRATE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(anlyInfoType.addNewRespRate(), valueFieldPair, Language.DEFAULT);
        }

        // Add data appraisal, repeatable
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.APPRAISALS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for (DataRow row : containerPair.getRight().getRowsFor(language)) {
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.APPRAISAL));
                if(hasValue(valueFieldPair, language)) {
                    fillTextType(anlyInfoType.addNewDataAppr(), valueFieldPair, language);
                }
            }
        }
    }

    private static void addDataAccess(StdyDscrType stdyDscrType, RevisionData revision, Configuration configuration, Language language) {
        DataAccsType dataAccs = stdyDscrType.addNewDataAccs();

        addDataAccessSetAvail(dataAccs, revision, language);

        addDataAccessUseStatement(dataAccs, revision, configuration, language);

        // Add notes
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATASETNOTES));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(dataAccs.addNewNotes(), valueFieldPair, language);
        }
    }

    private static void addDataAccessSetAvail(DataAccsType dataAccs, RevisionData revision, Language language) {
        // Add set availability
        SetAvailType setAvail = dataAccs.addNewSetAvail();

        // Add access place
        AccsPlacType acc = fillTextType(setAvail.addNewAccsPlac(), ACCS_PLAC.get(language));
        acc.setURI(ACCS_PLAC_URI.get(language));

        // Add original archive
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.ORIGINALLOCATION));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(setAvail.addNewOrigArch(), valueFieldPair, Language.DEFAULT);
        }

        // Add collection size
        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.COLLSIZE));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(setAvail.addNewCollSize(), valueFieldPair, language);
        }

        // Add complete
        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.COMPLETE));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(setAvail.addNewComplete(), valueFieldPair, language);
        }
    }

    private static void addDataAccessUseStatement(DataAccsType dataAccs, RevisionData revision, Configuration configuration, Language language) {
        // Add use statement
        UseStmtType useStmt = dataAccs.addNewUseStmt();

        // Add special permissions
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SPECIALTERMSOFUSE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(useStmt.addNewSpecPerm(), valueFieldPair, language);
        }

        // Add restrictions, excel row #164
        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TERMSOFUSE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(useStmt.addNewRestrctn(), RESTRICTION.get(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)).get(language));
        }

        // Add citation required
        fillTextType(useStmt.addNewCitReq(), CIT_REQ.get(language));

        // Add deposition required
        fillTextType(useStmt.addNewDeposReq(), DEPOS_REQ.get(language));

        // Add disclaimer required
        fillTextType(useStmt.addNewDisclaimer(), DISCLAIMER.get(language));
    }

    private static void addOtherStudyMaterial(StdyDscrType stdyDscrType, RevisionData revision, Language language, RevisionRepository revisions) {
        OthrStdyMatType othr = stdyDscrType.addNewOthrStdyMat();

        // Add related materials
        List<ValueDataField> fields = gatherFields(revision, Fields.RELATEDMATERIALS, Fields.RELATEDMATERIAL, language, language);
        for(ValueDataField field : fields) {
            fillTextType(othr.addNewRelMat(), field, language);
        }

        Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.STUDIES));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
            for(ReferenceRow row : referenceContainerPair.getRight().getReferences()) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> revisionPair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY);
                if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(DDIStudyDescription.class, "Could not find referenced study with ID: "+row.getReference().getValue());
                    continue;
                }
                String studyID = "-";
                String title = "-";
                RevisionData study = revisionPair.getRight();

                Pair<StatusCode, ValueDataField> valueFieldPair = study.dataField(ValueDataFieldCall.get(Fields.STUDYID));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    studyID = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                }

                valueFieldPair = study.dataField(ValueDataFieldCall.get(Fields.TITLE));
                if(hasValue(valueFieldPair, language)) {
                    title = valueFieldPair.getRight().getActualValueFor(language);
                }

                fillTextType(othr.addNewRelStdy(), studyID+" "+title);
            }
        }

        referenceContainerPair = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.PUBLICATIONS));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
            for(ReferenceRow row : referenceContainerPair.getRight().getReferences()) {
                if (row.getRemoved()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> revisionPair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.PUBLICATION);
                if (revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(DDIStudyDescription.class, "Could not find referenced publication with ID: " + row.getReference().getValue());
                    continue;
                }
                RevisionData publication = revisionPair.getRight();

                Pair<StatusCode, ValueDataField> valueFieldPair = publication.dataField(ValueDataFieldCall.get(Fields.PUBLICATIONRELPUBL));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    fillTextType(othr.addNewRelPubl(), valueFieldPair, Language.DEFAULT);
                }
            }
        }

        // Add publication comments
        fields = gatherFields(revision, Fields.PUBLICATIONCOMMENTS, Fields.PUBLICATIONCOMMENT, language, language);
        for(ValueDataField field : fields) {
            fillTextType(othr.addNewOthRefs(), field, language);
        }
    }
}
