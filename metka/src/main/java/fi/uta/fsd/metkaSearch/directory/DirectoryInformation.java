package fi.uta.fsd.metkaSearch.directory;

import fi.uta.fsd.metkaSearch.IndexWriterFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class DirectoryInformation {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryInformation.class);

    /**
     * Functions as an identifier for DirectoryInformation.
     * Follows simple rules:
     *   [location]:config/language
     *   for example ME:WIKIPEDIA/FI means the directory points to a finnish version of wikipedia index in memory
     *   if language is an empty string then it's disregarded as a path part.
     */
    private final DirectoryManager.DirectoryPath path;

    private final Directory directory;

    private final boolean writable;

    // Since all search operations are performed as once per command operation we don't need to keep record of if index is dirty or not
    // since every search will reopen the index anyway
    /*private volatile boolean dirty = false;*/
    private volatile IndexWriter indexWriter;

    public DirectoryInformation(String indexBaseDirectory, DirectoryManager.DirectoryPath path, boolean writable) throws IOException {
        this.path = path;
        this.writable = writable;
        if(this.path == null) {
            throw new UnsupportedOperationException("Needs to have a path");
        }

        if(!StringUtils.hasText(indexBaseDirectory)) {
            throw new UnsupportedOperationException("Index base directory is an empty path");
        }

        File base = new File(indexBaseDirectory);
        if(!base.exists() || !base.isDirectory()) {
            throw new UnsupportedOperationException("Index base directory doesn't exist or is not a directory");
        }

        if(path.isUseRam()) {
            directory = new RAMDirectory();
        } else {
            File fileDirectory = new File(indexBaseDirectory+path.getPath().substring(3));
            if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
            if(writable) fileDirectory.setWritable(true);
            try {
                directory = FSDirectory.open(fileDirectory); // This should open MMapDirectory
            } catch(Exception e) {
                throw new IOException("Couldn't open new FSDirectory");
            }
        }
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void clearIndex() {
        if(indexWriter != null) {
            try {
                indexWriter.close();
            } catch(Exception e) {

            }

        }
        indexWriter = IndexWriterFactory.createIndexWriter(directory);

        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch(IOException ioe) {
            logger.error("IOException while trying to clear all documents from index "+directory.toString(), ioe);
        }
    }

    public IndexWriter getIndexWriter() {
        if(!writable) {
            throw new UnsupportedOperationException("This DirectoryInformation is not writable, can't open index writer");
        }
        if(indexWriter == null) {
            // This can throw LockObtainFailedException if someone has abused the way
            // this code is supposed to be used. In that case we're pretty much out of luck with this
            // DirectoryInformation
            indexWriter = IndexWriterFactory.createIndexWriter(directory);
        }
        try {
            indexWriter.commit();
        } catch(AlreadyClosedException ace) {
            indexWriter = IndexWriterFactory.createIndexWriter(directory);
        } catch(IOException ioe) {
            logger.error("IOException while performing test commit on index in directory "+directory.toString(), ioe);
            return null;
        }
        return indexWriter;
    }

    // TODO: Since IndexReader is thread safe it should be cached for use by multiple queries instead of providing a new reader per search
    // For now though this requires the least amount of work so it's sufficient at the moment.
    public IndexReader getIndexReader() throws IOException {
        return DirectoryReader.open(directory);
    }

    /*public IndexReader getNRTIndexReader(boolean applyDeletes) throws IOException {
        if(indexWriter == null) {
            getIndexWriter();
            //throw new UnsupportedOperationException("No index writer to create NRT reader from");
        }
        return DirectoryReader.open(indexWriter, applyDeletes);
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectoryInformation that = (DirectoryInformation) o;

        if (!path.equals(that.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
