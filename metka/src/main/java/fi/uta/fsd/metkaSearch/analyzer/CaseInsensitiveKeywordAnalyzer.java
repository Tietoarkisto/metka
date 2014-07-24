package fi.uta.fsd.metkaSearch.analyzer;

import fi.uta.fsd.metkaSearch.LuceneConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;

import java.io.Reader;

public class CaseInsensitiveKeywordAnalyzer extends Analyzer {
    public static final CaseInsensitiveKeywordAnalyzer ANALYZER = new CaseInsensitiveKeywordAnalyzer();

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        TokenStreamComponents stream = new TokenStreamComponents(new KeywordTokenizer(reader));
        TokenStream result = new LowerCaseFilter(LuceneConfig.USED_VERSION, stream.getTokenStream());
        stream = new TokenStreamComponents(stream.getTokenizer(), result);
        return stream;
    }
}
