package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookType;
import codebook25.OtherMatType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

class DDIOtherMaterialDescription extends DDISectionBase {
    DDIOtherMaterialDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        super(revision, language, codeBook, info, configuration);
    }

    @Override
    ReturnResult read() {
        /*
         * We know that language has to be DEFAULT and that description tab should be clear so we can just insert the new data in
         */
        if(!hasContent(codeBook.getOtherMatArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, ContainerChange>> containerResult = getContainer(Fields.OTHERMATERIALS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        Pair<ContainerDataField, ContainerChange> container = containerResult.getRight();
        for(OtherMatType other : codeBook.getOtherMatArray()) {
            Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
            if(row.getLeft() != StatusCode.NEW_ROW) {
                continue;
            }
            if(StringUtils.hasText(other.getURI())) {
                valueSet(row.getRight(), Fields.OTHERMATERIALURI, other.getURI());
            }
            if(hasContent(other.getTxtArray()) && StringUtils.hasText(other.getTxtArray(0).xmlText())) {
                valueSet(row.getRight(), Fields.OTHERMATERIALTEXT, other.getTxtArray(0).xmlText());
            }
            if(hasContent(other.getLablArray()) && StringUtils.hasText(other.getLablArray(0).xmlText())) {
                valueSet(row.getRight(), Fields.OTHERMATERIALTEXT, other.getLablArray(0).xmlText());
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
