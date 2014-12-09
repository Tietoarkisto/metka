package fi.uta.fsd.metka.ddi.builder;

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

import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.*;

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

        DISTRIBUTR.put(Language.DEFAULT, "Yhteiskuntatieteellinen tietoarkisto");
        DISTRIBUTR.put(Language.EN, "Finnish Social Science Data Archive");
        DISTRIBUTR.put(Language.SV, "Finlands samhällsvetenskapliga dataarkiv");

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

        addCitationInfo(stdyDscrType, revision, language, configuration, revisions, references);

        addStudyAuthorization(revision, stdyDscrType, references, language);

        addStudyInfo(stdyDscrType, revision, language, configuration, references);

        addMethod(stdyDscrType, revision, language, references);

        addDataAccess(stdyDscrType, revision, configuration, language);

        addOtherStudyMaterial(stdyDscrType, revision, language, revisions);
    }

    private static void addCitationInfo(StdyDscrType stdyDscrType, RevisionData revisionData, Language language, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        // Add citation
        CitationType citationType = stdyDscrType.addNewCitation();

        addCitationTitle(revisionData, language, citationType, configuration);

        addCitationRspStatement(revisionData, citationType, references, language);

        addCitationProdStatement(revisionData, citationType, language, references, configuration);

        addCitationDistStatement(citationType, language);

        // Add SerStmt
        addCitationSerStatement(citationType, revisionData, language, revisions);

        // Add VerStmt
        addCitationVerStatement(citationType, revisionData, language);

        // Add biblcit
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.BIBLCIT));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(citationType.addNewBiblCit(), valueFieldPair, Language.DEFAULT);
        }
    }

    private static void addCitationProdStatement(RevisionData revision, CitationType citationType, Language language, ReferenceService references, Configuration configuration) {
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.PRODUCERS));
        String path = "producers.";
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                String rowRoot = path+row.getRowId()+".";

                String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERORGANISATION);
                String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERAGENCY);
                String section = getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERSECTION);
                ProducerType d;
                if(!StringUtils.hasText(agency) && !StringUtils.hasText(section)) {
                    if(!StringUtils.hasText(organisation)) {
                        continue;
                    }
                    d = fillTextType(prodStmtType.addNewProducer(), organisation);
                } else {
                    String producer = (StringUtils.hasText(agency)) ? agency : "";
                    producer += (StringUtils.hasText(producer) && StringUtils.hasText(section)) ? ". " : "";
                    producer += (StringUtils.hasText(section)) ? section : "";
                    if(!StringUtils.hasText(producer)) {
                        continue;
                    }
                    d = fillTextType(prodStmtType.addNewProducer(), producer);
                }

                String abbr = getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERORGANISATIONABBR);

                d.setAbbr(abbr);
                if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                    if(StringUtils.hasText(organisation)) {
                        d.setAffiliation(organisation);
                    }
                }

                Pair<StatusCode, ValueDataField> fieldPair = row.dataField(ValueDataFieldCall.get(Fields.PRODUCERROLE));
                if(hasValue(fieldPair, Language.DEFAULT)) {
                    String role = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
                    SelectionList list = configuration.getRootSelectionList(configuration.getField(Fields.PRODUCERROLE).getSelectionList());
                    Option option = list.getOptionWithValue(role);
                    if(option != null) {
                        d.setRole(option.getTitleFor(language));
                    }
                }
            }
        }

        // Add copyright
        fillTextType(prodStmtType.addNewCopyright(), COPYRIGHT.get(language));
    }

    private static void addCitationDistStatement(CitationType citationType, Language language) {
        DistStmtType distStmtType = citationType.addNewDistStmt();
        DistrbtrType d = fillTextType(distStmtType.addNewDistrbtr(), DISTRIBUTR.get(language));
        d.setAbbr(DISTRIBUTR_ABB.get(language));
        d.setURI(DISTRIBUTR_URI.get(language));
    }

    private static void addCitationRspStatement(RevisionData revision, CitationType citationType, ReferenceService references, Language language) {
        RspStmtType rsp = citationType.addNewRspStmt();
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.AUTHORS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            String pathRoot = "authors.";
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                if (row.getRemoved()) {
                    continue;
                }
                String rowRoot = pathRoot + row.getRowId() + ".";

                Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(Fields.AUTHORTYPE));
                if (!hasValue(pair, Language.DEFAULT)) {
                    // We require a type for collector before we can move forward
                    continue;
                }
                if(!pair.getRight().getActualValueFor(Language.DEFAULT).equals("1")) {
                    continue;
                }
                // We have a person author
                pair = row.dataField(ValueDataFieldCall.get(Fields.AUTHOR));
                if (!hasValue(pair, Language.DEFAULT)) {
                    // We must have a collector
                    continue;
                }
                AuthEntyType d = fillTextType(rsp.addNewAuthEnty(), pair, Language.DEFAULT);

                String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORORGANISATION);
                String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORAGENCY);
                String section = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORSECTION);

                String affiliation = (StringUtils.hasText(organisation)) ? organisation : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? ". " : "";
                affiliation += (StringUtils.hasText(agency)) ? agency : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? ". " : "";
                affiliation += (StringUtils.hasText(section)) ? section : "";

                if (StringUtils.hasText(affiliation)) {
                    d.setAffiliation(affiliation);
                }
            }
        }
        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.OTHERAUTHORS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            String pathRoot = "authors.";
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                if (row.getRemoved()) {
                    continue;
                }
                String rowRoot = pathRoot + row.getRowId() + ".";

                Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(Fields.OTHERAUTHORTYPE));
                if(!hasValue(pair, Language.DEFAULT)) {
                    // We require a type for collector before we can move forward
                    continue;
                }
                String colltype = pair.getRight().getActualValueFor(Language.DEFAULT);
                // It's easier to dublicate some functionality and make a clean split from the top than to evaluate each value separately
                if(colltype.equals("1")) {
                    // We have a person collector
                    pair = row.dataField(ValueDataFieldCall.get(Fields.AUTHOR));
                    if(!hasValue(pair, Language.DEFAULT)) {
                        // We must have a collector
                        continue;
                    }
                    OthIdType d = fillTextType(rsp.addNewOthId(), pair, Language.DEFAULT);

                    String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORORGANISATION);
                    String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORAGENCY);
                    String section = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORSECTION);

                    String affiliation = (StringUtils.hasText(organisation)) ? organisation : "";
                    affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? ". " : "";
                    affiliation += (StringUtils.hasText(agency)) ? agency : "";
                    affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? ". " : "";
                    affiliation += (StringUtils.hasText(section)) ? section : "";

                    if(StringUtils.hasText(affiliation)) {
                        d.setAffiliation(affiliation);
                    }
                } else if(colltype.equals("2")) {
                    // We have an organisation collector
                    String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORORGANISATION);
                    String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORAGENCY);
                    String section = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORSECTION);
                    OthIdType d;
                    if(!StringUtils.hasText(agency) && !StringUtils.hasText(section)) {
                        if(!StringUtils.hasText(organisation)) {
                            continue;
                        }
                        d = fillTextType(rsp.addNewOthId(), organisation);
                    } else {
                        String collector = (StringUtils.hasText(agency)) ? agency : "";
                        if(StringUtils.hasText(collector) && StringUtils.hasText(section)) {
                            collector += ". "+section;
                        } else if(StringUtils.hasText(section)) {
                            collector = section;
                        } else {
                            continue;
                        }
                        d = fillTextType(rsp.addNewOthId(), collector);
                    }
                    if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                        if(StringUtils.hasText(organisation)) {
                            d.setAffiliation(organisation);
                        }
                    }
                } else if(colltype.equals("3")) {
                    pair = row.dataField(ValueDataFieldCall.get(Fields.OTHERAUTHORGROUP));
                    if(hasValue(pair, language)) {
                        fillTextType(rsp.addNewOthId(), pair, language);
                    }
                }
            }
        }
    }

    private static void addCitationSerStatement(CitationType citationType, RevisionData revision, Language language, RevisionRepository revisions) {
        // Add series statement, excel row #70
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SERIES));
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
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.DATAVERSION));
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
        // TODO: Should this be the DDI package urn
        /*valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.PIDDDI+getXmlLang(language)));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            IDNoType idNoType = fillTextType(titlStmtType.addNewIDNo(), valueFieldPair, Language.DEFAULT);
            idNoType.setAgency(agency);
        }*/
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
                stt.setXmlLang(getXmlLang(l));
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
                        stt.setXmlLang(partitlelang);
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

    private static void addStudyAuthorization(RevisionData revision, StdyDscrType stdyDscrType, ReferenceService references, Language language) {
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.AUTHORS));
        String path = "authors.";
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            StudyAuthorizationType sa = stdyDscrType.addNewStudyAuthorization();
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }

                Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(Fields.AUTHORTYPE));
                if(!hasValue(pair, Language.DEFAULT)) {
                    continue;
                }
                // If author type is person then it's not correct for this entity
                if(pair.getRight().getActualValueFor(Language.DEFAULT).equals("1")) {
                    continue;
                }

                String rowRoot = path+row.getRowId()+".";

                String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORORGANISATION);
                String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORAGENCY);
                String section = getReferenceTitle(references, language, revision, rowRoot + Fields.AUTHORSECTION);
                AuthorizingAgencyType d;
                if(!StringUtils.hasText(agency) && !StringUtils.hasText(section)) {
                    if(!StringUtils.hasText(organisation)) {
                        continue;
                    }
                    d = fillTextType(sa.addNewAuthorizingAgency(), organisation);
                } else {
                    String authorizer = (StringUtils.hasText(agency)) ? agency : "";
                    authorizer += (StringUtils.hasText(authorizer) && StringUtils.hasText(section)) ? ". " : "";
                    authorizer += (StringUtils.hasText(section)) ? section : "";
                    if(!StringUtils.hasText(authorizer)) {
                        continue;
                    }
                    d = fillTextType(sa.addNewAuthorizingAgency(), authorizer);
                }

                String abbr = getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.PRODUCERORGANISATIONABBR);

                d.setAbbr(abbr);
                if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                    if(StringUtils.hasText(organisation)) {
                        d.setAffiliation(organisation);
                    }
                }
            }
        }
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
            addStudyInfoSubjectKeywords(subject, containerPair.getRight(), revision, language, references, configuration);
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

    private static void addStudyInfoSubjectKeywords(SubjectType subject, ContainerDataField container, RevisionData revision, Language language, ReferenceService references, Configuration configuration) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "keywords.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String keyword = null;
            String keywordvocaburi = null;

            ReferenceOption keywordvocab = references.getCurrentFieldOption(language, revision, rowRoot + Fields.KEYWORDVOCAB);
            keywordvocaburi = getReferenceTitle(references, language, revision, rowRoot + Fields.KEYWORDVOCABURI);
            SelectionList keywordvocab_list = configuration.getSelectionList(Lists.KEYWORDVOCAB_LIST);

            if(keywordvocab == null || keywordvocab_list.getFreeText().contains(keywordvocab.getValue())) {
                Pair<StatusCode, ValueDataField> keywordnovocabPair = row.dataField(ValueDataFieldCall.get(Fields.KEYWORDNOVOCAB));
                if(hasValue(keywordnovocabPair, language)) {
                    keyword = keywordnovocabPair.getRight().getActualValueFor(language);
                }
            } else {
                Pair<StatusCode, ValueDataField> keywordPair = row.dataField(ValueDataFieldCall.get(Fields.KEYWORD));
                if(hasValue(keywordPair, language)) {
                    keyword = keywordPair.getRight().getActualValueFor(language);
                }
            }
            if(!StringUtils.hasText(keyword)) {
                continue;
            }

            KeywordType kwt = fillTextType(subject.addNewKeyword(), keyword);
            if(keywordvocab != null) {
                kwt.setVocab(keywordvocab.getTitle().getValue());
            }
            if(StringUtils.hasText(keywordvocaburi)) {
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

            // Keyword should always be non null at this point
            TopcClasType tt = fillTextType(subject.addNewTopcClas(), topic);
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
                fillTextType(sumDscrType.addNewDataKind(), option.getTitleFor(language));
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

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.ANALYSISUNITOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            AnlyUnitType t = sumDscr.addNewAnlyUnit();
            ConceptType c = fillTextType(t.addNewConcept(), analysisunit);

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

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLECTORS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollDataCollector(dataCollType, containerPair.getRight(), revision, language, references);
        }

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

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.TIMEMETHODOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            TimeMethType t = dataColl.addNewTimeMeth();
            ConceptType c = fillTextType(t.addNewConcept(), timemethod);

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

    private static void addMethodDataCollDataCollector(DataCollType dataColl, ContainerDataField container, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "collectors.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            Pair<StatusCode, ValueDataField> pair = row.dataField(ValueDataFieldCall.get(Fields.COLLECTORTYPE));
            if(!hasValue(pair, Language.DEFAULT)) {
                // We require a type for collector before we can move forward
                continue;
            }
            String colltype = pair.getRight().getActualValueFor(Language.DEFAULT);
            // It's easier to dublicate some functionality and make a clean split from the top than to evaluate each value separately
            if(colltype.equals("1")) {
                // We have a person collector
                pair = row.dataField(ValueDataFieldCall.get(Fields.COLLECTOR));
                if(!hasValue(pair, Language.DEFAULT)) {
                    // We must have a collector
                    continue;
                }
                DataCollectorType d = fillTextType(dataColl.addNewDataCollector(), pair, Language.DEFAULT);

                String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORORGANISATION);
                String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORAGENCY);
                String section = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORSECTION);

                String affiliation = (StringUtils.hasText(organisation)) ? organisation : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? ". " : "";
                affiliation += (StringUtils.hasText(agency)) ? agency : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? ". " : "";
                affiliation += (StringUtils.hasText(section)) ? section : "";

                if(StringUtils.hasText(affiliation)) {
                    d.setAffiliation(affiliation);
                }
            } else if(colltype.equals("2")) {
                // We have an organisation collector
                String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORORGANISATION);
                String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORAGENCY);
                String section = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORSECTION);
                DataCollectorType d;
                if(!StringUtils.hasText(agency) && !StringUtils.hasText(section)) {
                    if(!StringUtils.hasText(organisation)) {
                        continue;
                    }
                    d = fillTextType(dataColl.addNewDataCollector(), organisation);
                } else {
                    String collector = (StringUtils.hasText(agency)) ? agency : "";
                    if(StringUtils.hasText(collector) && StringUtils.hasText(section)) {
                        collector += ". "+section;
                    } else if(StringUtils.hasText(section)) {
                        collector = section;
                    } else {
                        continue;
                    }
                    d = fillTextType(dataColl.addNewDataCollector(), collector);
                }

                String abbr = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORORGANISATIONABBR);

                d.setAbbr(abbr);
                if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                    if(StringUtils.hasText(organisation)) {
                        d.setAffiliation(organisation);
                    }
                }
            }
        }
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

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.COLLMODEOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            ConceptualTextType t = dataColl.addNewCollMode();

            ConceptType c = fillTextType(t.addNewConcept(), collmode);

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

            Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.INSTRUMENTOTHER));
            if(hasValue(valueFieldPair, language)) {
                txt = valueFieldPair.getRight().getActualValueFor(language);
            }

            // Keyword should always be non null at this point
            ResInstruType t = dataColl.addNewResInstru();

            ConceptType c = fillTextType(t.addNewConcept(), instrument);

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
