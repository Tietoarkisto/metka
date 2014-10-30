package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class DDIReader {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ReferenceService references;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private StudyVariableSearch variableSearch;

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
    public ReturnResult readDDIDocument(String path, RevisionData revision) {
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

        try {
            CodeBookDocument document = CodeBookDocument.Factory.parse(file);
            CodebookReader reader = new CodebookReader(revisions, references, document, revision, configPair.getRight(), variableSearch);
            return reader.read();
        } catch(IOException ioe) {
            Logger.error(DDIReader.class, "IOException during DDI-xml parsing.", ioe);
            return ReturnResult.EXCEPTION;
        } catch(XmlException xe) {
            Logger.error(DDIReader.class, "XmlException during DDI-xml parsing.", xe);
            return ReturnResult.EXCEPTION;
        }
    }

    private static class CodebookReader {
        private final RevisionRepository revisions;
        private final ReferenceService references;
        private final CodeBookDocument document;
        private final RevisionData revision;
        private final Configuration configuration;
        private final StudyVariableSearch variableSearch;

        private CodebookReader(RevisionRepository revisions, ReferenceService references, CodeBookDocument document, RevisionData revision, Configuration configuration, StudyVariableSearch variableSearch) {
            this.revisions = revisions;
            this.references = references;
            this.document = document;
            this.revision = revision;
            this.configuration = configuration;
            this.variableSearch = variableSearch;
        }

        private ReturnResult read() {
            CodeBookType codeBook = document.getCodeBook();
            Language docLang = Language.fromValue(codeBook.getXmlLang().toLowerCase());

            DateTimeUserPair info = DateTimeUserPair.build();

            DDISectionBase section;
            ReturnResult result;
/*
1) DDI-import voidaan suorittaa kuvailu-välilehden osalta vain silloin, kun kuvailu-välilehdellä ei ole mitään tallennettuja tietoja (eli se on tyhjä),
 muilla aineiston välilehdillä saa olla toki tallennettuja tietoja.
2) DDI-import voidaan suorittaa kuvailu-välilehden osalta vain default-kieliselle (eli meidän tapauksessa suomenkieliselle) DDI-tiedostolle.
*/
            boolean importDescription = docLang == Language.DEFAULT && isDescriptionTabClear();

            // TODO: Check if description tab is clear


            // TODO: Still unfinished
            section = new DDIDataDescription(revision, docLang, codeBook, info, configuration, revisions, variableSearch);
            result = section.read();

            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return result;
            }

            if(importDescription) {
                // TODO: Still unfinished
                section = new DDIStudyDescription(revision, docLang, codeBook, info, configuration, references);
                result = section.read();

                if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                    return result;
                }

                section = new DDIOtherMaterialDescription(revision, docLang, codeBook, info, configuration);
                result = section.read();

                if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                    return result;
                }
            }

            // Form biblcit
            StudyFactory fac = new StudyFactory();
            result = fac.formUrnAndBiblCit(revision, info, references, new MutablePair<Boolean, Boolean>());

            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return result;
            }
            return revisions.updateRevisionData(revision);
        }

        private boolean isDescriptionTabClear() {
            return false;
        }
    }
}
