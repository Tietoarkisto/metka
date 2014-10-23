package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;

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

    ReturnResult read() {
        if(codeBook.getStdyDscrArray().length == 0) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        StdyDscrType stdyDscr = codeBook.getStdyDscrArray(0);
        ReturnResult result;

        result = readCitation(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readStudyAuthorization(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readStudyInfo(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethod(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readDataAccess(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readOtherStudyMaterial(stdyDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readCitation(StdyDscrType stdyDscr) {
        if(hasContent(stdyDscr.getCitationArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        CitationType citation = stdyDscr.getCitationArray(0);

        ReturnResult result;

        result = readCitationTitle(citation);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readCitationRspStatement(citation);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readCitationProdStatement(citation);
    }

    private ReturnResult readCitationTitle(CitationType citation) {
        if(citation.getTitlStmt() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        TitlStmtType titlStmt = citation.getTitlStmt();
        ReturnResult result;
        result = readAltTitles(titlStmt);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readParTitles(titlStmt);
    }

    private ReturnResult readAltTitles(TitlStmtType titlStmt) {
        if(hasContent(titlStmt.getAltTitlArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.ALTTITLES);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(AbstractTextType tt : titlStmt.getAltTitlArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.ALTTITLE, tt.xmlText());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readParTitles(TitlStmtType titlStmt) {
        if(language != Language.DEFAULT) {
            // Par titles are default language only
            // TODO: Do we need these in some way from other than default language?
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        if(hasContent(titlStmt.getParTitlArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.PARTITLES);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(SimpleTextType stt : titlStmt.getParTitlArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.PARTITLE, stt.xmlText());
                valueSet(row.getRight(), Fields.PARTITLELANG, stt.getXmlLang());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    // TODO: Still unfinished
    private ReturnResult readCitationRspStatement(CitationType citation) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private static ReturnResult readCitationProdStatement(CitationType citation) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readStudyAuthorization(StdyDscrType stdyDscr) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    private ReturnResult readStudyInfo(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getStdyInfoArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        StdyInfoType stdyInfo = stdyDscr.getStdyInfoArray(0);

        ReturnResult result;

        result = readStudyInfoSubject(stdyInfo);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        if(hasContent(stdyInfo.getAbstractArray())) {
            AbstractType abstractType = stdyInfo.getAbstractArray(0);
            valueSet(Fields.ABSTRACT, abstractType.xmlText());
        }

        return readStudyInfoSumDesc(stdyInfo);
    }

    private ReturnResult readStudyInfoSubject(StdyInfoType stdyInfo) {
        if(!hasContent(stdyInfo.getSumDscrArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        SubjectType subject = stdyInfo.getSubjectArray(0);

        ReturnResult result;
        result = readStudyInfoSubjectKeywords(subject);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readStudyInfoSubjectTopics(subject);
    }

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSubjectKeywords(SubjectType subject) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSubjectTopics(SubjectType subject) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    private ReturnResult readStudyInfoSumDesc(StdyInfoType stdyInfo) {
        if(!hasContent(stdyInfo.getSumDscrArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SumDscrType sumDscr = stdyInfo.getSumDscrArray(0);

        ReturnResult result;
        result = readStudyInfoSumDescTimePrd(sumDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readStudyInfoSumDescCollDate(sumDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readStudyInfoSumDescNation(sumDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        if(hasContent(sumDscr.getGeogCoverArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.GEOGCOVERS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(AbstractTextType tt : sumDscr.getGeogCoverArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.GEOGCOVER, tt.xmlText());
            }
        }

        result = readStudyInfoSumDescAnlyUnit(sumDscr);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readStudyInfoSumDescUniverse(sumDscr);
    }

    private ReturnResult readStudyInfoSumDescTimePrd(SumDscrType sumDscr) {
        if(hasContent(sumDscr.getTimePrdArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.TIMEPERIODS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(TimePrdType t : sumDscr.getTimePrdArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.TIMEPERIODTEXT, t.xmlText());
                valueSet(row.getRight(), Fields.TIMEPERIOD, t.getDate());
                valueSet(row.getRight(), Fields.TIMEPERIODEVENT, t.getEvent().toString());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSumDescCollDate(SumDscrType sumDscr) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSumDescNation(SumDscrType sumDscr) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSumDescAnlyUnit(SumDscrType sumDscr) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readStudyInfoSumDescUniverse(SumDscrType sumDscr) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    private ReturnResult readMethod(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getMethodArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        MethodType method = stdyDscr.getMethodArray(0);

        ReturnResult result;
        result = readMethodDataColl(method);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        if(hasContent(method.getNotesArray())) {
            valueSet(Fields.DATAPROSESSING, method.getNotesArray(0).xmlText());
        }

        return readMethodAnalyze(method);
    }

    private ReturnResult readMethodDataColl(MethodType method) {
        if(!hasContent(method.getDataCollArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        DataCollType dataColl = method.getDataCollArray(0);

        ReturnResult result;
        result = readMethodDataCollTimeMeth(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollSampProc(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollCollMode(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollResInstru(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollDataCollector(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollSources(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readMethodDataCollWeight(dataColl);
    }

    // TODO: Still unfinished
    private ReturnResult readMethodDataCollTimeMeth(DataCollType dataColl) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readMethodDataCollDataCollector(DataCollType dataColl) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readMethodDataCollSampProc(DataCollType dataColl) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readMethodDataCollCollMode(DataCollType dataColl) {
        return ReturnResult.OPERATION_SUCCESSFUL;
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

    // TODO: Still unfinished
    private ReturnResult readMethodDataCollResInstru(DataCollType dataColl) {
        // TODO: How to correctly handle languages other than default?
        if(!hasContent(dataColl.getResInstruArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.INSTRUMENTS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.INSTRUMENTS);
        request.setLanguage(language);

        ReferencePath instrumentvocabPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.INSTRUMENTVOCAB).getReference()), null);

        return ReturnResult.OPERATION_SUCCESSFUL;

        /*for(DataApprType appr : anlyInfo.getDataApprArray()) {
            Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }
            valueSet(row.getRight(), Fields.APPRAISAL, appr.xmlText());
        }*/

        // Find out vocab selection

        // Set vocab

        // Find out instrument using vocab

        // Set instrument

        // Set instrumentother

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

    private ReturnResult readMethodDataCollSources(DataCollType dataColl) {
        if(dataColl.getSources() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SourcesType sources = dataColl.getSources();

        if(!hasContent(sources.getDataSrcArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.DATASOURCES);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
        for(SimpleTextType stt : sources.getDataSrcArray()) {
            Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }
            valueSet(row.getRight(), Fields.DATASOURCE, stt.xmlText());
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readMethodDataCollWeight(DataCollType dataColl) {
        if(!hasContent(dataColl.getWeightArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SimpleTextType stt = dataColl.getWeightArray(0);
        if(stt.xmlText().equals(WEIGHT_NO.get(language))) {
            valueSet(Fields.WEIGHTYESNO, "true");
            valueSet(Fields.WEIGHT, "");
        } else {
            valueSet(Fields.WEIGHTYESNO, "false");
            valueSet(Fields.WEIGHT, stt.xmlText());
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readMethodAnalyze(MethodType method) {
        if(method.getAnlyInfo() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        AnlyInfoType anlyInfo = method.getAnlyInfo();

        if(hasContent(anlyInfo.getRespRateArray())) {
            valueSet(Fields.RESPRATE, anlyInfo.getRespRateArray(0).xmlText(), Language.DEFAULT);
        }

        if(hasContent(anlyInfo.getDataApprArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.APPRAISALS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(DataApprType appr : anlyInfo.getDataApprArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.APPRAISAL, appr.xmlText());
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readDataAccess(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getDataAccsArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        DataAccsType dataAccs = stdyDscr.getDataAccsArray(0);
        ReturnResult result;

        result = readDataAccessSetAvail(dataAccs);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readDataAccessUseStatement(dataAccs);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        if(hasContent(dataAccs.getNotesArray())) {
            valueSet(Fields.DATASETNOTES, dataAccs.getNotesArray(0).xmlText());
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readDataAccessSetAvail(DataAccsType dataAccs) {
        if(!hasContent(dataAccs.getSetAvailArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SetAvailType setAvail = dataAccs.getSetAvailArray(0);
        if(hasContent(setAvail.getOrigArchArray())) {
            valueSet(Fields.ORIGINALLOCATION, setAvail.getOrigArchArray(0).xmlText(), Language.DEFAULT);
        }

        if(hasContent(setAvail.getCollSizeArray())) {
            valueSet(Fields.COLLSIZE, setAvail.getCollSizeArray(0).xmlText());
        }

        if(hasContent(setAvail.getCompleteArray())) {
            valueSet(Fields.COMPLETE, setAvail.getCompleteArray(0).xmlText());
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readDataAccessUseStatement(DataAccsType dataAccs) {
        if(!hasContent(dataAccs.getUseStmtArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        UseStmtType useStmt = dataAccs.getUseStmtArray(0);

        if(hasContent(useStmt.getSpecPermArray())) {
            valueSet(Fields.SPECIALTERMSOFUSE, useStmt.getSpecPermArray(0).xmlText());
        }

        if(hasContent(useStmt.getRestrctnArray())) {
            String restr = useStmt.getRestrctnArray(0).xmlText();

            for(String i : RESTRICTION.keySet()) {
                if(RESTRICTION.get(i).get(language).equals(restr)) {
                    valueSet(Fields.TERMSOFUSE, restr, Language.DEFAULT);
                    break;
                }
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readOtherStudyMaterial(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getOthrStdyMatArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        OthrStdyMatType othr = stdyDscr.getOthrStdyMatArray(0);

        if(hasContent(othr.getRelMatArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.RELATEDMATERIALS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(RelMatType relMat : othr.getRelMatArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.RELATEDMATERIAL, relMat.xmlText());
            }
        }

        if(hasContent(othr.getOthRefsArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.PUBLICATIONCOMMENTS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
            for(OthRefsType othRef : othr.getOthRefsArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }
                valueSet(row.getRight(), Fields.PUBLICATIONCOMMENT, othRef.xmlText());
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
