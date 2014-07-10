package fi.uta.fsd.metkaSearch;

import org.apache.lucene.util.Version;

public final class LuceneConfig {
    // This class is not meant to be extended
    private LuceneConfig() {}

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
}
