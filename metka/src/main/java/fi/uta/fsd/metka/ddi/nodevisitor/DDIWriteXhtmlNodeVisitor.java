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

import codebook25.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.w3.x1999.xhtml.*;
import org.w3.x1999.xhtml.DivType;
import org.w3.x1999.xhtml.PType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DDIWriteXhtmlNodeVisitor extends AbstractDDIWriteNodeVisitor {

    public DDIWriteXhtmlNodeVisitor(XmlObject stt) {
        super(stt, new Class[] {
                StringType.class,
                SimpleTextType.class,
                ConceptualTextType.class,

                PType.class,
                DivType.class,
                BrType.class,
                SpanType.class,
                AType.class,
                InlPresType.class,
                LiType.class,
                UlType.class,
                OlType.class
        });
    }

    protected void _callAddMethod(Class<?> clazz, String capitalizedTagName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // in xmlbeans adding methods are appended with "2" so try that first
        Method method;
        String addMethodName = "addNew";

        capitalizedTagName = convertTagnameIfNecessary(capitalizedTagName);
        try {
            method = clazz.getMethod(StringUtils.join(addMethodName , capitalizedTagName , "2"));
        } catch (NoSuchMethodException e) {
            // if no xhtml specific method is found, use without appending "2".
            method = clazz.getMethod(StringUtils.join(addMethodName , capitalizedTagName));
        }
        Class<?> returnType = method.getReturnType();
        Object value = method.invoke(this.stack.peek());
        this.stack.push((XmlObject) returnType.cast(value));
    }

    private String convertTagnameIfNecessary(String tagName) {
        return tagName;
    }
}
