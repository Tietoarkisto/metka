package fi.uta.fsd.metka.model.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Series.
 */
@Service
public class SeriesFactory {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private JSONUtil json;

    /**
     * Constructs a new dataset for a Revision entity.
     * Entity should have no previous data and its state should be DRAFT.
     * All required field values that can be inserted automatically will be added as UNMODIFIED changes with
     * original value containing some default value like revisionable id or choicelist default selection.
     *
     * As a result the supplied RevisionEntity will have a every required field that can be automatically set
     * initialised to its default value.
     *
     * TODO: This should be made more dynamic using the configuration as a processing instruction
     *       but for now everything is done manually.
     *
     * @param entity RevisionEntity for which this revision data is created.
     */
    public RevisionData newData(RevisionEntity entity)
            throws JsonProcessingException, JsonMappingException, IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.SERIES);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        DateTime time = new DateTime();

        RevisionData data = RevisionData.createRevisionData(entity, conf.getKey());

        ValueFieldChange change;
        ValueFieldContainer field;

        field = createValueFieldContainer(conf.getIdField(), time);
        setSimpleValue(field, entity.getKey().getRevisionableId()+"");
        change = createNewRevisionValueFieldChange(field);
        data.putChange(change);

        entity.setData(json.serialize(data));

        return data;
    }
}