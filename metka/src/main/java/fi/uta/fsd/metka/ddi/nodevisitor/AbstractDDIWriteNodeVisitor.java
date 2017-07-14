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

package fi.uta.fsd.metka.ddi.nodevisitor;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Stack;

public abstract class AbstractDDIWriteNodeVisitor implements NodeVisitor {

    protected Stack<XmlObject> stack;
    protected Class[] allowedClasses;

    public AbstractDDIWriteNodeVisitor(XmlObject stt, Class[] allowedClasses) {
        this.stack = new Stack<>();
        this.stack.add(stt);
        this.allowedClasses = allowedClasses;
    }

    @Override
    public void head(Node node, int depth) {
        String nodeName = node.nodeName();

        if ("#text".equals(nodeName)) {
            appendXML(((TextNode) node).text());
        } else {
            callAddMethod(nodeName);
            addAttributes(node.attributes());
        }
    }

    protected abstract void _callAddMethod(Class<?> clazz, String capitalizedTagName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    private void callAddMethod(String tagName) {

        String capitalizedTagName = StringUtils.capitalize(tagName);
        try {
            // possible values should be
            for(Class clazz : allowedClasses) {
                if (clazz.isInstance(this.stack.peek())) {
                    _callAddMethod(clazz, capitalizedTagName);
                    return;
                }
            }

            // allowed classes is missing a class to parse
            throw new UnsupportedOperationException("Could not find allowed class to parse" + tagName);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new UnsupportedOperationException("Could not parse tag " + tagName);
        }
    }

    private void addAttributes(Attributes attributes) {
        Iterator<Attribute> attIterator = attributes.iterator();
        XmlCursor cursor = this.stack.peek().newCursor();
        cursor.toFirstContentToken();
        while (attIterator.hasNext()) {
            Attribute att = attIterator.next();

            cursor.insertAttributeWithValue(att.getKey(),att.getValue());

        }
        cursor.dispose();

    }

    private void appendXML(String value) {
        XmlCursor cursor = this.stack.peek().newCursor();
        cursor.toEndToken();
        cursor.insertChars(value);
        cursor.dispose();
    }
    @Override
    public void tail(Node node, int depth) {
        if (!"#text".equals(node.nodeName())) {
            this.stack.pop();
        }
    }
}
