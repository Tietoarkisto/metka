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

package fi.uta.fsd.metkaSearch.filter;

import fi.uta.fsd.Logger;
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
    public FinnishTokenFilter(TokenStream input) throws IOException {
        this(input, null);

    }

    public FinnishTokenFilter(TokenStream input, Voikko voikko) throws IOException {
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

        try {
            formWordQueue();
        } catch (IllegalArgumentException e) {
            Logger.error(getClass(), "Could not parse term " + termAttr, e);
        }

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
                /*System.err.println(wordbase);*/
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