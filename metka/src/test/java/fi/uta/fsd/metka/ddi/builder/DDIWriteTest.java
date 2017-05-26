package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.metka.ddi.MetkaXmlOptions;
import fi.uta.fsd.metka.ddi.builder.CodebookWriter;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.xml.crypto.dsig.XMLObject;

/**
 * Created by henrisu on 22.11.2016.
 */
public class DDIWriteTest {

    private static Logger logger = LogManager.getLogger(DDIWriteTest.class);

    private class TestDDIWriter1 extends DDIWriteSectionBase {

        public TestDDIWriter1(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
            super(revision, language, codeBook, configuration, revisions, references);
        }

        @Override
        void write() {

            StdyDscrType stdyDscrType = codeBook.addNewStdyDscr();
            StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

            fillTextType(stdyInfo.addNewAbstract(), "<p>this abstract has one p tag</p>");
        }
    }

    private class TestDDIWriter2 extends DDIWriteSectionBase {

        public TestDDIWriter2(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
            super(revision, language, codeBook, configuration, revisions, references);
        }

        @Override
        void write() {
            StdyDscrType stdyDscrType = codeBook.addNewStdyDscr();
            StdyInfoType stdyInfo = stdyDscrType.addNewStdyInfo();

            fillTextType(stdyInfo.addNewAbstract(), "<p>this abstract has two p tags</p><p><br></p><p>this abstract has two p tags</p>");
        }
    }

    @Test
    public void writeSinglePTag() {
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();

        DDIWriteHeader ddiWriteHeader = new DDIWriteHeader(null, Language.DEFAULT,codeBookType,null,null,null);
        ddiWriteHeader.write();

        TestDDIWriter1 testDDIWriter = new TestDDIWriter1(null,Language.DEFAULT,codeBookType,null,null,null);
        // Add content to codebook document
        testDDIWriter.write();
        //how to test the result?

        logger.info("\n\n" + codeBookDocument.xmlText(MetkaXmlOptions.DDI_EXPORT_XML_OPTIONS));
        Assert.notNull(codeBookDocument);
    }

    @Test
    public void writeTwoPTagsAndBr() {
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();

        DDIWriteHeader ddiWriteHeader = new DDIWriteHeader(null, Language.DEFAULT,codeBookType,null,null,null);
        ddiWriteHeader.write();

        TestDDIWriter2 testDDIWriter = new TestDDIWriter2(null,Language.DEFAULT,codeBookType,null,null,null);
        // Add content to codebook document
        testDDIWriter.write();

        //how to test the result?
        logger.info("\n\n" + codeBookDocument.xmlText(MetkaXmlOptions.DDI_EXPORT_XML_OPTIONS));
        Assert.notNull(codeBookDocument);
    }
}
