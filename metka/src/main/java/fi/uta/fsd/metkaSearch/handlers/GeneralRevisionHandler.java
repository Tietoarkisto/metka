package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.data.collecting.ReferenceHandler;
import fi.uta.fsd.metka.data.enums.*;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.mvc.domain.ReferenceService;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.indexers.IndexerDocument;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
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
    //private final RevisionData data;
    private final GeneralRepository general;
    private final ConfigurationRepository configurations;
    private final ReferenceService references;
    //private final Pair<Boolean, LocalDateTime> removalInfo;

    GeneralRevisionHandler(DirectoryInformation indexer, GeneralRepository general,
                           ConfigurationRepository configurations, ReferenceService references) {
        this.indexer = indexer;

        //this.data = data;
        this.general = general;
        this.configurations = configurations;
        this.references = references;
        //this.removalInfo = removalInfo;
    }

    /*public DirectoryInformation getIndexer() {
        return indexer;
    }

    public RevisionData getData() {
        return data;
    }*/

    public boolean handle(RevisionIndexerCommand command) throws IOException {
        if(command == null) {
            return true;
        }
        RevisionData data = general.getRevision(command.getRevisionable(), command.getRevision());
        if(data == null) {
            return true;
        }
        Configuration config = configurations.findConfiguration(data.getConfiguration());
        if(config == null) {
            // We can't really do the indexing without an actual config
            return true;
        }
        // Do some checking to see if the document actually needs to be indexed or not (don't know how at the moment)
        // TODO: Actual checking, for now just removes any previous documents from the index
        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 4, data.getKey().getId(), data.getKey().getId(), true, true), MUST);
        bQuery.add(NumericRangeQuery.newLongRange("key.no", 4, data.getKey().getRevision().longValue(), data.getKey().getRevision().longValue(), true, true), MUST);
        indexer.getIndexWriter().deleteDocuments(bQuery);

        // Create document. This handler only indexes one document per request so the document can be inside the handle method.
        IndexerDocument document = new IndexerDocument(command.getPath().getLanguage());

        // This is used to determine if there's been some breaking bugs that mean that the document can't be added to the index
        boolean addDocument = true;

        Pair<Boolean, LocalDateTime> removalInfo = general.getRevisionableRemovedInfo(data.getKey().getId());

        // Do some default stuff
        document.indexIntegerField("key.id", data.getKey().getId(), YES);
        document.indexIntegerField("key.no", data.getKey().getRevision().longValue(), YES);
        document.indexKeywordField("key.configuration.type", data.getConfiguration().getType().toValue(), YES);
        document.indexIntegerField("key.configuration.version", data.getConfiguration().getVersion().longValue(), YES);
        document.indexKeywordField("state.removed", removalInfo.getLeft().toString(), YES);
        if(removalInfo.getLeft()) {
            document.indexKeywordField("state.removed.date", removalInfo.getLeft().toString(), YES);
        }

        if(data.getState() == RevisionState.APPROVED) {
            // Set state.approved field to true since this is an approved data, also set approval date and approved by values
            document.indexKeywordField("state.approved", "true", YES);
            document.indexKeywordField("state.approved.date", data.getApprovalDate().toString(), YES);
            document.indexKeywordField("state.approved.by", data.getApprovedBy(), YES);

            // Set state.draft field to false since this is an approved data
            document.indexKeywordField("state.draft", "false", YES);
        } else if(data.getState() == RevisionState.DRAFT) {
            // Set state.draft field to true since this is a draft data, also set current handler
            document.indexKeywordField("state.draft", "true", YES);
            document.indexKeywordField("state.draft.handler", data.getHandler(), YES);

            // Set state.approved field to false since this is a draft data
            document.indexKeywordField("state.approved", "false", YES);
        }

        if(data.getLastSaved() != null) {
            document.indexKeywordField("state.saved", "true", YES); // Specialized field that tells if the RevisionData has been saved by an user at least once
            document.indexKeywordField("state.saved.date", data.getLastSaved().toString(), YES);
            document.indexKeywordField("state.saved.by", data.getLastSavedBy(), YES);
        } else {
            document.indexKeywordField("state.saved", "false", YES);
        }

        addDocument = indexFields(data, document, "", config);

        if(addDocument) {
            document.indexText("general", document.getGeneral(), false, YES);
            PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(CaseInsensitiveWhitespaceAnalyzer.ANALYZER, document.getAnalyzers());
            indexer.getIndexWriter().addDocument(document.getDocument(), analyzer);
        }
        return addDocument;
    }

    private boolean indexFields(RevisionData data, IndexerDocument document, String root, Configuration config) {
        boolean addDocument = true;
        for(Field field : config.getFields().values()) {
            // Ignore subfields, they're indexed through container indexing
            if(field.getSubfield()) {
                continue;
            }

            addDocument = indexField(field, data, document, root, config, data);

            if(!addDocument) {
                break;
            }
        }
        return addDocument;
    }

    private boolean indexField(Field field, DataFieldContainer fieldContainer, IndexerDocument document, String root, Configuration config, RevisionData data) {
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

            // TODO: Make something better for this, for now intercept variables indexing here
            if(field.getKey().equals("variables") && field.getType() == FieldType.REFERENCECONTAINER && data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
                // TODO: Index individual variables
                Pair<StatusCode, ReferenceContainerDataField> pair = fieldContainer.dataField(ReferenceContainerDataFieldCall.get(field.getKey()));
                if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                    return result;
                }
                ReferenceContainerDataField df = pair.getRight();
                if(df.getReferences().size() == 0) {
                    return result;
                }
                result = indexStudyVariablesContainer(df, document, root+field.getKey(), data);
            } else {
                // For now index table into the same document as the top content.
                // This will create multiple values in similar field names. Search treats these automatically
                // as OR matches, meaning you only have to search for one of the values to find the document.
                if(field.getType() == FieldType.CONTAINER) {
                    result = indexContainer(field, fieldContainer, document, root, config, data);
                } else if(field.getType() == FieldType.REFERENCECONTAINER) {
                    // TODO: Index reference containers
                }
            }
        } else {
            // Try to index the field as a non container field
            result = indexNonContainerField(field, fieldContainer, document, root, config, data);
        }
        return result;
    }

    private boolean indexContainer(Field containerField, DataFieldContainer fieldContainer, IndexerDocument document, String root, Configuration config, RevisionData data) {
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
                result = indexField(field, row, document, root+container.getKey(), config, data);
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

    private boolean indexNonContainerField(Field field, DataFieldContainer fieldContainer, IndexerDocument document, String root, Configuration config, RevisionData data) {
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
                document.indexKeywordField(root + field.getKey(), saved.getActualValue());
                break;
            case BOOLEAN:
                // Index boolean field as a string with 'true' or 'false' as value. Boolean field should not be analyzer or tokenized
                document.indexKeywordField(root + field.getKey(), saved.getActualValue());
                break;
            case CONCAT:
                // Index concat field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(field, root, saved);
                break;
            case STRING:
                // Index string field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(field, root, saved);
                break;
            case INTEGER:
                // Convert value to correct number format (integer or long, or just stick with long for everything) and index as correct number field
                // TODO: Correct number conversion
                document.indexIntegerField(root + field.getKey(), Long.parseLong(saved.getActualValue()));
                break;
            case REAL:
                // Convert value to correct number format (float or double, or just stick with double for everything) and index as correct number field
                // TODO: Correct number conversion
                document.indexRealField(root + field.getKey(), Double.parseDouble(saved.getActualValue()));
                break;
            case SELECTION:
                result = indexSelectionField(field, saved, document, root, config, data);
                break;
            case REFERENCE:
                // TODO: Move reference handling to a sub process which knows how to handle the different reference types

                // Add value as string field for now with key being the field key
                result = indexReferenceField(field, saved, document, root, data);
                break;
            case CONTAINER:
            case REFERENCECONTAINER:
                // This should never happen since container types are handled separately but this removes compiler warnings
                break;
        }
        // Add save information to index for every field.
        document.indexKeywordField(root + field.getKey() + ".saved.date", saved.getValue().getSavedAt().toString());
        document.indexKeywordField(root + field.getKey() + ".saved.by", saved.getValue().getSavedBy());
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
    private boolean indexSelectionField(Field field, SavedDataField saved, IndexerDocument document, String root, Configuration config, RevisionData data) {
        String selectionKey = field.getSelectionList();
        SelectionList list = config.getRootSelectionList(selectionKey);

        // All SelectionList values are assumed to be indexed as exact until furthress notice
        switch(list.getType()) {
            case SUBLIST:
                // We've hit a loop in the list configuration, can index only the value and nothing else
                document.indexKeywordField(root + field.getKey(), saved.getActualValue());
                document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValue());
                break;
            case LITERAL:
                // Index only the value as title, or if wanted for uniformness then index the value as both value and title
                document.indexKeywordField(root + field.getKey(), saved.getActualValue());
                document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValue());
                break;
            case VALUE:
                // Index the title to the actual field and the value to field.value field. Index only the default language from title
                Option option = list.getOptionWithValue(saved.getActualValue());
                if(option != null) {
                    document.indexKeywordField(root + field.getKey() + ".value", option.getValue());
                    document.indexText(field, root, option.getDefaultTitle());
                } else {
                    // Some problem so possibly log error, but do nothing for now
                    break;
                }
                break;
            case REFERENCE:
                return indexReferenceField(field, saved, document, root, data);
        }

        return true;
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
    private boolean indexReferenceField(Field field, SavedDataField saved, IndexerDocument document, String root, RevisionData data) {
        // TODO: Split this off in some better way, for now intercept during varibales reference handling)
        if(field.getKey().equals("variables") && field.getType() == FieldType.REFERENCE && data.getConfiguration().getType() == ConfigurationType.STUDY) {
            // This is variables reference in study. Take some time and index variables to this document
            try {
                return indexStudyVariables(saved, document, root+field.getKey(), data);
            } catch(IOException ioe) {
                document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValue());
                document.indexKeywordField(root + field.getKey(), saved.getActualValue());
            }
        } else {
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
            document.indexKeywordField(root + field.getKey() + ".value", option.getValue());
            document.indexText(field, root, option);
        }
        return true;
    }

    private boolean indexStudyVariables(SavedDataField saved, IndexerDocument document, String root, RevisionData data) throws IOException {
        RevisionData revision = general.getLatestRevisionForId(Long.parseLong(saved.getActualValue()), data.getState() == RevisionState.APPROVED);
        if(revision == null) {
            return true;
        }
        Configuration config = configurations.findConfiguration(revision.getConfiguration());
        if(config == null) {
            return true;
        }
        return indexFields(revision, document, root, config);
    }

    private boolean indexStudyVariablesContainer(ReferenceContainerDataField field, IndexerDocument document, String root, RevisionData data) {
        boolean addDocument = true;
        for(SavedReference reference : field.getReferences()) {
            try {
                RevisionData revision = general.getLatestRevisionForId(Long.parseLong(reference.getActualValue()), data.getState() == RevisionState.APPROVED);
                if(revision == null) {
                    continue;
                }
                Configuration config = configurations.findConfiguration(revision.getConfiguration());
                if(config == null) {
                    continue;
                }
                if(!indexFields(revision, document, root, config)) {
                    addDocument = false;
                }
            } catch(IOException ioe) {
                // Just skip this variable
            }
        }
        return addDocument;
    }
}