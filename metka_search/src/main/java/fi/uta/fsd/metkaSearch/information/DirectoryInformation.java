package fi.uta.fsd.metkaSearch.information;

import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

public class DirectoryInformation {
    private final IndexerConfigurationType type;
    private final Directory directory;
    private volatile boolean dirty = false;
    private volatile IndexWriter indexWriter;

    public DirectoryInformation(IndexerConfigurationType type, Directory directory) {
        this.type = type;
        this.directory = directory;
    }

    public IndexerConfigurationType getType() {
        return type;
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

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public void setIndexWriter(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectoryInformation that = (DirectoryInformation) o;

        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
