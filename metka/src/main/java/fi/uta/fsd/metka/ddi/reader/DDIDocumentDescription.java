package fi.uta.fsd.metka.ddi.reader;

import codebook25.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;

import java.util.HashMap;
import java.util.Map;

class DDIDocumentDescription extends DDISectionBase {
    private static final Map<Language, String> DDI_TITLE_PREFIXES = new HashMap<>();

    static {
        DDI_TITLE_PREFIXES.put(Language.DEFAULT, "DDI-kuvailu: ");
        DDI_TITLE_PREFIXES.put(Language.EN, "DDI-description: ");
        DDI_TITLE_PREFIXES.put(Language.SV, "DDI-beskrivning: ");
    }

    DDIDocumentDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        super(revision, language, codeBook, info, configuration);
    }

    @Override
    void read() {
        if(codeBook.getDocDscrArray().length == 0) {
            return;
        }

        DocDscrType docDscr = codeBook.getDocDscrArray(0);

        readCitation(docDscr);

        // TODO: Read Vocabs ?
    }

    private void readCitation(DocDscrType docDscr) {
        // Add citation
        CitationType citation = docDscr.getCitation();
        if(citation == null) {
            return;
        }

        readTitle(citation);

        // Add Producer information
        readProducer(citation);

        // Add container version
        // TODO: Do we want to read versions from DDI
        readContainerVersion(revision, language, citation);
    }

    private void readTitle(CitationType citation) {
        TitlStmtType titlStmt = citation.getTitlStmt();

        if(titlStmt == null) {
            return;
        }
        if(titlStmt.getTitl() != null) {
            SimpleTextType titl = titlStmt.getTitl();
            String titlText = titl.xmlText();
            if(titlText.contains(DDI_TITLE_PREFIXES.get(language))) {
                titlText = titlText.replaceFirst(DDI_TITLE_PREFIXES.get(language), "");
            }
            valueSet(Fields.TITLE, titlText);
        }

        for(SimpleTextType titl : titlStmt.getParTitlArray()) {
            Language altLang = Language.fromValue(titl.getXmlLang().toLowerCase());
            if(altLang == language) {
                continue;
            }
            String titlText = titl.xmlText();
            if(titlText.contains(DDI_TITLE_PREFIXES.get(altLang))) {
                titlText = titlText.replaceFirst(DDI_TITLE_PREFIXES.get(language), "");
            }
            valueSet(Fields.TITLE, titlText, altLang);
        }
    }

    private void readProducer(CitationType citation) {
        // Add producer statement
        ProdStmtType prodStmt = citation.getProdStmt();

        if(prodStmt == null) {
            return;
        }

        // TODO: What do we do with tables (and versions specifically)

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
