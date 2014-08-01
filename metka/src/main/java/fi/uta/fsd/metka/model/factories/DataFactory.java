package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.RevisionKey;
import org.joda.time.LocalDateTime;


/**
 * Provides functionality common for all RevisionData factories
 */
public abstract class DataFactory {
    protected RevisionData createInitialRevision(RevisionEntity entity, Configuration config, LocalDateTime time) {
        RevisionData data = new RevisionData(
                new RevisionKey(entity.getKey().getRevisionableId(), entity.getKey().getRevisionNo()),
                config.getKey()
        );
        data.setState(entity.getState());
        data.dataField(SavedDataFieldCall.set(config.getIdField()).setValue(entity.getKey().getRevisionableId().toString()).setTime(time));
        return data;
    }

    /**
     * Used to create revision data based on old revision and new entity
     * @param entity RevisionEntity of the new revision
     * @param oldData Old data to which the new data is to be based on
     * @return
     */
    public static RevisionData createNewRevisionData(RevisionEntity entity, RevisionData oldData) {
        return createNewRevisionData(entity, oldData, oldData.getConfiguration());
    }

    /**
     * Used to create revision data based on old revision and new entity, with a specific configuration.
     * @param entity RevisionEntity of the new revision
     * @param oldData Old data to which the new data is to be based on
     * @param config Configuration key for the new data
     * @return
     */
    public static RevisionData createNewRevisionData(RevisionEntity entity, RevisionData oldData, ConfigurationKey config) {
        RevisionData data = new RevisionData(
                new RevisionKey(entity.getKey().getRevisionableId(), entity.getKey().getRevisionNo()),
                config
        );
        data.setState(entity.getState());

        // Copies fields from old data to new data using Copy and then normalizes them
        RevisionData.newRevisionBuilder(oldData, data);

        return data;
    }
}
