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
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
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

    private final String softwareName;
    private final String softwareVersion;
    private final int sizeX;
    private final int sizeY;
    private final List<PORUtil.PORVariableHolder> variables;
    private final Language language;
    private final List<RevisionData> variableRevisions;

    PORVariablesParser(String path, Language language, RevisionData variablesData, DateTimeUserPair info, String studyId,
                       RevisionRepository revisions, RevisionRemoveRepository remove, RevisionCreationRepository create, RevisionEditRepository edit) {
        this.language = language;
        this.remove = remove;
        this.revisions = revisions;
        this.create = create;
        this.edit = edit;
        this.variablesData = variablesData;
        this.info = info;
        this.studyId = studyId;

        PORReader reader = new PORReader();
        PORFile por;
        try {
            por = reader.parse(path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(getClass(), "IOException while reading POR-file with path " + path);
            throw new UnsupportedOperationException("Could not parse POR file");
        }

        this.variableRevisions = revisions.getVariableRevisionsOfVariables(variablesData.getKey().getId());

        // Group variables to list
        variables = PORUtil.groupVariables(por);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        String[] software = por.getSoftware().split(" ");
        softwareVersion = software.length > 1 ? software[software.length-1] : "";
        if(software.length == 1) {
            softwareName = software[0];
        } else if(software.length > 1) {
            String temp = "";
            for(int i = 0; i < software.length - 1; i++) {
                if(i > 0) {
                    temp += " ";
                }
                temp += software[i];
            }
            softwareName = temp;
        } else {
            softwareName = "";
        }

        sizeX = por.data.sizeX();
        sizeY = por.data.sizeY();
    }

    public ParseResult parse() {
        ParseResult result = ParseResult.NO_CHANGES;
        result = variablesBaseProperties(result);
        result = variablesParsing(result);
        return result;
    }

    private ParseResult variablesBaseProperties(ParseResult result) {
        // Insert data that is only relevant with default language file
        if(language == Language.DEFAULT) {
            // Set software field
            Pair<StatusCode, ValueDataField> fieldPair = variablesData.dataField(
                    ValueDataFieldCall.set(Fields.SOFTWARE, new Value(softwareName), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);

            // Set softwareversion
            fieldPair = variablesData.dataField(
                    ValueDataFieldCall.set(Fields.SOFTWAREVERSION, new Value(softwareVersion), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);


            // Set varquantity field
            fieldPair = variablesData.dataField(ValueDataFieldCall.set(Fields.VARQUANTITY, new Value(sizeX + ""), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);

            // Set casequantity field
            fieldPair = variablesData.dataField(ValueDataFieldCall.set(Fields.CASEQUANTITY, new Value(sizeY + ""), Language.DEFAULT).setInfo(info));
            result = checkResultForUpdate(fieldPair, result);
        }
        return result;
    }

    private ParseResult variablesParsing(ParseResult result) {
        // Make VariablesHandler
        /*VariableParser parser = new VariableParser(info, language);
        if(language == Language.DEFAULT) {
            return variablesParsingDefault(result, parser);
        } else {
            return variablesParsingNonDefault(result, parser);
        }*/

        VariableParser parser = new VariableParser(info, language);

        Logger.debug(getClass(), "Gathering entities for parsing");
        long start = System.currentTimeMillis();
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
        Logger.debug(getClass(), "Entities gathered. Took "+(System.currentTimeMillis()-start)+"ms");

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
            Logger.error(getClass(), "Missing variables container even though it should be present or created");
            return resultCheck(result, ParseResult.NO_VARIABLES_CONTAINER);
        }

        Pair<StatusCode, ValueDataField> studyField = variablesData.dataField(ValueDataFieldCall.get("study"));
        Logger.debug(getClass(), listOfEntitiesAndHolders.size()+" variables to parse.");
        int counter = 0;
        long timeSpent = 0L;
        for(Pair<RevisionData, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            // Iterate through entity/holder pairs. There should always be a holder but missing entity indicates that this is a new variable.
            // After all variables are handled there should be one non removed revisionable per variable in the current por-file.
            // Each revisionable should have an open draft revision (this is a shortcut but it would require doing actual change checking for all variable content to guarantee that no
            // unnecessary revisions are created. This is not required and so a new draft is provided per revisionable).
            // Variables entity should have an open draft revision that includes references to all variables as well as non grouped references for all variables that previously were
            // not in any groups.

            start = System.currentTimeMillis();


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
                    Logger.error(getClass(), "Couldn't create new variable revisionable for study "+studyField.getRight().getActualValueFor(Language.DEFAULT)+" and variables "+variablesData.toString());
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
                    Logger.error(getClass(), "Couldn't create new DRAFT revision for "+variableData.getKey().toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLE_DRAFT);
                }
                variableData = dataPair.getRight();
            }

            if(!AuthenticationUtil.isHandler(variableData)) {
                variableData.setHandler(AuthenticationUtil.getUserName());
                revisions.updateRevisionData(variableData);
            }

            // Merge variable to variable revision
            ParseResult mergeResult = parser.mergeToData(variableData, variable);

            if(mergeResult == ParseResult.REVISION_CHANGES) {
                ReturnResult updateResult = revisions.updateRevisionData(variableData);
                if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                    Logger.error(getClass(), "Could not update revision data for "+variableData.toString()+" with result "+updateResult);
                }
            }
            counter++;
            long end = System.currentTimeMillis()-start;
            Logger.debug(getClass(), "Parsed variable in "+end+"ms. Still "+(listOfEntitiesAndHolders.size()-counter)+" variables to parse.");
            timeSpent += end;
        }

        Logger.debug(getClass(), "Parsed variables in "+timeSpent+"ms");
        return result;
    }

    /*
    * Defult language variables parsing.
    * This should keep the order of variables in the por-file
    * It should not remove variables that don't contain a default language since those have been brought from separate translation-por
    * It should remove variables that contain default language but are not present on the current por-file
    */
    /*private ParseResult variablesParsingDefault(ParseResult result, VariableParser parser) {
        Logger.debug(getClass(), "Gathering entities for parsing in language "+language);
        long start = System.currentTimeMillis();

        // List variables included in this file and their revision counterparts.
        // This tells us which variables are already present, which need to be created and which might need to be removed
        Pair<StatusCode, ReferenceContainerDataField> variablesCont = variablesData.dataField(ReferenceContainerDataFieldCall.set(Fields.VARIABLES).setInfo(info));
        List<ReferenceRow> variableRows = gatherAndClear(variablesCont.getRight());
        List<Pair<RevisionData, PORUtil.PORVariableHolder>> listOfEntitiesAndHolders = new ArrayList<>();
        for(PORUtil.PORVariableHolder variable : variables) {
            RevisionData variableRevision = null;
            for(Iterator<ReferenceRow> i = variableRows.iterator(); i.hasNext(); ) {
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
        Logger.debug(getClass(), "Entities gathered. Took "+(System.currentTimeMillis()-start)+"ms");

        // Perform removal of variables that have default language but are missing from the current por-file
        ContainerDataField variableGroups = variablesData.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();
        for(RevisionData variableRevision : variableRevisions) {
            // All remaining rows in variableEntities should be checked for removal since no variable was found for them in the current POR-file

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

        // listOfEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable
        ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(listOfEntitiesAndHolders.size() > 0 && variablesContainer == null) {
            Pair<StatusCode, ReferenceContainerDataField> containerPair = variablesData.dataField(ReferenceContainerDataFieldCall.set("variables"));
            result = checkResultForUpdate(containerPair, result);
            variablesContainer = containerPair.getRight();
        }

        if(variablesContainer == null) {
            Logger.error(getClass(), "Missing variables container even though it should be present or created");
            return resultCheck(result, ParseResult.NO_VARIABLES_CONTAINER);
        }

        Pair<StatusCode, ValueDataField> studyField = variablesData.dataField(ValueDataFieldCall.get("study"));
        Logger.debug(getClass(), listOfEntitiesAndHolders.size()+" variables to parse.");
        int counter = 0;
        long timeSpent = 0L;
        for(Pair<RevisionData, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            start = System.currentTimeMillis();

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
                    Logger.error(getClass(), "Couldn't create new variable revisionable for study "+studyField.getRight().getActualValueFor(Language.DEFAULT)+" and variables "+variablesData.toString());
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
                    Logger.error(getClass(), "Couldn't create new DRAFT revision for "+variableData.getKey().toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLE_DRAFT);
                }
                variableData = dataPair.getRight();
            }

            if(!AuthenticationUtil.isHandler(variableData)) {
                variableData.setHandler(AuthenticationUtil.getUserName());
                revisions.updateRevisionData(variableData);
            }

            // Merge variable to variable revision
            ParseResult mergeResult = parser.mergeToData(variableData, variable);

            if(mergeResult == ParseResult.REVISION_CHANGES) {
                ReturnResult updateResult = revisions.updateRevisionData(variableData);
                if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                    Logger.error(getClass(), "Could not update revision data for "+variableData.toString()+" with result "+updateResult);
                }
            }
            counter++;
            long end = System.currentTimeMillis()-start;
            Logger.debug(getClass(), "Parsed variable in "+end+"ms. Still "+(listOfEntitiesAndHolders.size()-counter)+" variables to parse.");
            timeSpent += end;
        }

        Logger.debug(getClass(), "Parsed variables in "+timeSpent+"ms");
        return result;
    }*/

    /*
    * Non default language variables parsing
    * Should remove only variables that have no other languages attached and are missing from current por-file
    * Variables that are not yet present (i.e. variables that are only in translation files) are added to the end of the list
    */
    /*private ParseResult variablesParsingNonDefault(ParseResult result, VariableParser parser) {
        Logger.debug(getClass(), "Gathering entities for parsing");
        long start = System.currentTimeMillis();
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
        Logger.debug(getClass(), "Entities gathered. Took "+(System.currentTimeMillis()-start)+"ms");

        // Perform removal of variables that have only the current language in them but are missing from this file.
        ContainerDataField variableGroups = variablesData.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS)).getRight();
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

        // listOfEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable
        ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(listOfEntitiesAndHolders.size() > 0 && variablesContainer == null) {
            Pair<StatusCode, ReferenceContainerDataField> containerPair = variablesData.dataField(ReferenceContainerDataFieldCall.set("variables"));
            result = checkResultForUpdate(containerPair, result);
            variablesContainer = containerPair.getRight();
        }

        if(variablesContainer == null) {
            Logger.error(getClass(), "Missing variables container even though it should be present or created");
            return resultCheck(result, ParseResult.NO_VARIABLES_CONTAINER);
        }

        Pair<StatusCode, ValueDataField> studyField = variablesData.dataField(ValueDataFieldCall.get("study"));
        Logger.debug(getClass(), listOfEntitiesAndHolders.size()+" variables to parse.");
        int counter = 0;
        long timeSpent = 0L;
        for(Pair<RevisionData, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            start = System.currentTimeMillis();

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
                    Logger.error(getClass(), "Couldn't create new variable revisionable for study "+studyField.getRight().getActualValueFor(Language.DEFAULT)+" and variables "+variablesData.toString());
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
                    Logger.error(getClass(), "Couldn't create new DRAFT revision for "+variableData.getKey().toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLE_DRAFT);
                }
                variableData = dataPair.getRight();
            }

            if(!AuthenticationUtil.isHandler(variableData)) {
                variableData.setHandler(AuthenticationUtil.getUserName());
                revisions.updateRevisionData(variableData);
            }

            // Merge variable to variable revision
            ParseResult mergeResult = parser.mergeToData(variableData, variable);

            if(mergeResult == ParseResult.REVISION_CHANGES) {
                ReturnResult updateResult = revisions.updateRevisionData(variableData);
                if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                    Logger.error(getClass(), "Could not update revision data for "+variableData.toString()+" with result "+updateResult);
                }
            }
            counter++;
            long end = System.currentTimeMillis()-start;
            Logger.debug(getClass(), "Parsed variable in "+end+"ms. Still "+(listOfEntitiesAndHolders.size()-counter)+" variables to parse.");
            timeSpent += end;
        }

        Logger.debug(getClass(), "Parsed variables in "+timeSpent+"ms");
        return result;
    }*/

    private static List<ReferenceRow> gatherAndClear(ReferenceContainerDataField field) {
        if(field.getReferences() == null) {
            return new ArrayList<>();
        }
        List<ReferenceRow> rows = new ArrayList<>(field.getReferences());
        field.getReferences().clear();
        return rows;
    }
}
