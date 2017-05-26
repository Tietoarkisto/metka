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

package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.ddi.DDIReaderService;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CodebookReader {
    private final RevisionRepository revisions;
    private final RevisionEditRepository edit;
    private final ReferenceService references;
    private final CodeBookDocument document;
    private final RevisionData revision;
    private final Configuration configuration;
    private final StudyVariableSearch variableSearch;

    public CodebookReader(RevisionRepository revisions, RevisionEditRepository edit, ReferenceService references
            , CodeBookDocument document, RevisionData revision, Configuration configuration, StudyVariableSearch variableSearch) {
        this.revisions = revisions;
        this.edit = edit;
        this.references = references;
        this.document = document;
        this.revision = revision;
        this.configuration = configuration;
        this.variableSearch = variableSearch;
    }

    public ReturnResult read() {
        if(revision.getState() != RevisionState.DRAFT || !AuthenticationUtil.isHandler(revision)) {
            Logger.warning(getClass(), "User " + AuthenticationUtil.getUserName() + " tried to import DDI for study " + revision.getKey().toString() + " that either was not a draft or is handled by someone else.");
            return ReturnResult.OPERATION_FAIL;
        }
        CodeBookType codeBook = document.getCodeBook();
        Language docLang = codeBook.getXmlLang() == null ? Language.DEFAULT : Language.fromValue(codeBook.getXmlLang().toLowerCase());

        DateTimeUserPair info = DateTimeUserPair.build();

        DDIReadSectionBase section;
        ReturnResult result;

        Pair<StatusCode, ValueDataField> valuePair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(valuePair.getLeft() != StatusCode.FIELD_FOUND) {
            return ReturnResult.OPERATION_FAIL;
        }

        section = new DDIReadDataDescription(revision, docLang, codeBook, info, configuration, revisions, edit, variableSearch, valuePair.getRight().getActualValueFor(Language.DEFAULT));
        result = section.read();

        if(result != ReturnResult.OPERATION_SUCCESSFUL) {
            return result;
        }

        boolean importDescription = docLang == Language.DEFAULT && isDescriptionTabClear();

        if(importDescription) {
            // If language is DEFAULT and description tab is clear then import values
            section = new DDIReadStudyDescription(revision, docLang, codeBook, info, configuration, references);
            result = section.read();

            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return result;
            }

            section = new DDIReadOtherMaterialDescription(revision, docLang, codeBook, info, configuration);
            result = section.read();

            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return result;
            }
        }

        // Form biblcit
        StudyFactory fac = new StudyFactory();
        result = fac.formUrnAndBiblCit(revision, info, references, configuration, new MutablePair<Boolean, Boolean>());

        if(result != ReturnResult.OPERATION_SUCCESSFUL) {
            return result;
        }
        result = revisions.updateRevisionData(revision);
        return result == ReturnResult.REVISION_UPDATE_SUCCESSFUL ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    private boolean isDescriptionTabClear() {
        // Check all fields on description tab so that they don't contain input (disregards biblcit since it's formed automatically.
        if(!checkIsContainerClear(Fields.ALTTITLES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.PARTITLES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.AUTHORS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.OTHERAUTHORS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.PRODUCERS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.KEYWORDS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.TOPICS)) {
            return false;
        }
        if(!checkIsValueClear(Fields.ABSTRACT)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.TIMEPERIODS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.COUNTRIES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.UNIVERSES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.GEOGCOVERS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.COLLTIME)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.COLLECTORS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.ANALYSIS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.TIMEMETHODS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.COLLMODES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.INSTRUMENTS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.SAMPPROCS)) {
            return false;
        }
        if(!checkIsValueClear(Fields.RESPRATE)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.DATASOURCES)) {
            return false;
        }
        if(!checkIsValueClear(Fields.WEIGHTYESNO)) {
            return false;
        }
        if(!checkIsValueClear(Fields.WEIGHT)) {
            return false;
        }
        if(!checkIsValueClear(Fields.DATAPROSESSING)) {
            return false;
        }
        if(!checkIsValueClear(Fields.COLLSIZE)) {
            return false;
        }
        if(!checkIsValueClear(Fields.COMPLETE)) {
            return false;
        }
        if(!checkIsValueClear(Fields.DATASETNOTES)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.APPRAISALS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.RELATEDMATERIALS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.OTHERMATERIALS)) {
            return false;
        }
        if(!checkIsContainerClear(Fields.PUBLICATIONCOMMENTS)) {
            return false;
        }
        return true;
    }

    private boolean checkIsContainerClear(String key) {
        Pair<StatusCode, ContainerDataField> container = revision.dataField(ContainerDataFieldCall.get(key));
        return container.getLeft() != StatusCode.FIELD_FOUND || !container.getRight().hasRows();
    }

    private boolean checkIsValueClear(String key) {
        Pair<StatusCode, ValueDataField> value = revision.dataField(ValueDataFieldCall.get(key));
        if(value.getLeft() != StatusCode.FIELD_FOUND) {
            return true;
        }
        for(Language l : Language.values()) {
            if(value.getRight().hasValueFor(l)) {
                return false;
            }
        }
        return true;
    }
}
