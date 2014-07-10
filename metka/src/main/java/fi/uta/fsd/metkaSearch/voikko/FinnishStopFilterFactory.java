package fi.uta.fsd.metkaSearch.voikko;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FinnishStopFilterFactory extends TokenFilterFactory {

    private CharArraySet stopWords;

    public FinnishStopFilterFactory(Version version) {
        this(new HashMap<String,String>(Collections.singletonMap(LUCENE_MATCH_VERSION_PARAM, version.toString())));
    }

    public FinnishStopFilterFactory(Map<String, String> args) {
        super(args);

        initStopWords();
    }

    protected void initStopWords() {
        try {
            stopWords = WordlistLoader.getWordSet(IOUtils.getDecodingReader(getClass(),
                    "stopwords.txt", StandardCharsets.UTF_8), "#", getLuceneMatchVersion());
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load default stopword set");
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new StopFilter(getLuceneMatchVersion(), input, stopWords);
    }
}