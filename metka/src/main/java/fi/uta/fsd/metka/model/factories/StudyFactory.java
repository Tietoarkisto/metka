package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Study.
 */
public class StudyFactory extends DataFactory {
    private static Map<Language, String> compFile = new HashMap<>();
    private static Map<Language, String> collector = new HashMap<>();
    private static Map<Language, String> producer = new HashMap<>();
    private static Map<Language, String> distributor = new HashMap<>();

    static {
        compFile.put(Language.DEFAULT, "[elektroninen aineisto]");
        compFile.put(Language.EN, "[computer file]");
        compFile.put(Language.SV, "[datafil]");

        collector.put(Language.DEFAULT, "[aineistonkeruu]");
        collector.put(Language.EN, "[data collection]");
        collector.put(Language.SV, "[datainsamling]");

        producer.put(Language.DEFAULT, "[tuottaja]");
        producer.put(Language.EN, "[producer]");
        producer.put(Language.SV, "[producent]");

        distributor.put(Language.DEFAULT, "[jakaja]");
        distributor.put(Language.EN, "[distributor]");
        distributor.put(Language.SV, "[distributör]");
    }

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
            Logger.error(StudyFactory.class, "Called StudyFactory with type " + configuration.getKey().getType() + " configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        SelectionList list;
        Field confField;

        // Create studyid field. studyid_prefix and studyid_number were redundant and were removed
        list = configuration.getRootSelectionList(Lists.ID_PREFIX_LIST);
        data.dataField(ValueDataFieldCall.set(Fields.STUDYID, new Value(list.getDef()+studyNumber), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // submissionid, this is required information for creating a new study
        data.dataField(ValueDataFieldCall.set("submissionid", new Value(submissionid), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // Set dataarrivaldate
        data.dataField(ValueDataFieldCall.set("dataarrivaldate", new Value(arrivalDate), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    public ReturnResult formBiblCit(RevisionData data) {

        // Form biblCit for all languages if there is a title for that language.
        Pair<StatusCode, ValueDataField> titlePair = data.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(titlePair.getLeft() != StatusCode.FIELD_FOUND) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        ValueDataField title = titlePair.getRight();
        for(Language l : Language.values()) {
            if(!title.hasValueFor(l)) {
                continue;
            }
            String biblcit = "";

            // Person authors separated with ampersand
            String authors = "";
            Pair<StatusCode, ContainerDataField> authorsPair = data.dataField(ContainerDataFieldCall.get(Fields.AUTHORS));
            if(authorsPair.getLeft() == StatusCode.FIELD_FOUND && authorsPair.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : authorsPair.getRight().getRowsFor(Language.DEFAULT)) {
                    Pair<StatusCode, ValueDataField> typePair = row.dataField(ValueDataFieldCall.get(Fields.AUTHORTYPE));
                    if(typePair.getLeft() != StatusCode.FIELD_FOUND || !typePair.getRight().valueForEquals(Language.DEFAULT, "1")) {
                        // Type needs to be set and equal value "1" (person author)
                        continue;
                    }
                    Pair<StatusCode, ValueDataField> authorPair = row.dataField(ValueDataFieldCall.get(Fields.AUTHOR));
                    if(authorPair.getLeft() == StatusCode.FIELD_FOUND && authorPair.getRight().hasValueFor(Language.DEFAULT)) {
                        if(StringUtils.hasText(authors)) {
                            authors += " & ";
                        }
                        authors += authorPair.getRight().getActualValueFor(Language.DEFAULT);
                    }
                }
            }
            // Title on language, append with compFile.get(language)

            // dataversions.version+"."+descversions.version

            // first collector, append with collector.get(language)

            // first producer, append with producer.get(language)

            // Yhteiskuntatieteellinen tietoarkisto, append with distributor.get(language)

            // URN from DIP package (this is still unclear how it's formed

        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
