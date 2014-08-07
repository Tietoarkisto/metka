package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;

import java.util.HashMap;
import java.util.Map;

public class IndexerDocument {

    private final Document document = new Document();
    private final Map<String, Analyzer> analyzers = new HashMap<>();
    private final StringBuilder general = new StringBuilder();

    private final String language;

    public IndexerDocument(String language) {
        this.language = language;
    }

    public Map<String, Analyzer> getAnalyzers() {return analyzers;}
    public String getGeneral() {return general.toString();}
    public Document getDocument() {return document;}

    public void indexIntegerField(String key, Long value) {
        indexIntegerField(key, value, Store.NO);
        // TODO: Check precision
    }

    public void indexIntegerField(String key, Long value, Store store) {
        document.add(new LongField(key, value, store));
        // TODO: Check precision
    }

    public void indexRealField(String key, Double value) {
        indexRealField(key, value, Store.NO);
        // TODO: Check precision
    }

    public void indexRealField(String key, Double value, Store store) {
        document.add(new DoubleField(key, value, store));
        // TODO: Check precision
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

    public void indexText(Field field, String root, SavedDataField saved) {
        indexText(root+field.getKey(), saved.getActualValue(), field.getExact(), Store.NO);
    }

    public void indexText(Field field, String root, ReferenceOption option) {
        indexText(root+field.getKey(), option.getTitle().getValue(), field.getExact(), Store.NO);
    }

    public void indexText(Field field, String root, String value) {
        indexText(root+field.getKey(), value, field.getExact(), Store.NO);
    }

    public void indexText(String key, String value, boolean exact, Store store) {
        if(exact) {
            indexWhitespaceField(key, value, store);
        } else {
            general.append(" ");
            general.append(value);
            document.add(new TextField(key, value, store));
            addTextAnalyzer(key);
        }
    }

    private void addTextAnalyzer(String key) {
        if(language.equals(Language.DEFAULT.toValue())) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }
}
