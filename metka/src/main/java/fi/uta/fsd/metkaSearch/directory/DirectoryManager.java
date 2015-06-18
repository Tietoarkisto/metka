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

//import fi.uta.fsd.metkaAmqp.Logger;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DirectoryManager {

    private final Map<DirectoryPath, DirectoryInformation> indexDirectories = new ConcurrentHashMap<>();

    @Value("${dir.index}")
    private String indexBaseDirectory;

    // Hide default Constructor
    private DirectoryManager() {}

    /**
     * Forms a valid MetkaDirectory path.
     *
     * @param useRam
     * @param type Must not be null
     * @return
     */
    public static DirectoryPath formPath(boolean useRam, IndexerConfigurationType type, String... additionalParameters) {
        return new DirectoryPath(useRam, type, additionalParameters);
    }

    /**
     * Synchronized static method for fetching DirectoryInformation object based on path or creating one if one doesn't yet exist.
     * @param path
     * @return
     */
    public synchronized DirectoryInformation getIndexDirectory(DirectoryPath path, boolean writable) {
        DirectoryInformation index;
        try {
            if (writable) {
                index = getWritableDirectory(path);
            } else {
                index = new DirectoryInformation(indexBaseDirectory, path, false);
            }
        } catch(IOException ioe) {
            Logger.error(getClass(), "IOException while creating DirectoryInformation.");
            return null;
        }

        return index;
    }

    private synchronized DirectoryInformation getWritableDirectory(DirectoryPath path) throws IOException {
        DirectoryInformation index = indexDirectories.get(path);
        if(index == null) {
            index = new DirectoryInformation(indexBaseDirectory, path, true);
            indexDirectories.put(index.getPath(), index);
        }
        return index;
    }

    /**
     * Class that handles indexer paths.
     * Functions as an id in multiple situations.
     * Paths are equal if their String representation is equal.
     */
    public static class DirectoryPath {
        private final String path;
        private final boolean useRam;
        private final IndexerConfigurationType type;
        private final String[] additionalParameters;

        /**
         * Constructor for DirectoryPath object.
         * Forms a directory path that follows the form {ME|FS}:{type}/{language}[/additionalParameters...]
         * @param useRam Should the index use RAMDirectory or FSDirectory. This forms the base of the path with ME denoting memory and FS denoting file system directory.
         * @param type Base type of the index. In most cases this will be REVISION or CONTAINER
         * @param additionalParameters Array of additional parameters for the index location such as ConfigurationType
         */
        public DirectoryPath(boolean useRam, IndexerConfigurationType type, String... additionalParameters) {
            if(type == null) {
                throw new UnsupportedOperationException("Must have type");
            }
            this.useRam = useRam;
            this.type = type;
            this.additionalParameters = additionalParameters;

            StringBuilder pb = new StringBuilder();
            if(useRam) {
                pb.append("ME:");
            } else {
                pb.append("FS:");
            }
            pb.append(type);

            for(String additional : additionalParameters) {
                pb.append("/");
                pb.append(additional);
            }

            path = pb.toString();
        }

        public String getPath() {
            return path;
        }

        public boolean isUseRam() {
            return useRam;
        }

        public IndexerConfigurationType getType() {
            return type;
        }

        public String[] getAdditionalParameters() {
            return additionalParameters;
        }

        @Override
        public String toString() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DirectoryPath that = (DirectoryPath) o;

            if (!path.equals(that.path)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }
}
