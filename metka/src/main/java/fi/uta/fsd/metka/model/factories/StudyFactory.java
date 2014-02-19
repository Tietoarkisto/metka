package fi.uta.fsd.metka.model.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Study.
 */
@Service
public class StudyFactory {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private ObjectMapper metkaObjectMapper;

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
    public RevisionData newData(RevisionEntity entity, Integer acquisition_number)
            throws IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        DateTime time = new DateTime();

        RevisionData data = RevisionData.createRevisionData(entity, conf.getKey());

        ValueFieldChange change;
        ValueFieldContainer field;
        SimpleValue sv;
        Choicelist list;
        Field confField;

        // Study_id, this is revisionable id
        field = createValueFieldContainer("study_id", time);
        setSimpleValue(field, entity.getKey().getRevisionableId()+"");
        change = createNewRevisionValueFieldChange(field);
        data.putChange(change);

        // Study_id_prefix, this is a string that is added to the front of study_id
        list = conf.getChoicelists().get("id_prefix_list");
        field = createValueFieldContainer("study_id_prefix", time);
        setSimpleValue(field, list.getDef());
        change = createNewRevisionValueFieldChange(field);
        data.putChange(change);

        // Acquisition_number, this is required information for creating a new study
        field = createValueFieldContainer("submissionid", time);
        setSimpleValue(field, acquisition_number+"");
        change = createNewRevisionValueFieldChange(field);
        data.putChange(change);

        // TODO: automate all concat fields
        // create Study_number field, which concatenates study_id_prefix and study_id. This is the basis of study searches.
        field = createValueFieldContainer("id", time);
        confField = conf.getField(field.getKey());
        String concat = "";
        for(String fieldKey : confField.getConcatenate()) {
            /*ValueFieldContainer tempField = (ValueFieldContainer)data.getField(fieldKey);*/
            ValueFieldContainer tempField = getValueFieldContainerFromRevisionData(data, fieldKey);
            concat += extractStringSimpleValue(tempField);
        }
        setSimpleValue(field, concat);
        change = createNewRevisionValueFieldChange(field);
        data.putChange(change);

        entity.setData(metkaObjectMapper.writeValueAsString(data));

        return data;
    }
}
