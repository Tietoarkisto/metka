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
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;

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
    }

    static void addStudyDescription(RevisionData revision, Language language, Configuration configuration, CodeBookType codeBookType, RevisionRepository revisions) {
        // Add study description to codebook
        StdyDscrType stdyDscrType = codeBookType.addNewStdyDscr();

        addCitationInfo(stdyDscrType, revision, language, configuration, revisions);

        addStudyAuthorization(revision, stdyDscrType);

        addStudyInfo(stdyDscrType, revision, language);

        addMethod(stdyDscrType, revision, language);

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

    private static void addStudyInfo(StdyDscrType stdyDscrType, RevisionData revision, Language language) {
        StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

        addStudyInfoSubject(stdyInfo, revision);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField( ValueDataFieldCall.get(Fields.ABSTRACT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(stdyInfo.addNewAbstract(), valueFieldPair, language);
        }
        addStudyInfoSumDesc(stdyInfo, revision, language);


    }

    private static void addStudyInfoSubject(StdyInfoType stdyInfo, RevisionData revision) {
        /*// Add subject, excel row #84
        SubjectType subjectType= stdyInfo.addNewSubject();

        // Add keyword, repeatable TODO: Keyword has vocab or does not and values depend on that see excel row #85 - #89
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("keywords") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String keywordVocab = dataRow.dataField( ValueDataFieldCall.get("keywordvocab") ).getValue().getActualValue();
            String keyword = dataRow.dataField( ValueDataFieldCall.get("keyword") ).getValue().getActualValue();
            // String keywordNoVocab = dataRow.dataField( ValueDataFieldCall.get("keywordnovocab") ).getValue().getActualValue();
            String keywordVocabURI = dataRow.dataField( ValueDataFieldCall.get("keywordvocaburi") ).getValue().getActualValue();
            // String keywordURI = dataRow.dataField( ValueDataFieldCall.get("keyworduri") ).getValue().getActualValue();

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
            String topicVocab = dataRow.dataField( ValueDataFieldCall.get("topicvocab") ).getValue().getActualValue();
            String topic = dataRow.dataField( ValueDataFieldCall.get("topic") ).getValue().getActualValue();

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
        }*/
    }

    private static void addStudyInfoSumDesc(StdyInfoType stdyInfo, RevisionData revision, Language language) {
        /*// TODO: Add sum description
        // Add summary description
        SumDscrType sumDscrType = stdyInfo.addNewSumDscr();

        // Add time period, repeatable, excel row #96 - #98
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TIMEPERIODS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for (DataRow dataRow : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                // timeperiod is DATE type
                String timePeriod = dataRow.dataField( ValueDataFieldCall.get("timeperiod") ).getValue().getActualValue();
                LocalDate localDate = LocalDate.parse(timePeriod);
                String timePeriodText = dataRow.dataField( ValueDataFieldCall.get("timeperiodtext") ).getValue().getActualValue();
                // timeperiodevent is SELECTION type for timeperiodevent_list which is SUBLIST type for sublistKey start_end_single
                String timePeriodEvent = dataRow.dataField( ValueDataFieldCall.get("timeperiodevent") ).getValue().getActualValue();

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
        }


        // Add collection time, repeatable, excel row #99 - #101
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("colltime") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // colldate is DATE type
            String collDate = dataRow.dataField( ValueDataFieldCall.get("colldate") ).getValue().getActualValue();
            LocalDate localDate = LocalDate.parse(collDate);
            String collDateText = dataRow.dataField( ValueDataFieldCall.get("colldatetext") ).getValue().getActualValue();
            // colldateevent is SELECTION type for colldateevent_list
            String collDateEvent = dataRow.dataField( ValueDataFieldCall.get("colldateevent") ).getValue().getActualValue();

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
            String country = dataRow.dataField( ValueDataFieldCall.get("country") ).getValue().getActualValue();
            String countryAbbr = dataRow.dataField( ValueDataFieldCall.get("countryabbr") ).getValue().getActualValue();

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
            String geographicalCover = dataRow.dataField( ValueDataFieldCall.get("geogcover") ).getValue().getActualValue();

            ConceptualTextType conceptualTextType = sumDscrType.addNewGeogCover();
            xmlCursor = conceptualTextType.newCursor();
            xmlCursor.setTextValue(geographicalCover);
            xmlCursor.dispose();
        }

        // Add analyzing unit, repeatable, row #107 - #112
        // TODO: Analysis is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("analysis") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String analysisUnit = dataRow.dataField( ValueDataFieldCall.get("analysisunit") ).getValue().getActualValue();
            String analysisUnitVocab = dataRow.dataField( ValueDataFieldCall.get("analysisunitvocab") ).getValue().getActualValue();
            String analysisUnitVocabURI = dataRow.dataField( ValueDataFieldCall.get("analysisunitvocaburi") ).getValue().getActualValue();
            String analysisUnitURI = dataRow.dataField( ValueDataFieldCall.get("analysisunituri") ).getValue().getActualValue();
            String analysisUnitOther = dataRow.dataField( ValueDataFieldCall.get("analysisunitother") ).getValue().getActualValue();

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


            // Add new text (only if anlyUnit/concept=Muu havaintoyksikkö TAI Maantieteellinen alue)
            // TODO: How to check ? What path? Special handling for cases see excel row #112
            TxtType txtType = anlyUnitType.addNewTxt();
            xmlCursor = txtType.newCursor();
            xmlCursor.setTextValue(analysisUnitOther);
            xmlCursor.dispose();

        }

        // Add universe, repeatable
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("universes")).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            String universe = dataRow.dataField( ValueDataFieldCall.get("universe") ).getValue().getActualValue();
            // universeclusion is SELECTION type for universeclusion_list which is VALUE type
            String universeClusion = dataRow.dataField( ValueDataFieldCall.get("universeclusion") ).getValue().getActualValue();

            UniverseType universeType = sumDscrType.addNewUniverse();
            xmlCursor = universeType.newCursor();
            xmlCursor.setTextValue(universe);
            xmlCursor.dispose();
            // Add (I/E)clusion, 1 I, 2 E
            universeType.setClusion(UniverseType.Clusion.Enum.forInt( Integer.parseInt(universeClusion) ));
        }

        // Add data kind
        // datakind is SELECTION type for datakind_list which is VALUE type
        String dataKind = revisionData.dataField( ValueDataFieldCall.get("datakind") ).getValue().getActualValue();
        Field field = configuration.getField("datakind");
        String dataKindSelectionListKey = field.getSelectionList();
        SelectionList selectionList = configuration.getSelectionList(dataKindSelectionListKey);
        Option option = selectionList.getOptionWithValue(dataKind);
        // TODO: Default title for now, later on replace with the correct from language code ?
        String dataKindSelectedOptionValueTranslatedTitle = option.getDefaultTitle();
        DataKindType dataKindType = sumDscrType.addNewDataKind();
        xmlCursor = dataKindType.newCursor();
        xmlCursor.setTextValue(dataKindSelectedOptionValueTranslatedTitle);
        xmlCursor.dispose();*/
    }

    private static void addMethod(StdyDscrType stdyDscrType, RevisionData revision, Language language) {
        MethodType methodType = stdyDscrType.addNewMethod();

        addMethodDataColl(methodType, revision, language);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAPROSESSING));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(methodType.addNewNotes(), valueFieldPair, language);
        }
        addMethodAnalyzeInfo(methodType, revision, language);


    }

    private static void addMethodDataColl(MethodType methodType, RevisionData revision, Language language) {
        // TODO: Add data coll
        // Add data column
        DataCollType dataCollType = methodType.addNewDataColl();

        addMethodDataCollTimeMeth(dataCollType, revision, language);

        addMethodDataCollDataCollector(dataCollType);

        addMethodDataCollSampProc(dataCollType);

        addMethodDataCollCollMode(dataCollType);

        addMethodDataCollResInstru(dataCollType);

        addMethodDataCollSources(dataCollType, revision, language);

        addMethodDataCollWeight(dataCollType, revision, language);
    }

    private static void addMethodDataCollTimeMeth(DataCollType dataCollType, RevisionData revision, Language language) {
        /*// Add time method, repeatable
        // TODO: Timemethods is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("timemethods") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String timeMethodTopic = dataRow.dataField( ValueDataFieldCall.get("topic") ).getValue().getActualValue();
            String timeMethodTopicVocab = dataRow.dataField( ValueDataFieldCall.get("topicvocab") ).getValue().getActualValue();

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
        }*/
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

    private static void addMethodDataCollSampProc(DataCollType dataCollType) {
        /*// Add sample procurement, repeatable, row #128 - #134
        // TODO: Get correct values from reference and dependency
        // TODO: Is this supposed to be a vocabulary implementation similiar to analysis ?
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("sampprocs") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // sampproc is SELECTION type for sampproc_list which is REFERENCE type
            String sampProc = dataRow.dataField( ValueDataFieldCall.get("sampproc") ).getValue().getActualValue();
            // sampprocdesc is REFERENCE type for sampprocdesc_ref which is DEPENDENCY type
            String sampProcDesc = dataRow.dataField( ValueDataFieldCall.get("sampprocdesc") ).getValue().getActualValue();

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
        }*/
    }

    private static void addMethodDataCollCollMode(DataCollType dataCollType) {
        /*// Add collection mode, excel row #135 - #140
        // TODO: Collmodes is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collmodes") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String collModesTopic = dataRow.dataField( ValueDataFieldCall.get("topic") ).getValue().getActualValue();
            String collModesTopicVocab = dataRow.dataField( ValueDataFieldCall.get("topicvocab") ).getValue().getActualValue();

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
        }*/
    }

    private static void addMethodDataCollResInstru(DataCollType dataCollType) {
        /*// Add resource instrumentation, excel row #141 - #146
        // TODO: Instruments is a container with subfields topicvocab and topic
        containerDataField = revisionData.dataField( ContainerDataFieldCall.get("collmodes") ).getValue();
        for (DataRow dataRow : containerDataField.getRows()) {
            // TODO: Both lists contain REFERENCE type for topic_list / topicvocab_list which are REFERENCE type
            String instrumentsTopic = dataRow.dataField( ValueDataFieldCall.get("topic") ).getValue().getActualValue();
            String instrumentsTopicVocab = dataRow.dataField( ValueDataFieldCall.get("topicvocab") ).getValue().getActualValue();

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
        }*/
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
