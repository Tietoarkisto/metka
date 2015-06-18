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

import codebook25.CodeBookType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import org.apache.xmlbeans.XmlCursor;

import javax.xml.namespace.QName;

class DDIWriteHeader extends DDIWriteSectionBase {
    DDIWriteHeader(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        // Set namespaces. Get cursor
        XmlCursor xmlCursor = codeBook.newCursor();

        // Move cursor to last attribute
        xmlCursor.toLastAttribute();

        // Create new qualified name
        QName qName = new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");

        // Location string
        String location = "ddi:codebook:2_5 http://www.ddialliance.org/" + "Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd";

        // Set attribute
        xmlCursor.setAttributeText(qName, location);

        // Move cursor to last attribute
        xmlCursor.toLastAttribute();

        // Set version
        xmlCursor.insertAttributeWithValue("version", "2.5");

        // Dispose cursor
        xmlCursor.dispose();

        // Sets xml:lang attribute
        String languageCode = getXmlLang();
        codeBook.setXmlLang(languageCode);
    }
}
