package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionHandlerRepositoryImpl implements RevisionHandlerRepository {

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, TransferData> changeHandler(RevisionKey key, boolean clear) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }

        RevisionData data = pair.getRight();
        boolean update = false;
        if(clear) {
            if(data.getHandler() != null) {
                data.setHandler(null);
                update = true;
            }
        } else {
            if(!AuthenticationUtil.isHandler(data)) {
                data.setHandler(AuthenticationUtil.getUserName());
                update = true;
            }
        }

        if(update) {
            ReturnResult result = revisions.updateRevisionData(data);
            if (result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, null);
            }
        }

        // For now let's assume that these just work
        finalizeChange(data, clear);

        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(data.getKey().getId());
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(info.getLeft(), null);
        }

        revisions.indexRevision(data.getKey());

        return new ImmutablePair<>(ReturnResult.REVISION_UPDATE_SUCCESSFUL, TransferData.buildFromRevisionData(data, info.getRight()));
    }

    private void finalizeChange(RevisionData revision, boolean clear) {
        switch(revision.getConfiguration().getType()) {
            case STUDY:
                finalizeStudy(revision, clear);
                break;
            case STUDY_VARIABLES:
                finalizeStudyVariables(revision, clear);
                break;
            default:
                break;
        }
    }

    private void finalizeStudy(RevisionData revision, boolean clear) {
        Pair<StatusCode, ReferenceContainerDataField> referenceContainer = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
        if(referenceContainer.getLeft() == StatusCode.FIELD_FOUND && !referenceContainer.getRight().getReferences().isEmpty()) {
            changeStudyAttachmentHandlers(referenceContainer.getRight(), clear);
        }

        Pair<StatusCode, ValueDataField> value = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        if(value.getLeft() == StatusCode.FIELD_FOUND && value.getRight().hasValueFor(Language.DEFAULT)) {
           Pair<ReturnResult, RevisionData> variables = revisions.getLatestRevisionForIdAndType(value.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
           if(variables.getLeft() == ReturnResult.REVISION_FOUND) {
               changeStudyVariablesHandlers(variables.getRight(), clear);
           }
        }
    }

    private void changeStudyVariablesHandlers(RevisionData variables, boolean clear) {
        changeHandler(RevisionKey.fromModelKey(variables.getKey()), clear);
    }

    private void changeStudyAttachmentHandlers(ReferenceContainerDataField references, boolean clear) {
        for(ReferenceRow row : references.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                changeHandler(RevisionKey.fromModelKey(pair.getRight().getKey()), clear);
            }
        }
    }

    private void finalizeStudyVariables(RevisionData variables, boolean clear) {
        Pair<StatusCode, ReferenceContainerDataField> referenceContainer = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(referenceContainer.getLeft() == StatusCode.FIELD_FOUND && !referenceContainer.getRight().getReferences().isEmpty()) {
            changeStudyVariableHandlers(referenceContainer.getRight(), clear);
        }
    }

    private void changeStudyVariableHandlers(ReferenceContainerDataField references, boolean clear) {
        for(ReferenceRow row : references.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(row.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                changeHandler(RevisionKey.fromModelKey(pair.getRight().getKey()), clear);
            }
        }
    }
}
