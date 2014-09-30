package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
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

        CodeBookDocument document = CodeBookDocument.Factory.parse(file);
        CodeBookType codeBook = document.getCodeBook();

        return readCodeBook(codeBook, revision);
    }

    private ReturnResult readCodeBook(CodeBookType codeBook, RevisionData revision) {
        Language docLang = Language.fromValue(codeBook.getLang().toLowerCase());

        DDIDocumentDescription.readDocumentDescription(revision, docLang, codeBook);
        DDIStudyDescription.readStudyDescription(revision, docLang, codeBook, revisions, references);
        DDIFileDescription.readFileDescription(revision, docLang, codeBook, revisions);
        DDIDataDescription.readDataDescription(revision, docLang, codeBook, revisions);
        DDIOtherMaterialDescription.readOtherMaterialDescription(revision, docLang, codeBook);

        // TODO: Form biblCit at the end of the process

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
