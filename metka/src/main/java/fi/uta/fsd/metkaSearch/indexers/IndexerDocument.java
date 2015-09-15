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

package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;

import java.util.HashMap;
import java.util.Map;

public class IndexerDocument {
    private final Document document = new Document();
    private final Map<String, Analyzer> analyzers = new HashMap<>();

    // Language of the index being used. This determines how general search field is handled
    private final Language baseLanguage;

    public IndexerDocument(Language baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public Map<String, Analyzer> getAnalyzers() {return analyzers;}
    public Document getDocument() {return document;}

    /*public void indexIntegerField(String key, Long value, boolean generalSearch) {
        indexIntegerField(key, value, false, generalSearch);
    }*/

    public void indexIntegerField(String key, Long value, boolean store, boolean generalSearch) {
        LongField lf = new LongField(key, value, (store) ? LuceneConfig.LONG_TYPE_STORE : LuceneConfig.LONG_TYPE);
        document.add(lf);
        if(generalSearch) {indexGeneral(value.toString());}
    }

    /*public void indexRealField(String key, Double value, boolean generalSearch) {
        indexRealField(key, value, false, generalSearch);
    }*/

    public void indexRealField(String key, Double value, boolean store, boolean generalSearch) {
        DoubleField df = new DoubleField(key, value, (store) ? LuceneConfig.DOUBLE_TYPE_STORE : LuceneConfig.DOUBLE_TYPE);
        document.add(df);
        if(generalSearch) {indexGeneral(value.toString());}
    }



    public void indexKeywordField(String key, String value) {
        indexKeywordField(key, value, Store.NO, false);
    }

    public void indexKeywordField(String key, String value, Store store) {
        indexKeywordField(key, value, store, false);
    }

    public void indexKeywordField(String key, String value, boolean generalSearch) {
        indexKeywordField(key, value, Store.NO, generalSearch);
    }

    public void indexKeywordField(String key, String value, Store store, boolean generalSearch) {
        document.add(new TextField(key, value, store));
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
        if(generalSearch) {indexGeneral(value);}
    }

    public void indexStringField(String key, String value, Store store, boolean generalSearch) {
        document.add(new StringField(key, value, store));
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
        if(generalSearch) {indexGeneral(value);}
    }

    /*public void indexWhitespaceField(String key, String value, boolean generalSearch) {
        indexWhitespaceField(key, value, Store.NO, generalSearch);
    }

    public void indexWhitespaceField(String key, String value, Store store, boolean generalSearch) {
        document.add(new TextField(key, value, store));
        analyzers.put(key, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
        if(generalSearch) {indexGeneral(value);}
    }*/

    public void indexText(Language language, Field field, String root, ValueDataField saved, boolean generalSearch) {
        indexText(language, root+field.getIndexAs(), saved.getActualValueFor(language), field.getExact(), Store.NO, generalSearch);
    }

    public void indexText(Language language, Field field, String root, ReferenceOption option, boolean generalSearch) {
        indexText(language, root+field.getIndexAs(), option.getTitle().getValue(), field.getExact(), Store.NO, generalSearch);
    }

    public void indexText(Language language, Field field, String root, String value, boolean generalSearch) {
        indexText(language, root+field.getIndexAs(), value, field.getExact(), Store.NO, generalSearch);
    }

    public void indexText(Language language, String key, String value, boolean exact, Store store, boolean generalSearch) {
        if(exact) {
            // TODO: Can we use whitespace analyzer here or not? Using both whitespace and keyword is disorienting to user since they would have to know the type of the field even more than now
            indexKeywordField(key, value, store, generalSearch);
        } else {
            document.add(new TextField(key, value, store));
            addTextAnalyzer(language, key);
            if(generalSearch) {indexGeneral(value);}
        }
    }

    public void indexGeneral(String value) {
        document.add(new TextField("general", value, Store.NO));
        addTextAnalyzer(baseLanguage, "general");
    }

    private void addTextAnalyzer(Language language, String key) {
        if(analyzers.containsKey(key)) {
            return;
        }
        if(language == null) {
            analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
        } else if(language == Language.DEFAULT) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else if(language == Language.EN) {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, LuceneConfig.ENGLISH_ANALYZER);
        } else if(language == Language.SV) {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, LuceneConfig.SWEDISH_ANALYZER);
        } else {
            analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
        }
    }
}
