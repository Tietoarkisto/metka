package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;

class DDIStudyDescription {
    private static String getReferenceTitle(ReferenceService references, Language language, RevisionData revision, String path) {
        ReferenceOption option = references.getCurrentFieldOption(language, revision, path);
        if(option != null) {
            return option.getTitle().getValue();
        } else return null;
    }

    static void readStudyDescription(RevisionData revision, Language language, CodeBookType codeBook, RevisionRepository revisions, ReferenceService references) {
        if(codeBook.getStdyDscrArray().length == 0) {
            return;
        }

        StdyDscrType stdyDscr = codeBook.getStdyDscrArray(0);

        readCitation(stdyDscr, revision, language, revisions, references);

        readStudyAuthorization(revision, stdyDscr, references, language);

        readStudyInfo(stdyDscr, revision, language, references);

        readMethod(stdyDscr, revision, language, references);

        readDataAccess(stdyDscr, revision, language);

        readOtherStudyMaterial(stdyDscr, revision, language, revisions);
    }

    private static void readCitation(StdyDscrType stdyDscr, RevisionData revisionData, Language language, RevisionRepository revisions, ReferenceService references) {
        if(stdyDscr.getCitationArray().length == 0) {
            return;
        }

        CitationType citation = stdyDscr.getCitationArray(0);

        readCitationTitle(revisionData, language, citation);

        readCitationRspStatement(revisionData, citation, references, language);

        readCitationProdStatement(revisionData, citation, language, references);

        // Add SerStmt
        readCitationSerStatement(citation, revisionData, language, revisions);

        // Add VerStmt
        readCitationVerStatement(citation, revisionData, language);
    }

    private static void readCitationTitle(RevisionData revisionData, Language language, CitationType citation) {
        // TODO: Reverse process
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

    private static void readAltTitles(RevisionData revisionData, Language language, TitlStmtType titlStmt) {
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

    private static void readParTitles(RevisionData revisionData, Language language, TitlStmtType titlStmt) {
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

    private static void readCitationRspStatement(RevisionData revision, CitationType citation, ReferenceService references, Language language) {
        // TODO: Reverse process
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

    private static void readCitationProdStatement(RevisionData revision, CitationType citation, Language language, ReferenceService references) {
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

    private static void readCitationSerStatement(CitationType citation, RevisionData revision, Language language, RevisionRepository revisions) {
        // Add series statement, excel row #70
        /*Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SERIESID));
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
        }*/
    }

    private static void readCitationVerStatement(CitationType citation, RevisionData revisionData, Language language) {
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

    private static void readStudyAuthorization(RevisionData revision, StdyDscrType stdyDscr, ReferenceService references, Language language) {
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

    private static void readStudyInfo(StdyDscrType stdyDscrType, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        /*StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

        readStudyInfoSubject(stdyInfo, revision, language, references);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField( ValueDataFieldCall.get(Fields.ABSTRACT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(stdyInfo.addNewAbstract(), valueFieldPair, language);
        }

        readStudyInfoSumDesc(stdyInfo, revision, language, references);*/
    }

    private static void readStudyInfoSubject(StdyInfoType stdyInfo, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        /*SubjectType subject= stdyInfo.addNewSubject();

        readStudyInfoSubjectKeywords(subject, revision, language, references);

        readStudyInfoSubjectTopics(subject, revision, language, references);*/
    }

    private static void readStudyInfoSubjectKeywords(SubjectType subject, RevisionData revision, Language language, ReferenceService references) {
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

    private static void readStudyInfoSubjectTopics(SubjectType subject, RevisionData revision, Language language, ReferenceService references) {
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

    private static void readStudyInfoSumDesc(StdyInfoType stdyInfo, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        /*SumDscrType sumDscrType = stdyInfo.addNewSumDscr();

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

        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAKIND));
        if(hasValue(fieldPair, Language.DEFAULT)) {
            SelectionList list = configuration.getRootSelectionList(configuration.getField(Fields.DATAKIND).getSelectionList());
            Option option = list.getOptionWithValue(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(option != null) {
                fillTextType(sumDscrType.addNewDataKind(), option.getTitleFor(Language.DEFAULT));
            }
        }*/
    }

    private static void readStudyInfoSumDescTimePrd(SumDscrType sumDscr, RevisionData revision, Language language) {
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

    private static void readStudyInfoSumDescCollDate(SumDscrType sumDscr, RevisionData revision, Language language) {
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

    private static void readStudyInfoSumDescNation(SumDscrType sumDscr, RevisionData revision, Language language, ReferenceService references) {
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

    private static void readStudyInfoSumDescAnlyUnit(SumDscrType sumDscr, RevisionData revision, Language language, ReferenceService references) {
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

    private static void readStudyInfoSumDescUniverse(Language language, SumDscrType sumDscrType, RevisionData revision) {
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

    private static void readMethod(StdyDscrType stdyDscr, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        /*MethodType method = stdyDscr.addNewMethod();

        readMethodDataColl(method, revision, language, references);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAPROSESSING));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(method.addNewNotes(), valueFieldPair, language);
        }

        readMethodAnalyze(method, revision, language);*/
    }

    private static void readMethodDataColl(MethodType method, RevisionData revision, Language language, ReferenceService references) {
        // TODO: Reverse process
        // Add data column
        /*DataCollType dataCollType = methodType.addNewDataColl();

        readMethodDataCollTimeMeth(dataCollType, revision, language, references);

        readMethodDataCollSampProc(dataCollType, revision, language, references);

        readMethodDataCollCollMode(dataCollType, revision, language, references);

        readMethodDataCollResInstru(dataCollType, revision, language, references);

        readMethodDataCollDataCollector(dataCollType, revision, language, references);

        readMethodDataCollSources(dataCollType, revision, language);

        readMethodDataCollWeight(dataCollType, revision, language);*/
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

    private static void readMethodDataCollSources(DataCollType dataColl, RevisionData revision, Language language) {
        // TODO: Reverse process
        /*List<ValueDataField> fields = gatherFields(revision, Fields.DATASOURCES, Fields.DATASOURCE, language, language);
        SourcesType sources = dataCollType.addNewSources();
        for(ValueDataField field : fields) {
            fillTextType(sources.addNewDataSrc(), field, language);
        }*/
    }

    private static void readMethodDataCollWeight(DataCollType dataCollType, RevisionData revision, Language language) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHTYESNO));
        if(hasValue(valueFieldPair, Language.DEFAULT) && valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsBoolean()) {
            fillTextType(dataCollType.addNewWeight(), WEIGHT_NO.get(language));
        } else {
            valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHT));
            if(hasValue(valueFieldPair, language)) {
                fillTextType(dataCollType.addNewWeight(), valueFieldPair, language);
            }
        }*/
    }

    private static void readMethodAnalyze(MethodType methodType, RevisionData revision, Language language) {
        // TODO: Reverse process
        /*AnlyInfoType anlyInfoType = methodType.addNewAnlyInfo();

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
        }*/
    }

    private static void readDataAccess(StdyDscrType stdyDscrType, RevisionData revision, Language language) {
        // TODO: Reverse process
        /*DataAccsType dataAccs = stdyDscrType.addNewDataAccs();

        readDataAccessSetAvail(dataAccs, revision, language);

        readDataAccessUseStatement(dataAccs, revision, language);

        // Add notes
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATASETNOTES));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(dataAccs.addNewNotes(), valueFieldPair, language);
        }*/
    }

    private static void readDataAccessSetAvail(DataAccsType dataAccs, RevisionData revision, Language language) {
        // TODO: Reverse process
        // Add set availability
        /*SetAvailType setAvail = dataAccs.addNewSetAvail();

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
        }*/
    }

    private static void readDataAccessUseStatement(DataAccsType dataAccs, RevisionData revision, Language language) {
        // TODO: Reverse process
        // Add use statement
        /*UseStmtType useStmt = dataAccs.addNewUseStmt();

        // Add special permissions
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SPECIALTERMSOFUSE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(useStmt.addNewSpecPerm(), valueFieldPair, language);
        }

        // Add restrictions, excel row #164
        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TERMSOFUSE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(useStmt.addNewRestrctn(), RESTRICTION.get(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)).get(language));
        }*/
    }

    private static void readOtherStudyMaterial(StdyDscrType stdyDscr, RevisionData revision, Language language, RevisionRepository revisions) {
        // TODO: Reverse process
        /*OthrStdyMatType othr = stdyDscrType.addNewOthrStdyMat();

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
