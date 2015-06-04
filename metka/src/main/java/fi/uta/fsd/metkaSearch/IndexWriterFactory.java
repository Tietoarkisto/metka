package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

public class IndexWriterFactory {
    private static final IndexWriterConfig writerAppendConfig;

    static {
        // Create the default writer config. Use whitespace analyser as default.
        writerAppendConfig = new IndexWriterConfig(LuceneConfig.USED_VERSION, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
        // Set index open mode. Create index if missing, append if present
        writerAppendConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

    public static IndexWriter createIndexWriter(Directory directory) {
        IndexWriterConfig clone = writerAppendConfig.clone();
        try {
            IndexWriter writer = new IndexWriter(directory, clone);
            return writer;
        } catch(Exception e) {
            Logger.error(IndexWriterFactory.class, "Exception while creating index writer: ", e);
            return null;
        }
    }
}
