package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookDocument;
import codebook25.CodeBookType;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
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

        if(revision.getState() != RevisionState.DRAFT) {
            Pair<ReturnResult, RevisionData> pair = edit.edit(TransferData.buildFromRevisionData(revision, RevisionableInfo.FALSE));
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
            Logger.error(DDIReader.class, "IOException during DDI-xml parsing.", ioe);
            return ReturnResult.EXCEPTION;
        } catch(XmlException xe) {
            Logger.error(DDIReader.class, "XmlException during DDI-xml parsing.", xe);
            return ReturnResult.EXCEPTION;
        }
    }

    private static class CodebookReader {
        private final RevisionRepository revisions;
        private final RevisionEditRepository edit;
        private final ReferenceService references;
        private final CodeBookDocument document;
        private final RevisionData revision;
        private final Configuration configuration;
        private final StudyVariableSearch variableSearch;

        private CodebookReader(RevisionRepository revisions, RevisionEditRepository edit, ReferenceService references
                , CodeBookDocument document, RevisionData revision, Configuration configuration, StudyVariableSearch variableSearch) {
            this.revisions = revisions;
            this.edit = edit;
            this.references = references;
            this.document = document;
            this.revision = revision;
            this.configuration = configuration;
            this.variableSearch = variableSearch;
        }

        private ReturnResult read() {
            if(revision.getState() != RevisionState.DRAFT || !AuthenticationUtil.isHandler(revision)) {
                Logger.warning(DDIReader.class, "User "+AuthenticationUtil.getUserName()+" tried to import DDI for study "+revision.getKey().toString()+" that either was not a draft or is handled by someone else.");
                return ReturnResult.OPERATION_FAIL;
            }
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
            Pair<StatusCode, ValueDataField> valuePair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
            if(valuePair.getLeft() != StatusCode.FIELD_FOUND) {
                return ReturnResult.OPERATION_FAIL;
            }

            section = new DDIDataDescription(revision, docLang, codeBook, info, configuration, revisions, edit, variableSearch, valuePair.getRight().getActualValueFor(Language.DEFAULT));
            result = section.read();

            if(result != ReturnResult.OPERATION_SUCCESSFUL) {
                return result;
            }

            boolean importDescription = docLang == Language.DEFAULT && isDescriptionTabClear();

            if(importDescription) {
                // If language is DEFAULT and description tab is clear then import values
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
            result = revisions.updateRevisionData(revision);
            return result == ReturnResult.REVISION_UPDATE_SUCCESSFUL ? ReturnResult.OPERATION_SUCCESSFUL : result;
        }

        private boolean isDescriptionTabClear() {
            // Check all fields on description tab so that they don't contain input (disregards biblcit since it's formed automatically.
            if(!checkIsContainerClear(Fields.ALTTITLES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.PARTITLES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.AUTHORS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.OTHERAUTHORS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.PRODUCERS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.KEYWORDS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.TOPICS)) {
                return false;
            }
            if(!checkIsValueClear(Fields.ABSTRACT)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.TIMEPERIODS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.COUNTRIES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.UNIVERSES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.GEOGCOVERS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.COLLTIME)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.COLLECTORS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.ANALYSIS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.TIMEMETHODS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.COLLMODES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.INSTRUMENTS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.SAMPPROCS)) {
                return false;
            }
            if(!checkIsValueClear(Fields.RESPRATE)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.DATASOURCES)) {
                return false;
            }
            if(!checkIsValueClear(Fields.WEIGHTYESNO)) {
                return false;
            }
            if(!checkIsValueClear(Fields.WEIGHT)) {
                return false;
            }
            if(!checkIsValueClear(Fields.DATAPROSESSING)) {
                return false;
            }
            if(!checkIsValueClear(Fields.COLLSIZE)) {
                return false;
            }
            if(!checkIsValueClear(Fields.COMPLETE)) {
                return false;
            }
            if(!checkIsValueClear(Fields.DATASETNOTES)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.APPRAISALS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.RELATEDMATERIALS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.OTHERMATERIALS)) {
                return false;
            }
            if(!checkIsContainerClear(Fields.PUBLICATIONCOMMENTS)) {
                return false;
            }
            return true;
        }

        private boolean checkIsContainerClear(String key) {
            Pair<StatusCode, ContainerDataField> container = revision.dataField(ContainerDataFieldCall.get(key));
            return container.getLeft() != StatusCode.FIELD_FOUND || !container.getRight().hasRows();
        }

        private boolean checkIsValueClear(String key) {
            Pair<StatusCode, ValueDataField> value = revision.dataField(ValueDataFieldCall.get(key));
            if(value.getLeft() != StatusCode.FIELD_FOUND) {
                return true;
            }
            for(Language l : Language.values()) {
                if(value.getRight().hasValueFor(l)) {
                    return false;
                }
            }
            return true;
        }
    }
}
