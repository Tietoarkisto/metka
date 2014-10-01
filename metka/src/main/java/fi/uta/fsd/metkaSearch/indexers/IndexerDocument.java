package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IndexerDocument {
    private static final Logger logger = LoggerFactory.getLogger(IndexerDocument.class);

    private final Document document = new Document();
    private final Map<String, Analyzer> analyzers = new HashMap<>();
    private final StringBuilder general = new StringBuilder();

    public Map<String, Analyzer> getAnalyzers() {return analyzers;}
    public String getGeneral() {return general.toString();}
    public Document getDocument() {return document;}

    public void indexIntegerField(String key, Long value) {
        indexIntegerField(key, value, false);
    }

    public void indexIntegerField(String key, Long value, boolean store) {
        LongField lf = new LongField(key, value, (store) ? LuceneConfig.LONG_TYPE_STORE : LuceneConfig.LONG_TYPE);
        document.add(lf);
    }

    public void indexRealField(String key, Double value) {
        indexRealField(key, value, false);
    }

    public void indexRealField(String key, Double value, boolean store) {
        DoubleField df = new DoubleField(key, value, (store) ? LuceneConfig.DOUBLE_TYPE_STORE : LuceneConfig.DOUBLE_TYPE);
        document.add(df);
    }

    public void indexStringField(String key, String value, Store store) {
        document.add(new StringField(key, value, store));
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
    }

    public void indexKeywordField(String key, String value) {
        indexKeywordField(key, value, Store.NO);
    }

    public void indexKeywordField(String key, String value, Store store) {
        document.add(new TextField(key, value, store));
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
    }

    public void indexWhitespaceField(String key, String value) {
        indexWhitespaceField(key, value, Store.NO);
    }

    public void indexWhitespaceField(String key, String value, Store store) {
        document.add(new TextField(key, value, store));
        analyzers.put(key, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
    }

    public void indexText(Language language, Field field, String root, ValueDataField saved) {
        indexText(language, root+field.getKey(), saved.getActualValueFor(language), field.getExact(), Store.NO);
    }

    public void indexText(Language language, Field field, String root, ReferenceOption option) {
        indexText(language, root+field.getKey(), option.getTitle().getValue(), field.getExact(), Store.NO);
    }

    public void indexText(Language language, Field field, String root, String value) {
        indexText(language, root+field.getKey(), value, field.getExact(), Store.NO);
    }

    public void indexText(Language language, String key, String value, boolean exact, Store store) {
        if(exact) {
            indexKeywordField(key, value, store);
        } else {
            general.append(" ");
            general.append(value);
            document.add(new TextField(key, value, store));
            addTextAnalyzer(language, key);
        }
    }

    private void addTextAnalyzer(Language language, String key) {
        if(language == Language.DEFAULT) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }
}
