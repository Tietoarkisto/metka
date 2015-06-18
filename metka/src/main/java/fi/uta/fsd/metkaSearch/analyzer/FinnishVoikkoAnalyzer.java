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

package fi.uta.fsd.metkaSearch.analyzer;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.filter.FinnishTokenFilter;
import fi.uta.fsd.metkaSearch.voikko.FinnishStopFilterFactory;
import fi.uta.fsd.metkaSearch.voikko.VoikkoFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.puimula.libvoikko.Voikko;

import java.io.IOException;
import java.io.Reader;

public class FinnishVoikkoAnalyzer extends Analyzer {

    public static final Analyzer ANALYZER;
    static {
        Analyzer temp;
        try {
            temp = new FinnishVoikkoAnalyzer();
        } catch (IOException e) {
            Logger.error(FinnishVoikkoAnalyzer.class, "Could not initialize ANALYZER. Initializing with whitespace analyzer instead", e);
            temp = new WhitespaceAnalyzer(LuceneConfig.USED_VERSION);
        }
        ANALYZER = temp;
    }

    private final Voikko voikko;
    private TokenFilterFactory stopFilterFactory = new FinnishStopFilterFactory(LuceneConfig.USED_VERSION);

    public FinnishVoikkoAnalyzer() throws IOException {
        this(null);
    }

    public FinnishVoikkoAnalyzer(Voikko voikko) throws IOException {
        if(voikko == null) {
            Logger.debug(getClass(), "Trying to create Voikko-object");
            voikko = VoikkoFactory.create();
        }
        this.voikko = voikko;

    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new StandardTokenizer(LuceneConfig.USED_VERSION, reader);
        TokenStream result = new StandardFilter(LuceneConfig.USED_VERSION, source);
        result = new LowerCaseFilter(LuceneConfig.USED_VERSION, result);
        try {
            result = new FinnishTokenFilter(result, voikko);
        } catch(IOException ioe) {
            Logger.error(getClass(), "IOException while creating FinnishTokenFilter.", ioe);
        }
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
