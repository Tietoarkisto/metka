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
