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

package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.metka.ddi.MetkaXmlOptions;
import fi.uta.fsd.metka.enums.Language;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;
import org.springframework.util.Assert;

public class DDIWriteTest {

    private static Logger logger = LogManager.getLogger(DDIWriteTest.class);

    private XmlOptions options = MetkaXmlOptions.DDI_EXPORT_XML_OPTIONS;

    private class DDITestWriter extends DDIWriteSectionBase {

        public DDITestWriter(CodeBookType codeBookType) {
            super(null, Language.DEFAULT,codeBookType,null,null,null);
        }

        private String text;
        public void setText(String text) {
            this.text = text;
        }
        @Override
        void write() {
            AbstractType abstractType = codeBook.addNewStdyDscr().addNewStdyInfo().addNewAbstract();

            fillTextType(abstractType,text);
        }
    }

    @Test
    public void testSinglePTagWrite() {
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();


        // Add content to codebook document
        DDITestWriter writer = new DDITestWriter(codeBookType);
        writer.setText("<p>this is within p tags</p>");
        writer.write();

        System.out.println(codeBookDocument.xmlText(options));
    }

    @Test
    public void testTwoPTagWrite() {
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();


        // Add content to codebook document
        DDITestWriter writer = new DDITestWriter(codeBookType);
        writer.setText("<p>this is within first p tags</p>This is between p tags<p>this is within last p tags</p>");
        writer.write();

        String result = codeBookDocument.xmlText(options);

        // our rich text conforms to codebook defined elements, so it should not use xhtml namespace
        Assert.isTrue(!result.contains("xht:"));

        System.out.println(result);
    }

    @Test
    public void testAttributeWrite() {
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();


        // Add content to codebook document
        DDITestWriter writer = new DDITestWriter(codeBookType);
        writer.setText("Here is also text " +
                "<p style='text-style:bold'>this has text style bold</p>" +
                "<p>this text within p tags</p>");
        writer.write();

        String result = codeBookDocument.xmlText(options);

        System.out.println(result);
    }

    @Test
    public void testXhtmlFallback() {

        // this should go to xhtml fallback on rich text, since codebook p-element for example doesn't allow a-elements.

        String text = "<p><a href=\"http://test.test\"><i>inside i tag</i>between i and span tags<span>inside span tag</span></a>Test</p>\n" +
                "<p style=\"line-height: 1;\">Testing&nbsp;</p>\n" +
                "<p>Abstract</p>\n" +
                "<p>Test & &nbsp; &amp; Test</p>\n" +
                "<p><span style=\"font-weight: bold;\">Test font style</span></p>";

        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();


        // Add content to codebook document
        DDITestWriter writer = new DDITestWriter(codeBookType);
        writer.setText(text);
        writer.write();

        String result = codeBookDocument.xmlText(options);

        System.out.println(result);
    }
}
