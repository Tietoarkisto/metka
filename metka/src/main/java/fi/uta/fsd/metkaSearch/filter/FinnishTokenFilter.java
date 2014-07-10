package fi.uta.fsd.metkaSearch.filter;

import fi.uta.fsd.metkaSearch.iterator.WordBasesIterator;
import fi.uta.fsd.metkaSearch.voikko.VoikkoFactory;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.puimula.libvoikko.Analysis;
import org.puimula.libvoikko.Voikko;

import java.io.IOException;
import java.util.*;

public final class FinnishTokenFilter extends TokenFilter {

    protected final Voikko voikko;

    protected final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    protected final PositionLengthAttribute poslenAttr = addAttribute(PositionLengthAttribute.class);
    protected final PositionIncrementAttribute posincAttr = addAttribute(PositionIncrementAttribute.class);
    private List<String> wordbasesList;
    private Deque<String> wordQueue;
    public FinnishTokenFilter(TokenStream input) {
        this(input, null);

    }

    public FinnishTokenFilter(TokenStream input, Voikko voikko) {
        super(input);
        this.voikko = voikko == null ? VoikkoFactory.create() : voikko;
        wordbasesList = new ArrayList<String>();
        wordQueue = new LinkedList<String>();
    }

    @Override
    public boolean incrementToken() throws IOException {

        if(!wordQueue.isEmpty()) {
            String word = wordQueue.removeFirst();
            termAttr.setEmpty().append(word);
            posincAttr.setPositionIncrement(0);
            poslenAttr.setPositionLength(poslenAttr.getPositionLength()+1);
            return true;
        }

        if(!input.incrementToken())
            return false;

        wordbasesList.clear();

        analyze();

        if (wordbasesList.isEmpty()) {
            return true;
        }

        formWordQueue();

        return true;
    }

    private void analyze() {
        String word = termAttr.toString();

        List<Analysis> results = voikko.analyze(word);

        for (Analysis result : results) {
            String wordbases = result.get("WORDBASES");
            if (wordbases != null)
                wordbasesList.add(wordbases);
        }
    }

    private void formWordQueue() {
        if(wordbasesList.size() > 0) {
            Set<String> wordset = new LinkedHashSet<String>();
            wordset.add(termAttr.toString());
            for(String wordbase : wordbasesList) {
                for(WordBasesIterator i = new WordBasesIterator(wordbase); i.hasNext(); ) {
                    String iWord = i.next().toString();
                    if(wordset.add(iWord)) {
                        wordQueue.add(iWord);
                    }
                }
            }
        }
    }
}