package fi.uta.fsd.metka.ddi.builder;

import codebook25.CodeBookType;
import codebook25.OtherMatType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import org.apache.commons.lang3.tuple.Pair;

class DDIWriteOtherMaterialDescription extends DDIWriteSectionBase {
    DDIWriteOtherMaterialDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        Pair<StatusCode, ContainerDataField> containerPair = revision.dataField(ContainerDataFieldCall.get(Fields.OTHERMATERIALS));

        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                if(row.getRemoved()) {
                    continue;
                }
                OtherMatType otherMatType = codeBook.addNewOtherMat();

                setURI(row, otherMatType);
                addLabel(row, otherMatType);
                setText(row, otherMatType);
            }
        }
    }

    private void setURI(DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALURI));
        if(hasValue(valueFieldPair, language)) {
            otherMatType.setURI(valueFieldPair.getRight().getActualValueFor(language));
        }
    }

    private void addLabel(DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALLABEL));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewLabl(), valueFieldPair, language);
        }
    }

    private void setText(DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALTEXT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewTxt(), valueFieldPair, language);
        }
    }
}
