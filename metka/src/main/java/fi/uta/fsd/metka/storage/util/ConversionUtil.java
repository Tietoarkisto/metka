package fi.uta.fsd.metka.storage.util;

import org.springframework.util.StringUtils;

public final class ConversionUtil {
    // Private constructor to stop instantiation
    private ConversionUtil() {};

    public static Long stringToLong(Object value) throws NumberFormatException {
        if(value == null) {
            return null;
        } else if(value instanceof Long || value instanceof Integer) {
            return (Long)value;
        } else if(!(value instanceof String)) {
            throw new NumberFormatException("Value not String: "+value.getClass().getSimpleName());
        } else {
            String str = (String)value;
            if(StringUtils.isEmpty(str)) {
                return null;
            } else {
                return Long.decode(str);
            }
        }
    }

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
