package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookType;
import codebook25.OtherMatType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

class DDIOtherMaterialDescription extends DDISectionBase {
    DDIOtherMaterialDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        super(revision, language, codeBook, info, configuration);
    }

    @Override
    void read() {
        for(OtherMatType other : codeBook.getOtherMatArray()) {
            // TODO: What do we do with tables?
        }
        // TODO: Reverse process
        /*Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.OTHERMATERIALS));
        // TODO: Check that other materials container should actually be translated container
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            for(DataRow row : containerPair.getRight().getRowsFor(language)) {
                if(row.getRemoved()) {
                    continue;
                }
                OtherMatType otherMat = codeBookType.addNewOtherMat();

                readURI(language, row, otherMat);
                readLabel(language, row, otherMat);
                readText(language, row, otherMat);
            }
        }*/
    }

    private static void readURI(Language language, DataRow row, OtherMatType otherMat) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALURI));
        if(hasValue(valueFieldPair, language)) {
            otherMatType.setURI(valueFieldPair.getRight().getActualValueFor(language));
        }*/
    }

    private static void readLabel(Language language, DataRow row, OtherMatType otherMat) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALLABEL));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewLabl(), valueFieldPair, language);
        }*/
    }

    private static void readText(Language language, DataRow row, OtherMatType otherMat) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.OTHERMATERIALTEXT));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(otherMatType.addNewTxt(), valueFieldPair, language);
        }*/
    }
}
