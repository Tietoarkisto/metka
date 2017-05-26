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

package fi.uta.fsd.metka.storage.repository.impl;


import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;
import fi.uta.fsd.metka.model.transfer.*;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.FileMissingPayload;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Repository
public class RevisionSaveRepositoryImpl implements RevisionSaveRepository {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private StudyVariablesParser parser;

    @Autowired
    private ReferenceService references;

    @Autowired
    private RestrictionValidator validator;

    @Autowired
    private Cascader cascader;

    @Autowired
    private Messenger messenger;

    @Override
    public Pair<ReturnResult, TransferData> saveRevision(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(transferData.getKey().asCongregateKey());
        if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Couldn't find Revision " + transferData.getKey().toString() + " while saving.");
            return new ImmutablePair<>(revisionPair.getLeft(), transferData);
        }

        RevisionData revision = revisionPair.getRight();
        if(revision.getState() != RevisionState.DRAFT) {
            Logger.warning(getClass(), "Revision " + revision.toString() + " was not in DRAFT state when tried to initiate save");
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_A_DRAFT, transferData);
        }

        if(!AuthenticationUtil.isHandler(revision)) {
            Logger.warning(getClass(), "User " + AuthenticationUtil.getUserName() + " tried to save revision belonging to " + revision.getHandler());
            return new ImmutablePair<>(ReturnResult.WRONG_USER, transferData);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Couldn't find configuration "+revision.getConfiguration().toString()+" while saving "+revision.toString());
            return new ImmutablePair<>(configPair.getLeft(), transferData);
        }

        Configuration configuration = configPair.getRight();
        ReturnResult result = ReturnResult.OPERATION_SUCCESSFUL;
        // NOTICE: Validation for save is not really required at the moment.
        // If save validation is required at some point then uncomment this part and implement validation for transfer data
        /*
        // Do validation
        for(Operation operation : configPair.getRight().getRestrictions()) {
            if(!(operation.getType() == OperationType.SAVE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!validator.validate(transferData, operation.getTargets(), configPair.getRight())) {
                result = ReturnResult.RESTRICTION_VALIDATION_FAILURE;
                break;
            }
        }
        // If validation fails then halt the whole process
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {
            return new ImmutablePair<>(result, transferData);
        }*/

        // NOTICE: Cascade is not really a valid SAVE operation since the user modifies only one form at a time.
        // When some practical use for SAVE cascade is thought of then it can be added here
        /*for(Operation operation : configPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.APPROVE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!cascader.cascade(CascadeInstruction.build(OperationType.APPROVE, info), transferData, operation.getTargets(), configPair.getRight())) {
                result = ReturnResult.CASCADE_FAILURE;
                break;
            }
        }
        // If cascade fails then don't approve this revision
        if(result != ReturnResult.OPERATION_SUCCESSFUL) {
            return new ImmutablePair<>(result, transferData);
        }*/

        // Do actual change checking for field values
        SaveHandler handler = new SaveHandler(info, revision.getKey());
        MutablePair<Boolean, Boolean> changesAndErrors = handler.saveFields(configuration, transferData, revision);

        // Only finalize if there have been changes
        finalizeSave(revision, transferData, configuration, info, changesAndErrors);

        if(changesAndErrors.getLeft()) {
            // Set revision save info
            revision.setSaved(info);

            result = revisions.updateRevisionData(revision);
            if(result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, transferData);
            } else {
                messenger.sendAmqpMessage(messenger.FD_UPDATE, new RevisionPayload(revision));
                // Set transfer object save info since database values have changed
                transferData.getState().setSaved(info);
                //revisions.indexRevision(revision.getKey());
                return new ImmutablePair<>((changesAndErrors.getRight() ? ReturnResult.OPERATION_SUCCESSFUL_WITH_ERRORS : ReturnResult.OPERATION_SUCCESSFUL), transferData);
            }
        } else {
            return new ImmutablePair<>((changesAndErrors.getRight() ? ReturnResult.OPERATION_SUCCESSFUL_WITH_ERRORS : ReturnResult.NO_CHANGES), transferData);
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
                finalizeStudyAttachment(revision, transferData, changesAndErrors, info);
                break;
            case STUDY:
                finalizeStudy(revision, configuration, transferData, info, changesAndErrors);
                break;
            case PUBLICATION:
                finalizePublication(revision, transferData, changesAndErrors);
                break;
            default:
                break;
        }
    }

    private void finalizeStudy(RevisionData revision, Configuration configuration, TransferData transferData, DateTimeUserPair info, MutablePair<Boolean, Boolean> changesAndErrors) {
        StudyFactory fac = new StudyFactory();

        // Form packages and biblcit
        ReturnResult result = fac.formUrnAndBiblCit(revision, info, references, configuration, changesAndErrors);

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

    private void finalizePublication(RevisionData data, TransferData transferData, MutablePair<Boolean, Boolean> changesAndErrors) {
        if(changesAndErrors.getLeft()) {
            // Only do this if there has been changes to the data
            Pair<StatusCode, ValueDataField> pair = data.dataField(ValueDataFieldCall.get(Fields.PUBLICATIONFIRSTSAVED));
            if(pair.getLeft() == StatusCode.FIELD_FOUND && pair.getRight().hasValueFor(Language.DEFAULT)) {
                return;
            }
            pair = data.dataField(ValueDataFieldCall.set(Fields.PUBLICATIONFIRSTSAVED, new Value((new LocalDate()).toString()), Language.DEFAULT).setChangeMap(data.getChanges()));
            if(!(pair.getLeft() == StatusCode.FIELD_INSERT || pair.getLeft() == StatusCode.FIELD_CHANGED)) {
                return;
            }
            transferData.addField(TransferField.buildFromDataField(pair.getRight()));
        }
    }

    /* Attachment save
        All field values that will be saved are saved automatically before this
        so we don't need to handle that, this is only about doing the manual
        adjustments and checks that can't be performed through configuration.
        This can however change the values that are saved to database and in
        certain cases is the only way to change those values (e.g. file path).

        if filedip needs correction then
            correct filedip
            add error to notify user (this would be better as notification but those are not supported atm

        If newfile has value and newfile is file then
            calculate correct store path
            If filepath has value then
                remove old file
            copy new file to correct path
            set path to filepath
            if newfile is por-file then
                mark the file for parsing

        if filepath has value then
            if file doesn't exist or is directory then
                add error

        if file needs parsing then
            parse por-file
    */

    private void finalizeStudyAttachment(RevisionData revision, TransferData transfer, MutablePair<Boolean, Boolean> changesAndErrors, DateTimeUserPair info) {
        // Lets check filepaths
        checkFile(revision, transfer, changesAndErrors, info);

        // Lets check the filedip for need of correction and correct the value if necessary
        checkDip(revision, transfer, changesAndErrors, info);
    }

    private void checkFile(RevisionData revision, TransferData transfer, MutablePair<Boolean, Boolean> changesAndErrors, DateTimeUserPair info) {
        // Get study linked to this attachment
        ValueDataField linkedStudy = revision.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight();
        if(linkedStudy == null) {
            // We have no linked study, no need to do anything else since this attachment is broken
            Logger.error(getClass(), "No linked study for " + revision.toString());
            changesAndErrors.setRight(true);
            return;
        }

        // Get linked study revision data
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(linkedStudy.getActualValueFor(Language.DEFAULT));
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Could not find linked study "+linkedStudy.getActualValueFor(Language.DEFAULT)+" for "+revision.toString());
            return;
        }

        RevisionData study = dataPair.getRight();

        // Check file move and file update
        boolean fileMoved = checkFileMove(revision, transfer, changesAndErrors, info);
        boolean fileUpdate = checkFileUpdate(revision, transfer, changesAndErrors, info);

        // Validate that the file in file-field still exists and is a valid file
        // If not, then mark error to transfer data
        Pair<StatusCode, ValueDataField> fieldPair = revision.dataField(ValueDataFieldCall.get("file"));
        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            if(!transfer.hasField(Fields.FILE)) {
                transfer.addField(TransferField.buildFromDataField(fieldPair.getRight()));
            }
            TransferField tfFile = transfer.getField(Fields.FILE);

            if(notFile(fieldPair.getRight().getActualValueFor(Language.DEFAULT), tfFile, changesAndErrors)) {
                messenger.sendAmqpMessage(messenger.FB_FILES_MISSING, new FileMissingPayload(study, revision));

                Logger.error(getClass(), "Path in file-field is not a valid file");
                // Lets set fileUpdate to false since no matter the case we can't perform the parse check
                fileUpdate = false;
                fileMoved = false;
            }
        }

        // TODO: Check for things that could cause variables to be removed
        //if (hasAndIsOriginal(attachment)) return;

        // File has been updated and variables are not removed so update them instead
        if(fileUpdate || fileMoved) {
            // File was either moved or updated, check situations that can occur from either event
            if(fileUpdate) {
                // File was actually updated, check if parsing is required
                boolean needsParsing = false;
                Language varLang = null;

                ValueDataField pathField = revision.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight();
                String fileName = pathField != null ? FilenameUtils.getName(pathField.getActualValueFor(Language.DEFAULT)) : null;

                if(fileName != null) {
                    // We need a file name for this section to make sense
                    if(fileIsVarFile(fileName)) {
                        varLang = getVarFileLanguage(fileName);
                        // If there already is a varfile for this language then check that it matches current varfile
                        if(hasVariablesFileFor(varLang, study)) {
                            if(variablesFileForIncludes(varLang, revision.getKey().getId().toString()+"-", study)) {
                                needsParsing = true;
                            }
                        } else {
                            needsParsing = true;
                        }
                    }
                }
                if(needsParsing) {
                    //parseVariableFile(revision, study, varLang, info, changesAndErrors);
                    parser.parse(revision, VariableDataType.POR, study, varLang, info);
                }
            }
        }
    }

    private Language getVarFileLanguage(String fileName) {
        Language varLang;
        String base = FilenameUtils.getBaseName(fileName).toUpperCase();
        String lastChar = base.substring(base.length()-1);
        varLang = lastChar.equals("E") ? Language.EN : (lastChar.equals("S") ? Language.SV : Language.DEFAULT);
        return varLang;
    }

    /**
     * Checks if file should be moved.
     * This is based on the correct calculated path for the current attachment.
     * @param revision            Current attachment revision
     * @param transfer            Current transfer data
     * @param changesAndErrors    Tracks if there has been changes to the current revision or errors during the save process
     * @param info                DateTimeUserPair used for the duration of this operation
     * @return  boolean Tells if file was moved
     */
    private boolean checkFileMove(RevisionData revision, TransferData transfer, MutablePair<Boolean, Boolean> changesAndErrors, DateTimeUserPair info) {
        ValueDataField curFile = revision.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight();
        if(curFile != null && curFile.hasValueFor(Language.DEFAULT)) {
            String path = curFile.getActualValueFor(Language.DEFAULT);
            String destLoc = getAttachmentFilePath(revision, path);
            if(path.equals(destLoc)) {
                // No move necessary
                return false;
            }

            if(!transfer.hasField(Fields.FILE)) {
                transfer.addField(TransferField.buildFromDataField(curFile));
            }
            TransferField tfFile = transfer.getField(Fields.FILE);

            // We need to move the file
            File f = new File(path);
            File d = new File(destLoc);
            if(d.isFile()) {
                tfFile.addError(FieldError.AUTOMATIC_CHANGE_FAILED_FILE_EXISTS);
                changesAndErrors.setRight(true);
                return false;
            }
            try {
                Files.createDirectories(d.getParentFile().toPath());
                Files.move(f.toPath(), d.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch(Exception e) {
                Logger.error(getClass(), "Could not move file " + path + " to new location " + destLoc, e);
                changesAndErrors.setRight(true);
                tfFile.addError(FieldError.WRONG_LOCATION);
                return false;
            }

            revision.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(destLoc), Language.DEFAULT).setInfo(info));
            tfFile.getValueFor(Language.DEFAULT).setCurrent(destLoc);
            tfFile.addError(FieldError.AUTOMATIC_CHANGE);
            changesAndErrors.setLeft(true);
            changesAndErrors.setRight(true);
            return true;
        }
        return false;
    }

    /**
     * Checks if the current file should be updated with a new file.
     * @param revision          Current attachment revision data
     * @param transfer          Current attachment transfer data
     * @param changesAndErrors  Tracks if there has been changes to the current revision or errors during the save process
     * @param info              DateTimeUserPair used for the duration of this operation
     * @return  boolean Tells if file was updated
     */
    private boolean checkFileUpdate(RevisionData revision, TransferData transfer, MutablePair<Boolean, Boolean> changesAndErrors, DateTimeUserPair info) {
        TransferField tf = transfer.getField(Fields.NEWPATH);
        if (tf != null && tf.hasCurrentFor(Language.DEFAULT)) {
            // We have a new file path, lets check that it's an actual file and handle it accordingly
            String path = tf.getValueFor(Language.DEFAULT).getCurrent();
            if (notFile(path, tf, changesAndErrors)) {
                // If the file is not a valid file then mark an error and skip the rest of this block
                changesAndErrors.setRight(true);
                return false;
            }

            // Get old file location
            ValueDataField oldFileField = revision.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight();
            DateTimeUserPair oldInfo = oldFileField != null && oldFileField.hasCurrentFor(Language.DEFAULT) ? oldFileField.getCurrentFor(Language.DEFAULT).getSaved() : null;
            String oldPath = oldFileField != null && oldFileField.hasValueFor(Language.DEFAULT) ? oldFileField.getActualValueFor(Language.DEFAULT) : null;
            boolean oldChanges = changesAndErrors.getLeft();

            // Get new file destination
            String destLoc = oldPath != null && FilenameUtils.getBaseName(path).equals(FilenameUtils.getBaseName(oldPath)) ? oldPath : getAttachmentFilePath(revision, path);

            if (destLoc == null) {
                tf.addError(FieldError.MOVE_FAILED);
                changesAndErrors.setRight(true);
                return false;
            }

            ValueDataField variablesField = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();
            if(variablesField != null && variablesField.hasValueFor(Language.DEFAULT)) {
                if(!fileIsVarFile(FilenameUtils.getName(path))) {
                    tf.addError(FieldError.NOT_VARIABLE_FILE);
                    changesAndErrors.setRight(true);
                    return false;
                }
                Language varlang = getVarFileLanguage(FilenameUtils.getName(path));
                RevisionData variables = revisions.getRevisionData(variablesField.getActualValueFor(Language.DEFAULT)).getRight();
                if(variables != null) {
                    ValueDataField langField = variables.dataField(ValueDataFieldCall.get(Fields.LANGUAGE)).getRight();
                    if(langField != null && langField.hasValueFor(Language.DEFAULT)) {
                        if(!langField.getActualValueFor(Language.DEFAULT).toUpperCase().equals(varlang.toValue().toUpperCase())) {
                            tf.addError(FieldError.CAN_NOT_CHANGE_LANGUAGE);
                            changesAndErrors.setRight(true);
                            return false;
                        }
                    }
                }
            }



            // Set file path to file-field
            StatusCode result = revision.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(destLoc), Language.DEFAULT).setInfo(info)).getLeft();
            if (!(result == StatusCode.NO_CHANGE_IN_VALUE || result == StatusCode.FIELD_INSERT || result == StatusCode.FIELD_UPDATE)) {
                Logger.error(getClass(), "Could not update file path because " + result.name());
                changesAndErrors.setRight(true);
                tf.addError(FieldError.MOVE_FAILED);
                return false;
            } else {
                changesAndErrors.setLeft(true);
            }

            // Back up the old file in case we need to revert back to it
            if (oldPath != null) {
                File oldFile = new File(oldPath);
                if (oldFile.exists()) {
                    File bck = new File(oldPath + ".bck");
                    // If backup file already exists lets delete it so it doesn't block the new backup
                    try {
                        Files.deleteIfExists(bck.toPath());
                        Files.move(oldFile.toPath(), bck.toPath(), StandardCopyOption.ATOMIC_MOVE);
                    } catch (Exception e) {
                        Logger.error(getClass(), "Could not back up existing file " + oldPath + " to " + oldPath + ".bck reverting changes to file", e);
                        revision.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(oldPath), Language.DEFAULT).setInfo(oldInfo));
                        tf.addError(FieldError.MOVE_FAILED);
                        changesAndErrors.setLeft(oldChanges);
                        changesAndErrors.setRight(true);
                        return false;
                    }
                }
            }

            // Copy the new file to the correct location
            File newFile = new File(path);
            File destFile = new File(destLoc);
            if(destFile.exists() && oldPath == null) {
                // In this case there is already an existing file, let's mark FILE_EXISTS, and fail the operation
                // We don't need to move the backup file back since we know there is no backup file
                tf.addError(FieldError.FILE_EXISTS);
                revision.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(oldPath), Language.DEFAULT).setInfo(oldInfo));
                changesAndErrors.setRight(true);
                return false;
            }

            try {
                Files.createDirectories(destFile.getParentFile().toPath());
                // We can replace existing file since if there is for some reason an existing file then the backup has for some reason succeeded but as a copy instead
                Files.copy(newFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                Logger.error(getClass(), "Could not copy new file " + path + " to location " + destLoc + ", reverting changes", e);
                // Set field back to old path
                // Move backup back to old location
                if (oldPath != null) {
                    File oldFile = new File(oldPath);
                    File bck = new File(oldPath + ".bck");
                    if (bck.exists()) {
                        try {
                            Files.move(bck.toPath(), oldFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                        } catch (Exception e2) {
                            Logger.error(getClass(), "Could not move backup " + oldPath + ".bck back to old location", e2);
                        }
                    }
                }
                // Revert old path info
                StatusCode revresult = revision.dataField(ValueDataFieldCall.set(Fields.FILE, new Value(oldPath), Language.DEFAULT).setInfo(oldInfo)).getLeft();
                tf.addError(FieldError.MOVE_FAILED);
                changesAndErrors.setLeft(oldChanges);
                changesAndErrors.setRight(true);
                return false;
            }

            // Remove backup
            if (oldPath != null) {
                File bck = new File(oldPath + ".bck");
                if (bck.exists()) {
                    try {
                        Files.deleteIfExists(bck.toPath());
                    } catch (Exception e) {
                        Logger.error(getClass(), "Could not remove backup file, remove manually", e);
                    }
                }
            }
            // File has been updated at this point, all that remains is removing the backup
            return true;
        }

        return false;
    }

    private void checkDip(RevisionData revision, TransferData transfer, MutablePair<Boolean, Boolean> changesAndErrors, DateTimeUserPair info) {
        // Check that filedip is correct
        // If need be change filedip, mark it as changed and mark the whole data as containing errors (this would work better as warning or notice, but we don't have support for that yet)
        ValueDataField filedip = revision.dataField(ValueDataFieldCall.get("filedip")).getRight();

        if(filedip == null || !filedip.getActualValueFor(Language.DEFAULT).equals("2")) {
            // User has selected something else than 'No' as the filedip value, check if this is valid and correct if not
            boolean fixdip = false;

            ValueDataField original = revision.dataField(ValueDataFieldCall.get("fileoriginal")).getRight();
            boolean origLocation = original != null && original.hasValueFor(Language.DEFAULT) && original.getActualValueFor(Language.DEFAULT).equals("1");
            if(origLocation) {
                fixdip = true;
            }

            // Get path
            ValueDataField pathField = revision.dataField(ValueDataFieldCall.get("file")).getRight();
            String fileName = pathField != null && pathField.hasValueFor(Language.DEFAULT) ? FilenameUtils.getName(pathField.getActualValueFor(Language.DEFAULT)) : null;

            if(!fixdip && fileName != null && FilenameUtils.getExtension(fileName).toUpperCase().equals("XML")) {
                fixdip = true;
            }

            if(!fixdip && fileName != null && fileName.substring(0, 2).toUpperCase().equals("AR")) {
                fixdip = true;
            }

            if(!fixdip && fileName != null) {
                switch(fileName.substring(0, 3).toUpperCase()) {
                    case "SYF":
                    case "ANF":
                        fixdip = true;
                        break;
                }
            }

            if(fixdip) {
                // We need to fix filedip to 2 (i.e. 'No'), let's just assume that this succeeds
                revision.dataField(ValueDataFieldCall.set(Fields.FILEDIP, new Value("2"), Language.DEFAULT).setInfo(info));

                TransferField tf = transfer.getField(Fields.FILEDIP);
                tf.addValueFor(Language.DEFAULT, TransferValue.buildFromValueDataFieldFor(Language.DEFAULT, revision.dataField(ValueDataFieldCall.get(Fields.FILEDIP)).getRight()));
                tf.addError(FieldError.AUTOMATIC_CHANGE);
                changesAndErrors.setLeft(true);
                changesAndErrors.setRight(true);
            }
        }
    }

    /**
     * Calculates the correct path for the attachment.
     * This is based on file name as as well as given values for attachment's data fields.
     * @param revision    Current attachment revision
     * @param path        Current file path
     * @return  String correct path for file
     */
    private String getAttachmentFilePath(RevisionData revision, String path) {
        // We need to calculate the correct store path for the file
        // If some required field does not have value then assume the most common result
        String pathFromRoot = "/";

        Pair<ReturnResult, String> fileDirectory = revisions.getStudyFileDirectory(
                Long.parseLong(revision.dataField(ValueDataFieldCall.get("study")).getRight().getActualValueFor(Language.DEFAULT)));

        if(fileDirectory.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            Logger.error(getClass(), "Could not find revisionable when fetching file root for study in attachment " + revision.toString());
            return null;
        }

        String pathRoot = fileDirectory.getRight();
        String fileName = FilenameUtils.getName(path);

        ValueDataField origField = revision.dataField(ValueDataFieldCall.get("fileoriginal")).getRight();
        boolean origLocation = (origField != null && origField.getActualValueFor(Language.DEFAULT).equals("1"));

        if(origLocation) pathFromRoot += "original";

        boolean dataLocation = false;
        if(!origLocation) {
            String namePrefix = fileName.substring(0, 3).toUpperCase();
            switch(namePrefix) {
                default:
                    dataLocation = false;
                    break;
                case "ARF":
                case "SYF":
                case "ANF":
                case "DAF":
                    dataLocation = true;
                    break;

            }

            if(dataLocation) pathFromRoot += "data";
        }

        String destLoc = pathRoot+pathFromRoot+(pathFromRoot.length() > 1 ? "/" : "");
        destLoc += fileName;
        return destLoc;
    }

    private boolean notFile(String path, TransferField tf, MutablePair<Boolean, Boolean> changesAndErrors) {
        // Does the defined file path exist and does it point to a file
        File file = new File(path);
        // This should be true if we have text in RevisionData
        if(tf != null) {
            if(!file.exists()) {
                tf.addError(FieldError.NO_FILE);
                changesAndErrors.setRight(true);
            } else if(file.isDirectory()) {
                tf.addError(FieldError.IS_DIRECTORY);
                changesAndErrors.setRight(true);
            } else if(!file.isFile()) {
                tf.addError(FieldError.NO_FILE);
                changesAndErrors.setRight(true);
            }
        }

        return !file.isFile();
    }

    private boolean hasVariablesFileFor(Language varLang, RevisionData study) {
        Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES));
        if(conPair.getLeft() != StatusCode.FIELD_FOUND) {
            return false;
        }

        Pair<StatusCode, DataRow> rowPair = conPair.getRight().getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang.toValue()));
        if(rowPair.getLeft() != StatusCode.ROW_FOUND) {
            return false;
        }

        Pair<StatusCode, ValueDataField> fieldPair = rowPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARIABLESFILE));
        return fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT);
    }

    private boolean variablesFileForIncludes(Language varLang, String fileId, RevisionData study) {
        Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES));
        if(conPair.getLeft() != StatusCode.FIELD_FOUND) {
            return false;
        }

        Pair<StatusCode, DataRow> rowPair = conPair.getRight().getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang.toValue()));
        if(rowPair.getLeft() != StatusCode.ROW_FOUND) {
            return false;
        }

        Pair<StatusCode, ValueDataField> fieldPair = rowPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARIABLESFILE));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
            return false;
        }

        return fieldPair.getRight().valueForIncludes(Language.DEFAULT, fileId);
    }

    private boolean fileIsVarFile(String fileName) {
        fileName = fileName.toUpperCase();
        // Does the file name start with DAF
        if(fileName.length() < 3) {
            return false;
        }
        if(!fileName.substring(0, 3).equals("DAF")) {
            return false;
        }

        // Does the file extension define a variable file (i.e. POR)
        String extension = FilenameUtils.getExtension(fileName);
        if(!extension.equals("POR")) {
            // For now the only option. If more variable file types are added then this needs to be changed to a switch case
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

    private static ParseResult resultCheck(ParseResult result, ParseResult def) {
        return result != ParseResult.REVISION_CHANGES ? def : result;
    }

    /*private void parseVariableFile(RevisionData attachment, RevisionData study, Language varLang, DateTimeUserPair info, MutablePair<Boolean, Boolean> changesAndErrors) {
        ParseResult result = parser.parse(attachment, VariableDataType.POR, study, varLang, info);

        // Check that study has a link to the variable file (we should not be in this method if there is a link to another attachment)
        if(!hasVariablesFileFor(varLang, study)) {
            Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.set(Fields.STUDYVARIABLES));
            if(conPair.getLeft() == StatusCode.FIELD_INSERT) {
                result = ParseResult.REVISION_CHANGES;
            }

            Pair<StatusCode, DataRow> rowPair = conPair.getRight().getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang.toValue()), study.getChanges(), info);
            if(rowPair.getLeft() == StatusCode.ROW_INSERT) {
                result = ParseResult.REVISION_CHANGES;
            }

            Pair<StatusCode, ValueDataField> fieldPair = rowPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARIABLESFILE));
            if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                StatusCode setResult = rowPair.getRight().dataField(
                        ValueDataFieldCall.set(Fields.VARIABLESFILE, new Value(attachment.getKey().getId().toString()), Language.DEFAULT).setInfo(info).setChangeMap(study.getChanges()))
                        .getLeft();
                if(!(setResult == StatusCode.FIELD_UPDATE || setResult == StatusCode.FIELD_INSERT)) {
                    Logger.error(getClass(), "Study update failed with result " + setResult);
                    result = resultCheck(result, ParseResult.NO_CHANGES);
                } else {
                    result = resultCheck(result, ParseResult.REVISION_CHANGES);
                }
            }

            fieldPair = rowPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARIABLES));
            String varsKey = fieldPair.getRight().getActualValueFor(Language.DEFAULT);
            // If this is missing then something else has gone really wrong

            // Let's first check if we need to update the value at all
            fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
            if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT) || !fieldPair.getRight().valueForEquals(Language.DEFAULT, varsKey.split("-")[0])) {
                attachment.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(varsKey.split("-")[0]), Language.DEFAULT).setInfo(info));
                changesAndErrors.setLeft(true);
            }
        }

        if(result == ParseResult.REVISION_CHANGES) {
            revisions.updateRevisionData(study);
        }
    }*/


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

        /**
         * Saves changes to single RevisionData.
         *
         * @param configuration    Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
         * @param transferData     TransferData
         * @param revisionData     RevisionFata
         * @return Pair            changesAndErrors pair. Left value indicates the operation has changed revision data,
         *                         right value indicates that errors were marked somewhere while saving the data
         */
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
                // Save field, a pair of operation status and boolean marking if there is errors is being returned
                Pair<StatusCode, Boolean> saveResult = saveField(field, configuration, transferData, revisionData, revisionData.getChanges());
                // If saving field returns "field changed" status, mark changes-flag of result to true
                if (saveResult.getLeft() == StatusCode.FIELD_CHANGED && !result.getLeft()) result.setLeft(true);
                // If saving field returns an error flag, set it also on the result flag
                if (saveResult.getRight() && !result.getRight()) result.setRight(true);
            }

            // Check bidirectional fields. This requires some custom code in certain cases.

            // Next let's iterate through one revisionable at a time
            Map<Long, List<Pair<String, Boolean>>> revisionablePairs = new HashMap<>();
            for(Pair<Long, ? extends Pair<String, Boolean>> pair : bidirectional) {
                if(revisionablePairs.get(pair.getLeft()) == null) {
                    revisionablePairs.put(pair.getLeft(), new ArrayList<Pair<String, Boolean>>());
                }
                revisionablePairs.get(pair.getLeft()).add(pair.getRight());
            }
            for(Long id : revisionablePairs.keySet()) {
                checkBidirectionality(id, revisionablePairs.get(id));
            }

            return result;
        }

        private void checkBidirectionality(Long id, List<Pair<String, Boolean>> pairs) {
            Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id.toString());
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                // Something is wrong
                Logger.error(getClass(), "Tried to force bidirectionality for a revisionable that is nonexistent");
                return;
            }
            RevisionData data = dataPair.getRight();
            Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Couldn't find configuration for revision "+data.toString()+" while checking bidirectional values.");
                return;
            }
            Configuration config = configPair.getRight();
            // For now assumes that strings are all top level field keys.
            boolean changes = false;
            for(Pair<String, Boolean> pair : pairs) {
                Pair<StatusCode, ReferenceContainerDataField> fieldPair = data.dataField(ReferenceContainerDataFieldCall.set(pair.getLeft()));
                // let's make sure we have a field
                if(fieldPair.getRight() == null) {
                    Logger.error(getClass(), "Failed to create ReferenceContainerDataField while forcing bidirectionality with result "+fieldPair.getLeft());
                    continue;
                }
                ReferenceContainerDataField field = fieldPair.getRight();
                if(pair.getRight()) {
                    // Make sure we have the reference
                    Pair<StatusCode, ReferenceRow> rowPair = field.getOrCreateReferenceWithValue(key.getId().toString(), data.getChanges(), info);
                    if(rowPair.getLeft() == StatusCode.ROW_INSERT) {
                        changes = true;
                    }
                } else {
                    // Remove the reference if it exists
                    Pair<StatusCode, ReferenceRow> rowPair = field.getReferenceWithValue(key.getId().toString());
                    if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                        rowPair = field.removeReference(rowPair.getRight().getRowId(), data.getChanges(), info);
                    }
                    if(rowPair.getLeft() == StatusCode.ROW_CHANGE || rowPair.getLeft() == StatusCode.ROW_REMOVED) {
                        changes = true;
                    }
                }
            }
            if(changes) {
                doTypeSpecificBidirectionality(data, pairs);

                revisions.updateRevisionData(data);
            }
        }

        /**
         * Do things that are part of bidirectionality on a type specific level.
         * @param data     RevisionData
         * @param pairs    Pair
         */
        private void doTypeSpecificBidirectionality(RevisionData data, List<Pair<String, Boolean>> pairs) {
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
                        Pair<StatusCode, DataRow> rowPair = container.insertNewDataRow(language, changeMap);
                        if (rowPair.getLeft() == StatusCode.ROW_INSERT) {
                            row = rowPair.getRight();
                            row.setSaved(info);
                            changes = true;
                            tr.setRowId(row.getRowId());
                            tr.setUnapproved(row.getUnapproved());
                        }
                    } else {
                        // Old row, check the "removed" value.
                        Pair<StatusCode, DataRow> rowPair = container.getRowWithId(tr.getRowId());
                        if (rowPair.getLeft() != StatusCode.ROW_FOUND) {
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
                                Logger.error(getClass(), "Didn't find field " + subkey + " in configuration " + configuration.getKey().toString() + " even though " + field.getKey() + " has it as subfield.");
                                continue;
                            }

                            // Call save field for the subfield, this can cause recursion
                            Pair<StatusCode, Boolean> fieldSaveResult = saveField(subfield, configuration, tr, row, changeMap);

                            if (fieldSaveResult.getRight()) {
                                returnPair.setRight(true);
                            }
                            if (fieldSaveResult.getLeft() == StatusCode.FIELD_CHANGED) {
                                // Make sure that row change is in the container change since there has been an actual change.
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
                        Logger.error(getClass(), "TransferField and Container with key "+field.getKey()+" have different number of rows for language "+language);
                    }

                }
            }


            if (changes) {
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
                    if(tr == null) {
                        continue;
                    }
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
                            changes = true;
                            if(ref.getType() == ReferenceType.REVISIONABLE && StringUtils.hasText(field.getBidirectional())) {
                                bidirectional.add(new ImmutablePair<>(removePair.getRight().getReference().asInteger(),
                                        new ImmutablePair<>(field.getBidirectional(), false)));
                            }
                            if(removePair.getLeft() == StatusCode.ROW_REMOVED) {
                                // Row was removed completely, no need to continue on with saving
                                continue;
                            }
                        }
                    }
                    if (!StringUtils.hasText(tr.getValue())) {
                        // There should always be a value on existing row. If value is missing add error, set return value and skip this row.
                        // Saved reference value is immutable so nothing gets deleted through setting the value to null.
                        tr.addError(FieldError.MISSING_VALUE);
                        returnPair.setRight(true);
                    } else if (tr.getRowId() == null) {
                        // New row, insert new ReferenceRow
                        Pair<StatusCode, ReferenceRow> referencePair = container.getOrCreateReferenceWithValue(tr.getValue(), changeMap, info);
                        if (referencePair.getLeft() == StatusCode.ROW_INSERT) {
                            changes = true;
                            tr.setRowId(referencePair.getRight().getRowId());
                        }
                        if(ref.getType() == ReferenceType.REVISIONABLE && StringUtils.hasText(field.getBidirectional())) {
                                bidirectional.add(new ImmutablePair<>(referencePair.getRight().getReference().asInteger(),
                                        new ImmutablePair<>(field.getBidirectional(), true)));
                            }
                    } else {
                        // Old row, the only thing that can change is "removed". The actual reference value on ReferenceRow is immutable
                        // and is locked in when the row is created
                        Pair<StatusCode, ReferenceRow> referencePair = container.getReferenceWithId(tr.getRowId());
                        if (referencePair.getLeft() != StatusCode.ROW_FOUND) {
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
            FieldError typeError = value != null ? value.typeCheck(field.getType()) : null;
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
            boolean isFreeText = isFieldFreeText(field.getKey(), configuration);
            if (!(statusCode == StatusCode.FIELD_INSERT || statusCode == StatusCode.FIELD_UPDATE || (statusCode == StatusCode.FIELD_NOT_WRITABLE && isFreeText))) {
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
                Field freeField = configuration.getField(list.getFreeTextKey());
                if(freeField != null) {
                    if(!saveFreeText) {
                        TransferField tf = transferFields.getField(freeField.getKey());
                        if(tf == null) {
                            tf = new TransferField(freeField.getKey(), TransferFieldType.VALUE);
                            transferFields.addField(tf);
                        } else {
                            tf.getValues().clear();
                        }
                    }
                    Pair<StatusCode, Boolean> freeSave = saveValue(freeField, configuration, transferFields, dataFields, changeMap);
                    if(freeSave.getLeft() == StatusCode.FIELD_CHANGED) {
                        returnPair.setLeft(freeSave.getLeft());
                    }
                    if(freeSave.getRight()) {
                        returnPair.setRight(freeSave.getRight());
                    }
                }
            }
        }

        private boolean isFieldFreeText(String key, Configuration configuration) {
            for(SelectionList list : configuration.getSelectionLists().values()) {
                if(key.equals(list.getFreeTextKey())) {
                    return true;
                }
            }
            return false;
        }
    }
}
