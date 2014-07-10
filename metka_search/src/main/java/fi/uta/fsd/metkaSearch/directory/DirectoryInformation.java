package fi.uta.fsd.metkaSearch.directory;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.IndexWriterFactory;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class DirectoryInformation {
    // TODO Read from properties
    private static final String indexBaseDirectory = "/usr/share/metka/index/";

    /**
     * Functions as an identifier for DirectoryInformation.
     * Follows simple rules:
     *   [location]:config/language
     *   for example ME:WIKIPEDIA/FI means the directory points to a finnish version of wikipedia index in memory
     *   if language is an empty string then it's disregarded as a path part.
     */
    private final DirectoryManager.DirectoryPath path;

    private final Directory directory;

    private volatile boolean dirty = false;
    private volatile IndexWriter indexWriter;

    public DirectoryInformation(DirectoryManager.DirectoryPath path) throws IOException {
        this.path = path;
        if(this.path == null) {
            throw new UnsupportedOperationException("Needs to have a path");
        }

        if(path.isUseRam()) {
            directory = new RAMDirectory();
        } else {
            File fileDirectory = new File(indexBaseDirectory+path.getPath().substring(3));
            if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
            fileDirectory.setWritable(true);
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

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public IndexWriter getIndexWriter() throws IOException {
        if(indexWriter == null) {
            // This can throw LockObtainFailedException if someone has abused the way
            // this code is supposed to be used. In that case we're pretty much out of luck with this
            // DirectoryInformation
            indexWriter = IndexWriterFactory.createIndexWriter(directory);
        }
        return indexWriter;
    }

    public IndexReader getIndexReader() throws IOException {
        return DirectoryReader.open(directory);
    }

    public IndexReader getNRTIndexReader(boolean applyDeletes) throws IOException {
        if(indexWriter == null) {
            throw new UnsupportedOperationException("No index writer to create NRT reader from");
        }
        return DirectoryReader.open(indexWriter, applyDeletes);
    }

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
