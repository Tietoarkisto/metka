package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.IndexerDocument;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.lucene.document.Field.Store.YES;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;

class GeneralRevisionHandler implements RevisionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GeneralRevisionHandler.class);
    private final Indexer indexer;
    private final RevisionRepository revisions;
    private final ConfigurationRepository configurations;
    private final ReferenceService references;

    private final Map<ConfigurationKey, Configuration> configCache = new HashMap<>();

    // Requested language of the handler
    private final Language language;

    // This attribute should only ever be set to true and it tells the handler if it should add the document to index or not.
    // If we don't find content that's exclusive for requested language (i.e. translated content) then there's no point in
    // adding the document to index since it's not actually translated at all.
    private boolean contentForLanguage = false;

    GeneralRevisionHandler(Indexer indexer, RevisionRepository revisions,
                           ConfigurationRepository configurations, ReferenceService references) {
        this.indexer = indexer;

        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
        language = indexer.getPath().getLanguage();
    }

    private Pair<ReturnResult, Configuration> getConfiguration(ConfigurationKey key) {
        if(configCache.get(key) != null) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, configCache.get(key));
        } else {
            Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(key);
            if(pair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                return pair;
            } else {
                configCache.put(key, pair.getRight());
                return pair;
            }
        }
    }

    /**
     * Handle given command.
     *
     * @param command
     * @return Tells if the command handling lead to additional documents being added to index
     */
    public boolean handle(RevisionIndexerCommand command) {
        if(command == null) {
            return false;
        }
        logger.info("Trying to get revision ID: "+command.getRevisionable()+" NO: "+command.getRevision());
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(command.getRevisionable(), command.getRevision(), command.getType());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.info("Revision not found with result "+pair.getLeft());
            return false;
        }
        RevisionData data = pair.getRight();
        logger.info("Trying to get configuration for "+data.getConfiguration().toString());
        Pair<ReturnResult, Configuration> confPair = getConfiguration(data.getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            // We can't really do the indexing without an actual config
            logger.info("Configuration not found with result "+confPair.getLeft());
            return false;
        }
        Configuration config = confPair.getRight();
        // Do some checking to see if the document actually needs to be indexed or not (don't know how at the moment)
        // TODO: Actual checking, for now just removes any previous documents from the index
        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 4, data.getKey().getId(), data.getKey().getId(), true, true), MUST);
        bQuery.add(NumericRangeQuery.newLongRange("key.no", 4, data.getKey().getNo().longValue(), data.getKey().getNo().longValue(), true, true), MUST);
        indexer.removeDocument(bQuery);

        logger.info("Trying to find revision info.");
        Pair<ReturnResult, RevisionableInfo> removedInfoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(removedInfoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            // For some reason removed info check failed to find the entity. stop indexing
            logger.info("Revision info not found with reason "+removedInfoPair.getLeft());
            return false;
        }
        RevisionableInfo info = removedInfoPair.getRight();

        // Let's take language out of path since it's used so often.
        Language language = command.getPath().getLanguage();

        // Create document. This handler only indexes one document per request so the document can be inside the handle method.
        IndexerDocument document = new IndexerDocument();

        // Do some default stuff
        logger.info("Forming document for revision.");
        document.indexIntegerField("key.id", data.getKey().getId(), true);
        document.indexIntegerField("key.no", data.getKey().getNo().longValue(), true);
        document.indexKeywordField("key.configuration.type", data.getConfiguration().getType().toValue(), YES);
        document.indexIntegerField("key.configuration.version", data.getConfiguration().getVersion().longValue(), true);
        document.indexKeywordField("state.removed", info.getRemoved().toString(), YES);
        if(info.getRemoved()) {
            document.indexKeywordField("state.removed.time", info.getRemovedAt().toString(), YES);
            document.indexKeywordField("state.removed.user", info.getRemovedBy(), YES);
        }

        // There's approval date for the command language
        if(data.getApproved().get(language) != null) {
            document.indexKeywordField("state.approved.time", data.getApproved().get(language).getTime().toString(), YES);
            document.indexKeywordField("state.approved.user", data.getApproved().get(language).getTime().toString(), YES);
        }

        if(data.getState() == RevisionState.APPROVED) {
            // Set state.approved field to true since this is an approved data, also set approval date and approved by values
            document.indexKeywordField("state.approved", "true", YES);

            // Set state.draft field to false since this is an approved data
            document.indexKeywordField("state.draft", "false", YES);
        } else if(data.getState() == RevisionState.DRAFT) {
            // Set state.draft field to true since this is a draft data, also set current handler
            document.indexKeywordField("state.draft", "true", YES);
            document.indexKeywordField("state.draft.handler", data.getHandler(), YES);

            // Set state.approved field to false since this is a draft data
            document.indexKeywordField("state.approved", "false", YES);
        }

        if(data.getSaved() != null) {
            document.indexKeywordField("state.saved", "true", YES); // Specialized field that tells if the RevisionData has been saved by an user at least once
            document.indexKeywordField("state.saved.time", data.getSaved().getTime().toString(), YES);
            document.indexKeywordField("state.saved.user", data.getSaved().getUser(), YES);
        } else {
            document.indexKeywordField("state.saved", "false", YES);
        }

        indexFields(data, document, "", config);

        if(contentForLanguage) {
            logger.info("Adding document to index.");
            document.indexText(language, "revisions", document.getGeneral(), false, YES);
            PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(CaseInsensitiveWhitespaceAnalyzer.ANALYZER, document.getAnalyzers());
            indexer.addDocument(document.getDocument(), analyzer);
            return true;
        } else {
            logger.info("Document was not added to index because content was not found for requested language ("+command.getPath().getLanguage()+").");
            return false;
        }
    }

    private void indexFields(RevisionData data, IndexerDocument document, String root, Configuration config) {
        for(Field field : config.getFields().values()) {
            // Ignore subfields, they're indexed through container indexing
            if(field.getSubfield()) {
                continue;
            }
            indexField(field, data, document, root, config, data);
        }
    }

    private void indexField(Field field, DataFieldContainer fieldContainer, IndexerDocument document,
                               String root, Configuration config, RevisionData data) {
        if(root == null) {
            root = "";
        }
        if(StringUtils.hasText(root)) {
            root += ".";
        }
        if(field.getType().isContainer()) {
            // Field is a container type. These are indexed into separate indexes by different handlers, Create the commands and continue
            // TODO: Create commands to index the field

            // TODO: Make something better for this, for now intercept variables indexing here
            if(field.getKey().equals("variables") && field.getType() == FieldType.REFERENCECONTAINER
                    && data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
                // TODO: Index individual variables
                Pair<StatusCode, ReferenceContainerDataField> pair = fieldContainer.dataField(ReferenceContainerDataFieldCall.get(field.getKey()));
                if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                    return;
                }
                ReferenceContainerDataField df = pair.getRight();
                if(df.getReferences().size() == 0) {
                    return;
                }
                indexStudyVariablesContainer(df, document, root+field.getKey(), data);
            } else {
                // For now index table into the same document as the top content.
                // This will create multiple values in similar field names. Search treats these automatically
                // as OR matches, meaning you only have to search for one of the values to find the document.
                if(field.getType() == FieldType.CONTAINER) {
                    indexContainer(field, fieldContainer, document, root, config, data);
                } else if(field.getType() == FieldType.REFERENCECONTAINER) {
                    // TODO: Index reference containers
                }
            }
        } else {
            // Try to index the field as a non container field
            indexNonContainerField(field, fieldContainer, document, root, config, data);
        }
    }

    private void indexContainer(Field containerField, DataFieldContainer fieldContainer, IndexerDocument document, String root, Configuration config, RevisionData data) {
        // TODO: Get this from actual indexer configuration
        if(!containerField.getIndexed()) {
            // Field should not be indexed according to configuration, this also means that none of its subfields get indexed
            return;
        }

        Pair<StatusCode, ContainerDataField> pair = fieldContainer.dataField(ContainerDataFieldCall.get(containerField.getKey()).setConfiguration(config));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Field not found for some reason, don't stop indexing
            return;
        }
        ContainerDataField container = pair.getRight();
        // TODO: how to inform downstream that we're indexing fields in translated container, do the fields have value only in DEFAULT language or only in translated language?
        Language inputLang = containerField.getTranslatable() ? language : Language.DEFAULT;
        if(!container.hasRowsFor(inputLang)) {
            return;
        }
        for(DataRow row : container.getRowsFor(inputLang)) {
            for(String key : containerField.getSubfields()) {
                // Iterate through configured subfields and index them as necessary
                Field field = config.getField(key);
                if(!field.getSubfield()) {
                    // For some reason non subfield was returned, ignore
                    continue;
                }
                indexField(field, row, document, root+container.getKey(), config, data);
            }
        }
    }

    private void indexNonContainerField(Field field, DataFieldContainer fieldContainer, IndexerDocument document, String root, Configuration config, RevisionData data) {
        if(!field.getWritable()) {
            // Value is not written in revision data, nothing to index
            return;
        }
        // TODO: Get this from actual indexer configuration
        if(!field.getIndexed()) {
            // Field should not be indexed according to configuration
            return;
        }

        // Since field is not a container it's saved as a ValueDataField
        Pair<StatusCode, ValueDataField> pair = fieldContainer.dataField(ValueDataFieldCall.get(field.getKey()).setConfiguration(config));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Field was not found on given revision data. Nothing to index
            return;
        }

        Language inputLang = field.getTranslatable() ? language : Language.DEFAULT;

        ValueDataField saved = pair.getRight();
        if(!saved.hasValueFor(inputLang)) {
            // ValueDataField has no value to index or the value is empty, don't insert anything
            return;
        }

        switch(field.getType()) {
            case DATE:
            case DATETIME:
            case TIME:
                // Index all Date and time fields as StringFields since they should not be analyzed or tokenized in any way.
                document.indexKeywordField(root + field.getKey(), saved.getActualValueFor(inputLang));
                break;
            case BOOLEAN:
                // Index boolean field as a string with 'true' or 'false' as value. Boolean field should not be analyzer or tokenized
                document.indexKeywordField(root + field.getKey(), saved.getActualValueFor(inputLang));
                break;
            case CONCAT:
                // Index concat field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(inputLang, field, root, saved);
                break;
            case STRING:
                // Index string field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(inputLang, field, root, saved);
                break;
            case INTEGER:
                // Convert value to correct number format (integer or long, or just stick with long for everything) and index as correct number field
                // TODO: Correct number conversion
                document.indexIntegerField(root + field.getKey(), Long.parseLong(saved.getActualValueFor(inputLang)));
                break;
            case REAL:
                // Convert value to correct number format (float or double, or just stick with double for everything) and index as correct number field
                // TODO: Correct number conversion
                document.indexRealField(root + field.getKey(), Double.parseDouble(saved.getActualValueFor(inputLang)));
                break;
            case SELECTION:
                indexSelectionField(field, saved, document, root, config, data);
                break;
            case REFERENCE:
                indexReferenceField(field, saved, document, root, data);
                break;
            case CONTAINER:
            case REFERENCECONTAINER:
                // This should never happen since container types are handled separately but this removes compiler warnings
                break;
        }
        // Add save information to index for every field.
        document.indexKeywordField(root + field.getKey() + ".saved.time", saved.getValueFor(inputLang).getSaved().getTime().toString());
        document.indexKeywordField(root + field.getKey() + ".saved.user", saved.getValueFor(inputLang).getSaved().getUser());

        if(inputLang == language) contentForLanguage = true;
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
    private void indexSelectionField(Field field, ValueDataField saved, IndexerDocument document, String root, Configuration config, RevisionData data) {
        // If field is not translatable we are going to use DEFAULT as the indexing language, this needs to be detected in indexText methods too
        // We have to check this here instead of some more revisions method since we have to pass actual requested language on to other methods
        Language inputLang = field.getTranslatable() ? language : Language.DEFAULT;

        String selectionKey = field.getSelectionList();
        SelectionList list = config.getRootSelectionList(selectionKey);

        // All SelectionList values are assumed to be indexed as exact until further notice
        switch(list.getType()) {
            case SUBLIST:
                // We've hit a loop in the list configuration, can index only the value and nothing else
                document.indexKeywordField(root + field.getKey(), saved.getActualValueFor(inputLang));
                document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValueFor(inputLang));
                break;
            case LITERAL:
                // Index value both as value and title (they should be equal in any case)
                document.indexKeywordField(root + field.getKey(), saved.getActualValueFor(inputLang));
                document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValueFor(inputLang));
                break;
            case VALUE:
                // Index the title to the actual field and the value to field.value field. Index only the default language from title
                Option option = list.getOptionWithValue(saved.getActualValueFor(inputLang));
                if(option != null) {
                    document.indexKeywordField(root + field.getKey() + ".value", option.getValue());
                    document.indexText(inputLang, field, root, option.getDefaultTitle());
                } else {
                    // Some problem so possibly log error, but do nothing for now
                    break;
                }
                break;
            case REFERENCE:
                indexReferenceField(field, saved, document, root, data);
                return;
        }

        // We know that since we're at this point there has to be indexed content and so if input language equals given language then we've
        // found content
        if(inputLang == language) contentForLanguage = true;
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
    private void indexReferenceField(Field field, ValueDataField saved, IndexerDocument document, String root, RevisionData data) {
        Language inputLang = field.getTranslatable() ? language : Language.DEFAULT;
        // TODO: Split this off in some better way, for now intercept during variables reference handling)
        boolean foundContent = false;
        if(field.getKey().equals("variables") && field.getType() == FieldType.REFERENCE && data.getConfiguration().getType() == ConfigurationType.STUDY) {
            // This is variables reference in study. Take some time and index variables to this document
            document.indexKeywordField(root + field.getKey() + ".value", saved.getActualValueFor(Language.DEFAULT));
            indexStudyVariables(saved, document, root+field.getKey(), data);
        } else {
            ReferenceOption option = references.getCurrentFieldOption(inputLang, data, root + field.getKey());
            if(option != null) {
                document.indexKeywordField(root + field.getKey() + ".value", option.getValue());
                document.indexText(inputLang, field, root, option);
            }
            // If requested language equals the actual input language then we've found content for requested language.
            if(inputLang == language) contentForLanguage = true;
        }
    }

    private void indexStudyVariables(ValueDataField saved, IndexerDocument document, String root, RevisionData data) {
        // There shouldn't be different study variables objects for different languages so we can assume that the value is always found
        // from DEFAULT language
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(
                Long.parseLong(saved.getActualValueFor(Language.DEFAULT)),
                data.getState() == RevisionState.APPROVED, ConfigurationType.STUDY_VARIABLES);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Didn't find revision for study variables "+saved.getActualValueFor(Language.DEFAULT));
            return;
        }
        Pair<ReturnResult, Configuration> confPair = getConfiguration(pair.getRight().getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Didn't find configuration for "+pair.getRight().getConfiguration());
            return;
        }
        indexFields(pair.getRight(), document, root, confPair.getRight());
    }

    private void indexStudyVariablesContainer(ReferenceContainerDataField field, IndexerDocument document,
                                                 String root, RevisionData data) {
        for(ReferenceRow reference : field.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), data.getState() == RevisionState.APPROVED, ConfigurationType.STUDY_VARIABLE);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Didn't find revision for referenced study variable "+reference.getActualValue());
                continue;
            }
            Pair<ReturnResult, Configuration> confPair = getConfiguration(pair.getRight().getConfiguration());
            if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                logger.error("Didn't find configuration for "+pair.getRight().getConfiguration());
                continue;
            }
            indexFields(pair.getRight(), document, root, confPair.getRight());
        }
    }
}