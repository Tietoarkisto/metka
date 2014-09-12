package fi.uta.fsd.metkaSearch;

import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.util.Version;

public final class LuceneConfig {
    // This class is not meant to be extended
    private LuceneConfig() {}

    /**
     * FieldType used with LongFields. Field is not stored
     */
    public static final FieldType LONG_TYPE;

    /**
     * FieldType used with DoubleFields. Field is not stored
     */
    public static final FieldType DOUBLE_TYPE;

    /**
     * FieldType used with LongFields. Field is stored
     */
    public static final FieldType LONG_TYPE_STORE;

    /**
     * FieldType used with DoubleFields. Field is stored
     */
    public static final FieldType DOUBLE_TYPE_STORE;

    /**
     * Numeric precision step used throughout the system
     */
    public static final Integer PRECISION_STEP = 1;

    /**
     * Lucene version used throughout the software
     */
    public static final Version USED_VERSION = Version.LUCENE_48;
    /**
     * Defines how many idle loops (i.e. loops with no new commands) must happen
     * before changes are flushed to disk.
     */
    public static final int IDLE_LOOPS_BEFORE_FLUSH = 3;
    /**
     * Defines if indexing will take a break after multiple commands to flush changes to disk.
     */
    public static boolean FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS = true;
    /**
     * Defines maximum number of commands to handle between flushes if FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS is true.
     */
    public static final int MAX_COMMAND_BATCH_SIZE = 5;

    static {
        // Set LongType no store
        LongField tempLong;
        DoubleField tempDouble;
        FieldType tempType;

        tempLong = new LongField("temp", 1L, Field.Store.NO);
        tempType = new FieldType(tempLong.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        LONG_TYPE = tempType;

        tempLong = new LongField("temp", 1L, Field.Store.YES);
        tempType = new FieldType(tempLong.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        LONG_TYPE_STORE = tempType;

        tempDouble = new DoubleField("temp", 1L, Field.Store.NO);
        tempType = new FieldType(tempDouble.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        DOUBLE_TYPE = tempType;

        tempDouble = new DoubleField("temp", 1L, Field.Store.YES);
        tempType = new FieldType(tempDouble.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        DOUBLE_TYPE_STORE = tempType;

    }
}
