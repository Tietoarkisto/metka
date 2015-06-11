package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueContainer;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class RevisionEditRepositoryImpl implements RevisionEditRepository {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionHandlerRepository handler;

    @Override
    public Pair<ReturnResult, RevisionData> edit(TransferData transferData) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
                transferData.getKey().getId(), false, transferData.getConfiguration().getType());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "No revision for " + transferData.getConfiguration().getType() + " with id " + transferData.getKey().getId() + ". Can't get editable revision.");
            return dataPair;
        }
        RevisionData data = dataPair.getRight();
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            Logger.error(getClass(), "No revisionable for object for which revision was already found "+data.toString());
            return new ImmutablePair<>(infoPair.getLeft(), data);
        }

        if(infoPair.getRight().getRemoved()) {
            Logger.warning(getClass(), "Can't create draft for removed Revisionable " + data.toString());
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_REMOVED, data);
        }

        ReturnResult result;
        // If data is not a draft then try to create a new draft
        // If data is missing a handler then try to claim the data
        // Return either a claimed draft or a draft that is handled by someone else
        if(data.getState() != RevisionState.DRAFT) {
            // Data is not draft, we need a new revision
            result = checkEditPermissions(data);
            if(result != ReturnResult.CAN_CREATE_DRAFT) {
                Logger.warning(getClass(), "User can't create draft revision because: "+result);
                return new ImmutablePair<>(result, data);
            }
            Pair<ReturnResult, Configuration> configPair = configurations.findLatestConfiguration(data.getConfiguration().getType());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Couldn't find newest configuration for "+data.getConfiguration().getType()+" so can't create new editable revision for "+data.toString());
                return new ImmutablePair<>(configPair.getLeft(), data);
            }

            Pair<ReturnResult, RevisionKey> keyPair = revisions.createNewRevision(data);
            if(keyPair.getLeft() != ReturnResult.REVISION_CREATED) {
                return new ImmutablePair<>(keyPair.getLeft(), data);
            }

            // TODO: If update fails then the possibly created revision should be removed
            RevisionData newData = new RevisionData(keyPair.getRight().toModelKey(), configPair.getRight().getKey());
            newData.setState(RevisionState.DRAFT);
            copyDataToNewRevision(data, newData);
            // Move approve information to new revision, these are updated as needed when actual approval is required.
            for(Language lang : data.getApproved().keySet()) {
                newData.approveRevision(lang, data.approveInfoFor(lang));
            }

            ReturnResult update = revisions.updateRevisionData(newData);
            if(update != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(update, data);
            } else {
                data = newData;
            }
            result = ReturnResult.REVISION_CREATED;
        } else {
            result = ReturnResult.REVISION_FOUND;
        }
        if(!StringUtils.hasText(data.getHandler())) {
            // Try to claim revision since it hasn't been claimed yet
            handler.changeHandler(RevisionKey.fromModelKey(data.getKey()), false);
            // Get the revision again so that we have a claimed version
            dataPair = revisions.getRevisionData(RevisionKey.fromModelKey(data.getKey()));
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                return dataPair;
            }
            data = dataPair.getRight();
        }

        if(result == ReturnResult.REVISION_CREATED) {
            revisions.indexRevision(data.getKey());
        }

        return new ImmutablePair<>(result, data);
    }

    private ReturnResult checkEditPermissions(RevisionData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                Pair<StatusCode, ValueDataField> field = data.dataField(ValueDataFieldCall.get("study"));
                if(field.getLeft() != StatusCode.FIELD_FOUND) {
                    Logger.error(getClass(), "Didn't find study reference on "+data.toString()+" can't create new draft.");
                    return ReturnResult.REVISIONABLE_NOT_FOUND;
                }
                ValueContainer vc = field.getRight().getValueFor(Language.DEFAULT);
                return vc == null ? ReturnResult.REVISION_FOUND : checkStudyDraftStatus(vc.valueAsInteger());
            default:
                return ReturnResult.CAN_CREATE_DRAFT;
        }
    }

    private ReturnResult checkStudyDraftStatus(Long id) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(id, false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Didn't find revision for study id "+id+" with result "+pair.getLeft());
            return pair.getLeft();
        }
        if(pair.getRight().getState() != RevisionState.DRAFT) {
            return ReturnResult.REVISION_NOT_A_DRAFT;
        }

        if(!AuthenticationUtil.isHandler(pair.getRight())) {
            return ReturnResult.USER_NOT_HANDLER;
        }
        return ReturnResult.CAN_CREATE_DRAFT;
    }

    private void copyDataToNewRevision(RevisionData oldData, RevisionData newData) {

        for(DataField field : oldData.getFields().values()) {
            newData.getFields().put(field.getKey(), field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }
    }
}
