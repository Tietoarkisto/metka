package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.data.Change;
import fi.uta.fsd.metka.model.data.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.SimpleValue;
import org.springframework.util.NumberUtils;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/7/14
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModelAccessUtil {

    public static FieldContainer getContainerFromRevisionData(RevisionData data, String key) {
        FieldContainer container = null;
        if(data.getState() == RevisionState.DRAFT) {
            Change change = data.getChanges().get(key);
            if(change == null) {
                container = data.getFields().get(key);
            } else if(change.getNewField() != null) {
                container = change.getNewField();
            } else {
                container = change.getOriginalField();
            }
        } else {
            container = data.getFields().get(key);
        }

        return container;
    }

    public static Integer extractIntegerSimpleValue(FieldContainer field) {
        Integer integer = null;
        if(field != null && field.getValues().size() > 0) {
            String value = ((SimpleValue) field.getValues().get(0)).getValue();
            integer = NumberUtils.parseNumber(value, Integer.class);
        }

        return integer;
    }

    public static String extractStringSimpleValue(FieldContainer field) {
        String string = null;
        if(field != null && field.getValues().size() > 0) {
            string = ((SimpleValue) field.getValues().get(0)).getValue();
        }

        return string;
    }
}
