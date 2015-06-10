package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.binder.BinderPageListEntry;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.IndexerDocument;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;

class GeneralRevisionHandler implements RevisionHandler {
    private final Indexer indexer;
    private final RevisionRepository revisions;
    private final ConfigurationRepository configurations;
    private final ReferenceService references;
    private final StudyErrorsRepository studyErrors;
    private final BinderRepository binders;

    private final Map<ConfigurationKey, Configuration> configCache = new HashMap<>();

    // This attribute should only ever be set to true and it tells the handler if it should add the document to index or not.
    // If we don't find content that's exclusive for requested language (i.e. translated content) then there's no point in
    // adding the document to index since it's not actually translated at all.
    private boolean contentForLanguage = false;

    GeneralRevisionHandler(Indexer indexer, RevisionRepository revisions,
                           ConfigurationRepository configurations, ReferenceService references,
                           StudyErrorsRepository studyErrors, BinderRepository binders) {
        this.indexer = indexer;

        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
        this.studyErrors = studyErrors;
        this.binders = binders;
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
        boolean result = false;

        if(command == null) {
            return result;
        }
        Logger.debug(getClass(), "Trying to get revision ID: " + command.getId() + " NO: " + command.getNo());
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionDataOfType(command.getId(), command.getNo(), null);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.warning(getClass(), "Revision not found with result " + pair.getLeft());
            return result;
        }
        RevisionData data = pair.getRight();
        Logger.debug(getClass(), "Trying to get configuration for "+data.getConfiguration().toString());
        Pair<ReturnResult, Configuration> confPair = getConfiguration(data.getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            // We can't really do the indexing without an actual config
            Logger.warning(getClass(), "Configuration not found with result "+confPair.getLeft());
            return result;
        }
        Configuration config = confPair.getRight();
        // TODO: For now just removes any previous documents from the index, optimization is possible in cases where no changes have been made
        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 4, data.getKey().getId(), data.getKey().getId(), true, true), MUST);
        bQuery.add(NumericRangeQuery.newLongRange("key.no", 4, data.getKey().getNo().longValue(), data.getKey().getNo().longValue(), true, true), MUST);
        indexer.removeDocument(bQuery);

        Logger.debug(getClass(), "Trying to find revision info.");
        Pair<ReturnResult, RevisionableInfo> removedInfoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(removedInfoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            // For some reason removed info check failed to find the entity. stop indexing
            Logger.warning(getClass(), "Revision info not found with reason "+removedInfoPair.getLeft());
            return result;
        }
        RevisionableInfo info = removedInfoPair.getRight();

        // Loop through languages, we index the documents once for each language
        for(Language language : Language.values()) {
            // Reset content for language
            contentForLanguage = false;
            // Document for this language
            IndexerDocument document = new IndexerDocument(language);

            // Do some default stuff
            Logger.debug(getClass(), "Forming document for revision.");
            document.indexIntegerField("key.id", data.getKey().getId(), true, false);
            document.indexIntegerField("key.no", data.getKey().getNo().longValue(), true, false);
            document.indexKeywordField("key.configuration.type", data.getConfiguration().getType().toValue(), YES);
            document.indexIntegerField("key.configuration.version", data.getConfiguration().getVersion().longValue(), true, false);
            document.indexKeywordField("key.language", language.toValue(), YES, true);
            if(info.getRemoved()) {
                document.indexKeywordField("state.removed", "true", YES);
                document.indexKeywordField("state.removed.time", info.getRemovedAt().toString(), YES);
                document.indexKeywordField("state.removed.user", info.getRemovedBy(), YES);

                document.indexKeywordField("state.approved", "false", YES);
                document.indexKeywordField("state.draft", "false", YES);
            } else if(data.getState() == RevisionState.APPROVED) {
                // Set state.approved field to true since this is an approved data, also set approval date and approved by values
                document.indexKeywordField("state.approved", "true", YES);

                // Set state.draft field to false since this is an approved data
                document.indexKeywordField("state.removed", "false", YES);
                document.indexKeywordField("state.draft", "false", YES);
            } else if(data.getState() == RevisionState.DRAFT) {
                // Set state.draft field to true since this is a draft data, also set current handler
                document.indexKeywordField("state.draft", "true", YES);
                document.indexKeywordField("state.draft.handler", data.getHandler(), YES);

                // Set state.approved field to false since this is a draft data
                document.indexKeywordField("state.removed", "false", YES);
                document.indexKeywordField("state.approved", "false", YES);
            }

            // There's approval date for the command language
            if(data.isApprovedFor(language)) {
                document.indexKeywordField("state.approved.time", data.approveInfoFor(language).getApproved().getTime().toString(), YES);
                document.indexKeywordField("state.approved.user", data.approveInfoFor(language).getApproved().getUser(), YES);
                document.indexIntegerField("state.approved.revision", data.approveInfoFor(language).getRevision().longValue(), true, false);
            }

            if(data.getSaved() != null) {
                document.indexKeywordField("state.saved", "true", YES); // Specialized field that tells if the RevisionData has been saved by an user at least once
                document.indexKeywordField("state.saved.time", data.getSaved().getTime().toString(), YES);
                document.indexKeywordField("state.saved.user", data.getSaved().getUser(), YES);
            } else {
                document.indexKeywordField("state.saved", "false", YES);
            }

            // Index the actual fields
            indexFields(data, document, "", new Step("", null), config, language);

            finalizeIndexing(data, document, config, language);

            if(contentForLanguage || language == Language.DEFAULT) {
                Logger.debug(getClass(), "Adding document to index.");
                PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(CaseInsensitiveWhitespaceAnalyzer.ANALYZER, document.getAnalyzers());
                indexer.addDocument(document.getDocument(), analyzer);
                result = true;
            } else {
                Logger.debug(getClass(), "Document was not added to index because content was not found for requested language ("+language+").");
            }
        }

        return result;
    }

    private void indexFields(RevisionData data, IndexerDocument document, String root, Step path, Configuration config, Language language) {
        for(Field field : config.getFields().values()) {
            // Ignore subfields, they're indexed through container indexing
            if(field.getSubfield()) {
                continue;
            }
            indexField(field, data, document, root, path, config, data, language);
        }
    }

    private void indexField(Field field, DataFieldContainer fieldContainer, IndexerDocument document,
                               String root, Step path, Configuration config, RevisionData data, Language language) {
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
            if(false && field.getKey().equals("variables") && field.getType() == FieldType.REFERENCECONTAINER
                    && data.getConfiguration().getType() == ConfigurationType.STUDY_VARIABLES) {
                // TODO: Index individual variables
                Pair<StatusCode, ReferenceContainerDataField> pair = fieldContainer.dataField(ReferenceContainerDataFieldCall.get(field.getKey()));
                if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                    return;
                }
                ReferenceContainerDataField df = pair.getRight();
                if(!df.hasRows()) {
                    return;
                }
                indexStudyVariablesContainer(df, document, root + field.getIndexAs(), data, language);
            } else if(false && field.getKey().equals("files") && field.getType() == FieldType.REFERENCECONTAINER && data.getConfiguration().getType() == ConfigurationType.STUDY) {
                Pair<StatusCode, ReferenceContainerDataField> pair = fieldContainer.dataField(ReferenceContainerDataFieldCall.get(field.getKey()));
                if(pair.getLeft() != StatusCode.FIELD_FOUND) {
                    return;
                }
                ReferenceContainerDataField df = pair.getRight();
                if(!df.hasRows()) {
                    return;
                }
                indexStudyAttachmentContainer(df, document, root+field.getIndexAs(), data, language);
            } else {
                // For now index table into the same document as the top content.
                // This will create multiple values in similar field names. Search treats these automatically
                // as OR matches, meaning you only have to search for one of the values to find the document.
                if(field.getType() == FieldType.CONTAINER) {
                    indexContainer(field, fieldContainer, document, root, path, config, data, language);
                } else if(field.getType() == FieldType.REFERENCECONTAINER) {
                    // Intelligent and complex solution would be to perform a recursive join during search splitting off a new reader each time REVISIONABLE
                    // reference is encountered. This way we could make arbitrarily deep queries through references.
                    // This is non trivial to set up however and prone to multiple bugs. For now lets index reference containers as containers and just
                    // resolve the references on the row. We can declare multiple reference fields for reference container in data configuration and just show
                    // the most useful of those on GUI with GUI configuration.

                    indexReferenceContainer(field, fieldContainer, document, root, path, config, data, language);
                }
            }
        } else {
            // Try to index the field as a non container field
            if(field.getWritable()) {
                indexNonContainerField(field, fieldContainer, document, root, path, config, data, language);
            } else if(field.getType() == FieldType.REFERENCE) {
                path = new Step(field.getKey(), path);
                indexReferenceField(field, fieldContainer.dataField(ValueDataFieldCall.get(field.getKey())).getRight(), document, root, path, data, language);
            }

        }
    }

    private void indexContainer(Field containerField, DataFieldContainer fieldContainer, IndexerDocument document, String root, Step path,
                                Configuration config, RevisionData data, Language language) {
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

        Language inputLang = containerField.getTranslatable() ? language : Language.DEFAULT;
        if(!container.hasRowsFor(inputLang)) {
            return;
        }
        path = new Step(containerField.getKey(), path);
        for(DataRow row : container.getRowsFor(inputLang)) {
            Step rowPath = new Step(row.getRowId().toString(), path);
            for(String key : containerField.getSubfields()) {
                // Iterate through configured subfields and index them as necessary
                Field field = config.getField(key);
                if(!field.getSubfield()) {
                    // For some reason non subfield was returned, ignore
                    continue;
                }
                indexField(field, row, document, root+containerField.getIndexAs(), rowPath, config, data, language);
            }
        }
    }

    private void indexReferenceContainer(Field containerField, DataFieldContainer fieldContainer, IndexerDocument document, String root, Step path,
                                         Configuration config, RevisionData data, Language language) {
        // TODO: Get this from actual indexer configuration
        if(!containerField.getIndexed()) {
            // Field should not be indexed according to configuration, this also means that none of its subfields get indexed
            return;
        }

        Pair<StatusCode, ReferenceContainerDataField> pair = fieldContainer.dataField(ReferenceContainerDataFieldCall.get(containerField.getKey()).setConfiguration(config));
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Field not found for some reason, don't stop indexing
            return;
        }
        ReferenceContainerDataField container = pair.getRight();
        // Reference containers are not translatable but the actual referenced fields might contain information in other languages
        if(container.getReferences().isEmpty()) {
            return;
        }
        path = new Step(containerField.getKey(), path);
        for(ReferenceRow row : container.getReferences()) {
            Step rowPath = new Step(row.getRowId().toString(), path);
            // Index the actual row value
            document.indexKeywordField(root + containerField.getIndexAs() + ".value", row.getReference().getValue());
            for(String key : containerField.getSubfields()) {
                // Iterate through configured subfields and index them as necessary
                // We know that all of these have to be DEPENDENCY references so we should be able to index them as such here.
                // Reference container is a terminating field, it can't contain any other fields than its own references
                Field field = config.getField(key);
                if(field == null) {
                    // No field configuration found, ignore
                    continue;
                }
                if(!field.getSubfield()) {
                    // For some reason non subfield was returned, ignore
                    continue;
                }
                if(!field.getIndexed()) {
                    // Field should not be indexed, ignore
                    continue;
                }
                ReferenceOption option = references.getCurrentFieldOption(language, data, rowPath.printPath()+"."+field.getKey());
                if(option != null) {
                    document.indexText(language, field, root+containerField.getIndexAs()+".", option, field.getGeneralSearch());
                }
                //indexField(field, row, document, root+field.getIndexAs(), rowPath, config, data);
            }
        }
    }

    private void indexNonContainerField(Field field, DataFieldContainer fieldContainer, IndexerDocument document, String root, Step path,
            Configuration config, RevisionData data, Language language) {
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

        path = new Step(field.getKey(), path);
        switch(field.getType()) {
            case DATE:
            case DATETIME:
            case TIME:
                // Index all Date and time fields as StringFields since they should not be analyzed or tokenized in any way.
                document.indexKeywordField(root + field.getIndexAs(), saved.getActualValueFor(inputLang), field.getGeneralSearch());
                break;
            case BOOLEAN:
                // Index boolean field as a string with 'true' or 'false' as value. Boolean field should not be analyzer or tokenized
                document.indexKeywordField(root + field.getIndexAs(), saved.getActualValueFor(inputLang), field.getGeneralSearch());
                break;
            case CONCAT:
                // Index concat field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(inputLang, field, root, saved, field.getGeneralSearch());
                break;
            case STRING:
                // Index string field as a text field which should be analyzed and tokenized unless marked as exact field in config
                document.indexText(inputLang, field, root, saved, field.getGeneralSearch());
                break;
            case RICHTEXT:
                // Index RICHTEXT fields as text. They should have their 'exact' value forced to false so they should be parsed through an analyzer that will automatically strip all the html-elements from the text.
                document.indexText(inputLang, field, root, saved, field.getGeneralSearch());
                break;
            case INTEGER:
                // Convert value to correct number format (integer or long, or just stick with long for everything) and index as correct number field
                try {
                    document.indexIntegerField(root + field.getIndexAs(), Long.parseLong(saved.getActualValueFor(inputLang)), false, field.getGeneralSearch());
                } catch(NumberFormatException nfe) {
                    Logger.debug(getClass(), "Skipping field "+field.getKey()+" since value is not INTEGER and search would not find it");
                }
                break;
            case REAL:
                // Convert value to correct number format (float or double, or just stick with double for everything) and index as correct number field
                try {
                    document.indexRealField(root + field.getIndexAs(), Double.parseDouble(saved.getActualValueFor(inputLang)), false, field.getGeneralSearch());
                } catch(NumberFormatException nfe) {
                    Logger.debug(getClass(), "Skipping field "+field.getKey()+" since value is not REAL and search would not find it");
                }
                break;
            case SELECTION:
                indexSelectionField(field, saved, document, root, path, config, data, language);
                break;
            case REFERENCE:
                indexReferenceField(field, saved, document, root, path, data, language);
                break;
            case CONTAINER:
            case REFERENCECONTAINER:
                // This should never happen since container types are handled separately but this removes compiler warnings
                break;
        }
        // Add save information to index for every field.
        document.indexKeywordField(root + field.getIndexAs() + ".saved.time", saved.getValueFor(inputLang).getSaved().getTime().toString());
        document.indexKeywordField(root + field.getIndexAs() + ".saved.user", saved.getValueFor(inputLang).getSaved().getUser());

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
    private void indexSelectionField(Field field, ValueDataField saved, IndexerDocument document, String root, Step path,
            Configuration config, RevisionData data, Language language) {
        // If field is not translatable we are going to use DEFAULT as the indexing language, this needs to be detected in indexText methods too
        // We have to check this here instead of some more revisions method since we have to pass actual requested language on to other methods
        Language inputLang = field.getTranslatable() ? language : Language.DEFAULT;

        String selectionKey = field.getSelectionList();
        SelectionList list = config.getRootSelectionList(selectionKey);

        // All SelectionList values are assumed to be indexed as exact until further notice
        switch(list.getType()) {
            case SUBLIST:
                // We've hit a loop in the list configuration, can index only the value and nothing else
                document.indexKeywordField(root + field.getIndexAs(), saved.getActualValueFor(inputLang), field.getGeneralSearch());
                document.indexKeywordField(root + field.getIndexAs() + ".value", saved.getActualValueFor(inputLang));
                break;
            case LITERAL:
                // Index value both as value and title (they should be equal in any case)
                document.indexKeywordField(root + field.getIndexAs(), saved.getActualValueFor(inputLang), field.getGeneralSearch());
                document.indexKeywordField(root + field.getIndexAs() + ".value", saved.getActualValueFor(inputLang));
                break;
            case VALUE:
                // Index the title to the actual field and the value to field.value field. Index only the default language from title
                Option option = list.getOptionWithValue(saved.getActualValueFor(inputLang));
                if(option != null) {
                    document.indexText(inputLang, field, root, option.getTitleFor(inputLang), field.getGeneralSearch());
                    document.indexKeywordField(root + field.getIndexAs() + ".value", option.getValue());
                } else {
                    // Some problem so possibly log error, but do nothing for now
                    break;
                }
                break;
            case REFERENCE:
                indexReferenceField(field, saved, document, root, path, data, language);
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
    private void indexReferenceField(Field field, ValueDataField saved, IndexerDocument document, String root, Step path, RevisionData data, Language language) {
        Language inputLang = field.getTranslatable() ? language : Language.DEFAULT;
        // TODO: Split this off in some better way, for now intercept during variables reference handling)
        boolean foundContent = false;
        if(false && field.getKey().equals("variables") && field.getType() == FieldType.REFERENCE && data.getConfiguration().getType() == ConfigurationType.STUDY) {
            // This is variables reference in study. Take some time and index variables to this document
            document.indexKeywordField(root + field.getIndexAs() + ".value", saved.getActualValueFor(Language.DEFAULT));
            indexStudyVariables(saved, document, root+field.getIndexAs(), data, language);
        } else {
            ReferenceOption option = references.getCurrentFieldOption(language, data, path.printPath());
            if(option != null) {
                document.indexKeywordField(root + field.getIndexAs() + ".value", option.getValue());
                document.indexText(inputLang, field, root, option, field.getGeneralSearch());
            }
            // If requested language equals the actual input language then we've found content for requested language.
            if(inputLang == language) contentForLanguage = true;
        }
    }

    private void indexStudyVariables(ValueDataField saved, IndexerDocument document, String root, RevisionData data, Language language) {
        // There shouldn't be different study variables objects for different languages so we can assume that the value is always found
        // from DEFAULT language
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(
                Long.parseLong(saved.getActualValueFor(Language.DEFAULT)),
                data.getState() == RevisionState.APPROVED, ConfigurationType.STUDY_VARIABLES);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(), "Didn't find revision for study variables "+saved.getActualValueFor(Language.DEFAULT));
            return;
        }
        Pair<ReturnResult, Configuration> confPair = getConfiguration(pair.getRight().getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Didn't find configuration for "+pair.getRight().getConfiguration());
            return;
        }
        indexFields(pair.getRight(), document, root, new Step("", null), confPair.getRight(), language);
    }

    private void indexStudyVariablesContainer(ReferenceContainerDataField field, IndexerDocument document,
                                                 String root, RevisionData data, Language language) {
        for(ReferenceRow reference : field.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), data.getState() == RevisionState.APPROVED, ConfigurationType.STUDY_VARIABLE);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for referenced study variable "+reference.getActualValue());
                continue;
            }
            Pair<ReturnResult, Configuration> confPair = getConfiguration(pair.getRight().getConfiguration());
            if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Didn't find configuration for "+pair.getRight().getConfiguration());
                continue;
            }
            indexFields(pair.getRight(), document, root, new Step("", null), confPair.getRight(), language);
        }
    }

    private void indexStudyAttachmentContainer(ReferenceContainerDataField field, IndexerDocument document,
                                                 String root, RevisionData data, Language language) {
        for(ReferenceRow reference : field.getReferences()) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), data.getState() == RevisionState.APPROVED, ConfigurationType.STUDY_ATTACHMENT);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Didn't find revision for referenced study attachment "+reference.getActualValue());
                continue;
            }
            Pair<ReturnResult, Configuration> confPair = getConfiguration(pair.getRight().getConfiguration());
            if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Didn't find configuration for "+pair.getRight().getConfiguration());
                continue;
            }
            indexFields(pair.getRight(), document, root, new Step("", null), confPair.getRight(), language);
        }
    }

    private void finalizeIndexing(RevisionData data, IndexerDocument document, Configuration config, Language language) {
        switch(data.getConfiguration().getType()) {
            case STUDY:
                finalizeStudyIndexing(data, document, config, language);
                break;
            default:
                break;
        }
    }

    private void finalizeStudyIndexing(RevisionData data, IndexerDocument document, Configuration config, Language language) {
        indexStudyErrors(data, document, language);

        indexStudyBinderPages(data, document, language);
    }

    private void indexStudyErrors(RevisionData data, IndexerDocument document, Language language) {
        List<StudyError> errors = studyErrors.listErrorsForStudy(data.getKey().getId());
        if(errors.isEmpty()) {
            return;
        }
        String root = "errors.";

        ReferencePathRequest refReq = new ReferencePathRequest();
        ReferenceOption option;
        refReq.setLanguage(language);
        Reference datasetpart = new Reference("errordatasetpart", ReferenceType.JSON, "errordatasetpart", "value");
        datasetpart.setTitlePath("title");

        Reference partsection = new Reference("errorpartsection", ReferenceType.JSON, "errorpartsection", "value");
        datasetpart.setTitlePath("title");

        Reference errorlanguage = new Reference("errorlanguage", ReferenceType.JSON, "errorlanguage", "value");
        datasetpart.setTitlePath("title");

        for(StudyError error : errors) {
            // errordatasetpart
            if(StringUtils.hasText(error.getErrordatasetpart())) {
                ReferencePath datasetpartPath = new ReferencePath(datasetpart, error.getErrordatasetpart());
                refReq.setRoot(datasetpartPath);
                option = references.getCurrentFieldOption(refReq);
                if(option != null) {
                    document.indexStringField(root+"errordatasetpart", option.getTitle().getValue(), NO, false);
                    document.indexKeywordField(root+"errordatasetpart.value", option.getValue());
                }
            }

            // errorlabel
            if(StringUtils.hasText(error.getErrorlabel())) {
                document.indexText(language, root+"errorlabel", error.getErrorlabel(), false, NO, false);
            }

            // errorlanguage
            if(StringUtils.hasText(error.getErrorlanguage())) {
                ReferencePath errorlanguagePath = new ReferencePath(errorlanguage, error.getErrorlanguage());
                refReq.setRoot(errorlanguagePath);
                option = references.getCurrentFieldOption(refReq);
                if(option != null) {
                    document.indexStringField(root+"errorlanguage", option.getTitle().getValue(), NO, false);
                    document.indexKeywordField(root+"errorlanguage.value", option.getValue());
                }
            }

            // errornotes
            if(StringUtils.hasText(error.getErrornotes())) {
                document.indexText(language, root+"errornotes", error.getErrorlabel(), false, NO, false);
            }

            // errorpartsection
            if(StringUtils.hasText(error.getErrorpartsection())) {
                ReferencePath partsectionPath = new ReferencePath(partsection, error.getErrorpartsection());
                refReq.setRoot(partsectionPath);
                option = references.getCurrentFieldOption(refReq);
                if(option != null) {
                    document.indexStringField(root+"errorpartsection", option.getTitle().getValue(), NO, false);
                    document.indexKeywordField(root+"errorpartsection.value", option.getValue());
                }
            }

            // errorscore
            if(error.getErrorscore() != null) {
                document.indexIntegerField(root+"errorscore", error.getErrorscore().longValue(), false, false);
            }

            // errortriggerdate
            if(error.getErrortriggerdate() != null && StringUtils.hasText(error.getErrortriggerdate().toString())) {
                document.indexKeywordField(root + "errortriggerdate", error.getErrortriggerdate().toString(), false);
            }

            // errortriggerpro
            if(StringUtils.hasText(error.getErrortriggerpro())) {
                document.indexKeywordField(root + "errortriggerpro", error.getErrortriggerpro(), false);
            }

            // savedAt
            if(StringUtils.hasText(error.getSavedAt().toString())) {
                document.indexKeywordField(root + "savedAt", error.getSavedAt().toString(), false);
            }

            // savedBy
            if(StringUtils.hasText(error.getSavedBy())) {
                document.indexKeywordField(root + "savedBy", error.getSavedBy(), false);
            }
        }
    }

    private void indexStudyBinderPages(RevisionData data, IndexerDocument document, Language language) {
        Pair<ReturnResult, List<BinderPageListEntry>> pages = binders.listStudyBinderPages(data.getKey().getId());
        if(pages.getLeft() == ReturnResult.NO_RESULTS) {
            return;
        }
        String root = "binders.";
        for(BinderPageListEntry page : pages.getRight()) {
            // description
            if(StringUtils.hasText(page.getDescription())) {
                document.indexText(language, root+"description", page.getDescription(), false, NO, true);
            }

            // binderid
            if(page.getBinderId() != null) {
                document.indexIntegerField(root+"binderid", page.getBinderId(), false, false);
            }

            // studyid
            if(StringUtils.hasText(page.getStudyId())) {
                document.indexKeywordField(root + "studyid", page.getStudyId(), true);
            }

            // savedAt
            if(page.getSaved() != null && StringUtils.hasText(page.getSaved().getTime().toString())) {
                document.indexKeywordField(root + "savedAt", page.getSaved().getTime().toString(), false);
            }

            // savedBy
            if(page.getSaved() != null && StringUtils.hasText(page.getSaved().getUser())) {
                document.indexKeywordField(root + "savedBy", page.getSaved().getUser(), false);
            }
        }
    }

    private static class Step {
        private final String value;
        private final Step prev;

        private Step(String value, Step prev) {
            this.value = value;
            this.prev = prev;
        }

        public String printPath() {
            if(prev == null) {
                return value;
            }
            String path = prev.printPath();
            if(path.length() > 0) {
                path += ".";
            }
            return path += value;
        }
    }
}