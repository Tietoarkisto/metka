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
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

class DDIWriteFileDescription extends DDIWriteSectionBase {
    DDIWriteFileDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        // TODO: This needs to be changed to STUDYVARIABLES container handling
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(!hasValue(valueFieldPair, Language.DEFAULT)) {
            return;
        }

        // Get variables data since it contains most of the information needed for this. Some additional data is also needed from the actual file but very little.
        Pair<ReturnResult, RevisionData> revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Couldn't find expected variables revision with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
            return;
        }
        RevisionData variables = revisionDataPair.getRight();

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(!hasValue(valueFieldPair, Language.DEFAULT)) {
            Logger.error(getClass(), "Variables revision "+variables.toString()+" did not contain file reference although it should be present.");
            return;
        }
        revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Couldn't find study attachment with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger() +
                    " even though it's referenced from variables data " + variables.toString());
            return;
        }
        RevisionData attachment = revisionDataPair.getRight();

        // Get FileDscrType
        FileDscrType fileDscrType = codeBook.addNewFileDscr();
        setFileDescription(attachment, fileDscrType);

        // Get FileTxtType
        FileTxtType fileTxtType = fileDscrType.addNewFileTxt();

        // Sets file name and file id
        setFileNameAndID(variables, attachment, fileTxtType);

        // Set software information
        setSoftware(variables, fileTxtType);

        // Set dimension information
        setDimensions(variables, fileTxtType);
    }

    private void setDimensions(RevisionData variables, FileTxtType fileTxtType) {
        // Add dimensions
        DimensnsType dimensnsType = fileTxtType.addNewDimensns();
        Pair<StatusCode, ValueDataField> valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.CASEQUANTITY));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Add case quantity
            fillTextType(dimensnsType.addNewCaseQnty(), valueFieldPair, Language.DEFAULT);
        }

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARQUANTITY));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Add case quantity
            fillTextType(dimensnsType.addNewVarQnty(), valueFieldPair, Language.DEFAULT);
        }
    }

    private void setSoftware(RevisionData variables, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.SOFTWARE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            SoftwareType softwareType = fillTextType(fileTxtType.addNewSoftware(), valueFieldPair, Language.DEFAULT);

            // We can't separate version in any easy way from software information since it doesn't come in two distinct fields in POR-file
            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.SOFTWAREVERSION));
            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                softwareType.setVersion(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
        }
    }

    private void setFileNameAndID(RevisionData variables, RevisionData attachment, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Set file name
            SimpleTextType stt = fillTextType(fileTxtType.addNewFileName(), FilenameUtils.getName(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)));

            // set ID
            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILEID));
            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                stt.setID(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }

            // Add file type
            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILETYPE));
            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                fillTextType(fileTxtType.addNewFileType(), valueFieldPair, Language.DEFAULT);
            }
        }
    }

    private void setFileDescription(RevisionData attachment, FileDscrType fileDscrType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILEDESCRIPTION));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(fileDscrType, valueFieldPair, language);
        }
    }
}
