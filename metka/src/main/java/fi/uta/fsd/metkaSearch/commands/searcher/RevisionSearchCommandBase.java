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

package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Special case of SearchCommandBase
 * This should be used as base for all SearchCommands that target Revision indexes.
 * It contains the target ConfigurationType as an additional parameter and searchers will use this to find correct index configuration
 * so that they can use correct Analyzers for fields.
 * It is assumed that factory methods for the different SearchCommands will check the validity of the configuration type in the path and so
 * we can just extract it from the path while assuming that it is present
 */
public abstract class RevisionSearchCommandBase<T extends SearchResult> extends SearchCommandBase<T> {
    protected static void checkPath(DirectoryManager.DirectoryPath path, ConfigurationType type) throws UnsupportedOperationException {
        if(path.getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Given path is not for REVISION index");
        }
        if(path.getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("Too many additional parameters");
        }
    }

    private Language language;
    private final Map<String, Analyzer> analyzers = new HashMap<>();

    protected RevisionSearchCommandBase(DirectoryManager.DirectoryPath path, ResultList.ResultType resultType) {
        super(path, resultType);
    }

    protected void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    protected void addKeywordAnalyzer(String key) {
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
    }

    protected void addWhitespaceAnalyzer(String key) {
        analyzers.put(key, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
    }

    protected void addTextAnalyzer(String key) {
        if(language == Language.DEFAULT) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }

    protected Analyzer getAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(CaseInsensitiveWhitespaceAnalyzer.ANALYZER, analyzers);
        return analyzer;
    }

    protected static class BasicRevisionSearchResultHandler implements ResultHandler<RevisionResult> {
        public BasicRevisionSearchResultHandler() {}

        @Override
        public ResultList<RevisionResult> handle(IndexSearcher searcher, TopDocs results) {
            Logger.debug(getClass(), "Handling results and transforming them to RevisionResult list");
            ResultList<RevisionResult> list = new ListBasedResultList<>(ResultList.ResultType.REVISION);
            if(searcher == null || results == null) {
                return list;
            }
            for(ScoreDoc doc : results.scoreDocs) {
                try {
                    Document document = searcher.doc(doc.doc);
                    IndexableField field = document.getField("key.id");
                    Long id = null;
                    Long no = null;
                    Language language = null;
                    if(field != null) {
                        id = field.numericValue().longValue();
                    }
                    field = document.getField("key.no");
                    if(field != null) {
                        no = field.numericValue().longValue();
                    }
                    field = document.getField("key.language");
                    if(field != null) {
                        language = Language.fromValue(field.stringValue());
                    }
                    list.addResult(new RevisionResult(id, no, language));
                } catch(IOException ioe) {
                    list.addResult(new RevisionResult(null, null, null));
                }
            }

            return list;
        }
    }
}
