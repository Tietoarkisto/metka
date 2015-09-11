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

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.*;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * WARNING: Even in the case of partial success in import this class can make permanent changes to revisions including creating new ones.
 * If this is a problem then some cache system is needed where changed revisions are grouped together and updated at once.
 * However the current edit mechanism means that at least some new drafts can be created even then.
 */
class DDIReadDataDescription extends DDIReadSectionBase {
    private final RevisionRepository revisions;
    private final RevisionEditRepository edit;
    private final StudyVariableSearch variableSearch;
    private final String studyId;

    DDIReadDataDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration
            , RevisionRepository revisions, RevisionEditRepository edit, StudyVariableSearch variableSearch
            , String studyId) {
        super(revision, language, codeBook, info, configuration);
        this.revisions = revisions;
        this.edit = edit;
        this.variableSearch = variableSearch;
        this.studyId = studyId;
    }

    ReturnResult read() {
        if(!hasContent(codeBook.getDataDscrArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        ContainerDataField variablesCon = revision.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES)).getRight();
        if(variablesCon == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        DataRow row = variablesCon.getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(language.toValue())).getRight();
        if(row == null) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<StatusCode, ValueDataField> valuePair = row.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(valuePair.getLeft() != StatusCode.FIELD_FOUND || !valuePair.getRight().hasValueFor(language)) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        // Get variables data since it contains most of the information needed for this. Some additional data is also needed from the actual file but very little.
        Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(valuePair.getRight().getActualValueFor(Language.DEFAULT));
        if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(),
                    "Couldn't find expected variables revision with id: " + valuePair.getRight().getActualValueFor(Language.DEFAULT));
            return revisionPair.getLeft();
        }
        RevisionData variables = revisionPair.getRight();

        ReturnResult result;
        DataDscrType dataDscr = codeBook.getDataDscrArray(0);

        result = readVariableGroups(dataDscr, variables);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        revisions.updateRevisionData(variables);

        return readVars(dataDscr);
    }

    private ReturnResult readVariableGroups(DataDscrType dataDscr, RevisionData variables) {
        if(variables.getState() != RevisionState.DRAFT) {
            Pair<OperationResponse, RevisionData> pair = edit.edit(variables.getKey(), info);
            if(pair.getLeft().getResult().equals(ReturnResult.REVISION_FOUND.name())) {
                if(!AuthenticationUtil.isHandler(pair.getRight())) {
                    return ReturnResult.USER_NOT_HANDLER;
                }
            } else if(!pair.getLeft().getResult().equals(ReturnResult.REVISION_CREATED.name())) {
                return ReturnResult.valueOf(pair.getLeft().getResult());
            }
            variables = pair.getRight();
        } else if(!AuthenticationUtil.isHandler(variables)) {
            return ReturnResult.USER_NOT_HANDLER;
        }

        Pair<StatusCode, ValueDataField> valuePair = variables.dataField(ValueDataFieldCall.get("study"));
        if(valuePair.getLeft() != StatusCode.FIELD_FOUND || !valuePair.getRight().hasValueFor(Language.DEFAULT)) {
            return ReturnResult.OPERATION_FAIL;
        }
        Long variablesStudy = valuePair.getRight().getValueFor(Language.DEFAULT).valueAsInteger();

        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerPair = getContainer(Fields.VARGROUPS, variables);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }

        ContainerDataField vargroups = containerPair.getRight().getLeft();
        Map<String, Change> changeMap = containerPair.getRight().getRight();

        // Each language is a separate VARIABLES revisionable, clear the vargroups
        if(vargroups.hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
                vargroups.removeRow(row.getRowId(), variables.getChanges(), info);
            }
        }

        if(!hasContent(dataDscr.getVarGrpArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        for(VarGrpType varGrp : dataDscr.getVarGrpArray()) {
            if(!StringUtils.hasText(getText(varGrp)) && varGrp.getVar().size() == 0 && varGrp.getTxtArray().length == 0) {
                continue;
            }
            DataRow vargroup = null;
            RowChange vargroupChange = null;

            // Create new group and add the variables to it
            Pair<StatusCode, DataRow> rowPair = vargroups.insertNewDataRow(Language.DEFAULT, changeMap);
            if(rowPair.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }

            vargroup = rowPair.getRight();

            if(StringUtils.hasText(getText(varGrp))) {
                valueSet(vargroup, Fields.VARGROUPTITLE, getText(varGrp), Language.DEFAULT, variables.getChanges());
            }

            vargroupChange = ((ContainerChange)changeMap.get(Fields.VARGROUPS)).get(vargroup.getRowId());
            if(vargroupChange == null) {
                vargroupChange = new RowChange(vargroup.getRowId());
                ((ContainerChange)changeMap.get(Fields.VARGROUPS)).put(vargroupChange);
            }

            String[] splits = getVarNames(varGrp);
            if(splits.length > 0) {
                Pair<ReturnResult, ReferenceContainerDataField> pair = getReferenceContainer(vargroup, Fields.VARGROUPVARS, variables.getChanges());
                if(pair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                    continue;
                }

                for(String name : splits) {
                    Pair<ReturnResult, RevisionData> varPair = variableSearch.findVariableWithId(variablesStudy, studyId+"_"+name);
                    if(varPair.getLeft() != ReturnResult.REVISION_FOUND) {
                        continue;
                    }
                    pair.getRight().getOrCreateReferenceWithValue(varPair.getRight().getKey().getId().toString(), vargroupChange.getChanges(), info);
                }
            }

            // Setting vargroup texts is identical on all languages
            setVarGroupTexts(variables, varGrp, vargroup, changeMap, varGrp.getTxtArray());
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private String[] getVarNames(VarGrpType varGrp) {
        String[] splits = new String[0];
        if(varGrp.getVar().size() > 0) {
            if(varGrp.getVar().size() == 1) {
                if(varGrp.getVar().get(0) instanceof String) {
                    splits = ((String)varGrp.getVar().get(0)).split("\\s");
                }
            } else {
                splits = new String[varGrp.getVar().size()];
                for(int i = 0; i < varGrp.getVar().size(); i++) {
                    splits[i] = varGrp.getVar().get(i) instanceof String ? (String)varGrp.getVar().get(0) : "";
                }
            }
        }
        return splits;
    }

    private void setVarGroupTexts(RevisionData variables, VarGrpType varGrp, DataRow row, Map<String, Change> changeMap, TxtType[] txtArray) {
        if(txtArray != null && txtArray.length > 0) {
            Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> varTxtPair = getContainer(Fields.VARTEXTS, row, variables.getChanges());
            if(varTxtPair.getLeft() == ReturnResult.OPERATION_SUCCESSFUL) {
                ContainerDataField vartexts = varTxtPair.getRight().getLeft();
                Map<String, Change> vartextsChange = varTxtPair.getRight().getRight();

                ContainerChange vargroupsChange = (ContainerChange)changeMap.get(Fields.VARGROUPS);
                if(vargroupsChange == null) {
                    vargroupsChange = new ContainerChange(Fields.VARGROUPS);
                    changeMap.put(vargroupsChange.getKey(), vargroupsChange);
                }

                RowChange change = vargroupsChange.get(row.getRowId());
                if(change == null) {
                    change = new RowChange(row.getRowId());
                    vargroupsChange.put(change);
                }

                for(DataRow txtRow : vartexts.getRowsFor(Language.DEFAULT)) {
                    if(!txtRow.getRemoved()) {
                        vartexts.removeRow(txtRow.getRowId(), change.getChanges(), info);
                    }
                }
                for(TxtType txt : varGrp.getTxtArray()) {
                    Pair<StatusCode, DataRow> rowPair = vartexts.insertNewDataRow(Language.DEFAULT, vartextsChange);
                    if(rowPair.getLeft() != StatusCode.ROW_INSERT) {
                        continue;
                    }
                    RowChange txtRowChange = ((ContainerChange)vartextsChange.get(Fields.VARTEXTS)).get(rowPair.getRight().getRowId());
                    if(txtRowChange == null) {
                        txtRowChange = new RowChange(rowPair.getRight().getRowId());
                        ((ContainerChange)vartextsChange.get(Fields.VARTEXTS)).put(txtRowChange);
                    }
                    valueSet(rowPair.getRight(), Fields.VARTEXT, getText(txt), Language.DEFAULT, txtRowChange.getChanges());
                }
            }
        }
    }

    private ReturnResult readVars(DataDscrType dataDscr) {
        if(!hasContent(dataDscr.getVarArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        ReturnResult result;
        for(VarType var : dataDscr.getVarArray()) {
            result = readVar(var);
            if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private ReturnResult readVar(VarType var) {
        if(!StringUtils.hasText(var.getName())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        //String varId =

        Pair<ReturnResult, RevisionData> variablePair = variableSearch.findVariableWithId(revision.getKey().getId(), studyId+"_"+var.getName());
        if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.warning(getClass(), "Tried to import variable "+studyId+"_"+var.getName()+" that was not found for study "+revision.getKey().getId());
            // We don't need to stop the import process for variable that we can't find.
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        RevisionData variable = variablePair.getRight();

        if(variable.getState() != RevisionState.DRAFT) {
            Pair<OperationResponse, RevisionData> pair = edit.edit(variable.getKey(), info);
            if(pair.getLeft().getResult().equals(ReturnResult.REVISION_FOUND)) {
                if(!AuthenticationUtil.isHandler(pair.getRight())) {
                    return ReturnResult.USER_NOT_HANDLER;
                }
            } else if(!pair.getLeft().getResult().equals(ReturnResult.REVISION_CREATED.name())) {
                return ReturnResult.valueOf(pair.getLeft().getResult());
            }
            variable = pair.getRight();
        } else if(!AuthenticationUtil.isHandler(variable)) {
            return ReturnResult.USER_NOT_HANDLER;
        }

        // PREQTXTS
        clearTable(variable, variable.getChanges(), Fields.PREQTXTS);

        // QSTNLITS
        clearTable(variable, variable.getChanges(), Fields.QSTNLITS);

        // POSTQTXTS
        clearTable(variable, variable.getChanges(), Fields.POSTQTXTS);

        // IVUINSTRS
        clearTable(variable, variable.getChanges(), Fields.IVUINSTRS);

        // VARSECURITIES
        clearTable(variable, variable.getChanges(), Fields.VARSECURITIES);

        // VARTEXTS
        clearTable(variable, variable.getChanges(), Fields.VARTEXTS);

        // VARNOTES
        clearTable(variable, variable.getChanges(), Fields.VARNOTES);

        ReturnResult result;

        valueSet(variable, Fields.VARLABEL, hasContent(var.getLablArray()) ? getText(var.getLablArray(0)) : "", Language.DEFAULT, variable.getChanges());

        result = readVarQstn(var, variable);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARSECURITIES, Fields.VARSECURITY, var.getSecurityArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARTEXTS, Fields.VARTEXT, var.getTxtArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARNOTES, Fields.VARNOTE, var.getNotesArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        revisions.updateRevisionData(variable);
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private void clearTable(DataFieldContainer data, Map<String, Change> changeMap, String key) {
        Pair<StatusCode, ContainerDataField> pair = data.dataField(ContainerDataFieldCall.get(key));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasRowsFor(Language.DEFAULT)) {
            return;
        }
        for(Integer rowId : pair.getRight().getRowIdsFor(Language.DEFAULT)) {
            Pair<StatusCode, DataRow> rowPair = pair.getRight().getRowWithId(rowId);
            if(rowPair.getLeft() != StatusCode.ROW_FOUND) {
                continue;
            }
            DataRow row = rowPair.getRight();
            if(row.getRemoved()) {
                continue;
            }
            pair.getRight().removeRow(rowId, changeMap, info);
        }
    }

    private ReturnResult readVarQstn(VarType var, RevisionData variable) {
        if(!hasContent(var.getQstnArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        QstnType qstn = var.getQstnArray(0);

        ReturnResult result;

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.PREQTXTS, Fields.PREQTXT, qstn.getPreQTxtArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.QSTNLITS, Fields.QSTNLIT, qstn.getQstnLitArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.POSTQTXTS, Fields.POSTQTXT, qstn.getPostQTxtArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        return fillSingleValueContainer(variable, variable.getChanges(), Fields.IVUINSTRS, Fields.IVUINSTR, qstn.getIvuInstrArray());
    }
}
