package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaAmqp.Logger;
import org.apache.commons.io.FileUtils;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 24.1.2014
 * Time: 13:12
 */
public class LuceneAPI {
    // Initialize logger.
    private static final Logger log = Logger.getInstance();

    //Read properties file
    Properties prop = new Properties();
    InputStream input = null;

    // TODO Read from properties
    private static String indexBaseDirectory = "/tmp/index/";
    private String indexDirectory;

    // Different index types
    public enum IndexType {
        MEMORY,
        FILESYSTEM_READONLY,
        FILESYSTEM_WRITABLE
    }

    private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

    private Directory index = null;
    private IndexWriterConfig config;

    private IndexWriter writer = null;
    private File fileDirectory;

    // Hide default Constructor
    private LuceneAPI() {}


    public LuceneAPI(IndexType type, String indexDirectory) throws Exception {

        // Read properties from file.
        try {
            // Name of the properties file
            String filename = "lucene_config.properties";
            input = LuceneAPI.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                throw new IOException("Sorry, unable to find \"" + filename +"\"");
            }

            // load a properties file
            prop.load(input);

            indexBaseDirectory = prop.getProperty("indexBaseDirectory", indexBaseDirectory);

        } catch (IOException ex) {
            log.error(this, "" + ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(this, e.getMessage());
                }
            }
        }

        if(type!=IndexType.MEMORY) {
            if (indexDirectory == null) throw new Exception("Null index name!");
            if (indexDirectory.length()==0) throw new Exception("Empty index name!");
            this.indexDirectory = indexBaseDirectory + indexDirectory;
        } else {
            // If memory index is selected, ignore file
            this.indexDirectory = null;
        }
        try {
        switch (type) {
            case MEMORY:{
                index = new RAMDirectory();
                break;
            }
            case FILESYSTEM_WRITABLE: {
                fileDirectory = new File(this.indexDirectory);
                if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
                fileDirectory.setWritable(true);
                index = new SimpleFSDirectory(fileDirectory);
                break;
            }
            default:
            case FILESYSTEM_READONLY: {
                fileDirectory = new File(this.indexDirectory);
                if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
                fileDirectory.setReadOnly();
                index = new SimpleFSDirectory(fileDirectory);
                break;
            }
        }
        } catch (IOException e) {
            log.error(this, "Error when creating lucene index!");
            throw e;
        }

    }

    public void addDocument(Document doc) throws IOException {
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
    }


}
