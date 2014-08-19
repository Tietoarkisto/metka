package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Study.
 */
public class StudyFactory extends DataFactory {
    private static Logger logger = LoggerFactory.getLogger(StudyFactory.class);

    /**
     * Construct a new RevisionData for STUDY.
     * Sets the following fields:
     *   studyid_prefix - Letter part of the generated studyid
     *   studyid_number - number part of the generated studyid
     *   studyid - concatenation of the previous two fields
     *   submissionid - number associated with the study and provided as part of the request
     *   dataarrivaldate - date associated with the study and provided as part of the request
     * @param id
     * @param no
     * @param configuration
     * @param studyNumber
     * @param submissionid
     * @param arrivalDate
     * @return
     */
    public Pair<ReturnResult, RevisionData> newData(Long id, Integer no, Configuration configuration, String studyNumber, String submissionid, String arrivalDate) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY) {
            logger.error("Called StudyFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        SelectionList list;
        Field confField;

        // Studyno_prefix, this is a string that is added to the front of study_id
        list = configuration.getRootSelectionList(configuration.getField("studyid_prefix").getSelectionList());
        data.dataField(ValueDataFieldCall.set("studyid_prefix", new Value(list.getDef(), ""), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // studyno, this is a separate sequence from revisionable id and forms the number base for id
        data.dataField(ValueDataFieldCall.set("studyid_number", new Value(studyNumber, ""), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // submissionid, this is required information for creating a new study
        data.dataField(ValueDataFieldCall.set("submissionid", new Value(submissionid, ""), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // create id field, which concatenates studyno_prefix and studyno. This is the basis of study searches.
        // This is more of a proof of concept for concatenate fields than anything.
        confField = configuration.getField("studyid");
        String concat = "";
        for(String fieldKey : confField.getConcatenate()) {
            ValueDataField tempField = data.dataField(ValueDataFieldCall.get(fieldKey)).getRight();
            if(tempField != null) concat += tempField.getActualValueFor(Language.DEFAULT);
        }
        data.dataField(ValueDataFieldCall.set("studyid", new Value(concat, ""), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // Set dataarrivaldate
        data.dataField(ValueDataFieldCall.set("dataarrivaldate", new Value(arrivalDate, ""), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }
}
