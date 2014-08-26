package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.factories.*;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.*;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RevisionCreationRepositoryImpl implements RevisionCreationRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionCreationRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private JSONUtil json;

    @Autowired
    private GeneralRepository general;

    @Override
    public Pair<ReturnResult, RevisionData> create(RevisionCreateRequest request) {
        // TODO: Implement type specific creation requests
        Pair<ReturnResult, Configuration> configPair;
        switch(request.getType()) {
            case SERIES:
            case STUDY:
            case PUBLICATION:
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLES:
            case STUDY_VARIABLE:
                configPair = configurations.findLatestConfiguration(request.getType());
                break;
            default:
                logger.warn("Tried to create revisionable "+request.getType()+" which is not handled here but is instead created through some other means");
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("No configuration found for "+request.getType()+", halting new revisionable creation.");
            return new ImmutablePair<>(configPair.getLeft(), null);
        }
        ReturnResult result = checkRequestParameters(request);
        if(result != ReturnResult.ALL_PARAMETERS_FOUND) {
            logger.error("Some parameters missing, cannot continue revision creation");
            return new ImmutablePair<>(result, null);
        }
        RevisionableEntity revisionable = createRevisionable(request);
        if(revisionable == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_CREATED, null);
        }
        em.persist(revisionable);
        RevisionEntity revision = new RevisionEntity(new RevisionKey(revisionable.getId(), 1));
        Pair<ReturnResult, RevisionData> dataPair = createRevisionData(revisionable, revision, configPair.getRight(), request);
        if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
            logger.error("Couldn't create revision because of: "+dataPair.getLeft());
            logger.error("Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(dataPair.getLeft(), null);
        }

        Pair<SerializationResults, String> string = json.serialize(dataPair.getRight());
        if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
            logger.error("Couldn't serialize revision "+dataPair.getRight().toString());
            logger.error("Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_CREATE, null);
        }

        revision.setData(string.getRight());
        em.merge(revision);

        finalizeRevisionable(request, revisionable, dataPair.getRight());

        revisionable.setLatestRevisionNo(revision.getKey().getRevisionNo());
        em.merge(revisionable);

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, dataPair.getRight());
    }

    private ReturnResult checkRequestParameters(RevisionCreateRequest request) {
        switch(request.getType()) {
            case STUDY:
                if(!request.getParameters().containsKey("submissionid")) {
                    logger.error("Creation of STUDY requires that submission id is provided in parameter 'submissionid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("dataarrivaldate")) {
                    logger.error("Creation of STUDY requires that arrival date for data is provided in parameter 'dataarrivaldate'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_ATTACHMENT:
                // Check that some id is provided, assumes that this id points to a study
                if(!request.getParameters().containsKey("study")) {
                    logger.error("Creation of STUDY_ATTACHMENT requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLES:
                if(!request.getParameters().containsKey("study")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("fileid")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study attachment id is provided in parameter 'fileid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLE:
                if(!request.getParameters().containsKey("study")) {
                    logger.error("Creation of STUDY_VARIABLE requires that study.key.id is provided in parameter 'study'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("variablesid")) {
                    logger.error("Creation of STUDY_VARIABLE requires that study variables id is provided in parameter 'variablesid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("varid")) {
                    logger.error("Creation of STUDY_VARIABLE requires that variables varid is provided in parameter 'varid'");
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
            case SERIES:
                revisionable = new SeriesEntity();
                break;
            case STUDY:
                revisionable = new StudyEntity();
                break;
            case PUBLICATION:
                PublicationEntity p = new PublicationEntity();
                p.setPublicationId(general.getNewSequenceValue(ConfigurationType.PUBLICATION.toValue(), 3000L).getSequence());
                revisionable = p;
                break;
            case STUDY_ATTACHMENT:
                StudyAttachmentEntity sae = new StudyAttachmentEntity();
                sae.setStudy(Long.parseLong(request.getParameters().get("study")));
                revisionable = sae;
                break;
            case STUDY_VARIABLES:
                StudyVariablesEntity svs = new StudyVariablesEntity();
                svs.setStudy(Long.parseLong(request.getParameters().get("study")));
                revisionable = svs;
                break;
            case STUDY_VARIABLE:
                StudyVariableEntity sv = new StudyVariableEntity();
                sv.setStudy(Long.parseLong(request.getParameters().get("study")));
                sv.setStudyVariablesId(Long.parseLong(request.getParameters().get("variablesid")));
                sv.setVarId(request.getParameters().get("varid"));
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
            case SERIES: {
                SeriesFactory factory = new SeriesFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration);
                break;
            }
            case STUDY: {
                StudyFactory factory = new StudyFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        general.getNewSequenceValue(ConfigurationType.STUDY.toValue(), 10000L).getSequence().toString(),
                        request.getParameters().get("submissionid"), request.getParameters().get("dataarrivaldate"));
                break;
            }
            case STUDY_ATTACHMENT: {
                StudyAttachmentFactory factory = new StudyAttachmentFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get("study"));
                break;
            }
            case STUDY_VARIABLES: {
                VariablesFactory factory = new VariablesFactory();
                data = factory.newStudyVariables(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get("studyid"), request.getParameters().get("fileid"));
                break;
            }
            case STUDY_VARIABLE: {
                VariablesFactory factory = new VariablesFactory();
                data = factory.newVariable(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration,
                        request.getParameters().get("variablesid"), request.getParameters().get("studyid"), request.getParameters().get("varid"));
                break;
            }
            case PUBLICATION: {
                PublicationFactory factory = new PublicationFactory();
                data = factory.newData(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), configuration, ((PublicationEntity)revisionable).getPublicationId().toString());
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
        Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(revisionable.getStudy(), false, ConfigurationType.STUDY);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Didn't find  latest revision for study with id "+revisionable.getStudy()+" with result "+dataPair.getLeft());
            return;
        }

        RevisionData studyRevision = dataPair.getRight();
        Pair<StatusCode, ReferenceContainerDataField> filesPair = studyRevision.dataField(ReferenceContainerDataFieldCall.get("files"));
        if(filesPair.getLeft() != StatusCode.FIELD_FOUND) {
            filesPair = studyRevision.dataField(ReferenceContainerDataFieldCall.set("files"));
            if(filesPair.getLeft() != StatusCode.FIELD_INSERT) {
                logger.error("Couldn't create files reference container for study "+studyRevision.toString());
                return;
            }
        }
        ReferenceContainerDataField files = filesPair.getRight();

        // There shouldn't be a study attachment reference in the files container at this point but you never know, so let's get or create the reference
        Pair<StatusCode, ReferenceRow> referencePair = files.getOrCreateReferenceWithValue(revisionable.getId().toString(), studyRevision.getChanges(), DateTimeUserPair.build());

        // If new row was inserted then we now have a change in study revision, update revision to database
        if(referencePair.getLeft() == StatusCode.NEW_ROW) {
            ReturnResult updateResult = general.updateRevisionData(studyRevision);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                logger.error("Could not update "+studyRevision.toString()+", received result "+updateResult);
            }
        }
    }
}
