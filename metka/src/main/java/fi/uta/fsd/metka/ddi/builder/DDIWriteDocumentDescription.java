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
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.*;

class DDIWriteDocumentDescription extends DDIWriteSectionBase {

    DDIWriteDocumentDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        // Add document description
        DocDscrType docDscrType = codeBook.addNewDocDscr();

        // Add citation information
        addCitationType(docDscrType);

        // Collect references by hand
	    Set<String> usedVocabs = new HashSet<>();
        // anlyUnit
        usedVocabs.add(configuration.getReference("analysisunitvocab_ref").getTarget());
        // collMode
        usedVocabs.add(configuration.getReference("collmodevocab_ref").getTarget());
        // resInstru
        usedVocabs.add(configuration.getReference("instrumentvocab_ref").getTarget());
        // sampProc
        usedVocabs.add(configuration.getReference("sampprocvocab_ref").getTarget());
        // timeMethod
        usedVocabs.add(configuration.getReference("timemethodvocab_ref").getTarget());
        // topClass
        usedVocabs.add(configuration.getReference("topicvocab_ref").getTarget());

        for(String target : usedVocabs) {
            Reference usedVocabRef = new Reference("temp", ReferenceType.JSON, target, "codeListID", null);
            ReferencePath usedVocabRoot = new ReferencePath(usedVocabRef, null);
            ReferencePathRequest request = new ReferencePathRequest();
            request.setRoot(usedVocabRoot);
            request.setLanguage(Language.DEFAULT);
            request.setKey("temp");
            request.setContainer(null);
            List<ReferenceOption> vocabIds = references.collectReferenceOptions(request);
            for(ReferenceOption vocabId : vocabIds) {
                ControlledVocabUsedType type = docDscrType.addNewControlledVocabUsed();
                fillTextType(type.addNewCodeListID(), vocabId.getValue());
                Reference singleValueRef = new Reference("single", ReferenceType.JSON, target, "codeListID", null);

                ReferencePath singleValuePath = new ReferencePath(singleValueRef, vocabId.getValue());
                request.setLanguage(language);
                request.setRoot(singleValuePath);

                singleValueRef.setTitlePath("codeListName");
                List<ReferenceOption> singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListName(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListAgencyName");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListAgencyName(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListVersionID");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListVersionID(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListURN");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListURN(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("codeListSchemeURN");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    fillTextType(type.addNewCodeListSchemeURN(), singleValue.get(0).getTitle().getValue());
                }

                singleValueRef.setTitlePath("selector");
                singleValue = references.collectReferenceOptions(request);
                if(singleValue.size() > 0) {
                    UsageType usage = type.addNewUsage();
                    usage.setSelector(singleValue.get(0).getTitle().getValue());
                }
            }
        }

        addNotes(docDscrType);
    }

    private void addNotes(DocDscrType docDscrType) {
        // Back to doc description
        // Add notes, repeatable, excel row #40 - #41
        for(Language l : Language.values()) {
            NotesType notesType = fillTextType(docDscrType.addNewNotes(), getDDIText(l, "NOTES"));
            notesType.setXmlLang(getXmlLang(l));
        }
    }

    private void addCitationType(DocDscrType docDscrType) {
        // Add citation
        CitationType citationType = docDscrType.addNewCitation();
        // Add title statement (?)
        TitlStmtType titlStmtType = citationType.addNewTitlStmt();

        // Add titles

            // Add content of title field
        addTitleField(titlStmtType);

        // Add ID no
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        String agency = null;
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            agency = addIDNo(titlStmtType, valueFieldPair.getRight(), configuration.getRootSelectionList(Lists.ID_PREFIX_LIST));
        }

        // Add Producer information
        addProducerInfo(citationType, agency);

        // Add container version
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            addContainerVersion(citationType, containerPair.getRight());
        }

        valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            BiblCitType biblCitType = fillTextType(citationType.addNewBiblCit(), valueFieldPair.getRight().getActualValueFor(language)+getDDIText(language, "BIBL_CIT_POST"));
            biblCitType.setFormat(getDDIText(language, "BIBL_CIT_FORMAT"));
        }

        // Add holdings
        addHoldingsInfo(citationType);
    }

    private void addHoldingsInfo(CitationType citationType) {
        HoldingsType holdingsType = fillTextType(citationType.addNewHoldings(), getDDIText(language, "HOLDINGS"));
        holdingsType.setLocation(getDDIText(language, "HOLDINGS_LOCATION"));

        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            holdingsType.setURI(getDDIText(language, "HOLDINGS_URI_BASE")+valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private void addContainerVersion(CitationType citationType, ContainerDataField container) {
        Pair<StatusCode, ValueDataField> valueFieldPair;List<DataRow> rows = container.getRowsFor(language);
        DataRow row = rows.get(rows.size()-1);

        valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
        if(hasValue(valueFieldPair, language)) {
            VerStmtType verStmt = citationType.addNewVerStmt();
            VersionType ver = fillTextType(verStmt.addNewVersion(), valueFieldPair, language);
            valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSIONDATE));
            if(hasValue(valueFieldPair, language)) {
                LocalDateTime versiondate = new LocalDateTime(valueFieldPair.getRight().getActualValueFor(language));
                LocalDate localDate = versiondate.toLocalDate();
                ver.setDate(DATE_TIME_FORMATTER.print(localDate));
            }
        }
    }

    private void addProducerInfo(CitationType citationType, String agency) {
        // Add producer statement
        ProdStmtType prodStmtType = citationType.addNewProdStmt();

        // Add producer, repeatable
        ProducerType producerType = fillTextType(prodStmtType.addNewProducer(), getDDIText(language, "PRODUCER"));

        // Set abbreviation
        if(agency != null) producerType.setAbbr(agency);

        // Add copyright
        fillTextType(prodStmtType.addNewCopyright(), getDDIText(language, "COPYRIGHT"));

        // Add production date
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            ContainerDataField container = containerPair.getRight();
            for (DataRow dataRow : container.getRowsFor(language)) {
                if(dataRow.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> versionPair = dataRow.dataField( ValueDataFieldCall.get(Fields.DESCVERSION));
                Pair<StatusCode, ValueDataField> versionDatePair = dataRow.dataField( ValueDataFieldCall.get(Fields.VERSIONDATE));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionDatePair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().valueForEquals(language, "1.0")) {
                    LocalDate localDate = LocalDate.parse(versionDatePair.getRight().getActualValueFor(language));
                    SimpleTextAndDateType stadt = prodStmtType.addNewProdDate();
                    stadt.setDate(DATE_TIME_FORMATTER.print(localDate));
                }
            }
        }

        // Add production place (?)
        SimpleTextType stt = fillTextType(prodStmtType.addNewProdPlac(), getDDIText(language, "PRODPLAC"));
        fillTextType(stt.addNewAddress(), getDDIText(language, "PRODPLAC_ADDRESS"));
    }

    private String addIDNo(TitlStmtType titlStmtType, ValueDataField idField, SelectionList prefixList) {
        if(prefixList != null) {
            for(Option option : prefixList.getOptions()) {
                if(idField.getActualValueFor(Language.DEFAULT).indexOf(option.getValue()) == 0) {
                    // Add id number
                    IDNoType idNoType = fillTextType(titlStmtType.addNewIDNo(), idField.getActualValueFor(Language.DEFAULT).substring(option.getValue().length()));
                    idNoType.setAgency(option.getValue());
                    return option.getValue();
                }
            }
        }
        return null;
    }

    private void addTitleField(TitlStmtType titlStmtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(titlStmtType.addNewTitl(), getDDIText(language, "DDI_TITLE_PREFIX")+valueFieldPair.getRight().getActualValueFor(language));
        }

        for(Language altLang : Language.values()) {
            if(altLang == language) {
                continue;
            }
            if(hasValue(valueFieldPair, altLang)) {
                SimpleTextType stt = fillTextType(titlStmtType.addNewParTitl(), getDDIText(language, "DDI_TITLE_PREFIX")+valueFieldPair.getRight().getActualValueFor(altLang));
                stt.setXmlLang(getXmlLang(altLang));
            }
        }
    }
}
