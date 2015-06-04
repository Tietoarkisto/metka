package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Series.
 */
public class SeriesFactory extends DataFactory {

    /**
     * Creates and returns a new SERIES draft revision
     *
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param configuration RevisionEntity for which this revision data is created.
     */
    public Pair<ReturnResult, RevisionData> newData(Long id, Integer no, Configuration configuration) {
        if(configuration.getKey().getType() != ConfigurationType.SERIES) {
            Logger.error(getClass(), "Called SeriesFactory with type " + configuration.getKey().getType() + " configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
