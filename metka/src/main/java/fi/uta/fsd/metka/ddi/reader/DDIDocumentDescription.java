package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;

class DDIDocumentDescription {

    static void readDocumentDescription(RevisionData revision, Language language, CodeBookType codeBook) {
        if(codeBook.getDocDscrArray().length == 0) {
            return;
        }

        DocDscrType docDscr = codeBook.getDocDscrArray(0);

        readCitation(revision, language, docDscr);

        // TODO: Read Vocabs ?
    }

    private static void readCitation(RevisionData revision, Language language, DocDscrType docDscr) {
        // Add citation
        CitationType citation = docDscr.getCitation();
        if(citation == null) {
            return;
        }

        // Add title statement (?)
        TitlStmtType titlStmt = citation.getTitlStmt();

        if(titlStmt != null) {
            readTitle(revision, language, titlStmt);
        }


        // Add Producer information
        readProducer(revision, language, citation);

        // Add container version
        // TODO: Do we want to read versions from DDI
        readContainerVersion(revision, language, citation);
    }

    private static void readTitle(RevisionData revision, Language language, TitlStmtType titlStmt) {
        // TODO: Reverce process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(hasValue(valueFieldPair, language)) {
            fillTextType(titlStmtType.addNewTitl(), DDI_TITLE_PREFIXES.get(language)+valueFieldPair.getRight().getActualValueFor(language));
        }

        for(Language altLang : Language.values()) {
            if(altLang == language) {
                continue;
            }
            if(hasValue(valueFieldPair, altLang)) {
                fillTextType(titlStmtType.addNewParTitl(), DDI_TITLE_PREFIXES.get(language)+valueFieldPair.getRight().getActualValueFor(altLang));
            }
        }*/
    }

    private static void readProducer(RevisionData revisionData, Language language, CitationType citation) {
        // Add producer statement
        ProdStmtType prodStmt = citation.getProdStmt();

        if(prodStmt == null) {
            return;
        }

        // Set ID, repeatable
        // TODO: What is the value for this
        //producerType.setID("");

        // Set type
        // TODO: What is the value for this
        //producerType.setRole("");

        // Add production date
        // TODO: Do we want to read version from DDI?
        /*Pair<StatusCode, ContainerDataField> containerPair = revisionData.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
            ContainerDataField container = containerPair.getRight();
            for (DataRow dataRow : container.getRowsFor(language)) {
                if(dataRow.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = dataRow.dataField( ValueDataFieldCall.get(Fields.VERSIONDATE));
                if(valueFieldPair.getLeft() == StatusCode.FIELD_FOUND && valueFieldPair.getRight().valueForEquals(language, "1.0")) {
                    LocalDate localDate = LocalDate.parse(valueFieldPair.getRight().getActualValueFor(language));
                    SimpleTextAndDateType stadt = prodStmtType.addNewProdDate();
                    stadt.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(localDate));
                }
            }
        }*/
    }

    private static void readContainerVersion(RevisionData revision, Language language, CitationType citation) {
        // TODO: Reverce process
        /*Pair<StatusCode, ValueDataField> valueFieldPair;List<DataRow> rows = container.getRowsFor(language);
        DataRow row = rows.get(rows.size()-1);

        valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSION));
        if(hasValue(valueFieldPair, language)) {
            VerStmtType verStmt = citationType.addNewVerStmt();
            VersionType ver = fillTextType(verStmt.addNewVersion(), valueFieldPair, language);
            valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VERSIONDATE));
            if(hasValue(valueFieldPair, language)) {
                LocalDate localDate = LocalDate.parse(valueFieldPair.getRight().getActualValueFor(language));
                ver.setDate(DDIBuilder.DATE_TIME_FORMATTER.print(localDate));
            }
        }*/
    }
}
