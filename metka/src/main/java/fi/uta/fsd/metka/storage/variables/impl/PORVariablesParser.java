package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRemoveRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import spssio.por.PORFile;
import spssio.por.input.PORReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fi.uta.fsd.metka.storage.variables.impl.StudyVariablesParserImpl.checkResultForUpdate;
import static fi.uta.fsd.metka.storage.variables.impl.StudyVariablesParserImpl.resultCheck;

class PORVariablesParser implements VariablesParser {
    private final RevisionData variablesData;
    private final DateTimeUserPair info;
    private final String studyId;
    private final RevisionRemoveRepository remove;
    private final RevisionRepository revisions;
    private final RevisionCreationRepository create;
    private final RevisionEditRepository edit;

    private final String software;
    private final int sizeX;
    private final int sizeY;
    private final List<PORUtil.PORVariableHolder> variables;
    private final Language language;
    private final List<RevisionData> variableRevisions;

    PORVariablesParser(String path, RevisionData variablesData, DateTimeUserPair info, String studyId,
                       RevisionRepository revisions, RevisionRemoveRepository remove, RevisionCreationRepository create, RevisionEditRepository edit) {
        PORReader reader = new PORReader();
        PORFile por;
        try {
            por = reader.parse(path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(PORVariablesParser.class, "IOException while reading POR-file with path " + path);
            throw new UnsupportedOperationException("Could not parse POR file");
        }

        this.remove = remove;
        this.revisions = revisions;
        this.create = create;
        this.edit = edit;

        // Get language
        String baseName = FilenameUtils.getBaseName(path);
        String lastChar = baseName.substring(baseName.length()-1);
        switch(lastChar) {
            case "e":
                language = Language.EN;
                break;
            case "s":
                language = Language.SV;
                break;
            default:
                language = Language.DEFAULT;
                break;
        }

        this.variableRevisions = revisions.getVariableRevisionsOfVariables(variablesData.getKey().getId());

        // Group variables to list
        variables = PORUtil.groupVariables(por);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        software = por.getSoftware();
        sizeX = por.data.sizeX();
        sizeY = por.data.sizeY();
        this.variablesData = variablesData;
        this.info = info;
        this.studyId = studyId;
    }

    public ParseResult parse() {
        ParseResult result = ParseResult.NO_CHANGES;

        // Insert data that is only relevant with default language file
        if(language == Language.DEFAULT) {
            // Set software field
            Pair<StatusCode, ValueDataField> fieldPair = variablesData.dataField(
                    ValueDataFieldCall.set(Fields.SOFTWARE, new Value(software), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);


            // Set varquantity field
            fieldPair = variablesData.dataField(ValueDataFieldCall.set(Fields.VARQUANTITY, new Value(sizeX + ""), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);

            // Set casequantity field
            fieldPair = variablesData.dataField(ValueDataFieldCall.set(Fields.CASEQUANTITY, new Value(sizeY + ""), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);
        }

        // Make VariablesHandler
        VariableParser parser = new VariableParser(info, language);

        List<Pair<RevisionData, PORUtil.PORVariableHolder>> listOfEntitiesAndHolders = new ArrayList<>();
        for(PORUtil.PORVariableHolder variable : variables) {
            RevisionData variableRevision = null;
            for(Iterator<RevisionData> i = variableRevisions.iterator(); i.hasNext(); ) {
                variableRevision = i.next();
                Pair<StatusCode, ValueDataField> fieldPair = variableRevision.dataField(ValueDataFieldCall.get(Fields.VARID));
                if(fieldPair.getRight().getActualValueFor(Language.DEFAULT).equals(studyId + "_" + parser.getVarName(variable))) {
                    i.remove();
                    break;
                }
                variableRevision = null;
            }
            listOfEntitiesAndHolders.add(new ImmutablePair<>(variableRevision, variable));
        }


        ContainerDataField variableGroups = variablesData.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();

        // Only perform removal if we're dealing with default language variable file. Translation files should not remove variables even though they can add variables
        if(language == Language.DEFAULT) {
            for(RevisionData variableRevision : variableRevisions) {
                // All remaining rows in variableEntities should be removed since no variable was found for them in the current POR-file

                // If removal of the revision returns SUCCESS_DRAFT this means that there's more revisions to remove and second call with new latest revision should clear out any remaining revisions.
                if(remove.remove(TransferData.buildFromRevisionData(variableRevision, RevisionableInfo.FALSE)) == RemoveResult.SUCCESS_DRAFT) {
                    Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(variableRevision.getKey().getId(), false, ConfigurationType.STUDY_VARIABLE);
                    remove.remove(TransferData.buildFromRevisionData(dataPair.getRight(), RevisionableInfo.FALSE));
                }
                ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
                if(variablesContainer != null) {
                    // See that respective rows are removed from STUDY_VARIABLES
                    //    Remove from variables list
                    ReferenceRow reference = variablesContainer.getReferenceWithValue(variableRevision.getKey().getId().toString()).getRight();
                    if(reference != null) {
                        StatusCode status = variablesContainer.removeReference(reference.getRowId(), variablesData.getChanges(), info).getLeft();
                        if(status == StatusCode.ROW_CHANGE || status == StatusCode.ROW_REMOVED) {
                            result = resultCheck(result, ParseResult.REVISION_CHANGES);
                        }
                    }
                }

                // Remove
                if(variableGroups != null) {
                    for(DataRow row : variableGroups.getRowsFor(Language.DEFAULT)) {
                        variablesContainer = row.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS)).getRight();
                        if(variablesContainer != null) {
                            // See that respective rows are removed from VARGROUPVARS
                            //    Remove from variables list
                            ReferenceRow reference = variablesContainer.getReferenceWithValue(variableRevision.getKey().getId().toString()).getRight();
                            if(reference != null) {
                                StatusCode status = variablesContainer.removeReference(reference.getRowId(), variablesData.getChanges(), info).getLeft();
                                if(status == StatusCode.ROW_CHANGE || status == StatusCode.ROW_REMOVED) {
                                    result = resultCheck(result, ParseResult.REVISION_CHANGES);
                                }
                                // Since variable should always be only in one group at a time we can break out.
                                break;
                            }
                        }
                    }
                }
            }
        }

        // listOfEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable
        ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(listOfEntitiesAndHolders.size() > 0 && variablesContainer == null) {
            Pair<StatusCode, ReferenceContainerDataField> containerPair = variablesData.dataField(ReferenceContainerDataFieldCall.set("variables"));
            result = checkResultForUpdate(containerPair, result);
            variablesContainer = containerPair.getRight();
        }

        if(variablesContainer == null) {
            Logger.error(PORVariablesParser.class, "Missing variables container even though it should be present or created");
            return resultCheck(result, ParseResult.NO_VARIABLES_CONTAINER);
        }

        Pair<StatusCode, ValueDataField> studyField = variablesData.dataField(ValueDataFieldCall.get("study"));
        for(Pair<RevisionData, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            // Iterate through entity/holder pairs. There should always be a holder but missing entity indicates that this is a new variable.
            // After all variables are handled there should be one non removed revisionable per variable in the current por-file.
            // Each revisionable should have an open draft revision (this is a shortcut but it would require doing actual change checking for all variable content to guarantee that no
            // unnecessary revisions are created. This is not required and so a new draft is provided per revisionable).
            // Variables entity should have an open draft revision that includes references to all variables as well as non grouped references for all variables that previously were
            // not in any groups.

            RevisionData variableData = pair.getLeft();
            PORUtil.PORVariableHolder variable = pair.getRight();
            String varName = parser.getVarName(variable);
            String varId = studyId + "_" + parser.getVarName(variable);

            Pair<ReturnResult, RevisionData> dataPair;
            if(variableData == null) {
                RevisionCreateRequest request = new RevisionCreateRequest();
                request.setType(ConfigurationType.STUDY_VARIABLE);
                request.getParameters().put("study", studyField.getRight().getActualValueFor(Language.DEFAULT));
                request.getParameters().put("variablesid", variablesData.getKey().getId().toString());
                request.getParameters().put("varname", varName);
                request.getParameters().put("varid", varId);
                dataPair = create.create(request);
                if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                    Logger.error(PORVariablesParser.class, "Couldn't create new variable revisionable for study "+studyField.getRight().getActualValueFor(Language.DEFAULT)+" and variables "+variablesData.toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLES);
                }
                variableData = dataPair.getRight();
            }

            // Add Saved reference if missing
            variablesContainer.getOrCreateReferenceWithValue(variableData.getKey().getId().toString(), variablesData.getChanges(), info).getRight();
            result = resultCheck(result, ParseResult.REVISION_CHANGES);

            if(variableData.getState() != RevisionState.DRAFT) {
                dataPair = edit.edit(TransferData.buildFromRevisionData(variableData, RevisionableInfo.FALSE));
                if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                    Logger.error(PORVariablesParser.class, "Couldn't create new DRAFT revision for "+variableData.getKey().toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLE_DRAFT);
                }
                variableData = dataPair.getRight();
            }

            // Merge variable to variable revision
            ParseResult mergeResult = parser.mergeToData(variableData, variable);

            if(mergeResult == ParseResult.REVISION_CHANGES) {
                ReturnResult updateResult = revisions.updateRevisionData(variableData);
                if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                    Logger.error(PORVariablesParser.class, "Could not update revision data for "+variableData.toString()+" with result "+updateResult);
                }
            }
        }

        return result;
    }
}
