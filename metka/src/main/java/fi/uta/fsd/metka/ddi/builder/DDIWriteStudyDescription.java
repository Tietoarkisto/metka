/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
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

class DDIWriteStudyDescription extends DDIWriteSectionBase {

    DDIWriteStudyDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        // Add study description to codebook
        StdyDscrType stdyDscrType = codeBook.addNewStdyDscr();

        addCitationInfo(stdyDscrType);

        addStudyAuthorization(stdyDscrType);

        addStudyInfo(stdyDscrType);

        addMethod(stdyDscrType);

        addDataAccess(stdyDscrType);

        addOtherStudyMaterial(stdyDscrType);
    }

    private void addCitationInfo(StdyDscrType stdyDscrType) {
        // Add citation
        CitationType citationType = stdyDscrType.addNewCitation();

        addCitationTitle(citationType);

        addCitationRspStatement(citationType);

        addCitationProdStatement(citationType);

        addCitationDistStatement(citationType);

        // Add SerStmt
        addCitationSerStatement(citationType);

        // Add VerStmt
        addCitationVerStatement(citationType);

        // Add biblcit
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.BIBLCIT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(citationType.addNewBiblCit(), valueFieldPair, language);
        }
    }

    private void addCitationProdStatement(CitationType citationType) {
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.PRODUCERS));
        String path = "producers.";
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                String rowRoot = path+row.getRowId()+".";

                String organisation = getReferenceTitle(rowRoot + Fields.PRODUCERORGANISATION);
                String agency = getReferenceTitle(rowRoot + Fields.PRODUCERAGENCY);
                String section = getReferenceTitle(rowRoot + Fields.PRODUCERSECTION);
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

                String abbr = getReferenceTitle(rowRoot + Fields.PRODUCERSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.PRODUCERAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.PRODUCERORGANISATIONABBR);

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
        fillTextType(prodStmtType.addNewCopyright(), getDDIText(language, "COPYRIGHT_STDY"));
    }

    private void addCitationDistStatement(CitationType citationType) {
        DistStmtType distStmtType = citationType.addNewDistStmt();
        DistrbtrType d = fillTextType(distStmtType.addNewDistrbtr(), getDDIText(language, "DISTRIBUTR"));
        d.setAbbr(getDDIText(language, "DISTRIBUTR_ABB"));
        d.setURI(getDDIText(language, "DISTRIBUTR_URI"));
    }

    private void addCitationRspStatement(CitationType citationType) {
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

                String organisation = getReferenceTitle(rowRoot + Fields.AUTHORORGANISATION);
                String agency = getReferenceTitle(rowRoot + Fields.AUTHORAGENCY);
                String section = getReferenceTitle(rowRoot + Fields.AUTHORSECTION);

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

                    String organisation = getReferenceTitle(rowRoot + Fields.AUTHORORGANISATION);
                    String agency = getReferenceTitle(rowRoot + Fields.AUTHORAGENCY);
                    String section = getReferenceTitle(rowRoot + Fields.AUTHORSECTION);

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
                    String organisation = getReferenceTitle(rowRoot + Fields.AUTHORORGANISATION);
                    String agency = getReferenceTitle(rowRoot + Fields.AUTHORAGENCY);
                    String section = getReferenceTitle(rowRoot + Fields.AUTHORSECTION);
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

    private void addCitationSerStatement(CitationType citationType) {
        // Add series statement, excel row #70
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.SERIES));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            if(revisionPair.getLeft() == ReturnResult.REVISION_FOUND) {
                RevisionData series = revisionPair.getRight();
                valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESABBR));
                String seriesAbbr = null;
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    seriesAbbr = valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                }
                if(seriesAbbr != null) {
                    SerStmtType serStmtType = citationType.addNewSerStmt();
                    serStmtType.setURI(getDDIText(language, "SERIES_URI_PREFIX")+seriesAbbr);
                    valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESNAME));

                    SerNameType serName;
                    if(hasValue(valueFieldPair, language)) {
                        serName = fillTextType(serStmtType.addNewSerName(), valueFieldPair, language);
                    } else {
                        serName = fillTextType(serStmtType.addNewSerName(), "");
                    }
                    serName.setAbbr(seriesAbbr);
                    valueFieldPair = series.dataField(ValueDataFieldCall.get(Fields.SERIESDESC));
                    if(hasValue(valueFieldPair, language)) {
                        fillTextType(serStmtType.addNewSerInfo(), valueFieldPair, language);
                    }
                }
            } else {
                Logger.error(getClass(), "Did not find referenced SERIES with id: "+valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
        }
    }

    private void addCitationVerStatement(CitationType citationType) {
        VerStmtType verStmtType = citationType.addNewVerStmt();

        // Add version, repeatable
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.DATAVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.DATAVERSION));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    fillTextAndDateType(verStmtType.addNewVersion(), valueFieldPair, Language.DEFAULT);
                }
            }
        }
    }

    private void addCitationTitle(CitationType citationType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();
        if(hasValue(valueFieldPair, language)) {
            // Add title of requested language
            fillTextType(titlStmtType.addNewTitl(), valueFieldPair, language);
        }

        addAltTitles(titlStmtType);

        addParTitles(titlStmtType);

        String agency = "";
        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
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

    private void addParTitles(TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
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
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.PARTITLES));
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

    private void addAltTitles(TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair;// Add alternative titles
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.ALTTITLES));
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

    private void addStudyAuthorization(StdyDscrType stdyDscrType) {
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

                String organisation = getReferenceTitle(rowRoot + Fields.AUTHORORGANISATION);
                String agency = getReferenceTitle(rowRoot + Fields.AUTHORAGENCY);
                String section = getReferenceTitle(rowRoot + Fields.AUTHORSECTION);
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

                String abbr = getReferenceTitle(rowRoot + Fields.PRODUCERSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.PRODUCERAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.PRODUCERORGANISATIONABBR);

                d.setAbbr(abbr);
                if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                    if(StringUtils.hasText(organisation)) {
                        d.setAffiliation(organisation);
                    }
                }
            }
        }
    }

    private void addStudyInfo(StdyDscrType stdyDscrType) {
        StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

        addStudyInfoSubject(stdyInfo);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField( ValueDataFieldCall.get(Fields.ABSTRACT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(stdyInfo.addNewAbstract(), valueFieldPair, language);
        }

        addStudyInfoSumDesc(stdyInfo);
    }

    private void addStudyInfoSubject(StdyInfoType stdyInfo) {
        SubjectType subject= stdyInfo.addNewSubject();

        // Add subject
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.KEYWORDS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSubjectKeywords(subject, containerPair.getRight());
        }

        // Add topic
        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TOPICS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSubjectTopics(subject, containerPair.getRight());
        }
    }

    private void addStudyInfoSubjectKeywords(SubjectType subject, ContainerDataField container) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "keywords.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String keyword = null;
            String keywordvocaburi = null;

            ReferenceOption keywordvocab = references.getCurrentFieldOption(language, revision, configuration, rowRoot + Fields.KEYWORDVOCAB, true);
            keywordvocaburi = getReferenceTitle(rowRoot + Fields.KEYWORDVOCABURI);
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

    private void addStudyInfoSubjectTopics(SubjectType subject, ContainerDataField container) {
        // Let's hardcode the path since we know exactly what we are looking for.
        String pathRoot = "topics.";
        for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if(row.getRemoved()) {
                continue;
            }
            String rowRoot = pathRoot + row.getRowId() + ".";

            String topic = null;
            String topictop = null;
            String topicvocab = null;
            String topicvocaburi = null;

            topicvocab = getReferenceTitle(rowRoot + Fields.TOPICVOCAB);
            if(!StringUtils.hasText(topicvocab)) {
                continue;
            }

            topictop = getReferenceTitle(rowRoot + Fields.TOPICTOP);
            if(!StringUtils.hasText(topictop)) {
                continue;
            }

            topic = getReferenceTitle(rowRoot + Fields.TOPIC);
            if(!StringUtils.hasText(topic)) {
                continue;
            }


            topicvocaburi = getReferenceTitle(rowRoot + Fields.TOPICVOCABURI);

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

    private void addStudyInfoSumDesc(StdyInfoType stdyInfo) {
        SumDscrType sumDscrType = stdyInfo.addNewSumDscr();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TIMEPERIODS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescTimePrd(sumDscrType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLTIME));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescCollDate(sumDscrType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COUNTRIES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescNation(sumDscrType, containerPair.getRight());
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
            addStudyInfoSumDescAnlyUnit(sumDscrType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.UNIVERSES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addStudyInfoSumDescUniverse(sumDscrType, containerPair);
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

    private void addStudyInfoSumDescAnlyUnit(SumDscrType sumDscr, ContainerDataField container) {
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

            analysisunitvocab = getReferenceTitle(rowRoot + Fields.ANALYSISUNITVOCAB);
            if(!StringUtils.hasText(analysisunitvocab)) {
                continue;
            }

            analysisunit = getReferenceTitle(rowRoot + Fields.ANALYSISUNIT);
            if(!StringUtils.hasText(analysisunit)) {
                continue;
            }

            analysisunitvocaburi = getReferenceTitle(rowRoot + Fields.ANALYSISUNITVOCABURI);

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

    private void addStudyInfoSumDescUniverse(SumDscrType sumDscrType, Pair<StatusCode, ContainerDataField> containerPair) {
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

    private void addStudyInfoSumDescTimePrd(SumDscrType sumDscr, ContainerDataField container) {
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

    private void addStudyInfoSumDescCollDate(SumDscrType sumDscr, ContainerDataField container) {
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

    private void addStudyInfoSumDescNation(SumDscrType sumDscr, ContainerDataField container) {
        String path = "countries.";
        for (DataRow row : container.getRowsFor(Language.DEFAULT)) {
            if (row.getRemoved()) {
                continue;
            }
            // TODO: Country is not a reference anymore
            String rowPath = path + row.getRowId() + ".";
            String country = getReferenceTitle(rowPath+Fields.COUNTRY);
            if(!StringUtils.hasText(country)) {
                continue;
            }
            NationType n = fillTextType(sumDscr.addNewNation(), country);
            String abbr = getReferenceTitle(rowPath+Fields.COUNTRYABBR);
            if(abbr != null) {
                n.setAbbr(abbr);
            }
        }
    }

    private void addMethod(StdyDscrType stdyDscrType) {
        MethodType methodType = stdyDscrType.addNewMethod();

        addMethodDataColl(methodType);

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATAPROSESSING));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(methodType.addNewNotes(), valueFieldPair, language);
        }

        addMethodAnalyzeInfo(methodType);
    }

    private void addMethodDataColl(MethodType methodType) {
        // Add data column
        DataCollType dataCollType = methodType.addNewDataColl();

        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.TIMEMETHODS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollTimeMeth(dataCollType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.SAMPPROCS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollSampProc(dataCollType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLMODES));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollCollMode(dataCollType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.INSTRUMENTS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollResInstru(dataCollType, containerPair.getRight());
        }

        containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.COLLECTORS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
            addMethodDataCollDataCollector(dataCollType, containerPair.getRight());
        }

        addMethodDataCollSources(dataCollType);

        addMethodDataCollWeight(dataCollType);
    }

    private void addMethodDataCollTimeMeth(DataCollType dataColl, ContainerDataField container) {
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

            timemethodvocab = getReferenceTitle(rowRoot + Fields.TIMEMETHODVOCAB);
            if(!StringUtils.hasText(timemethodvocab)) {
                continue;
            }

            timemethod = getReferenceTitle(rowRoot + Fields.TIMEMETHOD);
            if(!StringUtils.hasText(timemethod)) {
                continue;
            }

            timemethodvocaburi = getReferenceTitle(rowRoot + Fields.TIMEMETHODVOCABURI);

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

    private void addMethodDataCollDataCollector(DataCollType dataColl, ContainerDataField container) {
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

                String organisation = getReferenceTitle(rowRoot + Fields.COLLECTORORGANISATION);
                String agency = getReferenceTitle(rowRoot + Fields.COLLECTORAGENCY);
                String section = getReferenceTitle(rowRoot + Fields.COLLECTORSECTION);

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
                String organisation = getReferenceTitle(rowRoot + Fields.COLLECTORORGANISATION);
                String agency = getReferenceTitle(rowRoot + Fields.COLLECTORAGENCY);
                String section = getReferenceTitle(rowRoot + Fields.COLLECTORSECTION);
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

                String abbr = getReferenceTitle(rowRoot + Fields.COLLECTORSECTIONABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.COLLECTORAGENCYABBR);
                abbr = (StringUtils.hasText(abbr)) ? abbr : getReferenceTitle(rowRoot + Fields.COLLECTORORGANISATIONABBR);

                d.setAbbr(abbr);
                if(StringUtils.hasText(agency) || StringUtils.hasText(section)) {
                    if(StringUtils.hasText(organisation)) {
                        d.setAffiliation(organisation);
                    }
                }
            }
        }
    }

    private void addMethodDataCollSampProc(DataCollType dataColl, ContainerDataField container) {
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

            sampprocvocab = getReferenceTitle(rowRoot + Fields.SAMPPROCVOCAB);
            if(!StringUtils.hasText(sampprocvocab)) {
                continue;
            }

            sampproc = getReferenceTitle(rowRoot + Fields.SAMPPROC);
            if(!StringUtils.hasText(sampproc)) {
                continue;
            }

            sampprocvocaburi = getReferenceTitle(rowRoot + Fields.SAMPPROCVOCABURI);

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

    private void addMethodDataCollCollMode(DataCollType dataColl, ContainerDataField container) {
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

            collmodevocab = getReferenceTitle(rowRoot + Fields.COLLMODEVOCAB);
            if(!StringUtils.hasText(collmodevocab)) {
                continue;
            }

            collmode = getReferenceTitle(rowRoot + Fields.COLLMODE);
            if(!StringUtils.hasText(collmode)) {
                continue;
            }

            collmodevocaburi = getReferenceTitle(rowRoot + Fields.COLLMODEVOCABURI);

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

    private void addMethodDataCollResInstru(DataCollType dataColl, ContainerDataField container) {
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

            instrumentvocab = getReferenceTitle(rowRoot + Fields.INSTRUMENTVOCAB);
            if(!StringUtils.hasText(instrumentvocab)) {
                continue;
            }

            instrument = getReferenceTitle(rowRoot + Fields.INSTRUMENT);
            if(!StringUtils.hasText(instrument)) {
                continue;
            }

            instrumentvocaburi = getReferenceTitle(rowRoot + Fields.INSTRUMENTVOCABURI);

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

    private void addMethodDataCollSources(DataCollType dataCollType) {
        List<ValueDataField> fields = gatherFields(revision, Fields.DATASOURCES, Fields.DATASOURCE, language, language);
        SourcesType sources = dataCollType.addNewSources();
        for(ValueDataField field : fields) {
            fillTextType(sources.addNewDataSrc(), field, language);
        }
    }

    private void addMethodDataCollWeight(DataCollType dataCollType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHTYESNO));
        if(hasValue(valueFieldPair, Language.DEFAULT) && valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsBoolean()) {
            fillTextType(dataCollType.addNewWeight(), getDDIText(language, "WEIGHT_NO"));
        } else {
            valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.WEIGHT));
            if(hasValue(valueFieldPair, language)) {
                fillTextType(dataCollType.addNewWeight(), valueFieldPair, language);
            }
        }
    }

    private void addMethodAnalyzeInfo(MethodType methodType) {
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

    private void addDataAccess(StdyDscrType stdyDscrType) {
        DataAccsType dataAccs = stdyDscrType.addNewDataAccs();

        addDataAccessSetAvail(dataAccs);

        addDataAccessUseStatement(dataAccs);

        // Add notes
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.DATASETNOTES));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(dataAccs.addNewNotes(), valueFieldPair, language);
        }
    }

    private void addDataAccessSetAvail(DataAccsType dataAccs) {
        // Add set availability
        SetAvailType setAvail = dataAccs.addNewSetAvail();

        // Add access place
        AccsPlacType acc = fillTextType(setAvail.addNewAccsPlac(), getDDIText(language, "ACCS_PLAC"));
        acc.setURI(getDDIText(language, "ACCS_PLAC_URI"));

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

    private void addDataAccessUseStatement(DataAccsType dataAccs) {
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
            fillTextType(useStmt.addNewRestrctn(), getDDIRestriction(language, valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)));
        }

        // Add citation required
        fillTextType(useStmt.addNewCitReq(), getDDIText(language, "CIT_REQ"));

        // Add deposition required
        fillTextType(useStmt.addNewDeposReq(), getDDIText(language, "DEPOS_REQ"));

        // Add disclaimer required
        fillTextType(useStmt.addNewDisclaimer(), getDDIText(language, "DISCLAIMER"));
    }

    private void addOtherStudyMaterial(StdyDscrType stdyDscrType) {
        OthrStdyMatType othr = stdyDscrType.addNewOthrStdyMat();

        // Add related materials
        List<ValueDataField> fields = gatherFields(revision, Fields.RELATEDMATERIALS, Fields.RELATEDMATERIAL, language, language);
        for(ValueDataField field : fields) {
            fillTextType(othr.addNewRelMat(), field, language);
        }

        // Add related studies (studyID + study name)
        Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.RELATEDSTUDIES));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
            for(ReferenceRow row : referenceContainerPair.getRight().getReferences()) {
                if(row.getRemoved() || !row.hasValue()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(row.getReference().getValue());
                if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(getClass(), "Could not find referenced study with ID: "+row.getReference().getValue());
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

        // Add related publications (publications -> publicationrelpubl)
        referenceContainerPair = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.PUBLICATIONS));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
            for(ReferenceRow row : referenceContainerPair.getRight().getReferences()) {
                if (row.getRemoved() || !row.hasValue()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(row.getReference().getValue());
                if (revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(getClass(), "Could not find referenced publication with ID: " + row.getReference().getValue());
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
