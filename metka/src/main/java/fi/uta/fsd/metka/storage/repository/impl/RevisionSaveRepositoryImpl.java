package fi.uta.fsd.metka.storage.repository.impl;


import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionSaveRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Repository
public class RevisionSaveRepositoryImpl implements RevisionSaveRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionSaveRepositoryImpl.class);

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private StudyVariablesParser parser;

    @Autowired
    private ReferenceService references;

    @Override
    public Pair<ReturnResult, TransferData> saveRevision(TransferData transferData) {
        Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionDataOfType(transferData.getKey().getId(), transferData.getKey().getNo(), transferData.getConfiguration().getType());
        if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Couldn't find Revision "+transferData.getKey().toString()+" while saving.");
            return new ImmutablePair<>(revisionPair.getLeft(), transferData);
        }

        RevisionData revision = revisionPair.getRight();
        if(revision.getState() != RevisionState.DRAFT) {
            logger.warn("Revision "+revision.toString()+" was not in DRAFT state when tried to initiate save");
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_A_DRAFT, transferData);
        }

        if(!revision.getHandler().equals(AuthenticationUtil.getUserName())) {
            logger.warn("User "+AuthenticationUtil.getUserName()+" tried to save revision belonging to "+revision.getHandler());
            return new ImmutablePair<>(ReturnResult.WRONG_USER, transferData);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Couldn't find configuration "+revision.getConfiguration().toString()+" while saving "+revision.toString());
            return new ImmutablePair<>(configPair.getLeft(), transferData);
        }

        Configuration configuration = configPair.getRight();

        // Create DateTime object for updating values
        DateTimeUserPair info = DateTimeUserPair.build();

        // Do actual change checking for field values
        SaveHandler handler = new SaveHandler(info, revision.getKey());
        MutablePair<Boolean, Boolean> changesAndErrors = handler.saveFields(configuration, transferData, revision);

        finalizeSave(revision, transferData, configuration, info, changesAndErrors);

        if(changesAndErrors.getLeft()) {
            // Set revision save info
            revision.setSaved(info);

            ReturnResult result = revisions.updateRevisionData(revision);
            if(result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, transferData);
            } else {
                // Set transfer object save info since database values have changed
                transferData.getState().setSaved(info);
                return new ImmutablePair<>((changesAndErrors.getRight() ? ReturnResult.SAVE_SUCCESSFUL_WITH_ERRORS : ReturnResult.SAVE_SUCCESSFUL), transferData);
            }
        } else {
            return new ImmutablePair<>(ReturnResult.NO_CHANGES_TO_SAVE, transferData);
        }
    }

    /**
     * Do type specific save operations if any
     * @param revision RevisionData to finalize
     * @param transferData TransferData sent to request and that will be returned to UI
     * @param configuration Configuration of revision data
     */
    private void finalizeSave(RevisionData revision, TransferData transferData, Configuration configuration, DateTimeUserPair info, MutablePair<Boolean, Boolean> changesAndErrors) {
        switch(revision.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
                finalizeStudyAttachment(revision, transferData);
                break;
            case STUDY:
                finalizeStudy(revision, transferData, info, changesAndErrors);

                break;
            default:
                break;
        }
    }

    private void finalizeStudy(RevisionData revision, TransferData transferData, DateTimeUserPair info, MutablePair<Boolean, Boolean> changesAndErrors) {
        StudyFactory fac = new StudyFactory();

        // Form packages and biblcit
        ReturnResult result = fac.formUrnAndBiblCit(revision, info, references, changesAndErrors);

        TransferField f = transferData.getField(Fields.BIBLCIT);
        Pair<StatusCode, ValueDataField> pair = revision.dataField(ValueDataFieldCall.get(Fields.BIBLCIT));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            if(f != null) {
                f.getValues().clear();
            }
        } else {
            ValueDataField field = pair.getRight();
            f = TransferField.buildFromDataField(field);
            transferData.getFields().put(f.getKey(), f);
        }

        f = transferData.getField(Fields.PACKAGES);
        Pair<StatusCode, ContainerDataField> packages = revision.dataField(ContainerDataFieldCall.get(Fields.PACKAGES));
        if(packages.getLeft() != StatusCode.FIELD_FOUND || !packages.getRight().hasRowsFor(Language.DEFAULT)) {
            if(f != null) {
                f.getRowsFor(Language.DEFAULT).clear();
            }
        } else {
            f = TransferField.buildFromDataField(packages.getRight());
            transferData.getFields().put(f.getKey(), f);
        }
    }

    /**
     * If this study attachment is not marked parsed and has a path then check if that path could be a por file that needs parsing.
     * If so then parse that por file and mark this study attachment as parsed.
     * @param attachment         RevisionData
     * @param transferData     TransferData
     */
    private void finalizeStudyAttachment(RevisionData attachment, TransferData transferData) {
        // Is attachment already parsed
        if (attachmentAlreadyParsed(attachment)) return;

        if (hasAndIsOriginal(attachment)) return;

        Pair<StatusCode, ValueDataField> fieldPair = attachment.dataField(ValueDataFieldCall.get("file"));
        if (!hasFile(fieldPair)) return;

        String path = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
        if (!fileIsVarFile(path)) {
            // We can mark attachment as parsed since if it's not a var file we don't need to regard it again in the future
            attachment.dataField(ValueDataFieldCall.set("parsed", new Value("true"), Language.DEFAULT));
            return;
        }

        // Get study linked to this attachment
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
                attachment.dataField(ValueDataFieldCall.get("study")).getRight().getValueFor(Language.DEFAULT).valueAsInteger(),
                false,
                ConfigurationType.STUDY);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return;
        }
        RevisionData study = dataPair.getRight();

        String fileName = FilenameUtils.getBaseName(path).toUpperCase();
        String lastChar = fileName.substring(fileName.length()-1);
        Language varLang;
        if(lastChar.equals("E") || lastChar.equals("S")) {
            varLang = lastChar.equals("E") ? Language.EN : Language.SV;
            // We have a translation file. Require that default language is already attached
            if(!hasVariablesFileFor(Language.DEFAULT, study)) {
                return;
            }
            if(hasVariablesFileFor(varLang, study)) {
                // If there already are variables attached for the language in the study check that it's the same attachment
                if(!variablesFileForEqual(varLang, attachment.getKey().getId(), study)) {
                    // TODO: Should we mark this as parsed since it shouldn't be used anymore?
                    return;
                }
            }
        } else {
            varLang = Language.DEFAULT;
            // We're parsing the default var file. If there already is a default file then check that it matches this
            if(hasVariablesFileFor(Language.DEFAULT, study)) {
                if(!variablesFileForEqual(Language.DEFAULT, attachment.getKey().getId(), study)) {
                    // TODO: Should we mark this as parsed since it shouldn't be used anymore?
                    return;
                }
            }
        }

        parseVariableFile(attachment, transferData, study, varLang);
    }

    private boolean hasVariablesFileFor(Language language, RevisionData study) {
        Pair<StatusCode, ValueDataField> fieldPair = study.dataField(ValueDataFieldCall.get("variablefile"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(language)) {
            return false;
        }

        return true;
    }

    private boolean variablesFileForEqual(Language language, Long value, RevisionData study) {
        Pair<StatusCode, ValueDataField> fieldPair = study.dataField(ValueDataFieldCall.get("variablefile"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            return false;
        }

        return fieldPair.getRight().valueForEquals(language, value.toString());
    }

    private boolean fileIsVarFile(String path) {
        // Does the file name start with DAF
        String fileName = FilenameUtils.getBaseName(path).toUpperCase();
        if(!fileName.substring(0, 3).equals("DAF")) {
            return false;
        }

        // Does the file extension define a variable file (i.e. POR)
        String extension = FilenameUtils.getExtension(path).toUpperCase();
        if(!extension.equals("POR")) {
            // For now the only option. If more variable file types are added then this needs to be changed to a switch case
            return false;
        }
        return true;
    }

    private boolean hasFile(Pair<StatusCode, ValueDataField> fieldPair) {
        // Does attachment have defined file path
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // We have no file path, no need to continue
            return false;
        }

        // Does the defined file path exist and does it point to a file
        File file = new File(fieldPair.getRight().getActualValueFor(Language.DEFAULT));
        if(!file.exists() || !file.isFile()) {
            return false;
        }

        return true;
    }

    private boolean hasAndIsOriginal(RevisionData revision) {
        // Does the attachment have value in "original" selection
        Pair<StatusCode, ValueDataField> orig = revision.dataField(ValueDataFieldCall.get("fileoriginal"));
        if(orig.getLeft() != StatusCode.FIELD_FOUND || !orig.getRight().hasValueFor(Language.DEFAULT)) {
            return true;
        }
        // Is the attachment marked as "original" file
        if(orig.getRight().getActualValueFor(Language.DEFAULT).equals("1")) {
            // TODO: If this value has changed from some other value to "1" and this attachment has been the variable file then remove all variables.
            return true;
        }
        return false;
    }

    private boolean attachmentAlreadyParsed(RevisionData revision) {
        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("parsed"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            // What to do if we failed to find the field for some other reason than it was missing?
            return false;
        }

        return fieldPair.getRight().hasValueFor(Language.DEFAULT) && fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsBoolean();
    }

    private void parseVariableFile(RevisionData attachment, TransferData transferData, RevisionData study, Language language) {
        ParseResult result = parser.parse(attachment, VariableDataType.POR, study, language);

        Pair<StatusCode, ValueDataField> fieldPair = attachment.dataField(ValueDataFieldCall.set("parsed", new Value("true"), Language.DEFAULT));
        TransferField parsed = transferData.getField("parsed");
        if(parsed == null) {
            parsed = new TransferField("parsed", TransferFieldType.VALUE);
            transferData.addField(parsed);
        }
        parsed.addValueFor(Language.DEFAULT, TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, fieldPair.getRight()));
        if(result == ParseResult.REVISION_CHANGES) {
            revisions.updateRevisionData(study);
        }
    }


    private class SaveHandler {
        private final DateTimeUserPair info;
        private final RevisionKey key;

        /**
         * Contains list of bidirectional references that need to be verified to be up to date.
         * Long|(String|Boolean) Where Long is the revisionable id of the target revisionable,
         * String is the field key of the bidirectional field and Boolean is either true if the value
         * should exist or false if the value should not exist (i.e. the corresponding row was removed),
         */
        private final List<ImmutablePair<Long, ImmutablePair<String, Boolean>>> bidirectional = new ArrayList<>();

        private SaveHandler(DateTimeUserPair info, RevisionKey key) {
            this.info = info;
            this.key = key;
        }

        private MutablePair<Boolean, Boolean> saveFields(Configuration configuration, TransferData transferData, RevisionData revisionData) {
            MutablePair<Boolean, Boolean> result = new MutablePair<>(false, false);
            // Loop through all fields, skip fields that are irrelevant or checked later and call correct methods to save DataFields

            for (Field field : configuration.getFields().values()) {
                if (!field.getWritable()) {
                    // Field is not writable, don't check for changes and don't add to RevisionData
                    continue;
                } else if (field.getSubfield()) {
                    // No need to process further, subfields are handled when their containers are processed
                    continue;
                }
                Pair<StatusCode, Boolean> saveResult = saveField(field, configuration, transferData, revisionData, revisionData.getChanges());
                if (saveResult.getLeft() == StatusCode.FIELD_CHANGED && !result.getLeft()) result.setLeft(true);
                if (saveResult.getRight() && !result.getRight()) result.setRight(true);
            }

            // Check bidirectional fields. This requires some custom code in certain cases.

            // First lets sort the List so that we're going to handle one revisionable at a time
            Collections.sort(bidirectional, new Comparator<ImmutablePair<Long, ImmutablePair<String, Boolean>>>() {
                @Override
                public int compare(ImmutablePair<Long, ImmutablePair<String, Boolean>> o1, ImmutablePair<Long, ImmutablePair<String, Boolean>> o2) {
                    int result = o1.getLeft().compareTo(o2.getLeft());
                    if(result == 0) {
                        result = o1.getRight().getLeft().compareTo(o2.getRight().getLeft());
                    }
                    return result;
                }
            });

            // Next let's iterate through one revisionable at a time
            List<Pair<String, Boolean>> revisionablePairs = new ArrayList<>();
            Long revisionableId = null;
            for(Pair<Long, ? extends Pair<String, Boolean>> pair : bidirectional) {
                if(pair.getLeft().equals(revisionableId)) {
                    revisionablePairs.add(pair.getRight());
                    continue;
                }
                if(revisionableId != null) {
                    // Do check
                    checkBidirectionality(revisionableId, revisionablePairs, info);

                    // Reset loop
                    revisionableId = pair.getLeft();
                    revisionablePairs.clear();
                } else {
                    revisionableId = pair.getLeft();
                    revisionablePairs.add(pair.getRight());
                }
            }

            return result;
        }

        private void checkBidirectionality(Long id, List<Pair<String, Boolean>> pairs, DateTimeUserPair info) {
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(id, false, null);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                // Something is wrong
                logger.error("Tried to force bidirectionality for a revisionable that is nonexistent");
                return;
            }
            RevisionData data = dataPair.getRight();
            Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                logger.error("Couldn't find configuration for revision "+data.toString()+" while checking bidirectional values.");
                return;
            }
            Configuration config = configPair.getRight();
            // For now assumes that strings are all top level field keys.
            boolean changes = false;
            for(Pair<String, Boolean> pair : pairs) {
                Pair<StatusCode, ReferenceContainerDataField> fieldPair = data.dataField(ReferenceContainerDataFieldCall.set(pair.getLeft()));
                // let's make sure we have a field
                if(fieldPair.getRight() == null) {
                    logger.error("Failed to create ReferenceContainerDataField while forcing bidirectionality with result "+fieldPair.getLeft());
                    continue;
                }
                ReferenceContainerDataField field = fieldPair.getRight();
                if(pair.getRight()) {
                    // Make sure we have the reference
                    Pair<StatusCode, ReferenceRow> rowPair = field.getOrCreateReferenceWithValue(key.getId().toString(), data.getChanges(), info);
                    if(rowPair.getLeft() == StatusCode.NEW_ROW) {
                        changes = true;
                    }
                } else {
                    // Remove the reference if it exists
                    Pair<StatusCode, ReferenceRow> rowPair = field.getReferenceWithValue(key.getId().toString());
                    if(rowPair.getLeft() == StatusCode.FOUND_ROW) {
                        field.removeReference(rowPair.getRight().getRowId(), data.getChanges(), info);
                    }
                    if(rowPair.getLeft() == StatusCode.NEW_ROW) {
                        changes = true;
                    }
                }
            }
            if(changes) {
                doTypeSpecificBidirectionality(data, pairs, info);

                revisions.updateRevisionData(data);
            }
        }

        /**
         * Do things that are part of bidirectionality on a type specific level.
         * @param data     RevisionData
         * @param pairs    Pair
         * @param info     DateTimeUserPair
         */
        private void doTypeSpecificBidirectionality(RevisionData data, List<Pair<String, Boolean>> pairs, DateTimeUserPair info) {
            switch(data.getConfiguration().getType()) {
                default:
                    return;
                case STUDY:
                    return;
            }
        }

        private Pair<StatusCode, Boolean> saveField(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap) {
            Pair<StatusCode, Boolean> returnPair;
            if (field.getType() == FieldType.CONTAINER) {
                returnPair = saveContainer(field, configuration, transferFields, dataFields, changeMap);
            } else if (field.getType() == FieldType.REFERENCECONTAINER) {
                returnPair = saveReferenceContainer(field, configuration, transferFields, dataFields, changeMap);
            } else {
                returnPair = saveValue(field, configuration, transferFields, dataFields, changeMap);
            }
            return returnPair;
        }

        /**
         * Saves changes to single ContainerDataField in provided DataFieldContainer from single CONTAINER type TransferField in provided TransferFieldContainer.
         * This can lead to recursive calls if needed.
         *
         * @param field          Field configuration of the field to be saved
         * @param configuration  Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
         * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
         * @param dataFields     DataFieldContainer that should contain the ContainerDataField described by Field configuration
         * @param changeMap      Map of changes that should contain changes for field being checked
         * @return Pair statusAndErrors pair. Left value indicates the returned status of the final operation performed, right value indicates that errors were marked somewhere within the TransferFields
         */
        private Pair<StatusCode, Boolean> saveContainer(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap) {
            TransferField tf = transferFields.getField(field.getKey());
            if (tf != null && tf.getType() != TransferFieldType.CONTAINER) {
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
            }
            Pair<StatusCode, ContainerDataField> codePair = dataFields.dataField(ContainerDataFieldCall.get(field.getKey()).setConfiguration(configuration));
            if (codePair.getLeft() == StatusCode.FIELD_FOUND && tf == null) {
                // Existing container is missing from TransferFields, client should never remove whole containers after they are created
                // Add container and return
                tf = TransferField.buildFromDataField(codePair.getRight());
                tf.addError(FieldError.MISSING_VALUE);
                transferFields.addField(tf);
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
            } else if (codePair.getLeft() == StatusCode.FIELD_MISSING && tf == null) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
            }
            if (codePair.getLeft() != StatusCode.FIELD_FOUND && (tf == null || !tf.hasRows())) {
                return new ImmutablePair<>(codePair.getLeft(), false);
            }
            if (codePair.getLeft() == StatusCode.FIELD_FOUND && codePair.getRight().hasRows() && !tf.hasRows()) {
                // Rows should never be removed like this but instead by marking them as removed
                // Mark error to transfer field and do no changes
                for (Language language : Language.values()) {
                    if (!field.getTranslatable() && language != Language.DEFAULT) {
                        continue;
                    }
                    if (!codePair.getRight().hasRowsFor(language)) {
                        continue;
                    }
                    for (DataRow dataRow : codePair.getRight().getRowsFor(language)) {
                        TransferRow tr = TransferRow.buildFromContainerRow(dataRow);
                        tr.addError(FieldError.MISSING_ROWS);
                        tf.addRowFor(language, tr);
                    }
                }
                tf.addError(FieldError.MISSING_ROWS);

                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, true);
            }

            if (codePair.getLeft() == StatusCode.FIELD_MISSING) {
                codePair = dataFields.dataField(ContainerDataFieldCall.set(field.getKey()).setConfiguration(configuration).setChangeMap(changeMap));
                if (codePair.getLeft() != StatusCode.FIELD_INSERT) {
                    return new ImmutablePair<>(codePair.getLeft(), false);
                }
            }

            // Return value tracker for individual rows
            MutablePair<StatusCode, Boolean> returnPair = new MutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
            ContainerDataField container = codePair.getRight();
            // Check that all rows that exist are present
            for (Language language : Language.values()) {
                if (!field.getTranslatable() && language != Language.DEFAULT) {
                    // If field is not translatable check that no other language has rows or if rows are present then clean them out of both
                    // ContainerDataField and TransferField
                    if (container.hasRowsFor(language)) {
                        container.getRows().put(language, null);
                    }
                    if (tf.hasRowsFor(language)) {
                        tf.addError(FieldError.NOT_TRANSLATABLE);
                        tf.getRows().put(language, null);
                        returnPair.setRight(true);
                    }
                }
                if (!container.hasRowsFor(language)) {
                    continue;
                }
                for (int i = 0, length = container.getRowsFor(language).size(); i < length; i++) {
                    DataRow row = container.getRowsFor(language).get(i);
                    boolean found = false;
                    if (tf.hasRowsFor(language)) {
                        for (TransferRow tr : tf.getRowsFor(language)) {
                            if (row.getRowId().equals(tr.getRowId())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        // There is an existing row missing from TransferField, mark error and return value.
                        // Add missing row to TransferField so that it's returned to UI
                        TransferRow tr = TransferRow.buildFromContainerRow(row);
                        tr.addError(FieldError.MISSING_ROWS);
                        returnPair.setRight(true);
                        // For now add missing row to the index where it is in the container.
                        // This might cause problems if order is changed when server tries to add new rows also
                        tf.getRowsFor(language).add(i, tr);
                    }
                }
            }

            if (!field.getTranslatable()) {
                saveContainerRowsFor(Language.DEFAULT, returnPair, field, configuration, tf, container, changeMap);
            } else {
                for (Language language : Language.values()) {
                    saveContainerRowsFor(language, returnPair, field, configuration, tf, container, changeMap);
                }
            }
            return returnPair;
        }

        private void saveContainerRowsFor(Language language, MutablePair<StatusCode, Boolean> returnPair, Field field,
                                          Configuration configuration, TransferField transferField, ContainerDataField container,
                                          Map<String, Change> changeMap) {

            boolean changes = false;

            ContainerChange containerChange = (ContainerChange) changeMap.get(field.getKey());
            // Create missing container change but do not insert it yet
            if (containerChange == null) {
                containerChange = new ContainerChange(field.getKey());
            }

            // TODO: Reorder the rows to the UI provided order assuming the container can be reordered

            // Regardless of if there are missing rows we are going to save the changes in existing and new rows
            // but no rows are going to be deleted because of them missing from TransferField
            if (transferField.hasRowsFor(language)) {
                for (TransferRow tr : transferField.getRowsFor(language)) {
                    DataRow row = null;
                    // We have no mechanism on UI at the moment for restoring rows only check change from not removed to removed
                    if(tr.getRemoved() && tr.getRowId() == null) {
                        // Row was added and then removed before any saving was done, we can skip this row.
                        continue;
                    } else if (tr.getRemoved()) {
                        // Removed value of a row was changed
                        Pair<StatusCode, DataRow> removePair = container.removeRow(tr.getRowId(), changeMap, info);
                        if(removePair.getLeft() != StatusCode.NO_CHANGE_IN_VALUE) {
                            if(removePair.getLeft() == StatusCode.ROW_CHANGE) {
                                // Row was changed but not removed completely, continue on but mark as changed
                                changes = true;
                            } else if(removePair.getLeft() == StatusCode.ROW_REMOVED) {
                                // Row was removed completely, no need to continue on with saving
                                changes = true;
                                continue;
                            }
                        }
                    }
                    if (tr.getRowId() == null && tr.getFields().size() > 0) {
                        // New row that has some fields in it. Create row in preparation for saving
                        Pair<StatusCode, DataRow> rowPair = container.insertNewDataRow(language, containerChange);
                        if (rowPair.getLeft() == StatusCode.NEW_ROW) {
                            row = rowPair.getRight();
                            row.setSaved(info);
                            changes = true;
                            tr.setRowId(row.getRowId());
                        }
                    } else {
                        // Old row, check the "removed" value.
                        Pair<StatusCode, DataRow> rowPair = container.getRowWithId(tr.getRowId());
                        if (rowPair.getLeft() != StatusCode.FOUND_ROW) {
                            tr.addError(FieldError.ROW_NOT_FOUND);
                            returnPair.setRight(true);
                            continue;
                        }
                        row = rowPair.getRight();
                    }
                    if (row != null) {
                        // Row was found, go through the subfields and forward calls to field saving
                        for (String subkey : field.getSubfields()) {
                            Field subfield = configuration.getField(subkey);
                            if (subfield == null) {
                                logger.error("Didn't find field " + subkey + " in configuration " + configuration.getKey().toString() + " even though " + field.getKey() + " has it as subfield.");
                                continue;
                            }
                            RowChange rowChange = containerChange.get(row.getRowId());
                            if (rowChange == null) {
                                rowChange = new RowChange(row.getRowId());
                            }

                            // Call save field for the subfield, this can cause recursion
                            Pair<StatusCode, Boolean> fieldSaveResult = saveField(subfield, configuration, tr, row, changeMap);

                            if (fieldSaveResult.getRight()) {
                                returnPair.setRight(true);
                            }
                            if (fieldSaveResult.getLeft() == StatusCode.FIELD_CHANGED) {
                                // Make sure that row change is in the container change since there has been an actual change.
                                containerChange.put(rowChange);
                                row.setSaved(info);
                                tr.setSaved(info);
                                changes = true;
                            }
                        }
                    }
                }

                cleanExtraRowsFromTransferField(language, transferField, container);

                if(!field.getFixedOrder()) {
                    List<TransferRow> trs = transferField.getRowsFor(language);
                    List<DataRow> rows = container.getRowsFor(language);
                    if(trs.size() == rows.size()) {
                        // Order rows in transferfield order
                        for(int i = 0; i < trs.size(); i++) {
                            if(!trs.get(i).getRowId().equals(rows.get(i).getRowId())) {
                                for(int j = i+1; j < trs.size(); j++) {
                                    if(rows.get(j).getRowId().equals(trs.get(i).getRowId())) {
                                        swap(rows, i, j);
                                        changes = true;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        logger.error("TransferField and Container with key "+field.getKey()+" have different number of rows for language "+language);
                    }

                }
            }


            if (changes) {
                changeMap.put(containerChange.getKey(), containerChange);
                returnPair.setLeft(StatusCode.FIELD_CHANGED);
            }
        }

        private <T> void swap(List<T> list, int a, int b) {
            T o = list.get(a);
            list.set(a, list.get(b));
            list.set(b, o);
        }

        /**
         * Saves changes to single ReferenceContainerDataField in provided DataFieldContainer from single REFERENCECONTAINER type TransferField in provided TransferFieldContainer.
         *
         * @param field          Field configuration of the field to be saved
         * @param configuration  Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCE etc.
         * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
         * @param dataFields     DataFieldContainer that should contain the ReferenceContainerDataField described by Field configuration
         * @param changeMap      Map of changes that should contain changes for field being checked
         * @return StatusCode|Boolean statusAndErrors pair. Left value indicates the returned status of the final operation performed, right value indicates that errors were marked somewhere within the TransferFields
         */
        private Pair<StatusCode, Boolean> saveReferenceContainer(Field field, Configuration configuration, TransferFieldContainer transferFields,
                                                                 DataFieldContainer dataFields, Map<String, Change> changeMap) {
            // With reference saving we can take a shortcut and just use DEFAULT as language every time since references are not translated
            TransferField tf = transferFields.getField(field.getKey());
            if (tf != null && tf.getType() != TransferFieldType.REFERENCECONTAINER) {
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
            }
            Pair<StatusCode, ReferenceContainerDataField> codePair = dataFields.dataField(ReferenceContainerDataFieldCall.get(field.getKey()).setConfiguration(configuration));
            if (codePair.getLeft() == StatusCode.FIELD_FOUND && tf == null) {
                // Existing reference container is missing from TransferFields, client should never remove whole containers after they are created
                // Add missing container
                tf = TransferField.buildFromDataField(codePair.getRight());
                transferFields.addField(tf);
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
            } else if (codePair.getLeft() == StatusCode.FIELD_MISSING && tf == null) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
            }
            if (codePair.getLeft() != StatusCode.FIELD_FOUND && (tf == null || !tf.hasRowsFor(Language.DEFAULT))) {
                return new ImmutablePair<>(codePair.getLeft(), false);
            }
            if (codePair.getLeft() == StatusCode.FIELD_FOUND && !codePair.getRight().getReferences().isEmpty() && !tf.hasRowsFor(Language.DEFAULT)) {
                // Rows should never be removed like this but instead by marking them as removed
                // Mark error to transfer field and do no changes
                for (ReferenceRow reference : codePair.getRight().getReferences()) {
                    // There is an existing row missing from TransferField, mark error and return value
                    TransferRow tr = TransferRow.buildFromContainerRow(reference);
                    tr.addError(FieldError.MISSING_ROWS);
                    tf.addRowFor(Language.DEFAULT, tr);
                }
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, true);
            }

            if (codePair.getLeft() == StatusCode.FIELD_MISSING) {
                codePair = dataFields.dataField(ReferenceContainerDataFieldCall.set(field.getKey()).setConfiguration(configuration).setChangeMap(changeMap));
                if (codePair.getLeft() != StatusCode.FIELD_INSERT) {
                    return new ImmutablePair<>(codePair.getLeft(), false);
                }
            }
            // Return value tracker for individual rows
            MutablePair<StatusCode, Boolean> returnPair = new MutablePair<>(StatusCode.FIELD_UPDATE, false);
            boolean changes = false;
            ReferenceContainerDataField container = codePair.getRight();

            // Check that all rows that exist are present
            for (int i = 0; i < container.getReferences().size(); i++) {
                ReferenceRow reference = container.getReferences().get(0);
                if (tf.getRow(reference.getRowId()) == null) {
                    // There is an existing row missing from TransferField, mark error and return value
                    // Adds the missing row to the index location it has on database version of the data
                    // Can mess with moving of rows since UI didn't know about the row before
                    TransferRow tr = TransferRow.buildFromContainerRow(reference);
                    tr.addError(FieldError.MISSING_ROWS);
                    tf.getRowsFor(Language.DEFAULT).add(i, tr);
                    returnPair.setRight(true);
                }
            }
            // Regardless of if there are missing rows we are going to save the changes in existing and new rows
            // but no rows are going to be deleted because of them missing from TransferField
            Reference ref = configuration.getReference(field.getReference());
            if (tf.hasRowsFor(Language.DEFAULT)) {
                for (TransferRow tr : tf.getRowsFor(Language.DEFAULT)) {
                    // We have no mechanism on UI at the moment for restoring rows only check change from not removed to removed
                    if(tr.getRemoved() && tr.getRowId() == null) {
                        // Row was added and then removed before any saving was done, we can skip this row.
                        continue;
                    } else if (tr.getRemoved()) {
                        // Removed value of a row was changed
                        Pair<StatusCode, ReferenceRow> removePair = container.removeReference(tr.getRowId(), changeMap, info);
                        if(removePair.getLeft() == StatusCode.ROW_CHANGE || removePair.getLeft() == StatusCode.ROW_REMOVED) {
                            if(removePair.getLeft() == StatusCode.ROW_CHANGE) {
                                // Row was changed but not removed completely, continue on but mark as changed
                                changes = true;
                            } else {
                                // Row was removed completely, no need to continue on with saving
                                changes = true;
                                continue;
                            }
                            if(ref.getType() == ReferenceType.REVISIONABLE && StringUtils.hasText(field.getBidirectional())) {
                                bidirectional.add(new ImmutablePair<>(removePair.getRight().getReference().asInteger(),
                                        new ImmutablePair<>(field.getBidirectional(), false)));
                            }
                        }
                    }
                    if (!StringUtils.hasText(tr.getValue())) {
                        // There should always be a value on existing row. If value is missing add error, set return value and skip this row.
                        // Saved reference value is immutable so nothing gets deleted through setting the value to null.
                        tr.addError(FieldError.MISSING_VALUE);
                        returnPair.setRight(true);
                    } else if (tr.getRowId() == null) {
                        // New row, insert new SavedReference
                        Pair<StatusCode, ReferenceRow> referencePair = container.getOrCreateReferenceWithValue(tr.getValue(), changeMap, info);
                        if (referencePair.getLeft() == StatusCode.NEW_ROW) {
                            referencePair.getRight().setUnapproved(true);
                            changes = true;
                            tr.setRowId(referencePair.getRight().getRowId());
                        }
                        if(ref.getType() == ReferenceType.REVISIONABLE && StringUtils.hasText(field.getBidirectional())) {
                                bidirectional.add(new ImmutablePair<>(referencePair.getRight().getReference().asInteger(),
                                        new ImmutablePair<>(field.getBidirectional(), false)));
                            }
                    } else {
                        // Old row, the only thing that can change is "removed". The actual reference value on SavedReference is immutable
                        // and is locked in when the row is created
                        Pair<StatusCode, ReferenceRow> referencePair = container.getReferenceWithId(tr.getRowId());
                        if (referencePair.getLeft() != StatusCode.FOUND_ROW) {
                            tr.addError(FieldError.ROW_NOT_FOUND);
                            returnPair.setRight(true);
                            continue;
                        }
                        ReferenceRow reference = referencePair.getRight();
                        if (!reference.valueEquals(tr.getValue())) {
                            // Something tried to change the value, add error, set return value and continue
                            tr.addError(FieldError.IMMUTABLE);
                            returnPair.setRight(true);
                        }
                    }
                }
                // Clean transfer field from rows that are permanently removed
                cleanExtraRowsFromTransferField(Language.DEFAULT, tf, container);
            }

            if (changes) {
                returnPair.setLeft(StatusCode.FIELD_CHANGED);
            } else {
                returnPair.setLeft(StatusCode.NO_CHANGE_IN_VALUE);
            }
            return returnPair;
        }

        private void cleanExtraRowsFromTransferField(Language language, TransferField tf, RowContainerDataField container) {
            Set<Integer> ids = container.getRowIdsFor(language);
            if(ids.isEmpty() && tf.hasRowsFor(language)) {
                tf.getRowsFor(language).clear();
                return;
            }
            for(Iterator<TransferRow> i = tf.getRowsFor(language).iterator(); i.hasNext(); ) {
                TransferRow tr = i.next();
                if(!ids.contains(tr.getRowId())) {
                    i.remove();
                }
            }
        }

        /**
         * Saves changes to single ValueDataField in provided DataFieldContainer from single VALUE type TransferField in provided TransferFieldContainer.
         *
         * @param field          Field configuration of the field to be saved
         * @param configuration  Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
         * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
         * @param dataFields     DataFieldContainer that should contain the ValueDataField described by Field configuration
         * @param changeMap      Map of changes for data field set operation
         * @return Boolean|Boolean changesAndErrors pair. Left value indicates that changes have taken place, right value indicates that errors were marked somewhere within the TransferFields
         */
        private Pair<StatusCode, Boolean> saveValue(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap) {
            if (field.getType() == FieldType.CONCAT) {
                // Concat type fields are not saved here but instead must be handled separately after all other fields have been saved
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
            }
            TransferField tf = transferFields.getField(field.getKey());

            if (tf != null && tf.getType() != TransferFieldType.VALUE) {
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
            }

            Pair<StatusCode, ValueDataField> fieldPair = dataFields.dataField(ValueDataFieldCall.get(field.getKey()));
            if (tf == null && fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
                return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
            } else if (tf == null) {
                // Add field to transfer field container since it should be present.
                // This is the only way to fix the situation
                tf = TransferField.buildFromDataField(fieldPair.getRight());
                transferFields.addField(tf);
                return new ImmutablePair<>(StatusCode.FIELD_MISSING, false);
            }

            MutablePair<StatusCode, Boolean> returnPair = new MutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);

            if (!field.getTranslatable()) {
                // If field is not marked as translatable but contains values for other than default language then add error
                for (Language language : Language.nonDefaultLanguages()) {
                    // If field is not translatable check that no other language has values or if values are present then clean them out of both
                    // ValueDataField and TransferField
                    if ((fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(language))) {
                        fieldPair.getRight().setCurrentFor(language, null);
                        fieldPair.getRight().setOriginalFor(language, null);
                    }
                    if (tf.hasCurrentFor(language)) {
                        tf.addError(FieldError.NOT_TRANSLATABLE);
                        tf.addValueFor(language, null);
                        returnPair.setRight(true);
                    }
                }
                saveValueFor(Language.DEFAULT, returnPair, field, configuration, tf, dataFields, changeMap, transferFields);
            } else {
                for (Language language : Language.values()) {
                    saveValueFor(language, returnPair, field, configuration, tf, dataFields, changeMap, transferFields);
                }
            }

            return returnPair;
        }

        private void saveValueFor(Language language, MutablePair<StatusCode, Boolean> returnPair, Field field,
                                  Configuration configuration, TransferField transferField, DataFieldContainer dataFields,
                                  Map<String, Change> changeMap, TransferFieldContainer transferFields) {
            Value value = transferField.currentAsValueFor(language);
            FieldError typeError = value.typeCheck(field.getType());
            if(typeError != null) {
                // This should mean that there is actually a value but let's just check just in case
                TransferValue tv = transferField.getValueFor(language);
                if(tv != null) {
                    tv.addError(typeError);
                    returnPair.setRight(true);
                }
            }
            Pair<StatusCode, ValueDataField> codePair = dataFields.dataField(
                    ValueDataFieldCall
                            .check(field.getKey(), value, language)
                            .setConfiguration(configuration));
            StatusCode statusCode = codePair.getLeft();
            if (!(statusCode == StatusCode.FIELD_INSERT || statusCode == StatusCode.FIELD_UPDATE)) {
                switch (statusCode) {
                    case FIELD_NOT_MUTABLE:
                        transferField.addErrorFor(language, FieldError.IMMUTABLE);
                        returnPair.setRight(true);
                        break;
                    case FIELD_NOT_EDITABLE:
                        transferField.addErrorFor(language, FieldError.NOT_EDITABLE);
                        returnPair.setRight(true);
                        break;
                    default:
                        break;
                }
            } else {
                codePair = dataFields.dataField(
                        ValueDataFieldCall
                                .set(field.getKey(), value, language)
                                .setInfo(info)
                                .setConfiguration(configuration)
                                .setChangeMap(changeMap));
                statusCode = codePair.getLeft();

                if (statusCode == StatusCode.FIELD_INSERT || statusCode == StatusCode.FIELD_UPDATE) {
                    returnPair.setLeft(StatusCode.FIELD_CHANGED);
                }

                // If this field is a SELECTION field then check for free text value.
                // If the current value is a free text value then save the free text field, otherwise clear the free text field
                if(field.getType() == FieldType.SELECTION) {
                    SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                    // If we don't have any free text values, don't have a defined free text field or don't have a current value then there's no point in continuing
                    if(list.getFreeText().isEmpty() || !value.hasValue() || !StringUtils.hasText(list.getFreeTextKey())) {
                        return;
                    }

                    boolean saveFreeText = false;
                    for(String free : list.getFreeText()) {
                        if(free.equals(value.getValue())) {
                            saveFreeText = true;
                            break;
                        }
                    }
                    if(saveFreeText) {
                        // Let's get the free text value from the

                        TransferField tf = transferFields.getField(list.getFreeTextKey());
                        if(tf == null || tf.getValueFor(language) == null) {
                            // New value is empty so let's try and clear the value just in case
                            dataFields.dataField(ValueDataFieldCall.set(list.getFreeTextKey(), new Value(""), language)
                                .setChangeMap(changeMap).setConfiguration(configuration).setInfo(info));
                        } else {
                            // We have at least some kind of value for free text, if current value is null then nothing should happen
                            // if there's an original value so we should be ok with trying to set the value to current value in transfer value
                            dataFields.dataField(ValueDataFieldCall.set(list.getFreeTextKey(), new Value(tf.getValueFor(language).getCurrent()), language)
                                .setChangeMap(changeMap).setConfiguration(configuration).setInfo(info));
                        }

                    } else {
                        // Let's make sure the free text field is clear
                        dataFields.dataField(ValueDataFieldCall.set(list.getFreeTextKey(), new Value(""), language)
                                .setChangeMap(changeMap).setConfiguration(configuration).setInfo(info));
                    }
                }
            }
        }
    }
}
