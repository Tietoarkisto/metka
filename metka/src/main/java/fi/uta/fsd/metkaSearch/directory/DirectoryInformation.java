/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metkaSearch.directory;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metkaSearch.IndexWriterFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class DirectoryInformation {

    /**
     * Functions as an identifier for DirectoryInformation.
     * Follows simple rules:
     *   [location]:config/language
     *   for example ME:WIKIPEDIA/FI means the directory points to a finnish version of wikipedia index in memory
     *   if language is an empty string then it's disregarded as a path part.
     */
    private final DirectoryManager.DirectoryPath path;

    private final Directory directory;

    private final boolean exists;

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
            exists = true;
        } else {
            File fileDirectory = new File(indexBaseDirectory+path.getPath().substring(3));
            if(fileDirectory.exists() && !fileDirectory.isDirectory()) throw new IOException("Index directory is not a directory!");
            exists = fileDirectory.exists();
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

    public boolean exists() {
        return exists;
    }

    public void clearIndex() {
        if(indexWriter == null) {
            indexWriter = getIndexWriter();
        }

        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch(IOException ioe) {
            Logger.error(getClass(), "IOException while trying to clear all documents from index "+directory.toString(), ioe);
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
            Logger.error(getClass(), "IOException while performing test commit on index in directory " + directory.toString(), ioe);
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
