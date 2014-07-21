package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.data.collecting.ReferenceHandler;
import fi.uta.fsd.metka.data.enums.*;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.mvc.domain.ReferenceService;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.lucene.document.Field.Store.*;
import static org.apache.lucene.search.BooleanClause.Occur.*;

class GeneralRevisionHandler implements RevisionHandler {
    private final DirectoryInformation indexer;
    private final RevisionData data;
    private final Configuration config;
    private final ReferenceService references;
    private final Pair<Boolean, LocalDateTime> removalInfo;

    private final Map<String, Analyzer> analyzers = new HashMap<>();

    private final StringBuilder general = new StringBuilder();

    GeneralRevisionHandler(DirectoryInformation indexer, RevisionData data, Configuration config, ReferenceService references, Pair<Boolean, LocalDateTime> removalInfo) {
        this.indexer = indexer;
        this.data = data;
        this.config = config;
        this.references = references;
        this.removalInfo = removalInfo;
    }

    public DirectoryInformation getIndexer() {
        return indexer;
    }

    public RevisionData getData() {
        return data;
    }

    public Configuration getConfig() {
        return config;
    }

    public boolean handle() throws IOException {
        // Do some checking to see if the document actually needs to be indexed or not (don't know how at the moment)
        // TODO: Actual checking, for now just removes any previous documents from the index
        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 1, data.getKey().getId(), data.getKey().getId(), true, true), MUST);
        bQuery.add(NumericRangeQuery.newIntRange("key.no", 1, data.getKey().getRevision(), data.getKey().getRevision(), true, true), MUST);
        indexer.getIndexWriter().deleteDocuments(bQuery);

        // Create document. This handler only indexes one document per request so the document can be inside the handle method.
        Document document = new Document();

        // This is used to determine if there's been some breaking bugs that mean that the document can't be added to the index
        boolean addDocument = true;

        // Do some default stuff
        document.add(new LongField("key.id", data.getKey().getId(), YES));
        document.add(new IntField("key.no", data.getKey().getRevision(), YES));
        document.add(new StringField("key.configuration.type", data.getConfiguration().getType().toValue(), YES));
        document.add(new IntField("key.configuration.version", data.getConfiguration().getVersion(), YES));
        document.add(new StringField("state.removed", removalInfo.getLeft().toString(), YES));
        if(removalInfo.getLeft()) {
            document.add(new StringField("state.removed.date", removalInfo.getLeft().toString(), YES));
        }

        if(data.getState() == RevisionState.APPROVED) {
            // Set state.approved field to true since this is an approved data, also set approval date and approved by values
            document.add(new StringField("state.approved", "true", YES));
            document.add(new StringField("state.approved.date", data.getApprovalDate().toString(), YES));
            document.add(new StringField("state.approved.by", data.getApprovedBy(), YES));

            // Set state.draft field to false since this is an approved data
            document.add(new StringField("state.draft", "false", YES));
        } else if(data.getState() == RevisionState.DRAFT) {
            // Set state.draft field to true since this is a draft data, also set current handler
            document.add(new StringField("state.draft", "true", YES));
            document.add(new StringField("state.draft.handler", data.getHandler(), YES));

            // Set state.approved field to false since this is a draft data
            document.add(new StringField("state.approved", "false", YES));
        }

        if(data.getLastSaved() != null) {
            document.add(new StringField("state.saved", "true", YES)); // Specialized field that tells if the RevisionData has been saved by an user at least once
            document.add(new StringField("state.saved.date", data.getLastSaved().toString(), YES));
            document.add(new StringField("state.saved.by", data.getLastSavedBy(), YES));
        } else {
            document.add(new StringField("state.saved", "false", YES));
        }

        for(Field field : config.getFields().values()) {
            // Ignore subfields, they're indexed through container indexing
            if(field.getSubfield()) {
                continue;
            }

            addDocument = indexField(field, data, document, "", analyzers);

            if(!addDocument) {
                break;
            }
        }
        if(addDocument) {
            // TODO: Give standard analyzer finnish stopwords if language is finnish
            document.add(new TextField("general", general.toString(), NO));
            addTextAnalyzer("general");
            PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);
            indexer.getIndexWriter().addDocument(document, analyzer);
        }
        return addDocument;
    }

    private boolean indexField(Field field, DataFieldContainer fieldContainer, Document document, String root, Map<String, Analyzer> analyzers) {
        if(root == null) {
            root = "";
        }
        if(!StringUtils.isEmpty(root)) {
            root += ".";
        }
        boolean result = true;
        if(field.getType().isContainer()) {
            // Field is a container type. These are indexed into separate indexes by different handlers, Create the commands and continue
            // TODO: Create commands to index the field

            // For now index table into the same document as the top content.
            // This will create multiple values in similar field names. Search treats these automatically
            // as OR matches, meaning you only have to search for one of the values to find the document.
            if(field.getType() == FieldType.CONTAINER) {
                result = indexContainer(field, fieldContainer, document, root, analyzers);
            } else if(field.getType() == FieldType.REFERENCECONTAINER) {
                // TODO: Index reference containers
            }
        } else {
            // Try to index the field as a non container field
            result = indexNonContainerField(field, fieldContainer, document, root, analyzers);
        }
        return result;
    }

    private boolean indexContainer(Field containerField, DataFieldContainer fieldContainer, Document document, String root, Map<String, Analyzer> analyzers) {
        // TODO: Get this from actual indexer configuration
        if(!containerField.getIndexed()) {
            // Field should not be indexed according to configuration, this also means that none of its subfields get indexed
            return true;
        }

        Pair<StatusCode, ContainerDataField> pair = fieldContainer.dataField(ContainerDataFieldCall.get(containerField.getKey()).setConfiguration(config));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Field not found for some reason, don't stop indexing
            return true;
        }
        ContainerDataField container = pair.getRight();
        boolean result = true;
        for(DataRow row : container.getRows()) {
            for(String key : containerField.getSubfields()) {
                // Iterate throught configured subfields and index them as necessary
                Field field = config.getField(key);
                if(!field.getSubfield()) {
                    // For some reason non subfield was returned, ignore
                    continue;
                }
                result = indexField(field, row, document, root+container.getKey(), analyzers);
                if(!result) {
                    break;
                }
            }
            if(!result) {
                break;
            }
        }

        return result;
    }

    private boolean indexNonContainerField(Field field, DataFieldContainer fieldContainer, Document document, String root, Map<String, Analyzer> analyzers) {
        if(!field.getWritable()) {
            // Value is not written in revision data, nothing to index
            return true;
        }
        // TODO: Get this from actual indexer configuration
        if(!field.getIndexed()) {
            // Field should not be indexed according to configuration
            return true;
        }

        // Since field is not a container it's saved as a SavedDataField
        Pair<StatusCode, SavedDataField> pair = fieldContainer.dataField(SavedDataFieldCall.get(field.getKey()).setConfiguration(config));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Field was not found on given revision data. Nothing to index
            return true;
        }
        SavedDataField saved = pair.getRight();
        if(!saved.hasValue() || StringUtils.isEmpty(saved.getActualValue())) {
            // SavedDataField has no value to index or the value is empty, don't insert anything
            return true;
        }

        boolean result = true;
        switch(field.getType()) {
            case DATE:
            case DATETIME:
            case TIME:
                // Index all Date and time fields as StringFields since they should not be analyzed or tokenized in any way.
                document.add(new StringField(root+field.getKey(), saved.getActualValue(), NO));
                break;
            case BOOLEAN:
                // Index boolean field as a string with 'true' or 'false' as value. Boolean field should not be analyzer or tokenized
                document.add(new StringField(root+field.getKey(), saved.getActualValue(), NO));
                break;
            case CONCAT:
                // Index concat field as a text field which should be analyzed and tokenized unless marked as exact field in config
                // TODO: get information of if field is marked exact
                indexTextOrString(field, saved.getActualValue(), root+field.getKey(), document);
                break;
            case STRING:
                // Index string field as a text field which should be analyzed and tokenized unless marked as exact field in config
                // TODO: get information of if field is marked exact from actual indexer configuration
                indexTextOrString(field, saved.getActualValue(), root+field.getKey(), document);
                break;
            case INTEGER:
                // Convert value to correct number format (integer or long, or just stick with long for everything) and index as correct number field
                // TODO: Correct number conversion
                document.add(new LongField(root+field.getKey(), Long.parseLong(saved.getActualValue()), NO));
                break;
            case REAL:
                // Convert value to correct number format (float or double, or just stick with double for everything) and index as correct number field
                // TODO: Correct number conversion
                document.add(new DoubleField(root+field.getKey(), Double.parseDouble(saved.getActualValue()), NO));
                break;
            case SELECTION:
                result = indexSelectionField(field, saved, document, root);
                break;
            case REFERENCE:
                // TODO: Move reference handling to a sub process which knows how to handle the different reference types

                // Add value as string field for now with key being the field key
                result = indexReferenceField(field, saved, document, root);
                break;
            case CONTAINER:
            case REFERENCECONTAINER:
                // This should never happen since container types are handled separately but this removes compiler warnings
                break;
        }
        // Add save information to index for every field.
        document.add(new StringField(root+field.getKey()+".saved.date", saved.getValue().getSavedAt().toString(), YES));
        document.add(new StringField(root+field.getKey()+".saved.by", saved.getValue().getSavedBy(), YES));
        return result;
    }

    /**
     * Index given selection field using given value.
     * We can assume that the field is of correct type, that the saved data belongs to that field and that the actual value is non empty string
     * since those are all things that should be checked before this method is reached.
     * @param field Field configuration
     * @param saved Current value in the field
     * @param document Where field should be indexed
     * @return Boolean telling if the indexing was successful. False should mean that the whole document is abandoned
     */
    private boolean indexSelectionField(Field field, SavedDataField saved, Document document, String root) {
        String selectionKey = field.getSelectionList();
        SelectionList list = config.getRootSelectionList(selectionKey);

        // All SelectionList values are assumed to be indexed as exact until furthress notice
        switch(list.getType()) {
            case SUBLIST:
                // We've hit a loop in the list configuration, can index only the value and nothing else
                document.add(new StringField(root+field.getKey(), saved.getActualValue(), NO));
                break;
            case LITERAL:
                // Index only the value as title, or if wanted for uniformness then index the value as both value and title
                document.add(new StringField(root+field.getKey(), saved.getActualValue(), NO));
                break;
            case VALUE:
                // Index the title to the actual field and the value to field.value field. Index only the default language from title
                Option option = list.getOptionWithValue(saved.getActualValue());
                if(option != null) {
                    document.add(new StringField(root+field.getKey()+".value", option.getValue(), NO));
                    indexTextOrString(field, option.getDefaultTitle(), root+field.getKey(), document);
                } else {
                    // Some problem so possibly log error, but do nothing for now
                    break;
                }
                break;
            case REFERENCE:
                return indexReferenceField(field, saved, document, root);
        }

        return true;
    }

    private void indexTextOrString(Field field, String value, String key, Document document) {
        if(field.getExact()) {
            document.add(new StringField(key, value, NO));
        } else {
            general.append(" ");
            general.append(value);
            document.add(new TextField(key, value, NO));
            addTextAnalyzer(key);
        }
    }

    /**
     * Indexes a reference field or a reference selection list to document.
     * We can assume that field is of correct type, Saved data field belongs to that field and saved data field has
     * non empty actual value since all of these things should have been checked before calling this method.
     * @param field Field configuration
     * @param saved Current value in the field
     * @param document Where field should be indexed
     * @return Boolean telling if the indexing was successful. False should mean that the whole document is abandoned
     */
    private boolean indexReferenceField(Field field, SavedDataField saved, Document document, String root) {
        // TODO: Handle reference collecting. Shouldn't be that hard if we leverage some of the reference solver
        ReferenceOption option = null;
        try {
            option = references.getCurrentFieldOption(data, root + field.getKey());
        } catch(IOException ioe) {
            ioe.printStackTrace();
            // Don't stop indexing but can't index this reference
            return true;
        }
        if(option == null) {
            // Nothing to index
            return true;
        }

        document.add(new StringField(root+field.getKey()+".value", option.getValue(), NO));
        indexTextOrString(field, option.getTitle().getValue(), root+field.getKey(), document);
        return true;
    }

    private void addTextAnalyzer(String key) {
        if(indexer.getPath().getLanguage().equals("fi")) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }
}
