package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

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

    public RevisionData newStudyVariables(RevisionEntity entity, Long studyId, Long fileId) throws IOException {
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
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId.toString()));
        data.dataField(SavedDataFieldCall.set("file").setTime(time).setValue(fileId.toString()));
        data.dataField(SavedDataFieldCall.set("varfileid").setTime(time).setValue("F1"));

        entity.setData(json.serialize(data));

        return data;
    }

    public RevisionData newVariable(RevisionEntity entity, Long variablesId) throws IOException {
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
        data.dataField(SavedDataFieldCall.set("variables").setTime(time).setValue(variablesId.toString()));

        entity.setData(json.serialize(data));

        return data;
    }
}
