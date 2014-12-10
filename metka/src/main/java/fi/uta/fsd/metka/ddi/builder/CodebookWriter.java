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
