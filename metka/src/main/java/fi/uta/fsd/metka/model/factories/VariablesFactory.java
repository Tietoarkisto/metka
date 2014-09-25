package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
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

    public Pair<ReturnResult, RevisionData> newStudyVariables(Long id, Integer no, Configuration configuration, String studyId, String fileId, String varfileid, String varfiletype) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLES) {
            logger.error("Called StudyVariablesFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build(new LocalDateTime());

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(ValueDataFieldCall.set("study", new Value(studyId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("file", new Value(fileId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfileid", new Value(varfileid), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfiletype", new Value(varfiletype), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfileno", new Value("F1"), Language.DEFAULT).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    public Pair<ReturnResult, RevisionData> newVariable(Long id, Integer no, Configuration configuration, String variablesId, String studyId, String varName, String varId) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLE) {
            logger.error("Called StudyVariablesFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build(new LocalDateTime());

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(ValueDataFieldCall.set("variables", new Value(variablesId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("study", new Value(studyId), Language.DEFAULT).setInfo(info));
        // Set varid field
        data.dataField(ValueDataFieldCall.set("varname", new Value(varName), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varid", new Value(varId), Language.DEFAULT).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
