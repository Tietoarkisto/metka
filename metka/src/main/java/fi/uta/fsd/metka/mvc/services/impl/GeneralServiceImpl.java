package fi.uta.fsd.metka.mvc.services.impl;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.ddi.builder.DDIBuilder;
import fi.uta.fsd.metka.ddi.reader.DDIReader;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private DDIBuilder ddiBuilder;

    @Autowired
    private DDIReader ddiReader;

    // TODO: Move to revision service
    /**
     * Return the id of next or previous revisionable of the same type as the current revisionable the user is looking at.
     * Used to navigate to previous or next object.
     *
     * @param currentId Id of the revisionable the user is looking at at the moment
     * @param type What type of revisionable is required (series, publication etc.)
     * @param forward do we want next or previous revisionable
     * @return Id of the adjanced revisionable object. If not found then error is thrown instead.
     */
    @Override public Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, ConfigurationType type, boolean forward) {
        return revisions.getAdjacentRevisionableId(currentId, type, forward);
    }

    /**
     * Returns a RevisionData for a specific revision id and number.
     * Doesn't check the returned revision for type.
     * @param id
     * @param revision
     * @return
     */
    @Override public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision) {
        return revisions.getRevisionData(id, revision);
    }

    @Override
    public Pair<ReturnResult, CodeBookDocument> exportDDI(Long id, Integer no, Language language) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(id, no);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Return error to user
            return new ImmutablePair<>(pair.getLeft(), null);
        } else if(pair.getRight().getConfiguration().getType() != ConfigurationType.STUDY) {
            // Only applicaple to studies
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        } else {
            RevisionData revision = pair.getRight();
            Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(revision.getConfiguration());
            if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                return new ImmutablePair<>(configurationPair.getLeft(), null);
            }
            Pair<ReturnResult, CodeBookDocument> cb = ddiBuilder.buildDDIDocument(language, revision, configurationPair.getRight());
            return cb;
        }
    }

    @Override
    public ReturnResult importDDI(TransferData transferData, String path) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return pair.getLeft();
        }
        return ddiReader.readDDIDocument(path, pair.getRight());
    }
}
