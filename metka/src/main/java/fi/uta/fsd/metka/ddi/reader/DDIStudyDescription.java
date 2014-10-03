package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;

import java.util.HashMap;
import java.util.Map;

class DDIStudyDescription extends DDISectionBase {
    private static final Map<Language, String> WEIGHT_NO = new HashMap<>();
    private static final Map<String, Map<Language, String>> RESTRICTION = new HashMap<>();

    static {
        WEIGHT_NO.put(Language.DEFAULT, "Aineisto ei sisällä painomuuttujia.");
        WEIGHT_NO.put(Language.EN, "There are no weight variables in the data.");
        WEIGHT_NO.put(Language.SV, "Datamaterialet innehåller inga viktvariabler.");

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
    }

    private final RevisionRepository revisions;
    private final ReferenceService references;

    DDIStudyDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, info, configuration);
        this.revisions = revisions;
        this.references = references;
    }

    private String getReferenceTitle(Language language, RevisionData revision, String path) {
        ReferenceOption option = references.getCurrentFieldOption(language, revision, path);
        if(option != null) {
            return option.getTitle().getValue();
        } else return null;
    }

    void read() {
        if(codeBook.getStdyDscrArray().length == 0) {
            return;
        }

        StdyDscrType stdyDscr = codeBook.getStdyDscrArray(0);

        readCitation(stdyDscr);

        readStudyAuthorization(stdyDscr);

        readStudyInfo(stdyDscr);

        readMethod(stdyDscr);

        readDataAccess(stdyDscr);

        readOtherStudyMaterial(stdyDscr);
    }

    private void readCitation(StdyDscrType stdyDscr) {
        if(stdyDscr.getCitationArray().length == 0) {
            return;
        }

        CitationType citation = stdyDscr.getCitationArray(0);

        readCitationTitle(citation);

        readCitationRspStatement(citation);

        readCitationProdStatement(citation);

        // TODO: Questions about versions still open
        readCitationVerStatement(citation);
    }

    private void readCitationTitle(CitationType citation) {
        TitlStmtType titlStmt = citation.getTitlStmt();
        if(titlStmt == null) {
            return;
        }

        valueSet(Fields.TITLE, titlStmt.xmlText());

        // TODO: Alt titles when tables are sorted out

        // TODO: Par titles when tables are sorted out

        // TODO: Skip ID?

        /*Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.TITLE));
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();
        if(hasValue(valueFieldPair, language)) {
            // Add title of requested language
            fillTextType(titlStmtType.addNewTitl(), valueFieldPair, language);
        }

        readAltTitles(revisionData, language, titlStmt);

        readParTitles(revisionData, language, titlStmt);

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
        }*/
    }

    private void readAltTitles(TitlStmtType titlStmt) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair;// Add alternative titles
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.ALTTITLES));
        // TODO: Do we translate alternate titles or do the alternate titles have translations?
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.ALTTITLE));
                if(hasValue(valueFieldPair, language)) {
                    fillTextType(titlStmtType.addNewAltTitl(), valueFieldPair, language);
                }
            }
        }*/
    }

    private void readParTitles(TitlStmtType titlStmt) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.TITLE));
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
        }*/
    }

    private void readCitationRspStatement(CitationType citation) {
        // TODO: Reverse process

        // TODO: Questions about tables and authors still open
        // Authors, other authors and producers need resolved answers before continuing


        /*RspStmtType rsp = citationType.addNewRspStmt();
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
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? " " : "";
                affiliation += (StringUtils.hasText(agency)) ? agency : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? " " : "";
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

                    String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORORGANISATION);
                    String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORAGENCY);
                    String section = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORSECTION);

                    String affiliation = (StringUtils.hasText(organisation)) ? organisation : "";
                    affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? " " : "";
                    affiliation += (StringUtils.hasText(agency)) ? agency : "";
                    affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? " " : "";
                    affiliation += (StringUtils.hasText(section)) ? section : "";

                    if(StringUtils.hasText(affiliation)) {
                        d.setAffiliation(affiliation);
                    }
                } else if(colltype.equals("2")) {
                    // We have an organisation collector
                    String organisation = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORORGANISATION);
                    String agency = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORAGENCY);
                    String section = getReferenceTitle(references, language, revision, rowRoot + Fields.COLLECTORSECTION);
                    OthIdType d;
                    if(!StringUtils.hasText(agency) && !StringUtils.hasText(section)) {
                        if(!StringUtils.hasText(organisation)) {
                            continue;
                        }
                        d = fillTextType(rsp.addNewOthId(), organisation);
                    } else {
                        String collector = (StringUtils.hasText(agency)) ? agency : "";
                        if(StringUtils.hasText(collector) && StringUtils.hasText(section)) {
                            collector += " "+section;
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
        }*/
    }

    private static void readCitationProdStatement(CitationType citation) {
        // TODO: Questions about tables and authors still open
        // Authors, other authors and producers need resolved answers before continuing

        // TODO: Reverse process
        /*ProdStmtType prodStmtType = citationType.addNewProdStmt();

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
                    producer += (StringUtils.hasText(producer) && StringUtils.hasText(section)) ? " " : "";
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
        }*/
    }

    private static void readCitationVerStatement(CitationType citation) {
        // TODO: Reverse process
        /*VerStmtType verStmtType = citationType.addNewVerStmt();

        // Add version, repeatable
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.DATAVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSION));
                if(hasValue(valueFieldPair, language)) {
                    fillTextAndDateType(verStmtType.addNewVersion(), valueFieldPair, language);
                }
            }
        }*/
    }

    private void readStudyAuthorization(StdyDscrType stdyDscr) {
        // TODO: Questions about tables and authors still open
        // Authors, other authors and producers need resolved answers before continuing

        // TODO: Reverse process
        /*Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.AUTHORS));
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
                    authorizer += (StringUtils.hasText(authorizer) && StringUtils.hasText(section)) ? " " : "";
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
        }*/
    }

    private void readStudyInfo(StdyDscrType stdyDscr) {
        if(stdyDscr.getStdyInfoArray().length == 0) {
            return;
        }
        StdyInfoType stdyInfo = stdyDscr.getStdyInfoArray(0);

        readStudyInfoSubject(stdyInfo);

        if(stdyInfo.getAbstractArray().length > 0) {
            AbstractType abstractType = stdyInfo.getAbstractArray(0);
            valueSet(Fields.ABSTRACT, abstractType.xmlText());
        }

        readStudyInfoSumDesc(stdyInfo);
    }

    private void readStudyInfoSubject(StdyInfoType stdyInfo) {
        // TODO: Questions about tables still open

        // TODO: Reverse process
        /*SubjectType subject= stdyInfo.addNewSubject();

        readStudyInfoSubjectKeywords(subject, revision, language, references);

        readStudyInfoSubjectTopics(subject, revision, language, references);*/
    }

    private void readStudyInfoSubjectKeywords(SubjectType subject) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "keywords.";
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
        }*/
    }

    private void readStudyInfoSubjectTopics(SubjectType subject) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "topics.";
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
        }*/
    }

    private void readStudyInfoSumDesc(StdyInfoType stdyInfo) {
        // TODO: Questions about tables are still open
        // TODO: Reverse process

        if(stdyInfo.getSumDscrArray().length == 0) {
            return;
        }
        SumDscrType sumDscr = stdyInfo.getSumDscrArray(0);
        /*

        readStudyInfoSumDescTimePrd(sumDscrType, revision, language);

        readStudyInfoSumDescCollDate(sumDscrType, revision, language);

        readStudyInfoSumDescNation(sumDscrType, revision, language, references);

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

        readStudyInfoSumDescAnlyUnit(sumDscrType, revision, language, references);

        readStudyInfoSumDescUniverse(language, sumDscrType, revision);
        */

        if(sumDscr.getDataKindArray().length > 0) {
            SelectionList list = configuration.getRootSelectionList(configuration.getField(Fields.DATAKIND).getSelectionList());
            String dataKind = sumDscr.getDataKindArray(0).xmlText();
            for(Option option : list.getOptions()) {
                if(option.getTitleFor(language).equals(dataKind)) {
                    valueSet(Fields.DATAKIND, option.getValue());
                    break;
                }
            }
        }
    }

    private void readStudyInfoSumDescTimePrd(SumDscrType sumDscr) {
        // TODO: Reverse process
        /*for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
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
        }*/
    }

    private void readStudyInfoSumDescCollDate(SumDscrType sumDscr) {
        // TODO: Reverse process
        /*for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
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
        }*/
    }

    private void readStudyInfoSumDescNation(SumDscrType sumDscr) {
        // TODO: Reverse process
        /*String path = "countries.";
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
        }*/
    }

    private void readStudyInfoSumDescAnlyUnit(SumDscrType sumDscr) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "analysis.";
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
        }*/
    }

    private void readStudyInfoSumDescUniverse(SumDscrType sumDscr) {
        /*for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
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
        }*/
    }

    private void readMethod(StdyDscrType stdyDscr) {
        if(stdyDscr.getMethodArray().length == 0) {
            return;
        }
        MethodType method = stdyDscr.getMethodArray(0);

        readMethodDataColl(method);

        if(method.getNotesArray().length > 0) {
            valueSet(Fields.DATAPROSESSING, method.getNotesArray(0).xmlText());
        }

        readMethodAnalyze(method);
    }

    private void readMethodDataColl(MethodType method) {
        if(method.getDataCollArray().length == 0) {
            return;
        }
        DataCollType dataColl = method.getDataCollArray(0);

        // TODO: Reverse process

        // TODO: Questions regarding tables still unanswered
        /*readMethodDataCollTimeMeth(dataCollType, revision, language, references);

        readMethodDataCollSampProc(dataCollType, revision, language, references);

        readMethodDataCollCollMode(dataCollType, revision, language, references);

        readMethodDataCollResInstru(dataCollType, revision, language, references);

        readMethodDataCollDataCollector(dataCollType, revision, language, references);

        readMethodDataCollSources(dataColl);*/

        readMethodDataCollWeight(dataColl);
    }

    private static void readMethodDataCollTimeMeth(DataCollType dataColl, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "timemethods.";
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
        }*/
    }

    private static void readMethodDataCollDataCollector(DataCollType dataColl, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "collectors.";
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
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(agency)) ? " " : "";
                affiliation += (StringUtils.hasText(agency)) ? agency : "";
                affiliation += (StringUtils.hasText(affiliation) && StringUtils.hasText(section)) ? " " : "";
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
                        collector += " "+section;
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
        }*/
    }

    private static void readMethodDataCollSampProc(DataCollType dataColl, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "sampprocs.";
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
        }*/
    }

    private static void readMethodDataCollCollMode(DataCollType dataColl, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "collmodes.";
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
        }*/
    }

    private static void readMethodDataCollResInstru(DataCollType dataColl, RevisionData revision, Language language, ReferenceService references) {
        // Let's hardcode the path since we know exactly what we are looking for.
        /*String pathRoot = "instruments.";
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
        }*/
    }

    private void readMethodDataCollSources(DataCollType dataColl) {
        // TODO: Questions about tables ...
        /*List<ValueDataField> fields = gatherFields(revision, Fields.DATASOURCES, Fields.DATASOURCE, language, language);
        SourcesType sources = dataCollType.addNewSources();
        for(ValueDataField field : fields) {
            fillTextType(sources.addNewDataSrc(), field, language);
        }*/
    }

    private void readMethodDataCollWeight(DataCollType dataColl) {
        if(dataColl.getWeightArray().length == 0) {
            return;
        }
        SimpleTextType stt = dataColl.getWeightArray(0);
        if(stt.xmlText().equals(WEIGHT_NO.get(language))) {
            valueSet(Fields.WEIGHTYESNO, "true");
            valueSet(Fields.WEIGHT, "");
        } else {
            valueSet(Fields.WEIGHTYESNO, "false");
            valueSet(Fields.WEIGHT, stt.xmlText());
        }
    }

    private void readMethodAnalyze(MethodType method) {
        if(method.getAnlyInfo() == null) {
            return;
        }
        AnlyInfoType anlyInfo = method.getAnlyInfo();

        if(anlyInfo.getRespRateArray().length > 0) {
            valueSet(Fields.RESPRATE, anlyInfo.getRespRateArray(0).xmlText(), Language.DEFAULT);
        }

        // TODO: Questions about tables still open
        /*

        // Add data appraisal, repeatable
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.APPRAISALS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for (DataRow row : containerPair.getRight().getRowsFor(language)) {
                valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.APPRAISAL));
                if(hasValue(valueFieldPair, language)) {
                    fillTextType(anlyInfoType.addNewDataAppr(), valueFieldPair, language);
                }
            }
        }*/
    }

    private void readDataAccess(StdyDscrType stdyDscr) {
        if(stdyDscr.getDataAccsArray().length == 0) {
            return;
        }
        DataAccsType dataAccs = stdyDscr.getDataAccsArray(0);

        readDataAccessSetAvail(dataAccs);

        readDataAccessUseStatement(dataAccs);

        if(dataAccs.getNotesArray().length > 0) {
            valueSet(Fields.DATASETNOTES, dataAccs.getNotesArray(0).xmlText());
        }
    }

    private void readDataAccessSetAvail(DataAccsType dataAccs) {
        if(dataAccs.getSetAvailArray().length == 0) {
            return;
        }
        SetAvailType setAvail = dataAccs.getSetAvailArray(0);
        if(setAvail.getOrigArchArray().length > 0) {
            valueSet(Fields.ORIGINALLOCATION, setAvail.getOrigArchArray(0).xmlText(), Language.DEFAULT);
        }

        if(setAvail.getCollSizeArray().length > 0) {
            valueSet(Fields.COLLSIZE, setAvail.getCollSizeArray(0).xmlText());
        }

        if(setAvail.getCompleteArray().length > 0) {
            valueSet(Fields.COMPLETE, setAvail.getCompleteArray(0).xmlText());
        }
    }

    private void readDataAccessUseStatement(DataAccsType dataAccs) {
        if(dataAccs.getUseStmtArray().length == 0) {
            return;
        }
        UseStmtType useStmt = dataAccs.getUseStmtArray(0);

        if(useStmt.getSpecPermArray().length > 0) {
            valueSet(Fields.SPECIALTERMSOFUSE, useStmt.getSpecPermArray(0).xmlText());
        }

        if(useStmt.getRestrctnArray().length > 0) {
            String restr = useStmt.getRestrctnArray(0).xmlText();

            for(String i : RESTRICTION.keySet()) {
                if(RESTRICTION.get(i).get(language).equals(restr)) {
                    valueSet(Fields.TERMSOFUSE, restr, Language.DEFAULT);
                    break;
                }
            }
        }
    }

    private void readOtherStudyMaterial(StdyDscrType stdyDscr) {
        if(stdyDscr.getOthrStdyMatArray().length == 0) {
            return;
        }
        OthrStdyMatType othr = stdyDscr.getOthrStdyMatArray(0);

        // TODO: Questions about tables still unanswered
        /*

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
        }*/
    }
}
