package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;


/**
 * Provides functionality common for all RevisionData factories
 */
public abstract class DataFactory {
    /**
     * Creates an empty draft RevisionData.
     *
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param config Configuration that the new revision data should use
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, ConfigurationKey config) {
        RevisionData data = new RevisionData(new RevisionKey(id, no), config);
        data.setHandler(AuthenticationUtil.getUserName());
        data.setState(RevisionState.DRAFT);
        return data;
    }

    /**
     * Used to create new draft revision based on old revision data
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param oldData Old data to which the new data is to be based on
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, RevisionData oldData) {
        return createDraftRevision(id, no, oldData, oldData.getConfiguration());
    }

    /**
     * Used to create new draft reivion based on old revision but using a specific configuration.
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param oldData Old data to which the new data is to be based on
     * @param config Configuration key for the new data
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, RevisionData oldData, ConfigurationKey config) {
        RevisionData data = createDraftRevision(id, no, config);

        // Copies fields from old data to new data using Copy and then normalizes them
        copyDataToNewRevision(oldData, data);

        return data;
    }

    private static void copyDataToNewRevision(RevisionData oldData, RevisionData newData) {

        for(DataField field : oldData.getFields().values()) {
            newData.getFields().put(field.getKey(), field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }
    }
}
