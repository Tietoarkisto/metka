package fi.uta.fsd.metka.model.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.data.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/3/14
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SeriesFactory {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private ObjectMapper metkaObjectMapper;

    /**
     * Constructs a new dataset for a Revision entity.
     * Entity should have no previous data and its state should be DRAFT.
     * All field values will be added as UNMODIFIED changes with original value being either
     * empty or a field container containing some default value like revisionable id or choicelist
     * default selection.
     *
     * As a result the supplied RevisionEntity will have a complete initial data set in its data field.
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

        DateTime time = new DateTime();

        RevisionData data = createRevisionData(entity, conf.getKey());

        Change change;
        FieldContainer field;
        SimpleValue sv;

        change = new Change("id");
        field = createFieldContainer(change.getKey(), time);
        // TODO: set savedBy using the user id who requested a new series
        setSimpleValue(field, entity.getKey().getRevisionableId()+"");
        change.setOriginalField(field);
        change.setOperation(ChangeOperation.UNCHANGED);
        data.getChanges().put(change.getKey(), change);

        entity.setData(metkaObjectMapper.writeValueAsString(data));

        return data;
    }

    public RevisionData createRevisionData(RevisionEntity entity, ConfigurationKey configuration) {
        RevisionData data = new RevisionData(
                new RevisionKey(entity.getKey().getRevisionableId(), entity.getKey().getRevisionNo()),
                configuration
        );
        data.setState(entity.getState());
        return data;
    }

    public FieldContainer createFieldContainer(String key, DateTime time) {
        FieldContainer field = new FieldContainer(key);
        field.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return field;
    }

    /**
     * When you create a new revision you need to copy old field values as changes to the new DRAFT.
     * This method does the initialisation of the Change object for you.
     * @param key
     * @param field
     * @return
     */
    public Change createNewRevisionChange(String key, FieldContainer field) {
        Change change = new Change(key);
        change.setOperation(ChangeOperation.UNCHANGED);
        change.setOriginalField(field);

        return change;
    }

    /*
    * This clears existing values from given fields and inserts a new SimpleValue with the given
    * value.
    * WARNING: This does not in any way check what type of value this field is supposed to have
     *         so you can easily insert illegal values with this.
     */
    public void setSimpleValue(FieldContainer field, String value) {
        field.getValues().clear();
        field.getValues().add(new SimpleValue(value));
    }
}
