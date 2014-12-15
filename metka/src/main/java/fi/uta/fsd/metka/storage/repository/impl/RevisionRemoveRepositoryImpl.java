package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
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
    private StudyErrorsRepository errors;

    @Autowired
    private BinderRepository binders;

    @Override
    public RemoveResult remove(TransferData transferData) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        RemoveResult result = allowRemoval(transferData);
        if(result != RemoveResult.ALLOW_REMOVAL) {
            return result;
        }

        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return removeDraft(pair.getRight());
        } else {
            return removeLogical(pair.getRight());
        }
    }

    private RemoveResult removeDraft(RevisionData data) {
        if(!AuthenticationUtil.isHandler(data)) {
            Logger.error(RevisionRemoveRepositoryImpl.class, "User " + AuthenticationUtil.getUserName() + " tried to remove draft belonging to " + data.getHandler());
            return RemoveResult.WRONG_USER;
        }
        RevisionEntity revision = em.find(RevisionEntity.class, RevisionKey.fromModelKey(data.getKey()));
        if(revision == null) {
            Logger.error(RevisionRemoveRepositoryImpl.class, "Draft revision with key "+data.getKey()+" was slated for removal but was not found from database.");
        } else {
            em.remove(revision);
        }

        if(data.getConfiguration().getType() == ConfigurationType.STUDY) {
            propagateStudyDraftRemoval(data);
        } else if(data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
            propagateStudyVariablesDraftRemoval(data);
        }

        List<RevisionEntity> entities = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId=:id", RevisionEntity.class)
                .setParameter("id", data.getKey().getId())
                .getResultList();

        if(entities.isEmpty()) {
            em.remove(em.find(RevisionableEntity.class, data.getKey().getId()));
            finalizeFinalRevisionRemoval(data);
            return RemoveResult.FINAL_REVISION;
        } else {
            RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            return RemoveResult.SUCCESS_DRAFT;
        }
    }

    private void propagateStudyDraftRemoval(RevisionData data) {
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
    }

    private void finalizeFinalRevisionRemoval(RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY: {
                // Study attachments, variables and variable revisions should have already been removed earlier during removal propagation.
                // This part should only remove things that are removed only if we don't have any revisions left.

                // Remove study errors and binder pages linking to this study
                errors.removeErrorsForStudy(data.getKey().getId());
                binders.removeStudyBinderPages(data.getKey().getId());
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

    private RemoveResult removeLogical(RevisionData data) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(data.getKey().getId(), false, data.getConfiguration().getType());

        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // This should never happen since we found the revision data provided for this method
            return RemoveResult.NOT_FOUND;
        }
        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return RemoveResult.OPEN_DRAFT;
        }

        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(pair.getRight().getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(RevisionRemoveRepositoryImpl.class, "Could not find configuration for data "+data.toString());
            return RemoveResult.CONFIGURATION_NOT_FOUND;
        }

        boolean result = true;
        for(Operation operation : confPair.getRight().getRestrictions()) {
            if(operation.getType() != OperationType.DELETE) {
                continue;
            }
            if(!validator.validate(data, operation.getTargets())) {
                result = false;
                break;
            }
        }

        if(!result) {
            return RemoveResult.RESTRICTION_VALIDATION_FAILURE;
        }

        RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
        entity.setRemoved(true);
        entity.setRemovalDate(new LocalDateTime());
        entity.setRemovedBy(AuthenticationUtil.getUserName());

        if(data.getConfiguration().getType() == ConfigurationType.STUDY) {
            propagateStudyLogicalRemoval(data);
        } else if(data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
            propagateStudyVariablesLogicalRemoval(data);
        }

        // TODO: What do we do about study errors and binder pages in this case?

        return RemoveResult.SUCCESS_LOGICAL;
    }

    private void propagateStudyLogicalRemoval(RevisionData data) {
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
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(field.currentAsValueFor(Language.DEFAULT).asInteger(), false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.ALLOW_REMOVAL;
        }
        return AuthenticationUtil.isHandler(pair.getRight())
                ? RemoveResult.ALLOW_REMOVAL
                : (pair.getRight().getState() != RevisionState.DRAFT
                    ? RemoveResult.STUDY_NOT_DRAFT
                    : RemoveResult.WRONG_USER);
    }
}