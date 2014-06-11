package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelFieldUtil.*;

/**
 * Factory related to study variables.
 * Provides initial data methods for study variables and a study variable
 */
@Service
public class VariablesFactory extends DataFactory {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private JSONUtil json;

    public RevisionData newStudyVariables(RevisionEntity entity, Integer studyId, Integer fileId) throws IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, conf, time);
        setSavedDataField(data, "study", studyId.toString(), time);
        setSavedDataField(data, "file", fileId.toString(), time);
        setSavedDataField(data, "varfileid", "F1", time);

        entity.setData(json.serialize(data));

        return data;
    }

    public RevisionData newVariable(RevisionEntity entity, Integer variablesId) throws IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLE);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, conf, time);
        setSavedDataField(data, "variables", variablesId.toString(), time);

        entity.setData(json.serialize(data));

        return data;
    }
}
