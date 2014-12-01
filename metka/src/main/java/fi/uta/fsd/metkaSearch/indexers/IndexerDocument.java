package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
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
        if(language == Language.DEFAULT) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }
}
