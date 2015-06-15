package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

class DDIReadStudyDescription extends DDIReadSectionBase {
    private final ReferenceService references;

    DDIReadStudyDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration, ReferenceService references) {
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
        if(!hasContent(stdyDscr.getCitationArray())) {
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
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.ALTTITLES);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(AbstractTextType tt : titlStmt.getAltTitlArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.ALTTITLE, getText(tt));
            }
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readParTitles(TitlStmtType titlStmt) {
        if(hasContent(titlStmt.getParTitlArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.PARTITLES);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(SimpleTextType stt : titlStmt.getParTitlArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.PARTITLE, getText(stt));
                valueSet(row.getRight(), Fields.PARTITLELANG, getText(stt));
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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.AUTHORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.AUTHORS);
        request.setLanguage(language);

        // These are all organization authors
        for(AuthEntyType auth : rsp.getAuthEntyArray()) {
            if(!StringUtils.hasText(getText(auth))) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            // Set type to person
            valueSet(row.getRight(), Fields.AUTHORTYPE, "1");
            valueSet(row.getRight(), Fields.AUTHOR, getText(auth));
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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.PRODUCERS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.PRODUCERS);
        request.setLanguage(language);

        for(ProducerType producer : prodStmt.getProducerArray()) {
            if(!StringUtils.hasText(getText(producer))) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            // If producer has affiliation then we know that it has at least an agency and possibly a section
            // If not then we know that the actual producer is an organization
            ReferenceOption option = findOrganization(StringUtils.hasText(producer.getAffiliation()) ? producer.getAffiliation() : getText(producer), Fields.PRODUCERORGANISATION);
            String orgValue = (option != null) ? option.getValue() : null;
            if(StringUtils.hasText(orgValue)) {
                valueSet(row.getRight(), Fields.PRODUCERORGANISATION, orgValue);
            }
            if(StringUtils.hasText(producer.getAffiliation())) {
                String agencyValue = null;
                String[] splits = getText(producer).split("\\. ");

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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.AUTHORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.AUTHORS);
        request.setLanguage(language);

        // These are all organization authors
        for(AuthorizingAgencyType auth : stdyAuth.getAuthorizingAgencyArray()) {
            if(!StringUtils.hasText(getText(auth))) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            // Set type to organization
            valueSet(row.getRight(), Fields.AUTHORTYPE, "2");
            // If organization author has affiliation then we know that it has at least an agency and possibly a section
            // If not then we know that the actual author is an organization
            ReferenceOption option = findOrganization(StringUtils.hasText(auth.getAffiliation()) ? auth.getAffiliation() : getText(auth), Fields.AUTHORORGANISATION);
            String orgValue = (option != null) ? option.getValue() : null;
            if(StringUtils.hasText(orgValue)) {
                valueSet(row.getRight(), Fields.AUTHORORGANISATION, orgValue);
            }
            if(StringUtils.hasText(auth.getAffiliation())) {
                String agencyValue = null;
                String[] splits = getText(auth).split("\\. ");

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
            valueSet(Fields.ABSTRACT, getText(abstractType));
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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.KEYWORDS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.KEYWORDS);
        request.setLanguage(language);

        SelectionList keywordvocab_list = configuration.getSelectionList(Lists.KEYWORDVOCAB_LIST);

        ReferencePath keywordvocabPath = getReferencePath(Fields.KEYWORDVOCAB, null);
        request.setRoot(keywordvocabPath);
        List<ReferenceOption> keywordvocabOptions = references.collectReferenceOptions(request);

        for(KeywordType k : subject.getKeywordArray()) {
            if(!StringUtils.hasText(k.getVocab())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            ReferenceOption keywordvocab = findOption(keywordvocabOptions, k.getVocab());
            if(keywordvocab != null) {
                valueSet(row.getRight(), Fields.KEYWORDVOCAB, keywordvocab.getValue());
            }

            if(StringUtils.hasText(getText(k))) {
                if(keywordvocab == null || keywordvocab_list.getFreeText().contains(keywordvocab.getValue())) {
                    // We have 'no vocab'
                    valueSet(row.getRight(), Fields.KEYWORDNOVOCAB, getText(k));
                } else {
                    valueSet(row.getRight(), Fields.KEYWORD, getText(k));
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readStudyInfoSubjectTopics(SubjectType subject) {
        if(!hasContent(subject.getTopcClasArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.TOPICS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.TOPICS);
        request.setLanguage(language);

        ReferencePath topicvocabPath = getReferencePath(Fields.TOPICVOCAB, null);
        request.setRoot(topicvocabPath);
        List<ReferenceOption> topicvocabOptions = references.collectReferenceOptions(request);

        for(TopcClasType t : subject.getTopcClasArray()) {
            if(!StringUtils.hasText(t.getVocab())) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            ReferenceOption option = findOption(topicvocabOptions, t.getVocab());
            if(option == null) {
                continue;
            }
            valueSet(row.getRight(), Fields.TOPICVOCAB, option.getValue());

            if(StringUtils.hasText(getText(t))) {
                topicvocabPath = getReferencePath(Fields.TOPICVOCAB, option.getValue());
                ReferencePath topictopPath = getReferencePath(Fields.TOPICTOP, null);
                // We need to loop through all topictops to find the one containing the actual topic so that we can fill in both fields.
                topicvocabPath.setNext(topictopPath);
                topictopPath.setPrev(topicvocabPath);
                request.setRoot(topicvocabPath);

                List<ReferenceOption> topOptions = references.collectReferenceOptions(request);
                for(ReferenceOption topOption : topOptions) {
                    topictopPath = getReferencePath(Fields.TOPICTOP, topOption.getValue());
                    ReferencePath topicPath = getReferencePath(Fields.TOPIC, null);

                    topicvocabPath.setNext(topictopPath);
                    topictopPath.setPrev(topicvocabPath);
                    topictopPath.setNext(topicPath);
                    topicPath.setPrev(topictopPath);

                    List<ReferenceOption> options = references.collectReferenceOptions(request);
                    option = findOption(options, getText(t));
                    if(option != null) {
                        valueSet(row.getRight(), Fields.TOPICTOP, topOption.getValue());
                        valueSet(row.getRight(), Fields.TOPIC, option.getValue());
                        break;
                    }
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
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.GEOGCOVERS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(AbstractTextType tt : sumDscr.getGeogCoverArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.GEOGCOVER, getText(tt));
            }
        }

        result = readConceptualTextTypeArray(sumDscr.getAnlyUnitArray(), Fields.ANALYSIS, Fields.ANALYSISUNITVOCAB, Fields.ANALYSISUNIT, Fields.ANALYSISUNITOTHER, null);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return readStudyInfoSumDescUniverse(sumDscr);
    }

    private ReturnResult readStudyInfoSumDescTimePrd(SumDscrType sumDscr) {
        if(hasContent(sumDscr.getTimePrdArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.TIMEPERIODS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(TimePrdType t : sumDscr.getTimePrdArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.TIMEPERIODTEXT, getText(t));
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
        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerPair = getContainer(Fields.COLLTIME);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        Map<String, Change> change = containerPair.getRight().getRight();
        for(CollDateType coll : sumDscr.getCollDateArray()) {
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if (row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            if (StringUtils.hasText(getText(coll))) {
                valueSet(row.getRight(), Fields.COLLDATETEXT, getText(coll));
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
        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerPair = getContainer(Fields.COUNTRIES);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        Map<String, Change> change = containerPair.getRight().getRight();

        for(NationType nation : sumDscr.getNationArray()) {
            if(!StringUtils.hasText(getText(nation))) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            valueSet(row.getRight(), Fields.COUNTRY, getText(nation));
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
        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerPair = getContainer(Fields.UNIVERSE);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        Map<String, Change> change = containerPair.getRight().getRight();
        for(UniverseType universe : sumDscr.getUniverseArray()) {
            if(!StringUtils.hasText(getText(universe))) {
                continue;
            }
            Pair<StatusCode, DataRow> row = container.insertNewDataRow(language, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            valueSet(row.getRight(), Fields.UNIVERSE, getText(universe));
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
            valueSet(Fields.DATAPROSESSING, getText(method.getNotesArray(0)));
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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(containerKey);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(containerKey);
        request.setLanguage(language);
        ReferencePath vocabPath = getReferencePath(vocabKey, null);
        if(vocabPath == null) {
            // TODO: Inform if there was an actual problem
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        request.setRoot(vocabPath);
        List<ReferenceOption> vocabOptions = references.collectReferenceOptions(request);

        for(ConceptualTextType ctt : ctta) {
            if(!hasContent(ctt.getConceptArray())) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            if(cttKey != null && StringUtils.hasText(getText(ctt))) {
                valueSet(row.getRight(), cttKey, getText(ctt));
            }

            if(hasContent(ctt.getTxtArray()) && StringUtils.hasText(getText(ctt.getTxtArray(0)))) {
                valueSet(row.getRight(), txtKey, getText(ctt.getTxtArray(0)));
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

            if(StringUtils.hasText(getText(c))) {
                vocabPath = getReferencePath(vocabKey, option.getValue());
                ReferencePath selectionPath = getReferencePath(conceptKey, null);
                if(selectionPath == null) {
                    continue;
                }

                vocabPath.setNext(selectionPath);
                selectionPath.setPrev(vocabPath);
                request.setRoot(vocabPath);

                List<ReferenceOption> options = references.collectReferenceOptions(request);
                option = findOption(options, getText(c));
                if(option != null) {
                    valueSet(row.getRight(), conceptKey, option.getValue());
                }
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReferencePath getReferencePath(String key, String value) {
        Reference reference = null;
        Field field = configuration.getField(key);
        if(field.getType() == FieldType.REFERENCE) {
            reference = configuration.getReference(field.getReference());
        } else if(field.getType() == FieldType.SELECTION) {
            reference = configuration.getReference(configuration.getSelectionList(field.getSelectionList()).getReference());
        }

        return reference != null ? new ReferencePath(reference.copy(), value) : null;
    }

    private ReferenceOption findOrganization(String title, String orgFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = getReferencePath(orgFieldKey, null);
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReferenceOption findAgency(String title, String orgFieldKey, String orgValue, String agencyFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = getReferencePath(orgFieldKey, orgValue);
        if(root != null) {
            root.setNext(getReferencePath(agencyFieldKey, null));
            if(root.getNext() != null) {
                root.getNext().setPrev(root);
            }
        }
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReferenceOption findSection(String title, String orgFieldKey, String orgValue, String agencyFieldKey, String agencyValue, String sectionFieldKey) {
        ReferencePathRequest request = new ReferencePathRequest();
        request.setLanguage(language);

        ReferencePath root = getReferencePath(orgFieldKey, orgValue);
        if(root != null) {
            root.setNext(getReferencePath(agencyFieldKey, agencyValue));
            if(root.getNext() != null) {
                root.getNext().setPrev(root);
                root.getNext().setNext(getReferencePath(sectionFieldKey, null));
                if(root.getNext().getNext() != null) {
                    root.getNext().getNext().setPrev(root.getNext());
                }
            }

        }
        request.setRoot(root);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        return findOption(options, title);
    }

    private ReturnResult readMethodDataCollDataCollector(DataCollType dataColl) {
        if(!hasContent(dataColl.getDataCollectorArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.COLLECTORS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        ContainerDataField container = containerResult.getRight().getLeft();
        Map<String, Change> change = containerResult.getRight().getRight();

        // Let's construct the request and path elements needed
        ReferencePathRequest request = new ReferencePathRequest();
        request.setContainer(Fields.COLLECTORS);
        request.setLanguage(language);

        for(DataCollectorType collector : dataColl.getDataCollectorArray()) {
            if(!StringUtils.hasText(getText(collector))) {
                continue;
            }

            Pair<StatusCode, DataRow> row = container.insertNewDataRow(Language.DEFAULT, change);
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            // If collector has an abbreviation then assume organization since persons don't have abbreviation.
            // It is possible for organization to not have abbreviation but there's no way for us to detect this.
            if(!StringUtils.hasText(collector.getAbbr())) {
                valueSet(row.getRight(), Fields.COLLECTORTYPE, "1");
                valueSet(row.getRight(), Fields.COLLECTOR, getText(collector));
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
                ReferenceOption option = findOrganization(StringUtils.hasText(collector.getAffiliation()) ? collector.getAffiliation() : getText(collector), Fields.COLLECTORORGANISATION);
                String orgValue = (option != null) ? option.getValue() : null;
                if(StringUtils.hasText(orgValue)) {
                    valueSet(row.getRight(), Fields.COLLECTORORGANISATION, orgValue);
                }
                if(StringUtils.hasText(collector.getAffiliation())) {
                    String agencyValue = null;
                    String[] splits = getText(collector).split("\\. ");

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

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.DATASOURCES);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
        for(SimpleTextType stt : sources.getDataSrcArray()) {
            Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }
            valueSet(row.getRight(), Fields.DATASOURCE, getText(stt));
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readMethodDataCollWeight(DataCollType dataColl) {
        if(!hasContent(dataColl.getWeightArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SimpleTextType stt = dataColl.getWeightArray(0);

        // NOTICE:
        // Since we can only compare to the newest version of NO_WEIGHT text we can't be sure about the checkbox status.
        // As this is the case it makes more sense to consistently just transfer the text and set the checkbox to false.
        // The checkbox status should always be checked manually after DDI-import.
        valueSet(Fields.WEIGHTYESNO, "false");
        valueSet(Fields.WEIGHT, getText(stt));

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readMethodAnalyze(MethodType method) {
        if(method.getAnlyInfo() == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        AnlyInfoType anlyInfo = method.getAnlyInfo();

        if(hasContent(anlyInfo.getRespRateArray())) {
            valueSet(Fields.RESPRATE, getText(anlyInfo.getRespRateArray(0)), Language.DEFAULT);
        }

        if(hasContent(anlyInfo.getDataApprArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.APPRAISALS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(DataApprType appr : anlyInfo.getDataApprArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.APPRAISAL, getText(appr));
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

        if(hasContent(dataAccs.getNotesArray())) {
            valueSet(Fields.DATASETNOTES, getText(dataAccs.getNotesArray(0)));
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readDataAccessSetAvail(DataAccsType dataAccs) {
        if(!hasContent(dataAccs.getSetAvailArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        SetAvailType setAvail = dataAccs.getSetAvailArray(0);

        if(hasContent(setAvail.getCollSizeArray())) {
            valueSet(Fields.COLLSIZE, getText(setAvail.getCollSizeArray(0)));
        }

        if(hasContent(setAvail.getCompleteArray())) {
            valueSet(Fields.COMPLETE, getText(setAvail.getCompleteArray(0)));
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readOtherStudyMaterial(StdyDscrType stdyDscr) {
        if(!hasContent(stdyDscr.getOthrStdyMatArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        OthrStdyMatType othr = stdyDscr.getOthrStdyMatArray(0);

        if(hasContent(othr.getRelMatArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.RELATEDMATERIALS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(RelMatType relMat : othr.getRelMatArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.RELATEDMATERIAL, getText(relMat));
            }
        }

        if(hasContent(othr.getOthRefsArray())) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.PUBLICATIONCOMMENTS);
            if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                return containerResult.getLeft();
            }
            Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
            for(OthRefsType othRef : othr.getOthRefsArray()) {
                Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
                if(row.getLeft() != StatusCode.ROW_INSERT) {
                    continue;
                }
                valueSet(row.getRight(), Fields.PUBLICATIONCOMMENT, getText(othRef));
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
