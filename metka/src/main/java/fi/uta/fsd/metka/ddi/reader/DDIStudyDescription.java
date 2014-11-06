package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
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

    private final ReferenceService references;

    DDIStudyDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration, ReferenceService references) {
        super(revision, language, codeBook, info, configuration);
        this.references = references;
    }

    ReturnResult read() {
        /*
         * We know that language has to be DEFAULT and that description tab should be clear so we can just insert the new data in
         */
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

        result = readCitationRspStatementAuth(citation);
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

    private ReturnResult readCitationRspStatementAuth(CitationType citation) {
        if(citation.getRspStmt() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        RspStmtType rsp = citation.getRspStmt();
        if(!hasContent(rsp.getAuthEntyArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.AUTHORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.AUTHORS);
        request.setLanguage(language);

        // These are all organization authors
        for(AuthEntyType auth : rsp.getAuthEntyArray()) {
            if(!StringUtils.hasText(auth.xmlText())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            // Set type to person
            valueSet(row.getRight(), Fields.AUTHORTYPE, "1");
            valueSet(row.getRight(), Fields.AUTHOR, auth.xmlText());
            if(StringUtils.hasText(auth.getAffiliation())) {
                String[] splits = auth.getAffiliation().split("\\. ");
                String orgValue = null;
                String agencyValue = null;
                if(splits.length > 0) {
                    ReferenceOption option = findOrganization(splits[0], Fields.AUTHORORGANISATION);
                    orgValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(orgValue)) {
                        valueSet(row.getRight(), Fields.AUTHORORGANISATION, orgValue);
                    }
                }
                if(splits.length > 1 && orgValue != null) {
                    ReferenceOption option = findAgency(splits[1], Fields.AUTHORORGANISATION, orgValue, Fields.AUTHORAGENCY);
                    agencyValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(agencyValue)) {
                        valueSet(row.getRight(), Fields.AUTHORAGENCY, agencyValue);
                    }
                }
                if(splits.length > 2 && agencyValue != null) {
                    ReferenceOption option = findSection(splits[2], Fields.AUTHORORGANISATION, orgValue, Fields.AUTHORAGENCY, agencyValue, Fields.AUTHORSECTION);
                    String sectionValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(sectionValue)) {
                        valueSet(row.getRight(), Fields.AUTHORSECTION, sectionValue);
                    }
                }
            }

        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readCitationProdStatement(CitationType citation) {
        if(citation.getProdStmt() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        ProdStmtType prodStmt = citation.getProdStmt();

        if(!hasContent(prodStmt.getProducerArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.PRODUCERS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.PRODUCERS);
        request.setLanguage(language);

        for(ProducerType producer : prodStmt.getProducerArray()) {
            if(!StringUtils.hasText(producer.xmlText())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            // If producer has affiliation then we know that it has at least an agency and possibly a section
            // If not then we know that the actual producer is an organization
            ReferenceOption option = findOrganization(StringUtils.hasText(producer.getAffiliation()) ? producer.getAffiliation() : producer.xmlText(), Fields.PRODUCERORGANISATION);
            String orgValue = (option != null) ? option.getValue() : null;
            if(StringUtils.hasText(orgValue)) {
                valueSet(row.getRight(), Fields.PRODUCERORGANISATION, orgValue);
            }
            if(StringUtils.hasText(producer.getAffiliation())) {
                String agencyValue = null;
                String[] splits = producer.xmlText().split("\\. ");

                if(splits.length > 0 && orgValue != null) {
                    option = findAgency(splits[0], Fields.PRODUCERORGANISATION, orgValue, Fields.PRODUCERAGENCY);
                    agencyValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(agencyValue)) {
                        valueSet(row.getRight(), Fields.PRODUCERAGENCY, agencyValue);
                    }
                }
                if(splits.length > 1 && agencyValue != null) {
                    option = findSection(splits[1], Fields.PRODUCERORGANISATION, orgValue, Fields.PRODUCERAGENCY, agencyValue, Fields.PRODUCERSECTION);
                    String sectionValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(sectionValue)) {
                        valueSet(row.getRight(), Fields.PRODUCERSECTION, sectionValue);
                    }
                }
            }
            if(StringUtils.hasText(producer.getRole())) {
                SelectionList list = configuration.getRootSelectionList(configuration.getField(Fields.PRODUCERROLE).getSelectionList());
                for(Option o : list.getOptions()) {
                    if(o.getTitleFor(language).equals(producer.getRole())) {
                        valueSet(row.getRight(), Fields.PRODUCERROLE, o.getValue());
                        break;
                    }
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readStudyAuthorization(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getStudyAuthorizationArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        StudyAuthorizationType stdyAuth = stdyDscr.getStudyAuthorizationArray(0);
        if(!hasContent(stdyAuth.getAuthorizingAgencyArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.AUTHORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.AUTHORS);
        request.setLanguage(language);

        // These are all organization authors
        for(AuthorizingAgencyType auth : stdyAuth.getAuthorizingAgencyArray()) {
            if(!StringUtils.hasText(auth.xmlText())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            // Set type to organization
            valueSet(row.getRight(), Fields.AUTHORTYPE, "2");
            // If organization author has affiliation then we know that it has at least an agency and possibly a section
            // If not then we know that the actual author is an organization
            ReferenceOption option = findOrganization(StringUtils.hasText(auth.getAffiliation()) ? auth.getAffiliation() : auth.xmlText(), Fields.AUTHORORGANISATION);
            String orgValue = (option != null) ? option.getValue() : null;
            if(StringUtils.hasText(orgValue)) {
                valueSet(row.getRight(), Fields.AUTHORORGANISATION, orgValue);
            }
            if(StringUtils.hasText(auth.getAffiliation())) {
                String agencyValue = null;
                String[] splits = auth.xmlText().split("\\. ");

                if(splits.length > 0 && orgValue != null) {
                    option = findAgency(splits[0], Fields.AUTHORORGANISATION, orgValue, Fields.AUTHORAGENCY);
                    agencyValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(agencyValue)) {
                        valueSet(row.getRight(), Fields.AUTHORAGENCY, agencyValue);
                    }
                }
                if(splits.length > 1 && agencyValue != null) {
                    option = findSection(splits[1], Fields.AUTHORORGANISATION, orgValue, Fields.AUTHORAGENCY, agencyValue, Fields.AUTHORSECTION);
                    String sectionValue = (option != null) ? option.getValue() : null;
                    if(StringUtils.hasText(sectionValue)) {
                        valueSet(row.getRight(), Fields.AUTHORSECTION, sectionValue);
                    }
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
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

    private ReturnResult readStudyInfoSubjectKeywords(SubjectType subject) {
        if(!hasContent(subject.getKeywordArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.KEYWORDS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.KEYWORDS);
        request.setLanguage(language);

        ReferencePath keywordvocabPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.KEYWORDVOCAB).getReference()), null);
        request.setRoot(keywordvocabPath);
        List<ReferenceOption> keywordvocabOptions = references.collectReferenceOptions(request);

        for(KeywordType k : subject.getKeywordArray()) {
            if(!StringUtils.hasText(k.getVocab())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            ReferenceOption option = findOption(keywordvocabOptions, k.getVocab());
            if(option == null) {
                continue;
            }
            valueSet(row.getRight(), Fields.KEYWORDVOCAB, option.getValue());

            if(StringUtils.hasText(k.xmlText())) {
                keywordvocabPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.KEYWORDVOCAB).getReference()), option.getValue());
                ReferencePath keywordPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.KEYWORD).getReference()), null);

                keywordvocabPath.setNext(keywordPath);
                keywordPath.setPrev(keywordvocabPath);
                request.setRoot(keywordvocabPath);

                List<ReferenceOption> options = references.collectReferenceOptions(request);
                option = findOption(options, k.xmlText());
                if(option != null) {
                    valueSet(row.getRight(), Fields.KEYWORD, option.getValue());
                } else {
                    valueSet(row.getRight(), Fields.KEYWORDNOVOCAB, k.xmlText());
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readStudyInfoSubjectTopics(SubjectType subject) {
        if(!hasContent(subject.getTopcClasArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.TOPICS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.TOPICS);
        request.setLanguage(language);

        ReferencePath topicvocabPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.TOPICVOCAB).getReference()), null);
        request.setRoot(topicvocabPath);
        List<ReferenceOption> topicvocabOptions = references.collectReferenceOptions(request);

        for(TopcClasType t : subject.getTopcClasArray()) {
            if(!StringUtils.hasText(t.getVocab())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            ReferenceOption option = findOption(topicvocabOptions, t.getVocab());
            if(option == null) {
                continue;
            }
            valueSet(row.getRight(), Fields.TOPICVOCAB, option.getValue());

            if(StringUtils.hasText(t.xmlText())) {
                topicvocabPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.TOPICVOCAB).getReference()), option.getValue());
                ReferencePath topicPath = new ReferencePath(configuration.getReference(configuration.getField(Fields.TOPIC).getReference()), null);

                topicvocabPath.setNext(topicPath);
                topicPath.setPrev(topicvocabPath);
                request.setRoot(topicvocabPath);

                List<ReferenceOption> options = references.collectReferenceOptions(request);
                option = findOption(options, t.xmlText());
                if(option != null) {
                    valueSet(row.getRight(), Fields.TOPIC, option.getValue());
                }
            }

        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReferenceOption findOption(List<ReferenceOption> options, String text) {
        for(ReferenceOption option : options) {
            if(option.getTitle().getValue().equals(text)) {
                return option;
            }
        }
        return null;
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

        result = readConceptualTextTypeArray(sumDscr.getAnlyUnitArray(), Fields.ANALYSIS, Fields.ANALYSISUNITVOCAB, Fields.ANALYSISUNIT, Fields.ANALYSISUNITOTHER, null);
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

    private ReturnResult readStudyInfoSumDescCollDate(SumDscrType sumDscr) {
        if(!hasContent(sumDscr.getCollDateArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(Fields.COLLTIME);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        ContainerChange change = containerPair.getRight().getRight();
        for(CollDateType coll : sumDscr.getCollDateArray()) {
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if (row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            if (StringUtils.hasText(coll.xmlText())) {
                valueSet(row.getRight(), Fields.COLLDATETEXT, coll.xmlText());
            }
            if(StringUtils.hasText(coll.getDate())) {
                valueSet(row.getRight(), Fields.COLLDATE, coll.getDate());
            }
            if(coll.getEvent() != null) {
                valueSet(row.getRight(), Fields.COLLDATEEVENT, coll.getEvent().toString());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readStudyInfoSumDescNation(SumDscrType sumDscr) {
        if(!hasContent(sumDscr.getNationArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(Fields.COUNTRIES);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        ContainerChange change = containerPair.getRight().getRight();

        for(NationType nation : sumDscr.getNationArray()) {
            if(!StringUtils.hasText(nation.xmlText())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            valueSet(row.getRight(), Fields.COUNTRY, nation.xmlText());
            if(StringUtils.hasText(nation.getAbbr())) {
                valueSet(row.getRight(), Fields.COUNTRYABBR, nation.getAbbr());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readStudyInfoSumDescUniverse(SumDscrType sumDscr) {
        if(!hasContent(sumDscr.getUniverseArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(Fields.UNIVERSE);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        ContainerChange change = containerPair.getRight().getRight();
        for(UniverseType universe : sumDscr.getUniverseArray()) {
            if(!StringUtils.hasText(universe.xmlText())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            valueSet(row.getRight(), Fields.UNIVERSE, universe.xmlText());
            if(universe.getClusion() != null) {
                valueSet(row.getRight(), Fields.UNIVERSECLUSION, universe.getClusion().toString());
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
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
        result = readConceptualTextTypeArray(dataColl.getTimeMethArray(), Fields.TIMEMETHODS, Fields.TIMEMETHODVOCAB, Fields.TIMEMETHOD, Fields.TIMEMETHODOTHER, null);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readConceptualTextTypeArray(dataColl.getSampProcArray(), Fields.SAMPPROCS, Fields.SAMPPROCVOCAB, Fields.SAMPPROC, Fields.SAMPPROCOTHER, Fields.SAMPPROCTEXT);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readConceptualTextTypeArray(dataColl.getCollModeArray(), Fields.COLLMODES, Fields.COLLMODEVOCAB, Fields.COLLMODE, Fields.COLLMODEOTHER, null);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readConceptualTextTypeArray(dataColl.getResInstruArray(), Fields.INSTRUMENTS, Fields.INSTRUMENTVOCAB, Fields.INSTRUMENT, Fields.INSTRUMENTOTHER, null);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollDataCollector(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = readMethodDataCollSources(dataColl);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readMethodDataCollWeight(dataColl);
    }

    private ReturnResult readConceptualTextTypeArray(ConceptualTextType[] ctta, String containerKey, String vocabKey, String conceptKey, String txtKey, String cttKey) {
        if(!hasContent(ctta)) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(containerKey);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(containerKey);
        request.setLanguage(language);

        ReferencePath vocabPath = new ReferencePath(configuration.getReference(configuration.getField(vocabKey).getReference()), null);
        request.setRoot(vocabPath);
        List<ReferenceOption> vocabOptions = references.collectReferenceOptions(request);

        for(ConceptualTextType ctt : ctta) {
            if(!hasContent(ctt.getConceptArray())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            if(cttKey != null && StringUtils.hasText(ctt.xmlText())) {
                valueSet(row.getRight(), cttKey, ctt.xmlText());
            }

            if(hasContent(ctt.getTxtArray()) && StringUtils.hasText(ctt.getTxtArray(0).xmlText())) {
                valueSet(row.getRight(), txtKey, ctt.getTxtArray(0).xmlText());
            }

            ConceptType c = ctt.getConceptArray(0);
            if(!StringUtils.hasText(c.getVocab())) {
                continue;
            }


            ReferenceOption option = findOption(vocabOptions, c.getVocab());
            if(option == null) {
                continue;
            }
            valueSet(row.getRight(), vocabKey, option.getValue());

            if(StringUtils.hasText(c.xmlText())) {
                vocabPath = new ReferencePath(configuration.getReference(configuration.getField(vocabKey).getReference()), option.getValue());
                ReferencePath selectionPath = new ReferencePath(configuration.getReference(configuration.getField(conceptKey).getReference()), null);

                vocabPath.setNext(selectionPath);
                selectionPath.setPrev(vocabPath);
                request.setRoot(vocabPath);

                List<ReferenceOption> options = references.collectReferenceOptions(request);
                option = findOption(options, c.xmlText());
                if(option != null) {
                    valueSet(row.getRight(), conceptKey, option.getValue());
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReferenceOption findOrganization(String title, String orgFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = new ReferencePath(configuration.getReference(configuration.getField(orgFieldKey).getReference()), null);
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReferenceOption findAgency(String title, String orgFieldKey, String orgValue, String agencyFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = new ReferencePath(configuration.getReference(configuration.getField(orgFieldKey).getReference()), orgValue);
        root.setNext(new ReferencePath(configuration.getReference(configuration.getField(agencyFieldKey).getReference()), null));
        root.getNext().setPrev(root);
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReferenceOption findSection(String title, String orgFieldKey, String orgValue, String agencyFieldKey, String agencyValue, String sectionFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = new ReferencePath(configuration.getReference(configuration.getField(orgFieldKey).getReference()), orgValue);
        root.setNext(new ReferencePath(configuration.getReference(configuration.getField(agencyFieldKey).getReference()), agencyValue));
        root.getNext().setPrev(root);
        root.getNext().setNext(new ReferencePath(configuration.getReference(configuration.getField(sectionFieldKey).getReference()), null));
        root.getNext().getNext().setPrev(root.getNext());
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReturnResult readMethodDataCollDataCollector(DataCollType dataColl) {
        if(!hasContent(dataColl.getDataCollectorArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.COLLECTORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        ContainerChange change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.COLLECTORS);
        request.setLanguage(language);

        for(DataCollectorType collector : dataColl.getDataCollectorArray()) {
            if(!StringUtils.hasText(collector.xmlText())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }

            // If collector has an abbreviation then assume organization since persons don't have abbreviation.
            // It is possible for organization to not have abbreviation but there's no way for us to detect this.
            if(!StringUtils.hasText(collector.getAbbr())) {
                valueSet(row.getRight(), Fields.COLLECTORTYPE, "1");
                valueSet(row.getRight(), Fields.COLLECTOR, collector.xmlText());
                if(StringUtils.hasText(collector.getAffiliation())) {
                    String[] splits = collector.getAffiliation().split("\\. ");
                    String orgValue = null;
                    String agencyValue = null;
                    if(splits.length > 0) {
                        ReferenceOption option = findOrganization(splits[0], Fields.COLLECTORORGANISATION);
                        orgValue = (option != null) ? option.getValue() : null;
                        if(StringUtils.hasText(orgValue)) {
                            valueSet(row.getRight(), Fields.COLLECTORORGANISATION, orgValue);
                        }
                    }
                    if(splits.length > 1 && orgValue != null) {
                        ReferenceOption option = findAgency(splits[1], Fields.COLLECTORORGANISATION, orgValue, Fields.COLLECTORAGENCY);
                        agencyValue = (option != null) ? option.getValue() : null;
                        if(StringUtils.hasText(agencyValue)) {
                            valueSet(row.getRight(), Fields.COLLECTORAGENCY, agencyValue);
                        }
                    }
                    if(splits.length > 2 && agencyValue != null) {
                        ReferenceOption option = findSection(splits[2], Fields.COLLECTORORGANISATION, orgValue, Fields.COLLECTORAGENCY, agencyValue, Fields.COLLECTORSECTION);
                        String sectionValue = (option != null) ? option.getValue() : null;
                        if(StringUtils.hasText(sectionValue)) {
                            valueSet(row.getRight(), Fields.COLLECTORSECTION, sectionValue);
                        }
                    }
                }
            } else {
                valueSet(row.getRight(), Fields.COLLECTORTYPE, "2");
                // If organization collector has affiliation then we know that it has at least an agency and possibly a section
                // If not then we know that the actual collector is an organization
                ReferenceOption option = findOrganization(StringUtils.hasText(collector.getAffiliation()) ? collector.getAffiliation() : collector.xmlText(), Fields.COLLECTORORGANISATION);
                String orgValue = (option != null) ? option.getValue() : null;
                if(StringUtils.hasText(orgValue)) {
                    valueSet(row.getRight(), Fields.COLLECTORORGANISATION, orgValue);
                }
                if(StringUtils.hasText(collector.getAffiliation())) {
                    String agencyValue = null;
                    String[] splits = collector.xmlText().split("\\. ");

                    if(splits.length > 0 && orgValue != null) {
                        option = findAgency(splits[0], Fields.COLLECTORORGANISATION, orgValue, Fields.COLLECTORAGENCY);
                        agencyValue = (option != null) ? option.getValue() : null;
                        if(StringUtils.hasText(agencyValue)) {
                            valueSet(row.getRight(), Fields.COLLECTORAGENCY, agencyValue);
                        }
                    }
                    if(splits.length > 1 && agencyValue != null) {
                        option = findSection(splits[1], Fields.COLLECTORORGANISATION, orgValue, Fields.COLLECTORAGENCY, agencyValue, Fields.COLLECTORSECTION);
                        String sectionValue = (option != null) ? option.getValue() : null;
                        if(StringUtils.hasText(sectionValue)) {
                            valueSet(row.getRight(), Fields.COLLECTORSECTION, sectionValue);
                        }
                    }
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
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
