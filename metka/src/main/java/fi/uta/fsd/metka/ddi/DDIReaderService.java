package fi.uta.fsd.metka.ddi;

import codebook25.CodeBookDocument;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.ddi.reader.CodebookReader;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class DDIReaderService {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionEditRepository edit;

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
        if(!StringUtils.hasText(path)) {
            return ReturnResult.EMPTY_PATH;
        }
        File file = new File(path);
        if(!file.exists() || !file.isFile()) {
            return ReturnResult.MALFORMED_PATH;
        }
        if(revision.getConfiguration().getType() != ConfigurationType.STUDY) {
            return ReturnResult.INCORRECT_TYPE_FOR_OPERATION;
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            return configPair.getLeft();
        }

        if(revision.getState() != RevisionState.DRAFT) {
            Pair<ReturnResult, RevisionData> pair = edit.edit(TransferData.buildFromRevisionData(revision, RevisionableInfo.FALSE), null);
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                if(!AuthenticationUtil.isHandler(pair.getRight())) {
                    return ReturnResult.USER_NOT_HANDLER;
                }
            } else if(pair.getLeft() != ReturnResult.REVISION_CREATED) {
                return pair.getLeft();
            }
            revision = pair.getRight();
        } else if(!AuthenticationUtil.isHandler(revision)) {
            return ReturnResult.USER_NOT_HANDLER;
        }

        try {
            CodeBookDocument document = CodeBookDocument.Factory.parse(file);
            CodebookReader reader = new CodebookReader(revisions, edit, references, document, revision, configPair.getRight(), variableSearch);
            return reader.read();
        } catch(IOException ioe) {
            Logger.error(getClass(), "IOException during DDI-xml parsing.", ioe);
            return ReturnResult.EXCEPTION;
        } catch(XmlException xe) {
            Logger.error(getClass(), "XmlException during DDI-xml parsing.", xe);
            return ReturnResult.EXCEPTION;
        }
    }
}
