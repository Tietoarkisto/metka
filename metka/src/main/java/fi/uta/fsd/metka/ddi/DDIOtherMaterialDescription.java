package fi.uta.fsd.metka.ddi;

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
import fi.uta.fsd.metka.names.Fields;
import org.apache.commons.lang3.tuple.Pair;

import static fi.uta.fsd.metka.ddi.DDIBuilder.fillTextType;
import static fi.uta.fsd.metka.ddi.DDIBuilder.hasValue;

class DDIOtherMaterialDescription {
    static void addOtherMaterialDescription(RevisionData revisionData, Language language, Configuration configuration, CodeBookType codeBookType) {
        Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.OTHERMATERIALS));
        // TODO: Check that other materials container should actually be translated container
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                if(row.getRemoved()) {
                    continue;
                }
                OtherMatType otherMatType = codeBookType.addNewOtherMat();

                setURI(language, row, otherMatType);
                addLabel(language, row, otherMatType);
                setText(language, row, otherMatType);
            }
        }
    }

    private static void setURI(Language language, DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALURI));
        if(hasValue(valueFieldPair, language)) {
            otherMatType.setURI(valueFieldPair.getRight().getActualValueFor(language));
        }
    }

    private static void addLabel(Language language, DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALLABEL));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewLabl(), valueFieldPair, language);
        }
    }

    private static void setText(Language language, DataRow row, OtherMatType otherMatType) {
        Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALTEXT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewTxt(), valueFieldPair, language);
        }
    }
}
