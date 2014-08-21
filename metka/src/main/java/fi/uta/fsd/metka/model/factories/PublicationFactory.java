package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicationFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(PublicationFactory.class);

    /**
     * Creates and returns a new SERIES draft revision
     *
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param configuration RevisionEntity for which this revision data is created.
     */
    public Pair<ReturnResult, RevisionData> newData(Long id, Integer no, Configuration configuration, String publicationId) {
        if(configuration.getKey().getType() != ConfigurationType.PUBLICATION) {
            logger.error("Called PublicationFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        data.dataField(ValueDataFieldCall.set("publicationid", new Value(publicationId), Language.DEFAULT).setConfiguration(configuration));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
