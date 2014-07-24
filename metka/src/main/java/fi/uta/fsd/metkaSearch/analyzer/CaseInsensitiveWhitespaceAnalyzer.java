package fi.uta.fsd.metkaSearch.analyzer;

import fi.uta.fsd.metkaSearch.LuceneConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

import java.io.Reader;

public final class CaseInsensitiveWhitespaceAnalyzer extends Analyzer {
    public static final CaseInsensitiveWhitespaceAnalyzer ANALYZER = new CaseInsensitiveWhitespaceAnalyzer();

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        TokenStreamComponents stream = new TokenStreamComponents(new WhitespaceTokenizer(LuceneConfig.USED_VERSION, reader));
        TokenStream result = new LowerCaseFilter(LuceneConfig.USED_VERSION, stream.getTokenStream());
        stream = new TokenStreamComponents(stream.getTokenizer(), result);
        return stream;
    }
}
