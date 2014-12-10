package fi.uta.fsd.metka.ddi;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.metka.ddi.builder.CodebookWriter;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DDIBuilderService {
    @Autowired
    private RevisionRepository revisions;
    @Autowired
    private ReferenceService references;

    public Pair<ReturnResult, CodeBookDocument> buildDDIDocument(Language language, RevisionData revisionData, Configuration configuration) {
        // Create the codebook xml document
        CodeBookDocument codeBookDocument = CodeBookDocument.Factory.newInstance();
        CodeBookType codeBookType = codeBookDocument.addNewCodeBook();

        // Add content to codebook document
        CodebookWriter writer = new CodebookWriter(language, revisionData, configuration, codeBookType, revisions, references);
        writer.write();

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, codeBookDocument);
    }
}
