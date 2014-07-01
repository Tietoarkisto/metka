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
import java.util.concurrent.ConcurrentHashMap;

public class LuceneFactory {
    // Initialize logger.
    //private static final Logger log = Logger.getInstance();

    // TODO Read from properties
    private static final String indexBaseDirectory = "/usr/share/metka/index/";
    private static final Map<IndexerConfigurationType, DirectoryInformation> indexDirectories = new ConcurrentHashMap<>();

    private static volatile DirectoryInformation currentRAMDirectory;

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
                        try {
                            Directory indexDirectory = new MMapDirectory(fileDirectory);
                            index = new DirectoryInformation(configType, indexDirectory);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        //Directory indexDirectory = FSDirectory.open(fileDirectory);

                    }
                    break;
                }
                default: {
                    index = null;
                    break;
                }
            }
        } catch (Exception e) {
            //log.error(this, "Error when creating lucene index!");
            e.printStackTrace();
            throw e;
        }
        return index;
    }

    /**
     * Returns a DirectoryInformation with RAMDirectory and IndexWriter pointed at that RAMDirectory
     * by calling getIndexWriter with null parameter.
     * @return
     * @throws Exception
     */
    public static DirectoryInformation getIndexWriter() throws Exception {
        return getIndexWriter(null);
    }

    /**
     * Returns a DirectoryInformation with IndexWriter and either a RAMDirectory or a FSDirectory based on whether a
     * config type was provided or not. Null config type produces a RAMDirectory.
     * @param configType - IndexerConfigurationType. Can be null
     * @return
     * @throws Exception
     */
    public static DirectoryInformation getIndexWriter(IndexerConfigurationType configType) throws Exception {
        DirectoryInformation index;
        if(configType == null) {
            index = getIndexDirectory(IndexType.MEMORY, null);
        } else {
            index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        }

        if(index != null && index.getIndexWriter() == null) {
            //try {
                IndexWriter writer = IndexWriterFactory.createIndexWriter(index.getDirectory());
                index.setIndexWriter(writer);
            /*} catch(LockObtainFailedException ex) {
                ex.printStackTrace();
                // This exception should only raise if two threads try to open an index writer simultaneously.
                // Exception should only happen between creating of IndexWriter and assigning it to DirectoryInformation
                // and so we don't really need to worry about it since the other thread should assign the writer
                // without problems to this same object.
                // We will however fetch the indexer again to make sure that we have the correct properties.
                // This recursion should happen only once.
                return getIndexWriter(configType);
            }*/
        }

        return index;
    }

    public static IndexReader getIndexReader() throws Exception {
        return getIndexReader(null);
    }

    public static IndexReader getIndexReader(IndexerConfigurationType configType) throws Exception {
        DirectoryInformation index;
        if(configType == null) {
            index = getIndexDirectory(IndexType.MEMORY, null);
        } else {
            index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        }
        IndexReader reader = null;
        if(index != null) {
            reader = DirectoryReader.open(index.getDirectory());
        }
        return reader;
    }

    public static IndexReader getNRTIndexReader(boolean applyDeletes) throws Exception {
        return getNRTIndexReader(null, applyDeletes);
    }

    public static IndexReader getNRTIndexReader(IndexerConfigurationType configType, boolean applyDeletes) throws Exception {
        DirectoryInformation index;
        if(configType == null) {
            index = getIndexDirectory(IndexType.MEMORY, null);
        } else {
            index = getIndexDirectory(IndexType.FILESYSTEM, configType);
        }
        if(index != null && index.getIndexWriter() == null) {
            // No writer to open nrt reader with, return null
            return null;
        }

        IndexReader reader = null;
        if(index != null) {
            reader = DirectoryReader.open(index.getIndexWriter(), applyDeletes);
        }
        return reader;
    }
}
