package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.SeriesFactory;
import fi.uta.fsd.metka.model.factories.StudyAttachmentFactory;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.*;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
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
        Pair<ReturnResult, RevisionData> dataPair = createRevisionData(revision, configPair.getRight(), request);
        if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
            logger.error("Couldn't create revision because of: "+dataPair.getLeft());
            logger.error("Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(dataPair.getLeft(), null);
        }

        Pair<ReturnResult, String> string = json.serialize(dataPair.getRight());
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Couldn't serialize revision "+dataPair.getRight().toString());
            logger.error("Removing revisionable "+revisionable.toString());
            em.remove(revisionable);
            return new ImmutablePair<>(string.getLeft(), null);
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
                if(!request.getParameters().containsKey("id")) {
                    logger.error("Creation of STUDY_ATTACHMENT requires that study.key.id is provided in parameter 'id'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLES:
                if(!request.getParameters().containsKey("studyid")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study.key.id is provided in parameter 'studyid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("fileid")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study attachment id is provided in parameter 'fileid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            case STUDY_VARIABLE:
                if(!request.getParameters().containsKey("studyid")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study.key.id is provided in parameter 'studyid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                if(!request.getParameters().containsKey("variablesid")) {
                    logger.error("Creation of STUDY_VARIABLES requires that study attachment id is provided in parameter 'variablesid'");
                    return ReturnResult.PARAMETERS_MISSING;
                }
                break;
            default:
                break;
        }
        return ReturnResult.ALL_PARAMETERS_FOUND;
    }

    private RevisionableEntity createRevisionable(RevisionCreateRequest request) {
        RevisionableEntity revisionable = null;
        switch(request.getType()) {
            case SERIES:
                revisionable = new SeriesEntity();
                break;
            case STUDY:

                revisionable = new StudyEntity();
                break;
            case STUDY_ATTACHMENT:
                // Check that some id is provided, assumes that this id points to a study
                revisionable = new StudyAttachmentEntity();
                ((StudyAttachmentEntity)revisionable).setStudyId(Long.parseLong(request.getParameters().get("id")));
                break;
            case STUDY_VARIABLES:
                StudyVariablesEntity svs = new StudyVariablesEntity();
                svs.setStudyId(Long.parseLong(request.getParameters().get("studyid")));
                revisionable = svs;
                break;
            case STUDY_VARIABLE:
                StudyVariableEntity sv = new StudyVariableEntity();
                sv.setStudyId(Long.parseLong(request.getParameters().get("studyid")));
                sv.setStudyVariablesId(Long.parseLong(request.getParameters().get("variablesid")));
                revisionable =sv;
            case PUBLICATION:
                // TODO: Publication
                break;
            default:
                // shouldn't happen
                return null;
        }
        return revisionable;
    }

    private Pair<ReturnResult, RevisionData> createRevisionData(RevisionEntity revision, Configuration configuration, RevisionCreateRequest request) {
        Pair<ReturnResult, RevisionData> data = null;
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
                        request.getParameters().get("id"));
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
                        request.getParameters().get("variablesid"), request.getParameters().get("studyid"));
                break;
            }
            case PUBLICATION:
                break;
            default:
                // Shouldn't happen
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }
        return data;
    }

    /**
     * This is called to insert any possible values that could not be assigned before RevisionData creation
     * @param request
     * @param revisionable
     * @param data
     */
    private void finalizeRevisionable(RevisionCreateRequest request, RevisionableEntity revisionable, RevisionData data) {
        switch(request.getType()) {
            case STUDY_ATTACHMENT:
                // TODO: Add a row for this study attachment to the newest revision of target study (which should be a DRAFT but don't check that)
                //Pair<ReturnResult, RevisionData> pair = general.getLatestRevisionForIdAndType()
                break;
            default:
                // Nothing to finalize
                break;
        }
    }
}
