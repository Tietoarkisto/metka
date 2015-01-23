package fi.uta.fsd;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class Logger {
    private static final Map<Class, org.slf4j.Logger> loggers = new HashMap<>();

    private static org.slf4j.Logger getLogger(Class c) {
        if(!loggers.containsKey(c)) {
            loggers.put(c, LoggerFactory.getLogger(c));
        }
        return loggers.get(c);
    }

    public static void error(Class c, String message) {
        getLogger(c).error(message);
    }

    public static void error(Class c, String message, Exception e) {
        getLogger(c).error(message, e);
    }

    public static void warning(Class c, String message) {
        getLogger(c).warn(message);
    }

    public static void warning(Class c, String message, Exception e) {
        getLogger(c).warn(message, e);
    }

    public static void info(Class c, String message) {
        getLogger(c).info(message);
    }

    public static void info(Class c, String message, Exception e) {
        getLogger(c).info(message, e);
    }

    public static void debug(Class c, String message) {
        getLogger(c).debug(message);
    }

    public static void debug(Class c, String message, Exception e) {
        getLogger(c).debug(message, e);
    }
}
