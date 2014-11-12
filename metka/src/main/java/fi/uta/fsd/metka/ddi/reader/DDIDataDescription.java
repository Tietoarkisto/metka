package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * WARNING: Even in the case of partial success in import this class can make permanent changes to revisions including creating new ones.
 * If this is a problem then some cache system is needed where changed revisions are grouped together and updated at once.
 * However the current edit mechanism means that at least some new drafts can be created even then.
 */
class DDIDataDescription extends DDISectionBase {
    private final RevisionRepository revisions;
    private final RevisionEditRepository edit;
    private final StudyVariableSearch variableSearch;
    private final String studyId;

    DDIDataDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration
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

        Pair<StatusCode, ValueDataField> valuePair = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(valuePair.getLeft() != StatusCode.FIELD_FOUND || !valuePair.getRight().hasValueFor(Language.DEFAULT)) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        // Get variables data since it contains most of the information needed for this. Some additional data is also needed from the actual file but very little.
        Pair<ReturnResult, RevisionData> revisionPair = revisions.getLatestRevisionForIdAndType(
                valuePair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
        if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(DDIDataDescription.class,
                    "Couldn't find expected variables revision with id: " + valuePair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
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
            Pair<ReturnResult, RevisionData> pair = edit.edit(TransferData.buildFromRevisionData(variables, RevisionableInfo.FALSE));
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                if(!AuthenticationUtil.isHandler(pair.getRight())) {
                    return ReturnResult.USER_NOT_HANDLER;
                }
            } else if(pair.getLeft() != ReturnResult.REVISION_CREATED) {
                return pair.getLeft();
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

        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(Fields.VARGROUPS, variables);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }

        ContainerDataField vargroups = containerPair.getRight().getLeft();
        ContainerChange vargroupsChange = containerPair.getRight().getRight();

        // On DEFAULT language clear the vargroups container, on other languages just perform merge operations
        if(language == Language.DEFAULT && vargroups.hasRowsFor(Language.DEFAULT)) {
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
            if(language != Language.DEFAULT) {
                // Try to find the correct group for non default language, if no group is found or language is default then new group is formed later in the code
                String[] splits = getVarNames(varGrp);
                // If there are no listed variables in this group we simply can not find it so skip to the next one
                if(splits == null || splits.length == 0) {
                    continue;
                }
                for(String varName : splits) {
                    Pair<ReturnResult, RevisionData> variable = variableSearch.findVariableWithId(variablesStudy, studyId+"_"+varName);
                    if(variable.getLeft() != ReturnResult.REVISION_FOUND) {
                        continue;
                    }
                    if(!vargroups.hasRowsFor(Language.DEFAULT)) {
                        continue;
                    }
                    for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
                        if(row.getRemoved()) {
                            continue;
                        }
                        Pair<StatusCode, ReferenceContainerDataField> varsPair = row.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS));
                        if(varsPair.getLeft() != StatusCode.FIELD_FOUND) {
                            continue;
                        }
                        Pair<StatusCode, ReferenceRow> varRef = varsPair.getRight().getReferenceWithValue(variable.getRight().getKey().getId().toString());
                        if(varRef.getLeft() == StatusCode.FOUND_ROW) {
                            vargroup = row;
                            break;
                        }
                    }
                    if(vargroup != null) {
                        break;
                    }
                }
            }
            if(vargroup == null) {
                // Either we're in default language or we have a group that's not in default language,
                // Create new group and add the variables to it

                Pair<StatusCode, DataRow> rowPair = vargroups.insertNewDataRow(Language.DEFAULT, vargroupsChange);
                if(rowPair.getLeft() != StatusCode.NEW_ROW) {
                    continue;
                }

                vargroup = rowPair.getRight();

                if(StringUtils.hasText(getText(varGrp))) {
                    valueSet(vargroup, Fields.VARGROUPTITLE, getText(varGrp), Language.DEFAULT, variables.getChanges());
                }

                vargroupChange = vargroupsChange.get(vargroup.getRowId());
                if(vargroupChange == null) {
                    vargroupChange = new RowChange(vargroup.getRowId());
                    vargroupsChange.put(vargroupChange);
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
            }

            // Setting vargroup texts is identical on all languages
            if(vargroup != null) {
                setVarGroupTexts(variables, varGrp, vargroup, vargroupsChange, varGrp.getTxtArray());
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private String[] getVarNames(VarGrpType varGrp) {
        String[] splits = new String[0];
        if(varGrp.getVar().size() > 0) {
            if(varGrp.getVar().size() == 1) {
                if(varGrp.getVar().get(0) instanceof String) {
                    splits = ((String)varGrp.getVar().get(0)).split(" ");
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

    private void setVarGroupTexts(RevisionData variables, VarGrpType varGrp, DataRow row, ContainerChange vargroupsChange, TxtType[] txtArray) {
        if(txtArray != null && txtArray.length > 0) {
            Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> varTxtPair = getContainer(Fields.VARTEXTS, row, variables.getChanges());
            if(varTxtPair.getLeft() == ReturnResult.OPERATION_SUCCESSFUL) {
                ContainerDataField vartexts = varTxtPair.getRight().getLeft();
                ContainerChange vartextsChange = varTxtPair.getRight().getRight();

                RowChange change = vargroupsChange.get(row.getRowId());
                if(change == null) {
                    change = new RowChange(row.getRowId());
                    vargroupsChange.put(change);
                }

                for(DataRow txtRow : vartexts.getRowsFor(language)) {
                    if(!txtRow.getRemoved()) {
                        vartexts.removeRow(txtRow.getRowId(), change.getChanges(), info);
                    }
                }
                for(TxtType txt : varGrp.getTxtArray()) {
                    Pair<StatusCode, DataRow> rowPair = vartexts.insertNewDataRow(language, vartextsChange);
                    if(rowPair.getLeft() != StatusCode.NEW_ROW) {
                        continue;
                    }
                    RowChange txtRowChange = vartextsChange.get(rowPair.getRight().getRowId());
                    if(txtRowChange == null) {
                        txtRowChange = new RowChange(rowPair.getRight().getRowId());
                        vartextsChange.put(txtRowChange);
                    }
                    valueSet(rowPair.getRight(), Fields.VARTEXT, getText(txt), language, txtRowChange.getChanges());
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
        if(!StringUtils.hasText(var.getID())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, RevisionData> variablePair = variableSearch.findVariableWithId(revision.getKey().getId(), var.getID());
        if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.info(DDIDataDescription.class, "Tried to import variable "+var.getID()+" that was not found for study "+revision.getKey().getId());
            // We don't need to stop the import process for variable that we can't find.
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        RevisionData variable = variablePair.getRight();

        if(variable.getState() != RevisionState.DRAFT) {
            Pair<ReturnResult, RevisionData> pair = edit.edit(TransferData.buildFromRevisionData(variable, RevisionableInfo.FALSE));
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                if(!AuthenticationUtil.isHandler(pair.getRight())) {
                    return ReturnResult.USER_NOT_HANDLER;
                }
            } else if(pair.getLeft() != ReturnResult.REVISION_CREATED) {
                return pair.getLeft();
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

        valueSet(variable, Fields.VARLABEL, hasContent(var.getLablArray()) ? getText(var.getLablArray(0)) : "", language, variable.getChanges());

        result = readVarQstn(var, variable);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARSECURITIES, Fields.VARSECURITY, var.getSecurityArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARTEXTS, Fields.VARTEXT, var.getTxtArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARNOTES, Fields.VARNOTE, var.getNotesArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        VariablesFactory fac = new VariablesFactory();
        fac.checkVariableTranslations(variable, info);

        revisions.updateRevisionData(variable);
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private void clearTable(DataFieldContainer data, Map<String, Change> changeMap, String key) {
        Pair<StatusCode, ContainerDataField> pair = data.dataField(ContainerDataFieldCall.get(key));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasRowsFor(language)) {
            return;
        }
        for(Integer rowId : pair.getRight().getRowIdsFor(language)) {
            Pair<StatusCode, DataRow> rowPair = pair.getRight().getRowWithId(rowId);
            if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
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
