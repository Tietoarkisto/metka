package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

class DDIDataDescription extends DDISectionBase {
    private final RevisionRepository revisions;
    private final StudyVariableSearch variableSearch;

    DDIDataDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration, RevisionRepository revisions, StudyVariableSearch variableSearch) {
        super(revision, language, codeBook, info, configuration);
        this.revisions = revisions;
        this.variableSearch = variableSearch;
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
        // TODO: Still unclear on how to handle languages
        /*result = readVariableGroups(dataDscr, variables);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}*/

        return readVars(dataDscr);
    }

    // TODO: Still unfinished
    private ReturnResult readVariableGroups(DataDscrType dataDscr, RevisionData variables) {
        // TODO: Open a draft if needed
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerPair = getContainer(Fields.VARGROUPS, variables);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }

        ContainerDataField vargroups = containerPair.getRight().getLeft();
        ContainerChange vargroupsChange = containerPair.getRight().getRight();

        // TODO: Should we clear the array only on default language?
        /*for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
            vargroups.removeRow(row.getRowId(), variables.getChanges(), info);
        }*/

        if(!hasContent(dataDscr.getVarGrpArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        for(VarGrpType varGrp : dataDscr.getVarGrpArray()) {
            Pair<StatusCode, DataRow> rowPair = vargroups.insertNewDataRow(Language.DEFAULT, vargroupsChange);
            if(rowPair.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }
            DataRow row = rowPair.getRight();
            // TODO: Should we set title only on default?
            /*if(language == Language.DEFAULT) {
                if(StringUtils.hasText(varGrp.xmlText())) {
                    valueSet(row, Fields.VARGROUPTITLE, varGrp.xmlText(), )
                }
            }*/

            // TODO: How do we find the correct row on other languages.
        }

        return ReturnResult.OPERATION_SUCCESSFUL;

        /*Pair<ReturnResult, RevisionData> revisionDataPair;Pair<StatusCode, ContainerDataField> containerPair = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS));
        ContainerDataField vargroups = containerPair.getRight();
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && vargroups.hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VARGROUPTITLE));
                // TODO: Should variable group title be translatable?
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    VarGrpType varGrpType = fillTextType(dataDscrType.addNewVarGrp(), valueFieldPair, Language.DEFAULT);

                    // TODO: do we want variable names or variable id:s?
                    Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = row.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS));
                    if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
                        String vars = "";
                        for(ReferenceRow reference : referenceContainerPair.getRight().getReferences()) {
                            if(reference.getRemoved()) {
                                 continue;
                            }
                            revisionDataPair = revisions.getLatestRevisionForIdAndType(reference.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                            if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                                Logger.error(DDIDataDescription.class, "Referenced study variable with id: " + reference.getReference().asInteger() + " could not be found with result " + revisionDataPair.getLeft());
                                continue;
                            }
                            if(StringUtils.hasText(vars)) {
                                vars += " ";
                            }
                            valueFieldPair = revisionDataPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARNAME));
                            if(!hasValue(valueFieldPair, language)) {
                                vars += "-";
                            } else {
                                vars += valueFieldPair.getRight().getActualValueFor(language);
                            }
                        }
                        varGrpType.setVar(Lists.asList(vars, new String[0]));
                    }

                    containerPair = row.dataField(ContainerDataFieldCall.get(Fields.VARGROUPTEXTS));
                    if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
                        for(DataRow vartextrow : containerPair.getRight().getRowsFor(language)) {
                            if(vartextrow.getRemoved()) {
                                continue;
                            }
                            valueFieldPair = vartextrow.dataField(ValueDataFieldCall.get(Fields.VARGROUPTEXT));
                            if(hasValue(valueFieldPair, language)) {
                                fillTextType(varGrpType.addNewTxt(), valueFieldPair, language);
                            }

                        }
                    }
                }
            }
        }*/
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

    // TODO: Still unfinished
    private ReturnResult readVar(VarType var) {
        // TODO: Open a draft if needed
        if(!StringUtils.hasText(var.getID())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, RevisionData> variablePair = variableSearch.findVariableWithId(revision.getKey().getId(), var.getID());
        if(variablePair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.info(DDIDataDescription.class, "Tried to import variable "+var.getID()+" not that was not found for study "+revision.getKey().getId());
            // We don't need to stop the import process for variable that we can't find.
            return ReturnResult.OPERATION_SUCCESSFUL;
        }

        RevisionData variable = variablePair.getRight();
        // TODO: Clear individual variable of stuff that can be imported

        // PREQTXTS
        clearTable(variable, variable.getChanges(), Fields.PREQTXTS);
        // QSTNLITS

        // POSTQTXTS

        // IVUINSTRS

        // VARSECURITIES

        // VARTEXTS

        // VARNOTES

        ReturnResult result;

        valueSet(variable, Fields.VARLABEL, hasContent(var.getLablArray()) ? var.getLablArray(0).xmlText() : "", language, variable.getChanges());

        // TODO: Skip for now
        /*result = readCategories(var, variable);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}*/

        result = readVarQstn(var, variable);
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARSECURITIES, Fields.VARSECURITY, var.getSecurityArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARTEXTS, Fields.VARTEXT, var.getTxtArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        result = fillSingleValueContainer(variable, variable.getChanges(), Fields.VARNOTES, Fields.VARNOTE, var.getNotesArray());
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {return result;}

        // TODO: Check 'translated' either here or during the field fill process

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private void clearTable(DataFieldContainer data, Map<String, Change> changeMap, String key) {
        Pair<StatusCode, ContainerDataField> pair = data.dataField(ContainerDataFieldCall.get(key));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            return;
        }
        for(DataRow row : pair.getRight().getRowsFor(language)) {
            if(row.getRemoved()) {
                continue;
            }
            pair.getRight().removeRow(row.getRowId(),changeMap, info);
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
