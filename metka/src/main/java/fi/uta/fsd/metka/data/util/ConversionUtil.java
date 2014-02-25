package fi.uta.fsd.metka.data.util;

import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/25/14
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConversionUtil {
    public static Integer stringToInteger(Object value) throws NumberFormatException {
        if(value == null) {
            return null;
        } else if(value instanceof Integer) {
            return (Integer)value;
        } else if(!(value instanceof String)) {
            throw new NumberFormatException("Value not String: "+value.getClass().getSimpleName());
        } else {
            String str = (String)value;
            if(StringUtils.isEmpty(str)) {
                return null;
            } else {
                return Integer.decode(str);
            }
        }
    }
}
