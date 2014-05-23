package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

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

        RevisionData data = createInitialRevision(entity, conf.getKey());

        SavedDataField field = new SavedDataField(conf.getIdField());
        field.setModifiedValue(setSimpleValue(createSavedValue(time), entity.getKey().getRevisionableId() + ""));
        data.putField(field).putChange(new Change(field.getKey()));

        field = new SavedDataField("study");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), studyId.toString()));
        data.putField(field).putChange(new Change(field.getKey()));

        field = new SavedDataField("file");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), fileId.toString()));
        data.putField(field).putChange(new Change(field.getKey()));

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

        RevisionData data = createInitialRevision(entity, conf.getKey());

        SavedDataField field = new SavedDataField(conf.getIdField());
        field.setModifiedValue(setSimpleValue(createSavedValue(time), entity.getKey().getRevisionableId() + ""));
        data.putField(field).putChange(new Change(field.getKey()));

        field = new SavedDataField("variables");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), variablesId.toString()));
        data.putField(field).putChange(new Change(field.getKey()));

        entity.setData(json.serialize(data));

        return data;
    }
}
