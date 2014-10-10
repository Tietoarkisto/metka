package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
@Transactional
public class DDIReader {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ReferenceService references;

    @Autowired
    private ConfigurationRepository configurations;

    /**
     * Tries to read CodeBook from provided path and merge it into given RevisionData (checked to be of type STUDY).
     * Additional revisions may be affected depending on what data is contained in the CodeBook and what data does the
     * provided revision have. No variables will be created for example but if variables are present both in the database and
     * in the CodeBook then merge of those variables is attempted.
     * @param path
     * @param revision
     * @return
     * @throws Exception
     */
    public ReturnResult readDDIDocument(String path, RevisionData revision) throws Exception {
        File file = new File(path);
        if(!file.exists() || !file.isFile()) {
            return ReturnResult.PARAMETERS_MISSING;
        }
        if(revision.getConfiguration().getType() != ConfigurationType.STUDY) {
            return ReturnResult.INCORRECT_TYPE_FOR_OPERATION;
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            return configPair.getLeft();
        }

        CodeBookDocument document = CodeBookDocument.Factory.parse(file);
        CodebookReader reader = new CodebookReader(revisions, references, document, revision, configPair.getRight());

        return reader.read();
    }

    private static class CodebookReader {
        private final RevisionRepository revisions;
        private final ReferenceService references;
        private final CodeBookDocument document;
        private final RevisionData revision;
        private final Configuration configuration;

        private CodebookReader(RevisionRepository revisions, ReferenceService references, CodeBookDocument document, RevisionData revision, Configuration configuration) {
            this.revisions = revisions;
            this.references = references;
            this.document = document;
            this.revision = revision;
            this.configuration = configuration;
        }

        private ReturnResult read() {
            CodeBookType codeBook = document.getCodeBook();
            Language docLang = Language.fromValue(codeBook.getXmlLang().toLowerCase());

            DateTimeUserPair info = DateTimeUserPair.build();

            DDISectionBase section = new DDIDocumentDescription(revision, docLang, codeBook, info, configuration);
            section.read();

            // TODO: implement when table question is resolved
            section = new DDIOtherMaterialDescription(revision, docLang, codeBook, info, configuration);
            section.read();

            // TODO: Implement when questions about tables and variables are answered
            section = new DDIDataDescription(revision, docLang, codeBook, info, configuration, revisions);
            section.read();

            section = new DDIStudyDescription(revision, docLang, codeBook, info, configuration, revisions, references);
            section.read();

            // TODO: If needed
            DDIFileDescription.readFileDescription(revision, docLang, codeBook, revisions);

            // Form biblcit
            // TODO: Or should we just read biblcit from DDI even though it will be overwritten in the next save?
            StudyFactory fac = new StudyFactory();
            fac.formUrnAndBiblCit(revision, info, references, new MutablePair<Boolean, Boolean>());

            return revisions.updateRevisionData(revision);
        }
    }
}
