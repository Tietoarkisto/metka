package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Factory related to study variables.
 * Provides initial data methods for study variables and a study variable
 */
@Service
public class VariablesFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(VariablesFactory.class);
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private JSONUtil json;

    public RevisionData newStudyVariables(RevisionEntity entity, Long studyId, Long fileId) {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Pair<ReturnResult, Configuration> pair = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES);

        if(pair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("No configuration found for study variables. Halting RevisionData creation.");
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, pair.getRight());
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId.toString()));
        data.dataField(SavedDataFieldCall.set("file").setTime(time).setValue(fileId.toString()));
        data.dataField(SavedDataFieldCall.set("varfileid").setTime(time).setValue("F1"));

        Pair<ReturnResult, String> string = json.serialize(data);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Failed to serialize "+data.toString());
            return null;
        } else {
            entity.setData(string.getRight());
            return data;
        }
    }

    public RevisionData newVariable(RevisionEntity entity, Long variablesId, Long studyId) {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Pair<ReturnResult, Configuration> pair = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLE);

        if(pair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("No configuration found for study variable. Halting RevisionData creation.");
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, pair.getRight());
        data.dataField(SavedDataFieldCall.set("variables").setTime(time).setValue(variablesId.toString()));
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId.toString()));

        Pair<ReturnResult, String> string = json.serialize(data);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Failed to serialize "+data.toString());
            return null;
        } else {
            entity.setData(string.getRight());
            return data;
        }
    }
}
