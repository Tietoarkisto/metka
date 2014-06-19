package fi.uta.fsd.metkaSearch;

//import fi.uta.fsd.metkaAmqp.Logger;
import fi.uta.fsd.metkaSearch.analyzer.DefaultAnalyzer;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.information.DirectoryInformation;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LuceneFactory {
    // Initialize logger.
    //private static final Logger log = Logger.getInstance();

    // TODO Read from properties
    private static final String indexBaseDirectory = "/tmp/index/";
    private static final Map<IndexerConfigurationType, DirectoryInformation> indexDirectories = new HashMap<>();
    private static final Map<IndexerConfigurationType, DirectoryInformation> indexDirectoriesWritable = new HashMap<>();
    private static final Map<IndexerConfigurationType, DirectoryInformation> indexDirectoriesReadOnly = new HashMap<>();

    private static final IndexWriterConfig writerConfig;
    private static DirectoryInformation currentRAMDirectory;

    static {
        writerConfig = new IndexWriterConfig(Version.LUCENE_46, new WhitespaceAnalyzer(Version.LUCENE_46)); // Use whitespace analyser as default.
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND); // Create index if missing, append if present
    }

    // Different index types
    public enum IndexType {
        MEMORY,                 // Indicates the request for RAMDirectory
        FILESYSTEM,             // Indicated the request for an FSDirectory, assumed to be writable
        FILESYSTEM_WRITABLE,    // Requests specifically a writable directory, not used atm
        FILESYSTEM_READONLY     // Requests specifically a readonly directory, not used atm
        }

    // Hide default Constructor
    private LuceneFactory() {}

    public static DirectoryInformation getIndexDirectory(IndexType type, IndexerConfigurationType configType) throws Exception {
        DirectoryInformation index;

        if(type != IndexType.MEMORY) {
            if (configType == null) throw new Exception("Null config type!");
        }

        try {
        switch (type) {
            case MEMORY:{
                if(currentRAMDirectory == null) {
                    currentRAMDirectory = new DirectoryInformation(configType, new RAMDirectory());
                }
                index = currentRAMDirectory;
                break;
            }
            case FILESYSTEM: {
                index = indexDirectories.get(configType);
                if(index == null) {
                    File fileDirectory = new File(indexBaseDirectory+configType);
                    if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
                    fileDirectory.setWritable(true);
                    index = new DirectoryInformation(configType, FSDirectory.open(fileDirectory));
                }
            }
            default: {
                index = null;
            }
            /*case FILESYSTEM_WRITABLE: {
                index = indexDirectoriesWritable.get(configType);
                if(index == null) {

                    File fileDirectory = new File(indexBaseDirectory+configType);
                    if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
                    fileDirectory.setWritable(true);
                    index = new SimpleFSDirectory(fileDirectory);
                    indexDirectoriesWritable.put(configType, index);
                }
                break;
            }
            default:
            case FILESYSTEM_READONLY: {
                index = indexDirectoriesReadOnly.get(configType);
                if(index == null) {

                    File fileDirectory = new File(indexBaseDirectory+configType);
                    if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
                    fileDirectory.setReadOnly();
                    index = new SimpleFSDirectory(fileDirectory);
                    indexDirectoriesReadOnly.put(configType, index);
                }
                break;
            }*/
        }
        } catch (IOException e) {
            //log.error(this, "Error when creating lucene index!");
            e.printStackTrace();
            throw e;
        }
        return index;
    }

    public static DirectoryInformation getInMemoryIndexWriter() throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.MEMORY, null);
        if(index.getIndexWriter() == null) {
            try {
                IndexWriter writer = new IndexWriter(index.getDirectory(), writerConfig); // This should fail in threaded situations
                index.setIndexWriter(writer);
            } catch(LockObtainFailedException ex) {
                // This exception should only raise if two threads try to open an index writer simultaneously.
                // Exception should only happen between creating of IndexWriter and assigning it to DirectoryInformation
                // and so we don't really need to worry about it since the other thread should assign the writer
                // without problems to this same object
            }
        }

        return index;
    }

    public static DirectoryInformation getIndexWriter(IndexerConfigurationType configType) throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        if(index.getIndexWriter() == null) {
            try {
                IndexWriter writer = new IndexWriter(index.getDirectory(), writerConfig); // This should fail in threaded situations
                index.setIndexWriter(writer);
            } catch(LockObtainFailedException ex) {
                // This exception should only raise if two threads try to open an index writer simultaneously.
                // Exception should only happen between creating of IndexWriter and assigning it to DirectoryInformation
                // and so we don't really need to worry about it since the other thread should assign the writer
                // without problems to this same object
            }
        }

        return index;
    }

    public static IndexReader getInMemoryIndexReader() throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.MEMORY, null);
        IndexReader reader = DirectoryReader.open(index.getDirectory());
        return reader;
    }

    public static IndexReader getInMemoryNRTIndexReader(boolean applyDeletes) throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.MEMORY, null);
        if(index.getIndexWriter() == null) {
            // No writer to open nrt reader with, return null
            return null;
        }
        IndexReader reader = DirectoryReader.open(index.getIndexWriter(), applyDeletes);
        return reader;
    }

    public static IndexReader getIndexReader(IndexerConfigurationType configType) throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        IndexReader reader = DirectoryReader.open(index.getDirectory());
        return reader;
    }

    public static IndexReader getNRTIndexReader(IndexerConfigurationType configType, boolean applyDeletes) throws Exception {
        DirectoryInformation index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        if(index.getIndexWriter() == null) {
            // No writer to open nrt reader with, return null
            return null;
        }
        IndexReader reader = DirectoryReader.open(index.getIndexWriter(), applyDeletes);
        return reader;
    }

    /*public void addDocument(Document doc) throws IOException {
        // Open writer
        config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        config.setReaderPooling(true);
        writer = new IndexWriter(index, config);
        // Add new document
        writer.addDocument(doc);
        // Close writer
        writer.close();
    }

    public void destroyIndex() throws IOException {
        // Destroy function is probably not needed anywhere except in test case.
        // Open writer
        config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        config.setReaderPooling(true);
        writer = new IndexWriter(index, config);
        // delete all items in the index
        writer.deleteAll();
        // Close writer
        writer.close();
        if (fileDirectory != null) {
            // Delete files (Empty index)
            FileUtils.deleteDirectory(fileDirectory);
        }
    }


    public TopScoreDocCollector findDocuments(String field, String searchQuery) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(1,true);
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

        Query query = new QueryParser(Version.LUCENE_46, field, analyzer).parse(searchQuery);
        searcher.search(query, collector);
        return collector;
    }*/


}
