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
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.factories.*;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.*;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RevisionCreationRepositoryImpl implements RevisionCreationRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private JSONUtil json;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private SequenceRepository sequences;

    @Autowired
    private Messenger messenger;

    @Override
    public Pair<ReturnResult, RevisionData> create(RevisionCreateRequest request) {
        if(request.getType() == null) {
            Logger.error(getClass(), "No configuration type given and so no creation can occur");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findLatestConfiguration(request.getType());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "No configuration found for "+request.getType()+", halting new revisionable creation.");
            return new ImmutablePair<>(configPair.getLeft(), null);
        }
        ReturnResult result = checkRequestParameters(request);
        if(result != ReturnResult.ALL_PARAMETERS_FOUND) {
            Logger.error(getClass(), "Some parameters missing, cannot continue revision creation");
            return new ImmutablePair<>(result, null);
        }
        RevisionableEntity revisionable = createRevisionable(request);
        if(revisionable == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_CREATED, null);
        }
        em.persist(revisionable);
        RevisionEntity revision = new RevisionEntity(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(new RevisionKey(revisionable.getId(), 1)));
        Pair<ReturnResult, RevisionData> dataPair = createRevisionData(revisionable, revision, configPair.getRight(), request);
        if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
            Logger.error(getClass(), "Couldn't create revision because of: "+dataPair.getLeft());
            Logger.error(getClass(), "Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(dataPair.getLeft(), null);
        }

        RevisionData data = dataPair.getRight();

        Pair<SerializationResults, String> string = json.serialize(data);
        if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
            Logger.error(getClass(), "Couldn't serialize revision " + data.toString());
            Logger.error(getClass(), "Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_CREATE, null);
        }

        revision.setData(string.getRight());
        em.merge(revision);

        revisionable.setLatestRevisionNo(revision.getKey().getRevisionNo());
        finalizeRevisionable(request, revisionable, data);

        em.merge(revisionable);

        revisions.indexRevision(revision.getKey().toModelKey());

        messenger.sendAmqpMessage(messenger.FD_CREATE, new RevisionPayload(data));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    private ReturnResult checkRequestParameters(RevisionCreateRequest request) {
        switch(request.getType()) {
            case STUDY:
                if(!request.getParameters().containsKey(Fields.SUBMISSIONID)) {
                    Logger.error(getClass(), "Creation of STUDY requires that submission id is provided in parameter 'submissionid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.DATAARRIVALDATE)) {
                    Logger.error(getClass(), "Creation of STUDY requires that arrival date for data is provided in parameter 'dataarrivaldate'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_ATTACHMENT:
                // Check that some id is provided, assumes that this id points to a study
                if(!request.getParameters().containsKey(Fields.STUDY)) {
                    Logger.error(getClass(), "Creation of STUDY_ATTACHMENT requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLES:
                if(!request.getParameters().containsKey(Fields.STUDY)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLES requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.LANGUAGE)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLES requires that language is provided in parameter 'language'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.FILEID)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLES requires that study attachment id is provided in parameter 'fileid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.VARFILEID)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLES requires that study attachment file name is provided in parameter 'varfileid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.VARFILETYPE)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLES requires that study attachments file type is provided in parameter 'varfiletype'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLE:
                if(!request.getParameters().containsKey(Fields.STUDY)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLE requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.VARIABLESID)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLE requires that study variables id is provided in parameter 'variablesid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.LANGUAGE)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLE requires that study language is provided in parameter 'language'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.VARNAME)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLE requires that variables varname is provided in parameter 'varname'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey(Fields.VARID)) {
                    Logger.error(getClass(), "Creation of STUDY_VARIABLE requires that variables varid is provided in parameter 'varid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            default:
                break;
        }
        return ReturnResult.ALL_PARAMETERS_FOUND;
    }

    /**
     * Creates correctly sub typed RevisionableEntity based on request
     * @param request    RevisionCreateRequest
     * @return RevisionableEntity
     */
    private RevisionableEntity createRevisionable(RevisionCreateRequest request) {
        RevisionableEntity revisionable;
        switch(request.getType()) {
            case BINDER_PAGE:
                revisionable = new BinderPageEntity();
                break;
            case STUDY_ERROR:
                revisionable = new StudyErrorEntity();
                break;
            case SERIES:
                revisionable = new SeriesEntity();
                break;
            case STUDY:
                revisionable = new StudyEntity();
                break;
            case PUBLICATION:
                PublicationEntity p = new PublicationEntity();
                p.setPublicationId(sequences.getNewSequenceValue(ConfigurationType.PUBLICATION.toValue(), 3000L).getSequence());
                revisionable = p;
                break;
            case STUDY_ATTACHMENT:
                StudyAttachmentEntity sae = new StudyAttachmentEntity();
                sae.setStudyAttachmentStudy(Long.parseLong(request.getParameters().get(Fields.STUDY)));
                revisionable = sae;
                break;
            case STUDY_VARIABLES:
                StudyVariablesEntity svs = new StudyVariablesEntity();
                svs.setStudyVariablesStudy(Long.parseLong(request.getParameters().get(Fields.STUDY)));
                revisionable = svs;
                break;
            case STUDY_VARIABLE:
                StudyVariableEntity sv = new StudyVariableEntity();
                sv.setStudyVariableStudy(Long.parseLong(request.getParameters().get(Fields.STUDY)));
                sv.setStudyVariablesId(Long.parseLong(request.getParameters().get(Fields.VARIABLESID)));
                sv.setVarId(request.getParameters().get(Fields.VARID));
                revisionable = sv;
                break;
            default:
                // shouldn't happen
                return null;
        }
        return revisionable;
    }

    /**
     * Uses a DataFactory subclass to create initial RevisionData. Pulls parameters from request.
     * @param revisionable     RevisionableEntity
     * @param revision         RevisionEntity
     * @param configuration    Configuration
     * @param request          RevisionCreateRequest
     * @return Pair - ReturnResult, RevisionData
     */
    private Pair<ReturnResult, RevisionData> createRevisionData(RevisionableEntity revisionable, RevisionEntity revision,
                                                                Configuration configuration, RevisionCreateRequest request) {
        Pair<ReturnResult, RevisionData> data;
        switch(request.getType()) {
            case BINDER_PAGE:
                data = new ImmutablePair<>(ReturnResult.REVISION_CREATED,
                        DataFactory.createDraftRevision(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration.getKey()));
                break;
            case STUDY_ERROR:
                data = new ImmutablePair<>(ReturnResult.REVISION_CREATED,
                        DataFactory.createDraftRevision(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration.getKey()));
                break;
            case SERIES: {
                SeriesFactory factory = new SeriesFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration);
                break;
            }
            case STUDY: {
                StudyFactory factory = new StudyFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        sequences.getNewSequenceValue(ConfigurationType.STUDY.toValue(), 10000L).getSequence().toString(),
                        request.getParameters().get(Fields.SUBMISSIONID), request.getParameters().get(Fields.DATAARRIVALDATE));
                break;
            }
            case STUDY_ATTACHMENT: {
                StudyAttachmentFactory factory = new StudyAttachmentFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get(Fields.STUDY));
                break;
            }
            case STUDY_VARIABLES: {
                VariablesFactory factory = new VariablesFactory();
                data = factory.newStudyVariables(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get(Fields.STUDY), request.getParameters().get(Fields.FILEID),
                        request.getParameters().get(Fields.VARFILEID), request.getParameters().get(Fields.VARFILETYPE),
                        request.getParameters().get(Fields.LANGUAGE));
                break;
            }
            case STUDY_VARIABLE: {
                VariablesFactory factory = new VariablesFactory();
                data = factory.newVariable(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get(Fields.VARIABLESID), request.getParameters().get(Fields.STUDY), request.getParameters().get(Fields.VARNAME),
                        request.getParameters().get(Fields.VARID), request.getParameters().get(Fields.LANGUAGE));
                break;
            }
            case PUBLICATION: {
                PublicationFactory factory = new PublicationFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        ((PublicationEntity)revisionable).getPublicationId().toString());
                break;
            }
            default:
                // Shouldn't happen
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }
        return data;
    }

    /**
     * This is called to insert any possible values that could not be assigned before RevisionData creation
     * @param request         RevisionCreateRequest
     * @param revisionable    RevisionableEntity
     * @param data            RevisionData
     */
    private void finalizeRevisionable(RevisionCreateRequest request, RevisionableEntity revisionable, RevisionData data) {
        switch(request.getType()) {
            case STUDY:
                finalizeStudy((StudyEntity) revisionable, data);
                break;
            case STUDY_ATTACHMENT:
                finalizeStudyAttachment((StudyAttachmentEntity)revisionable);
                break;
            case PUBLICATION:
                finalizePublication(data);
                break;
            default:
                // Nothing to finalize
                break;
        }
    }

    /**
     * Set generated study id into StudyEntity
     * @param revisionable StudyEntity
     * @param data         RevisionData
     */
    private void finalizeStudy(StudyEntity revisionable, RevisionData data) {
        ValueDataField studyid = data.dataField(ValueDataFieldCall.get("studyid")).getRight();
        revisionable.setStudyId(studyid.getActualValueFor(Language.DEFAULT));
    }

    /**
     * Adds study attachment reference to "files" reference container on target study if it's yet not present (should not be).
     * @param revisionable StudyAttachmentEntity used for all relevant info about study attachment
     */
    private void finalizeStudyAttachment(StudyAttachmentEntity revisionable) {
        // Get the latest revision for study and, if it exists, get or create files reference container
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(revisionable.getStudyAttachmentStudy().toString());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Didn't find  latest revision for study with id "+revisionable.getStudyAttachmentStudy()+" with result "+dataPair.getLeft());
            return;
        }

        RevisionData studyRevision = dataPair.getRight();
        Pair<StatusCode, ReferenceContainerDataField> filesPair = studyRevision.dataField(ReferenceContainerDataFieldCall.get(Fields.FILES));
        if(filesPair.getLeft() != StatusCode.FIELD_FOUND) {
            filesPair = studyRevision.dataField(ReferenceContainerDataFieldCall.set(Fields.FILES));
            if(filesPair.getLeft() != StatusCode.FIELD_INSERT) {
                Logger.error(getClass(), "Couldn't create files reference container for study "+studyRevision.toString());
                return;
            }
        }
        ReferenceContainerDataField files = filesPair.getRight();

        // There shouldn't be a study attachment reference in the files container at this point but you never know, so let's get or create the reference
        Pair<StatusCode, ReferenceRow> referencePair = files.getOrCreateReferenceWithValue(revisionable.getId().toString()+"-"+revisionable.getLatestRevisionNo(), studyRevision.getChanges(), DateTimeUserPair.build());

        // If new row was inserted then we now have a change in study revision, update revision to database
        if(referencePair.getLeft() == StatusCode.ROW_INSERT) {
            ReturnResult updateResult = revisions.updateRevisionData(studyRevision);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                Logger.error(getClass(), "Could not update "+studyRevision.toString()+", received result "+updateResult);
            }
        }
    }

    private void finalizePublication(RevisionData data) {
        Pair<StatusCode, ValueDataField> pair = data.dataField(ValueDataFieldCall.get(Fields.PUBLICATIONFIRSTSAVED));
        if(pair.getLeft() == StatusCode.FIELD_FOUND && pair.getRight().hasValueFor(Language.DEFAULT)) {
            return;
        }
        data.dataField(ValueDataFieldCall.set(Fields.PUBLICATIONFIRSTSAVED, new Value((new LocalDate()).toString()), Language.DEFAULT).setChangeMap(data.getChanges()));
    }
}
