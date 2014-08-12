package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexWriterFactory {
    private static final Logger logger = LoggerFactory.getLogger(IndexWriterFactory.class);
    private static final IndexWriterConfig writerConfig;

    static {
        // Create the default writer config. Use whitespace analyser as default.
        writerConfig = new IndexWriterConfig(LuceneConfig.USED_VERSION, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
        // Set index open mode. Create index if missing, append if present
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

    public static IndexWriter createIndexWriter(Directory directory) {
        IndexWriterConfig clone = writerConfig.clone();
        try {
            IndexWriter writer = new IndexWriter(directory, clone);
            return writer;
        } catch(Exception e) {
            logger.error("Exception while creating index writer: ", e);
            return null;
        }
    }
}
