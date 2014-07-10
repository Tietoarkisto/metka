package fi.uta.fsd.metkaSearch;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.IOException;

public class IndexWriterFactory {
    private static final IndexWriterConfig writerConfig;

    static {
        // Create the default writer config. Use whitespace analyser as default.
        writerConfig = new IndexWriterConfig(LuceneConfig.USED_VERSION, new WhitespaceAnalyzer(LuceneConfig.USED_VERSION));
        // Set index open mode. Create index if missing, append if present
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

    public static IndexWriter createIndexWriter(Directory directory) throws IOException {
        IndexWriterConfig clone = writerConfig.clone();
        IndexWriter writer = new IndexWriter(directory, clone);
        return writer;
    }
}
