package fi.uta.fsd.metkaSearch.analyzer;

import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.filter.FinnishTokenFilter;
import fi.uta.fsd.metkaSearch.voikko.FinnishStopFilterFactory;
import fi.uta.fsd.metkaSearch.voikko.VoikkoFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.puimula.libvoikko.Voikko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

public class FinnishVoikkoAnalyzer extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(FinnishVoikkoAnalyzer.class);

    public static final FinnishVoikkoAnalyzer ANALYZER = new FinnishVoikkoAnalyzer();

    private final Voikko voikko;
    private TokenFilterFactory stopFilterFactory = new FinnishStopFilterFactory(LuceneConfig.USED_VERSION);

    public FinnishVoikkoAnalyzer() {
        this(null);
    }

    public FinnishVoikkoAnalyzer(Voikko voikko) {
        if(voikko == null) {
            logger.info("Trying to create Voikko-object");
            voikko = VoikkoFactory.create();
        }
        this.voikko = voikko;

    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new StandardTokenizer(LuceneConfig.USED_VERSION, reader);
        TokenStream result = new StandardFilter(LuceneConfig.USED_VERSION, source);
        result = new LowerCaseFilter(LuceneConfig.USED_VERSION, result);
        result = new FinnishTokenFilter(result, voikko);
        //result = stopFilterFactory.create(result);
        TokenStreamComponents components = new TokenStreamComponents(source, result);

        return components;
    }

    /*@Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new ClassicTokenizer(LuceneConfig.USED_VERSION, reader);
        TokenStream result = new LowerCaseFilter(LuceneConfig.USED_VERSION, source);
        result = new FinnishTokenFilter(result, voikko);
        //result = stopFilterFactory.create(result);
        TokenStreamComponents components = new TokenStreamComponents(source, result);

        return components;
    }*/

}
