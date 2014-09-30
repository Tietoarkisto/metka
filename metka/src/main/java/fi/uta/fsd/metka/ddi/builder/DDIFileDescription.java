package fi.uta.fsd.metka.ddi.builder;

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

import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.fillTextType;
import static fi.uta.fsd.metka.ddi.builder.DDIBuilder.hasValue;

class DDIFileDescription {
    static void addfileDescription(RevisionData revisionData, Language language, Configuration configuration, CodeBookType codeBookType, RevisionRepository revisions) {
        Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(!hasValue(valueFieldPair, Language.DEFAULT)) {
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
        if(!hasValue(valueFieldPair, Language.DEFAULT)) {
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
        ;// Add dimensions
        DimensnsType dimensnsType = fileTxtType.addNewDimensns();
        Pair<StatusCode, ValueDataField> valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.CASEQUANTITY));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Add case quantity
            fillTextType(dimensnsType.addNewCaseQnty(), valueFieldPair, Language.DEFAULT);
        }

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARQUANTITY));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Add case quantity
            fillTextType(dimensnsType.addNewVarQnty(), valueFieldPair, Language.DEFAULT);
        }
    }

    private static void setSoftware(RevisionData variables, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.SOFTWARE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            SoftwareType softwareType = fillTextType(fileTxtType.addNewSoftware(), valueFieldPair, Language.DEFAULT);

            // We can't separate version in any easy way from software information since it doesn't come in two distinct fields in POR-file
            softwareType.setVersion(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
    }

    private static void setFileNameAndID(RevisionData variables, RevisionData attachment, FileTxtType fileTxtType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            // Set file name
            SimpleTextType stt = fillTextType(fileTxtType.addNewFileName(), FilenameUtils.getName(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)));

            // set ID
            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILEID));
            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                stt.setID(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }

            // Add file type
            valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILETYPE));
            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                fillTextType(fileTxtType.addNewFileType(), valueFieldPair, Language.DEFAULT);
            }
        }
    }

    private static void setFileDescription(Language language, RevisionData attachment, FileDscrType fileDscrType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILEDESCRIPTION));
        // TODO: Is this actually a translatable field
        if(hasValue(valueFieldPair, language)) {
            fillTextType(fileDscrType, valueFieldPair, language);
        }
    }
}
