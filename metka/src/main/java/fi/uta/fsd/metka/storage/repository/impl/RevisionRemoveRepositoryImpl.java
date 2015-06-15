package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Performs a removal to given transfer data if possible
 *
 * Valid results are:
 *      SUCCESS_DRAFT - Draft was removed successfully, user should be moved to latest approved revision
 *      SUCCESS_LOGICAL - Logical removal was performed successfully, new up to date data should be loaded for the revision
 *      FINAL_REVISION - Draft was removed but no further revisions existed for the revisionable so the revisionable was removed also.
 * All other return values are errors
 * TODO: Should cascaded removals halt the original remove if they fail?
 */
@Repository
public class RevisionRemoveRepositoryImpl implements RevisionRemoveRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RestrictionValidator validator;

    @Autowired
    private Cascader cascader;

    @Autowired
    private StudyErrorsRepository errors;

    @Autowired
    private BinderRepository binders;

    @Override
    public RemoveResult remove(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        if(transferData.getState().getUiState() == UIRevisionState.DRAFT) {
            return removeDraft(transferData, info);
        } else if(transferData.getState().getUiState() == UIRevisionState.APPROVED) {
            return removeLogical(transferData, info);
        } else {
            return RemoveResult.ALREADY_REMOVED;
        }
    }

    @Override
    public RemoveResult removeDraft(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        RemoveResult result = allowRemoval(transferData);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return result;
        }

        RevisionData data = pair.getRight();

        if(data.getState() != RevisionState.DRAFT) {
            return RemoveResult.NOT_DRAFT;
        }

        if(!AuthenticationUtil.isHandler(data)) {
            Logger.error(getClass(), "User " + AuthenticationUtil.getUserName() + " tried to remove draft belonging to " + data.getHandler());
            return RemoveResult.WRONG_USER;
        }

        result = validateAndCascade(data, info, true);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return result;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, RevisionKey.fromModelKey(data.getKey()));
        if(revision == null) {
            Logger.error(getClass(), "Draft revision with key "+data.getKey()+" was slated for removal but was not found from database.");
        } else {
            em.remove(revision);
        }

        List<RevisionEntity> entities = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId=:id", RevisionEntity.class)
                .setParameter("id", data.getKey().getId())
                .getResultList();

        if(entities.isEmpty()) {
            em.remove(em.find(RevisionableEntity.class, data.getKey().getId()));
            finalizeFinalRevisionRemoval(data, info);
            result = RemoveResult.FINAL_REVISION;
        } else {
            RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            result = RemoveResult.SUCCESS_DRAFT;
        }
        addRemoveIndexCommand(transferData, result);
        return result;
    }

    @Override
    public RemoveResult removeLogical(TransferData transferData, DateTimeUserPair info) {
        if(info == null) {
            info = DateTimeUserPair.build();
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        RemoveResult result = allowRemoval(transferData);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return result;
        }

        pair = revisions.getLatestRevisionForIdAndType(transferData.getKey().getId(), false, transferData.getConfiguration().getType());

        // NOTICE: These could be moved to restrictions quite easily
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // This should never happen since we found the revision data provided for this method
            return RemoveResult.NOT_FOUND;
        }
        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return RemoveResult.OPEN_DRAFT;
        }

        RevisionData data = pair.getRight();

        result = validateAndCascade(data, info, false);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return result;
        }

        RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
        entity.setRemoved(true);
        entity.setRemovalDate(info.getTime());
        entity.setRemovedBy(info.getUser());

        /*if(data.getConfiguration().getType() == ConfigurationType.STUDY) {
            propagateStudyLogicalRemoval(data);
        } else if(data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
            propagateStudyVariablesLogicalRemoval(data);
        }*/

        // TODO: What do we do about study errors and binder pages in this case?

        result = RemoveResult.SUCCESS_LOGICAL;
        addRemoveIndexCommand(transferData, result);
        return result;
    }

    private void finalizeFinalRevisionRemoval(RevisionData data, DateTimeUserPair info) {
        switch(data.getConfiguration().getType()) {
            case STUDY: {
                // Study attachments, variables and variable revisions should have already been removed earlier during removal propagation.
                // This part should only remove things that are removed only if we don't have any revisions left.

                // Remove study errors and binder pages linking to this study
                errors.removeErrorsForStudy(data.getKey().getId());
                binders.removeStudyBinderPages(data.getKey().getId());
            }
            case STUDY_VARIABLES: {
                Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get(Fields.STUDY));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY);
                    if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                        RevisionData study = revPair.getRight();
                        Pair<StatusCode, ContainerDataField> conPair = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES));
                        if(conPair.getLeft() == StatusCode.FIELD_FOUND) {
                            String varLang = data.dataField(ValueDataFieldCall.get(Fields.LANGUAGE)).getRight().getActualValueFor(Language.DEFAULT);
                            Pair<StatusCode, DataRow> rowPair = conPair.getRight().getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(varLang));
                            if(rowPair.getLeft() == StatusCode.ROW_FOUND) {
                                conPair.getRight().removeRow(rowPair.getRight().getRowId(), study.getChanges(), info);
                                revisions.updateRevisionData(study);
                            }
                        }
                    }
                }

                fieldPair = data.dataField(ValueDataFieldCall.get(Fields.FILE));
                if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                    Pair<ReturnResult, RevisionData> revPair = revisions.getLatestRevisionForIdAndType(fieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false,
                            ConfigurationType.STUDY_ATTACHMENT);
                    if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                        RevisionData attachment = revPair.getRight();
                        fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
                        if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                            attachment.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(""), Language.DEFAULT).setInfo(info));
                            revisions.updateRevisionData(attachment);
                        }
                    }
                }
            }
            default: {
                break;
            }
        }

        // TODO: remove references to this object.
        // Since we don't have a mapping object for references we need to do this by type for now.
        // It might be handy to do some processing of data configurations when they are saved and to form a reference web from them.
        // This would allow for automatic clean operations at certain key points like this. Basically collecting the foreign keys
        // and enabling cascade effects.
    }

    private RemoveResult validateAndCascade(RevisionData data, DateTimeUserPair info, Boolean draft) {
        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(data.getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Could not find configuration for data "+data.toString());
            return RemoveResult.CONFIGURATION_NOT_FOUND;
        }

        // Do validation
        ReturnResult rr = ReturnResult.OPERATION_SUCCESSFUL;
        for(Operation operation : confPair.getRight().getRestrictions()) {
            if(!(operation.getType() == OperationType.REMOVE
                    || operation.getType() == (draft?OperationType.REMOVE_DRAFT:OperationType.REMOVE_LOGICAL)
                    || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!validator.validate(data, operation.getTargets(), confPair.getRight())) {
                rr = ReturnResult.RESTRICTION_VALIDATION_FAILURE;
                break;
            }
        }
        // If validation fails then halt the whole process
        if(rr != ReturnResult.OPERATION_SUCCESSFUL) {
            return RemoveResult.RESTRICTION_VALIDATION_FAILURE;
        }

        // Do cascade
        for(Operation operation : confPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.REMOVE
                    || operation.getType() == (draft?OperationType.REMOVE_DRAFT:OperationType.REMOVE_LOGICAL)
                    || operation.getType() == OperationType.ALL)) {
                continue;
            }
            if(!cascader.cascade(CascadeInstruction.build(OperationType.REMOVE, info, draft), data, operation.getTargets(), confPair.getRight())) {
                rr = ReturnResult.CASCADE_FAILURE;
            }
        }
        // If cascade fails then don't approve this revision
        if(rr != ReturnResult.OPERATION_SUCCESSFUL) {
            return RemoveResult.CASCADE_FAILURE;
        }

        return RemoveResult.ALLOW_REMOVAL;
    }

    private RemoveResult allowRemoval(TransferData transferData) {
        switch(transferData.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                return checkStudyAttachmentRemoval(transferData);
            default:
                return RemoveResult.ALLOW_REMOVAL;
        }
    }

    private RemoveResult checkStudyAttachmentRemoval(TransferData transferData) {
        TransferField field = transferData.getField(Fields.STUDY);
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(field.asValueFor(Language.DEFAULT).asInteger(), false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        return AuthenticationUtil.isHandler(pair.getRight())
                ? RemoveResult.ALLOW_REMOVAL
                : (pair.getRight().getState() != RevisionState.DRAFT
                        ? RemoveResult.STUDY_NOT_DRAFT
                        : RemoveResult.WRONG_USER);
    }

    private void addRemoveIndexCommand(TransferData transferData, RemoveResult result) {
        switch(result) {
            case FINAL_REVISION:
                // TODO: In case of study we should check that references pointing to this study will get removed from revisions (from which point is the question).
            case SUCCESS_DRAFT:
                // One remove operation should be enough for both of these since there should only be one affected document
                revisions.removeRevision(transferData.getKey());
                break;
            case SUCCESS_LOGICAL:
                // In this case we need to reindex all affected documents instead
                List<Integer> nos = revisions.getAllRevisionNumbers(transferData.getKey().getId());
                for(Integer no : nos) {
                    Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(transferData.getKey().getId(), no);
                    if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                        revisions.indexRevision(pair.getRight().getKey());
                    }
                }
                break;
            default:
                // Errors don't need special handling
                break;
        }
    }

    /*private void propagateStudyDraftRemoval(RevisionData data) {
        Pair<StatusCode, ValueDataField> variablesPair = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(variablesPair.getLeft() == StatusCode.FIELD_FOUND && variablesPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Variables found
            Pair<ReturnResult, RevisionData> variables = revisions.getLatestRevisionForIdAndType(variablesPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
            if(variables.getLeft() == ReturnResult.REVISION_FOUND && variables.getRight().getState() == RevisionState.DRAFT) {
                // Variables were found and were in draft state, just call removeDraft on them and it should propagate after that.
                removeDraft(variables.getRight());
            } else if(variables.getLeft() == ReturnResult.REVISION_FOUND) {
                // Variables were found but were not in draft state, trigger just the propagation to variable entities.
                propagateStudyVariablesDraftRemoval(variables.getRight());
            }
        }
        Pair<StatusCode, ReferenceContainerDataField> attachmentsPair = data.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
        if(attachmentsPair.getLeft() == StatusCode.FIELD_FOUND && !attachmentsPair.getRight().getReferences().isEmpty()) {
            // Attachments found
            for(ReferenceRow row : attachmentsPair.getRight().getReferences()) {
                Pair<ReturnResult, RevisionData> attachment = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
                if(attachment.getLeft() == ReturnResult.REVISION_FOUND && attachment.getRight().getState() == RevisionState.DRAFT) {
                    // Attachment found and is in draft state, just call removeDraft
                    removeDraft(attachment.getRight());
                }
            }

        }
    }

    private void propagateStudyVariablesDraftRemoval(RevisionData data) {
        Pair<StatusCode, ReferenceContainerDataField> variablesPair = data.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(variablesPair.getLeft() == StatusCode.FIELD_FOUND && !variablesPair.getRight().getReferences().isEmpty()) {
            // Attachments found
            for(ReferenceRow row : variablesPair.getRight().getReferences()) {
                Pair<ReturnResult, RevisionData> variable = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                if(variable.getLeft() == ReturnResult.REVISION_FOUND && variable.getRight().getState() == RevisionState.DRAFT) {
                    // Attachment found and is in draft state, just call removeDraft
                    removeDraft(variable.getRight());
                }
            }

        }
    }*/



    /*private void propagateStudyLogicalRemoval(RevisionData data) {
        Pair<StatusCode, ValueDataField> variablesPair = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(variablesPair.getLeft() == StatusCode.FIELD_FOUND && variablesPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Variables found
            Pair<ReturnResult, RevisionData> variables = revisions.getLatestRevisionForIdAndType(variablesPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
            if(variables.getLeft() == ReturnResult.REVISION_FOUND && variables.getRight().getState() == RevisionState.APPROVED) {
                // Variables were found and were in draft state, just call removeDraft on them and it should propagate after that.
                removeLogical(variables.getRight());
            } else if(variables.getLeft() == ReturnResult.REVISION_FOUND) {
                // Variables were found but were not in draft state, trigger just the propagation to variable entities.
                propagateStudyVariablesDraftRemoval(variables.getRight());
            }
        }
        Pair<StatusCode, ReferenceContainerDataField> attachmentsPair = data.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
        if(attachmentsPair.getLeft() == StatusCode.FIELD_FOUND && !attachmentsPair.getRight().getReferences().isEmpty()) {
            // Attachments found
            for(ReferenceRow row : attachmentsPair.getRight().getReferences()) {
                Pair<ReturnResult, RevisionData> attachment = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
                if(attachment.getLeft() == ReturnResult.REVISION_FOUND && attachment.getRight().getState() == RevisionState.APPROVED) {
                    // Attachment found and is in draft state, just call removeDraft
                    removeLogical(attachment.getRight());
                }
            }

        }
    }

    private void propagateStudyVariablesLogicalRemoval(RevisionData data) {
        Pair<StatusCode, ReferenceContainerDataField> variablesPair = data.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(variablesPair.getLeft() == StatusCode.FIELD_FOUND && !variablesPair.getRight().getReferences().isEmpty()) {
            // Attachments found
            for(ReferenceRow row : variablesPair.getRight().getReferences()) {
                Pair<ReturnResult, RevisionData> variable = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                if(variable.getLeft() == ReturnResult.REVISION_FOUND && variable.getRight().getState() == RevisionState.APPROVED) {
                    // Attachment found and is in draft state, just call removeDraft
                    removeLogical(variable.getRight());
                }
            }

        }
    }*/
}