package fi.uta.fsd.metka.ddi;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;

class DDIFileDescription {
    static void addfileDescription(RevisionData revisionData, Language language, Configuration configuration, CodeBookType codeBookType, RevisionRepository revisions) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(valueFieldPair.getLeft() != StatusCode.FIELD_FOUND || !valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            return;
        }

        // Get variables data since it contains most of the information needed for this. Some additional data is also needed from the actual file but very little.
        Pair<ReturnResult, RevisionData> revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(DDIFileDescription.class, "Couldn't find expected variables revision with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
            return;
        }
        RevisionData variables = revisionDataPair.getRight();

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(valueFieldPair.getLeft() != StatusCode.FIELD_FOUND || !valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Logger.error(DDIFileDescription.class, "Variables revision "+variables.toString()+" did not contain file reference although it should be present.");
            return;
        }
        revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_ATTACHMENT);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(DDIFileDescription.class, "Couldn't find study attachment with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger() +
                    " even though it's referenced from variables data " + variables.toString());
            return;
        }
        RevisionData attachment = revisionDataPair.getRight();

        // Get FileDscrType
        FileDscrType fileDscrType = codeBookType.addNewFileDscr();
        setFileDescription(language, attachment, fileDscrType);

        // Get FileTxtType
        FileTxtType fileTxtType = fileDscrType.addNewFileTxt();

        // Sets file name and file id
        setFileNameAndID(variables, attachment, fileTxtType);

        // Set software information
        setSoftware(variables, fileTxtType);

        // Set dimension information
        setDimensions(variables, fileTxtType);
    }

    private static void setDimensions(RevisionData variables, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair;// Add dimensions
        DimensnsType dimensnsType = fileTxtType.addNewDimensns();
        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.CASEQUANTITY));
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Add case quantity
            SimpleTextType stt = dimensnsType.addNewCaseQnty();
            XmlCursor xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            xmlCursor.dispose();
        }

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARQUANTITY));
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            // Add case quantity
            SimpleTextType stt = dimensnsType.addNewVarQnty();
            XmlCursor xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            xmlCursor.dispose();
        }
    }

    private static void setSoftware(RevisionData variables, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair;
        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.SOFTWARE));
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            SoftwareType softwareType = fileTxtType.addNewSoftware();
            XmlCursor xmlCursor = softwareType.newCursor();
            xmlCursor.setTextValue(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            xmlCursor.dispose();

            // We can't separate version in any easy way from software information since it doesn't come in two distinct fields in POR-file
            softwareType.setVersion(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private static void setFileNameAndID(RevisionData variables, RevisionData attachment, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            SimpleTextType stt = fileTxtType.addNewFileName();
            XmlCursor xmlCursor = stt.newCursor();
            xmlCursor.setTextValue(FilenameUtils.getName(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)));
            xmlCursor.dispose();
            // Set ID

            // TODO: What does file type actually mean? For now use upper case file extension
            // Add file type
            FileTypeType fileTypeType = fileTxtType.addNewFileType();
            xmlCursor = fileTypeType.newCursor();
            xmlCursor.setTextValue(FilenameUtils.getExtension(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)).toUpperCase());
            xmlCursor.dispose();

            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILEID));
            if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                stt.setID(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
        }
    }

    private static void setFileDescription(Language language, RevisionData attachment, FileDscrType fileDscrType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILEDESCRIPTION));
        // TODO: Is this actually a translatable field
        if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().hasValueFor(language)) {
            XmlCursor xmlCursor = fileDscrType.newCursor();
            xmlCursor.setTextValue(valueFieldPair.getRight().getActualValueFor(language));
            xmlCursor.dispose();
        }
    }
}
