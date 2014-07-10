package fi.uta.fsd.metkaSearch.analyzer;

import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class DefaultAnalyzer extends Analyzer {
    // TODO: Change to exact phrase indexing somehow
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new StandardTokenizer(LuceneConfig.USED_VERSION, reader);
        TokenStream result = new StandardFilter(LuceneConfig.USED_VERSION, source);
        TokenStreamComponents components = new TokenStreamComponents(source, result);
        return components;
    }
}
