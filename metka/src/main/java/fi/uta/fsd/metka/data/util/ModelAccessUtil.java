package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.springframework.util.NumberUtils;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/7/14
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModelAccessUtil {

    // TODO: Configuration checking that the field is a valid target, for now assume accurate use
    public static ValueFieldContainer getValueFieldContainerFromRevisionData(RevisionData data, String key) {
        ValueFieldContainer container = null;
        if(data.getState() == RevisionState.DRAFT) {
            ValueFieldChange change = (ValueFieldChange)data.getChanges().get(key);
            if(change == null) {
                container = (ValueFieldContainer)data.getFields().get(key);
            } else if(change.getNewField() != null) {
                container = change.getNewField();
            } else {
                container = change.getOriginalField();
            }
        } else {
            container = (ValueFieldContainer)data.getFields().get(key);
        }

        return container;
    }

    public static Integer extractIntegerSimpleValue(ValueFieldContainer field) {
        Integer integer = null;
        if(field != null && field.getValues().size() > 0) {
            String value = ((SimpleValue) field.getValues().get(0)).getValue();
            integer = NumberUtils.parseNumber(value, Integer.class);
        }

        return integer;
    }

    public static String extractStringSimpleValue(ValueFieldContainer field) {
        String string = null;
        if(field != null && field.getValues().size() > 0) {
            string = ((SimpleValue) field.getValues().get(0)).getValue();
        }

        return string;
    }
}
