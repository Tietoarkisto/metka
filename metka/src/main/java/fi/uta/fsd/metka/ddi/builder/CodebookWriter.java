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

public class CodebookWriter {
    private final Language language;
    private final RevisionData revision;
    private final Configuration configuration;
    private final CodeBookType codeBook;
    private final RevisionRepository revisions;
    private final ReferenceService references;

    public CodebookWriter(Language language, RevisionData revision, Configuration configuration, CodeBookType codeBook, RevisionRepository revisions, ReferenceService references) {
        this.language = language;
        this.revision = revision;
        this.configuration = configuration;
        this.codeBook = codeBook;
        this.revisions = revisions;
        this.references = references;
    }

    public void write() {

        DDIWriteSectionBase section;

        section = new DDIWriteHeader(revision, language, codeBook, configuration, revisions, references);
        section.write();

        section = new DDIWriteDocumentDescription(revision, language, codeBook, configuration, revisions, references);
        section.write();

        section = new DDIWriteStudyDescription(revision, language, codeBook, configuration, revisions, references);
        section.write();

        section = new DDIWriteFileDescription(revision, language, codeBook, configuration, revisions, references);
        section.write();

        section = new DDIWriteDataDescription(revision, language, codeBook, configuration, revisions, references);
        section.write();

        section = new DDIWriteOtherMaterialDescription(revision, language, codeBook, configuration, revisions, references);
        section.write();
    }
}
