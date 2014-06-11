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
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static fi.uta.fsd.metka.data.util.ModelFieldUtil.*;
import static fi.uta.fsd.metka.data.util.ModelValueUtil.*;

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
    public RevisionData newData(RevisionEntity entity, Integer studyNumber, Integer submissionid) throws IOException {
        if(StringUtils.isEmpty(entity.getData()) && entity.getState() != RevisionState.DRAFT)
            return null;

        Configuration conf = null;
        conf = configurationRepository.findLatestConfiguration(ConfigurationType.STUDY);

        if(conf == null) {
            // TODO: Log error that no configuration found for study
            return null;
        }

        LocalDateTime time = new LocalDateTime();

        RevisionData data = createInitialRevision(entity, conf, time);

        SelectionList list;
        Field confField;

        // TODO: define autofill fields in the configuration.
        // These can be basically idField, CONCAT fields and SELECTION fields (insert selectionList default value if any) as well as values that are expected to be delivered to Factory.

        // Studyno_prefix, this is a string that is added to the front of study_id
        list = conf.getRootSelectionList(conf.getField("studyno_prefix").getSelectionList());
        setSavedDataField(data, "studyno_prefix", list.getDef(), time);

        // studyno, this is a separate sequence from revisionable id and forms the number base for id
        setSavedDataField(data, "studyno_number", studyNumber.toString(), time);

        // submissionid, this is required information for creating a new study
        setSavedDataField(data, "submissionid", submissionid.toString(), time);

        // create id field, which concatenates studyno_prefix and studyno. This is the basis of study searches.
        // This is more of a proof of concept for concatenate fields than anything.
        confField = conf.getField("id");
        String concat = "";
        for(String fieldKey : confField.getConcatenate()) {
            SavedDataField tempField = getSimpleSavedDataField(data, fieldKey);
            concat += extractStringSimpleValue(tempField);
        }
        setSavedDataField(data, "id", concat, time);

        // Set dataarrivaldate
        // TODO: Tieto tulee tiipiistä, toistaiseksi käytetään kuluvaa päivää
        setSavedDataField(data, "dataarrivaldate", new LocalDate().toString(), time);


        entity.setData(json.serialize(data));

        return data;
    }
}
