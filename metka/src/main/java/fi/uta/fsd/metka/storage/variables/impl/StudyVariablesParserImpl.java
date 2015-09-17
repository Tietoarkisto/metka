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

package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * This class handles default language study variables parsing, creating and merging.
 * Separate class needs to be create for translation file handling since it doesn't do deletion
 * and (hopefully not) creation but instead just adds different translation values to fields that are marked translatable.
 */
// TODO: This class is a mess, clean it up
@Repository
public class StudyVariablesParserImpl implements StudyVariablesParser {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionRemoveRepository remove;

    @Autowired
    private RevisionCreationRepository create;

    // This should possibly be used instead of directly updating revision data if parser returns revision changed
    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionRestoreRepository restore;

    static ParseResult checkResultForUpdate(Pair<StatusCode, ? extends DataField> fieldPair, ParseResult result) {
        if(fieldPair.getLeft() == StatusCode.FIELD_UPDATE || fieldPair.getLeft() == StatusCode.FIELD_INSERT) {
            return resultCheck(result, ParseResult.REVISION_CHANGES);
        }
        return result;
    }

    static ParseResult checkRowResultForUpdate(Pair<StatusCode, ? extends ContainerRow> rowPair, ParseResult result) {
        if(rowPair.getLeft() == StatusCode.ROW_CHANGE || rowPair.getLeft() == StatusCode.ROW_INSERT || rowPair.getLeft() == StatusCode.ROW_REMOVED) {
            return resultCheck(result, ParseResult.REVISION_CHANGES);
        }
        return result;
    }

    static ParseResult resultCheck(ParseResult result, ParseResult def) {
        return result != ParseResult.REVISION_CHANGES ? def : result;
    }


    @Override
    public ParseResult parse(RevisionData attachment, VariableDataType type, RevisionData study, Language varLang, DateTimeUserPair info) {
        // Sanity check
        if(type == null) {
            return ParseResult.NO_TYPE_GIVEN;
        }
        if(study == null) {
            return ParseResult.DID_NOT_FIND_STUDY;
        }

        // Prepare
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        ParseResult result = ParseResult.NO_CHANGES;

        // **********************
        // StudyAttachment checks
        // **********************

        // Check for file path from attachment
        Pair<StatusCode, ValueDataField> fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Logger.error(getClass(), "Did not find path in "+attachment.toString()+" even though shouldn't arrive at this point without path.");
            return ParseResult.VARIABLES_FILE_HAD_NO_PATH;
        }
        String fileName = fieldPair.getRight().getActualValueFor(Language.DEFAULT);

        // Get or create study variables
        RevisionData variablesData;
        Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.set(Fields.STUDYVARIABLES));
        if(conPair.getLeft() == StatusCode.FIELD_INSERT) {
            result = ParseResult.REVISION_CHANGES;
        }
        Pair<StatusCode, DataRow> rowPair = conPair.getRight().getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang.toValue()), study.getChanges(), info);
        if(rowPair.getLeft() == StatusCode.ROW_INSERT) {
            result = ParseResult.REVISION_CHANGES;
        }
        fieldPair = rowPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARIABLES));

        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put(Fields.STUDY, study.getKey().getId().toString());
            request.getParameters().put(Fields.FILEID, attachment.getKey().asCongregateKey());
            request.getParameters().put(Fields.VARFILEID, FilenameUtils.getBaseName(fileName));
            request.getParameters().put(Fields.LANGUAGE, varLang.toValue());
            String ext = FilenameUtils.getExtension(fileName.toUpperCase());
            request.getParameters().put(Fields.VARFILETYPE, ext.equals("POR") ? "SPSS Portable" : ext);
            Pair<ReturnResult, RevisionData> dataPair = create.create(request);

            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                Logger.error(getClass(), "Couldn't create new variables revisionable for study "+study.toString()+" and file "+attachment.toString());
                return ParseResult.COULD_NOT_CREATE_VARIABLES;
            }

            rowPair.getRight().dataField(
                    ValueDataFieldCall.set(Fields.VARIABLES, new Value(dataPair.getRight().getKey().asCongregateKey()), Language.DEFAULT).setInfo(info).setChangeMap(study.getChanges()));
            result = ParseResult.REVISION_CHANGES;
            variablesData = dataPair.getRight();
        } else {
            String key = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
            Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(key);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Couldn't find revision for study variables with id "+fieldPair.getRight().getActualValueFor(Language.DEFAULT)
                        +" even though it's referenced from study "+study.toString());
                return ParseResult.DID_NOT_FIND_VARIABLES;
            }
            variablesData = dataPair.getRight();
        }

        if(variablesData.getState() != RevisionState.DRAFT) {
            //Pair<OperationResponse, RevisionData> dataPair = edit.edit(TransferData.buildFromRevisionData(variablesData, RevisionableInfo.FALSE), info);
            Pair<OperationResponse, RevisionData> dataPair = edit.edit(variablesData.getKey(), info);
            if(!(dataPair.getLeft().equals(ReturnResult.REVISION_CREATED) || dataPair.getLeft().equals(ReturnResult.REVISION_FOUND))) {
                Logger.error(getClass(), "Couldn't create new DRAFT or didn't find DRAFT revision for "+variablesData.getKey().toString());
                return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLES_DRAFT);
            }
            variablesData = dataPair.getRight();
        }

        boolean updateVariables = false;

        fieldPair = variablesData.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(fieldPair.getRight() == null || !fieldPair.getRight().hasValueFor(Language.DEFAULT) || !fieldPair.getRight().valueForEquals(Language.DEFAULT, attachment.getKey().asCongregateKey())) {
            variablesData.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(attachment.getKey().asCongregateKey()), Language.DEFAULT).setInfo(info));
            updateVariables = true;
        }

        if(!AuthenticationUtil.isHandler(variablesData)) {
            variablesData.setHandler(AuthenticationUtil.getUserName());
            updateVariables = true;
        }

        if(updateVariables) {
            // After this point the parser will update the variables data if necessary so this data should not be used for anything else
            revisions.updateRevisionData(variablesData);
        }

        // ************************
        // Actual variables parsing
        // ************************
        ParseResult variablesResult = ParseResult.NO_CHANGES;
        VariablesParser parser = null;
        switch(type) {
            case POR:
                // Read POR file
                String studyId = study.dataField(ValueDataFieldCall.get(Fields.STUDYID)).getRight().getActualValueFor(Language.DEFAULT);
                parser = new PORVariablesParser(
                        fileName,
                        varLang,
                        info,
                        studyId,
                        revisions,
                        remove,
                        create,
                        edit,
                        restore);
                break;
        }
        if(parser != null) {
            long start = System.currentTimeMillis(); // Debug times
            Logger.debug(getClass(), "Starting variables parsing for study");
            variablesResult = parser.parse(variablesData);
            result = resultCheck(result, variablesResult);
            Logger.debug(getClass(), "Variables parsing for study ended. Spent "+(System.currentTimeMillis()-start)+"ms");
        }/*
        if(variablesResult == ParseResult.REVISION_CHANGES) {
            ReturnResult updateResult = revisions.updateRevisionData(variablesData);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                Logger.error(getClass(), "Could not update revision data for "+variablesData.toString()+" with result "+updateResult);
                return resultCheck(result, ParseResult.VARIABLES_SERIALIZATION_FAILED);
            }
        }*/
        return result;
    }
}
