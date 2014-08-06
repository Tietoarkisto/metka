package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory related to study variables.
 * Provides initial data methods for study variables and a study variable
 */
public class VariablesFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(VariablesFactory.class);

    public Pair<ReturnResult, RevisionData> newStudyVariables(Long id, Integer no, Configuration configuration, String studyId, String fileId) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLES) {
            logger.error("Called StudyVariablesFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId));
        data.dataField(SavedDataFieldCall.set("file").setTime(time).setValue(fileId));
        data.dataField(SavedDataFieldCall.set("varfileid").setTime(time).setValue("F1"));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    public Pair<ReturnResult, RevisionData> newVariable(Long id, Integer no, Configuration configuration, String variablesId, String studyId) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLE) {
            logger.error("Called StudyVariablesFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(SavedDataFieldCall.set("variables").setTime(time).setValue(variablesId));
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
