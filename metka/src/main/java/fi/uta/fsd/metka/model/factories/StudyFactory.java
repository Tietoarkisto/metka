package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Study.
 */
@Service
public class StudyFactory extends DataFactory {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private JSONUtil json;

    /**
     * Constructs a new dataset for a Revision entity.
     * Entity should have no previous data and its state should be DRAFT.
     * All required field values that can be inserted automatically will be added as
     * modified values containing some default value like revisionable id or selectionList default selection.
     *
     * As a result the supplied RevisionEntity will have a every required field that can be automatically set
     * initialised to its default value.
     *
     * TODO: This should be made more dynamic using the configuration as a processing instruction
     *       but for now everything is done manually.
     *
     * @param entity RevisionEntity for which this revision data is created.
     */
    public RevisionData newData(RevisionEntity entity, Integer acquisition_number) throws IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, conf.getKey());

        SavedDataField field;
        SelectionList list;
        Field confField;

        // TODO: define autofill fields in the configuration.
        // These can be basically idField, CONCAT fields and SELECTION fields (insert selectionList default value if any) as well as values that are expected to be delivered to Factory.

        // Study_id, this is revisionable id
        field = new SavedDataField(conf.getIdField());
        field.setModifiedValue(setSimpleValue(createSavedValue(time), entity.getKey().getRevisionableId() + ""));
        data.putField(field).putChange(new Change(field.getKey()));

        // Study_id_prefix, this is a string that is added to the front of study_id
        list = conf.getSelectionList("id_prefix_list");
        field = new SavedDataField("study_id_prefix");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), list.getDef()));
        data.putField(field).putChange(new Change(field.getKey()));

        // Acquisition_number, this is required information for creating a new study
        field = new SavedDataField("submissionid");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), acquisition_number+""));
        data.putField(field).putChange(new Change(field.getKey()));

        // create Study_number field, which concatenates study_id_prefix and study_id. This is the basis of study searches.
        // This is more of a proof of concept for concatenate fields than anything.
        field = new SavedDataField("id");
        confField = conf.getField(field.getKey());
        String concat = "";
        for(String fieldKey : confField.getConcatenate()) {
            SavedDataField tempField = getSavedDataFieldFromRevisionData(data, fieldKey);
            concat += extractStringSimpleValue(tempField);
        }
        field.setModifiedValue(setSimpleValue(createSavedValue(time), concat));
        data.putField(field).putChange(new Change(field.getKey()));

        // TODO: Tieto tulee tiipiistä, toistaiseksi käytetään kuluvaa päivää
        field = new SavedDataField("dataarrivaldate");
        field.setModifiedValue(setSimpleValue(createSavedValue(time), new LocalDate().toString()));
        data.putField(field);

        entity.setData(json.serialize(data));

        return data;
    }
}
