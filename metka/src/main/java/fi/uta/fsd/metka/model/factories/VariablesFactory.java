package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
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

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES);

        if(conf == null) {
            logger.error("No configuration found for study variables. Halting RevisionData creation.");
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

    public RevisionData newVariable(RevisionEntity entity, Long variablesId, Long studyId) {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY_VARIABLE);

        if(conf == null) {
            logger.error("No configuration found for study variable. Halting RevisionData creation.");
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, conf, time);
        data.dataField(SavedDataFieldCall.set("variables").setTime(time).setValue(variablesId.toString()));
        data.dataField(SavedDataFieldCall.set("study").setTime(time).setValue(studyId.toString()));

        entity.setData(json.serialize(data));

        return data;
    }
}
