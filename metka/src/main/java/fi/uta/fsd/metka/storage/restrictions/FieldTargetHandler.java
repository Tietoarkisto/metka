package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

class FieldTargetHandler {
    private static final Logger logger = LoggerFactory.getLogger(FieldTargetHandler.class);
    static boolean handle(Target target, DataFieldContainer context, RevisionValidator revisionValidator, RestrictionValidator validator,
                          RevisionData revision, Configuration configuration, RevisionRepository revisions, SearcherComponent searcher) {
        Field field = configuration.getField(target.getContent());
        if(field == null) {
            return false;
        }
        if(field.getSubfield() && target.getParent() == null) {
            // Something is wrong. Field is marked as a subfield but target doesn't have a parent
            return false;
        }

        DataField d;
        switch (field.getType()) {
            case CONTAINER:
                d = context.dataField(ContainerDataFieldCall.get(target.getContent())).getRight();
                break;
            case REFERENCECONTAINER:
                d = context.dataField(ReferenceContainerDataFieldCall.get(target.getContent())).getRight();
                break;
            default:
                d = context.dataField(ValueDataFieldCall.get(target.getContent())).getRight();
                break;
        }
        for(Check check : target.getChecks()) {
            // Check is enabled
            if(validator.validate(revision, check.getRestrictors())) {
                if(!checkConditionForField(field, d, check.getCondition(), revision, configuration, searcher)) {
                    return false;
                }
            }
        }

        if(d != null && !target.getTargets().isEmpty()) {
            // This level is done and we have a field, we can check sub targets in cases where it makes sense
            switch(field.getType()) {
                case CONTAINER:
                    return checkContainerSubTargets((ContainerDataField)d, target, revisionValidator);
                case REFERENCECONTAINER:
                    return checkReferenceContainerSubTargets(field, (ReferenceContainerDataField) d, target, configuration, revisions, validator);
                case REFERENCE:
                    return checkReferenceSubTargets(field, (ValueDataField) d, target, configuration, revisions, validator);
                case SELECTION:
                    SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                    if(list == null) {
                        logger.error("Could not find root list for "+field.getSelectionList());
                    }
                    if(list != null && list.getType() == SelectionListType.REFERENCE) {
                        return checkSelectionReferenceSubTargets(field, list, (ValueDataField)d, target, configuration, revisions, validator);
                    }
                    break;
            }
        }

        return true;
    }

    private static boolean checkContainerSubTargets(ContainerDataField d, Target t, RevisionValidator revisionValidator) {
        // TODO: For now validates all languages, fix this when language support is added to validation
        for(Language l : Language.values()) {
            if(!d.hasRowsFor(l)) {
                continue;
            }
            for(DataRow row : d.getRowsFor(l)) {
                for(Target sub : t.getTargets()) {
                    if(!revisionValidator.validateTarget(sub, row)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkReferenceContainerSubTargets(Field field, ReferenceContainerDataField d, Target t,
                                                             Configuration configuration, RevisionRepository revisions, RestrictionValidator validator) {
        Reference reference = configuration.getReference(field.getReference());
        if(reference == null) {
            logger.error("Could not find reference "+field.getReference());
            return false;
        }
        if(reference.getType() != ReferenceType.REVISIONABLE) {
            // At the moment we are only parsing revisionable references, other references could be handled at some point but for now return true
            return true;
        }
        if(d != null && !d.getReferences().isEmpty()) {
            for(ReferenceRow row : d.getReferences()) {
                if(!row.hasValue()) {
                    logger.error("Reference with rowId "+row.getRowId()+" has no value.");
                }
                if(!checkReferenceSubTargets(row.getReference().asInteger(), reference, t, revisions, validator)) {
                    logger.warn("Reference "+Long.parseLong(row.getActualValue())+" failed validation.");
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkReferenceSubTargets(Field field, ValueDataField d, Target t,
                                                    Configuration configuration, RevisionRepository revisions, RestrictionValidator validator) {
        Reference reference = configuration.getReference(field.getReference());
        return checkReferenceSubTargets(field, reference, d, t, revisions, validator);
    }

    private static boolean checkSelectionReferenceSubTargets(Field field, SelectionList list, ValueDataField d, Target t,
                                                             Configuration configuration, RevisionRepository revisions, RestrictionValidator validator) {
        Reference reference = configuration.getReference(list.getReference());
        return checkReferenceSubTargets(field, reference, d, t, revisions, validator);
    }


    private static boolean checkReferenceSubTargets(Field field, Reference reference, ValueDataField d, Target t, RevisionRepository revisions, RestrictionValidator validator) {
        if(reference == null) {
            logger.error("Could not find reference "+field.getReference());
            return false;
        }
        if(reference.getType() != ReferenceType.REVISIONABLE) {
            // At the moment we are only parsing revisionable references, other references could be handled at some point but for now return true
            return true;
        }
        // TODO: For now checks each language separately, fix this when implementing language support for validation
        for(Language l : Language.values()) {
            if(!d.hasValueFor(l)) {
                continue;
            }
            if(!checkReferenceSubTargets(d.getValueFor(l).valueAsInteger(), reference, t, revisions, validator)) {
                logger.warn("Reference "+Long.parseLong(d.getActualValueFor(l))+" failed validation for language "+l+".");
                return false;
            }
        }
        return true;
    }

    private static boolean checkReferenceSubTargets(Long id, Reference reference, Target t, RevisionRepository revisions, RestrictionValidator validator) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(id, false, ConfigurationType.fromValue(reference.getTarget()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // If we don't find the revision data then return true by default, existence should have been checked earlier if this is required
            return true;
        }

        return validator.validate(pair.getRight(), t.getTargets());
    }

    // CONDITION HANDLERS
    // *************************
    private static boolean checkConditionForField(Field field, DataField d, Condition condition, RevisionData revision,
                                                  Configuration configuration, SearcherComponent searcher) {
        switch(field.getType()) {
            case CONTAINER:
                return checkConditionForContainerField((ContainerDataField) d, condition);
            case REFERENCECONTAINER:
                return checkConditionForReferenceContainerField((ReferenceContainerDataField) d, condition);
            default:
                return checkConditionForValueField((ValueDataField) d, condition, configuration, revision, searcher);
        }
    }

    private static boolean checkConditionForContainerField(ContainerDataField d, Condition condition) {
        switch(condition.getType()) {
            case IS_EMPTY:
                return IsEmptyCheck.isEmpty(d, condition.getTarget());
            case NOT_EMPTY:
                return NotEmptyCheck.notEmpty(d, condition.getTarget());
            default:
                return true;
        }
    }

    private static boolean checkConditionForReferenceContainerField(ReferenceContainerDataField d, Condition condition) {
        switch(condition.getType()) {
            case IS_EMPTY:
                return IsEmptyCheck.isEmpty(d);
            case NOT_EMPTY:
                return NotEmptyCheck.notEmpty(d);
            default:
                return true;
        }
    }

    /**
     * Should be called only with non subfield value fields
     * @param d            ValueDataField - Should not be a subfield and is handled with that assumption
     * @param condition    Condition to be checked
     * @return boolean telling if validation is successful
     */
    private static boolean checkConditionForValueField(ValueDataField d, Condition condition, Configuration configuration,
                                                       RevisionData revision, SearcherComponent searcher) {
        switch(condition.getType()) {
            case IS_EMPTY:
                return IsEmptyCheck.isEmpty(d, condition.getTarget());
            case NOT_EMPTY:
                return NotEmptyCheck.notEmpty(d, condition.getTarget());
            case UNIQUE:
                return UniqueCheck.unique(d, condition.getParent().getParent(), revision, searcher, configuration);
            case INCREASING:
                return ChangeCheck.change(true, d, condition.getParent().getParent());
            case DECREASING:
                return ChangeCheck.change(false, d, condition.getParent().getParent());
            case EQUALS:
                if(condition.getTarget() == null) {
                    // Condition must have a target for equality checking
                    logger.error("Condition "+condition.toString()+" didn't have a target ");
                    return false;
                }
                return EqualsCheck.equals(d, condition.getTarget(), configuration);
            default:
                return true;
        }
    }

    private static class UniqueCheck {
        private static boolean unique(ValueDataField d, Target t, RevisionData revision, SearcherComponent searcher, Configuration configuration) {
            if(d == null) {
                return false;
            }
            if (t.getParent() == null && d.getParent() instanceof RevisionData) {
                // Field must have a value in at least one language and values in all languages must be unique within their language.
                boolean hasSomeValue = false;
                boolean allValuesUnique = true;
                for(Language l : Language.values()) {
                    if(!d.hasValueFor(l)) {
                        continue;
                    }
                    hasSomeValue = true;
                    // Value is top level, perform search
                    try {
                        ExpertRevisionSearchCommand command =
                                ExpertRevisionSearchCommand.build(
                                        configuration.getKey().getType().toValue()+" lang:"+l.toValue()+" -key.id:" + revision.getKey().getId() + " +" + d.getKey() + ":\"" + d.getActualValueFor(l) + "\""
                                        , configuration);
                        ResultList<RevisionResult> result = searcher.executeSearch(command);
                        if (!result.getResults().isEmpty()) {
                            allValuesUnique = false;
                        }
                    } catch (QueryNodeException e) {
                        logger.error("QRE during expert search creation.", e);
                        allValuesUnique = false;
                    } catch(ParseException pe) {
                        logger.error("ParseException during expert search creation.", pe);
                        allValuesUnique = false;
                    }
                }
                return (hasSomeValue && allValuesUnique);
            } else if(t.getParent() != null && d.getParent() instanceof DataRow) {
                // value is subfield, check uniqueness inside container
                // TODO: Implement
                return true;
            } else if((t.getParent() == null && d.getParent() instanceof DataRow)
                    || (t.getParent() != null && d.getParent() instanceof RevisionData)) {
                logger.error("Parent informations don't match between field and target.");
                return false;
            }
            return false;
        }
    }

    private static class EqualsCheck {
        private static boolean equals(ValueDataField d, Target t, Configuration configuration) {
            switch (t.getType()) {
                case QUERY:
                    // Query equality is not defined for field at this time but could be
                    return true;
                case FIELD:
                    // TODO: Validate against field value
                    //
                    return true;
                case NAMED:
                    // Fetch named target and then recurse through
                    Target named = configuration.getNamedTargets().get(t.getContent());
                    if(named != null) {
                        named = named.copy();
                        named.setParent(t.getParent());
                    }
                    // Named target must not be null for valid return
                    return named != null && equals(d, named, configuration);
                case VALUE:
                    // If target has an empty content then return false, empty values should be checked with IS_EMPTY
                    if (!StringUtils.hasText(t.getContent())) {
                        return false;
                    }
                    // If field is null then return false, null value can't equal anything
                    if (d == null) {
                        return false;
                    }
                    // TODO: When language support for restrictions is added then fix this.
                    // Checks values in all languages for equality, if one of them equals then the whole value equals
                    for (Language l : Language.values()) {
                        if (d.hasValueFor(l) && d.valueForEquals(l, t.getContent()))
                            return true;
                    }
                    // Value did not equal
                    return false;
                case LANGUAGE:
                    // Language equality is not defined for field at this time but could be
                    return true;
                default:
                    // Should not happen so let's return false in this case
                    return false;
            }
        }
    }

    private static class NotEmptyCheck {
        private static boolean notEmpty(ContainerDataField field, Target t) {
            if(t == null || t.getType() != TargetType.LANGUAGE) {
                // Only language condition target is defined at the moment, any other type target is treated as null
                return field != null && field.hasRows();
            } else {
                return field != null && field.hasRowsFor(Language.fromValue(t.getContent()));
            }
        }

        private static boolean notEmpty(ReferenceContainerDataField field) {
            return field != null && !field.getReferences().isEmpty();
        }

        private static boolean notEmpty(ValueDataField field, Target t) {
            if(field == null) {
                return false;
            }
            if(t == null || t.getType() != TargetType.LANGUAGE) {
                // Only language condition target is defined at the moment, any other type target is treated as null
                boolean hasValue = false;
                // Value in some language is enough
                for(Language l : Language.values()) {
                    if(field.hasValueFor(l)) {
                        hasValue = true;
                        break;
                    }
                }
                return hasValue;
            } else {
                return field.hasValueFor(Language.fromValue(t.getContent()));
            }
        }
    }

    private static class IsEmptyCheck {
        private static boolean isEmpty(ContainerDataField field, Target t) {
            if(t == null || t.getType() != TargetType.LANGUAGE) {
                // Only language condition target is defined at the moment, any other type target is treated as null
                return field == null || !field.hasRows();
            } else {
                return field == null || !field.hasRowsFor(Language.fromValue(t.getContent()));
            }
        }

        private static boolean isEmpty(ReferenceContainerDataField field) {
            return field == null || field.getReferences().isEmpty();
        }

        private static boolean isEmpty(ValueDataField field, Target t) {
            if(field == null) {
                return true;
            }
            if(t == null || t.getType() != TargetType.LANGUAGE) {
                // Only language condition target is defined at the moment, any other type target is treated as null
                boolean hasValue = false;
                // Can not have value in any language
                for (Language l : Language.values()) {
                    if (field.hasValueFor(l)) {
                        hasValue = true;
                        break;
                    }
                }
                return !hasValue;
            } else {
                return !field.hasValueFor(Language.fromValue(t.getContent()));
            }
        }
    }

    /**
     * Checks both increasing and decreasing
     */
    private static class ChangeCheck {
        private static boolean change(boolean increase, ValueDataField d, Target t) {
            if (t.getParent() == null && d.getParent() instanceof RevisionData) {
                /*case INCREASING:
                // TODO: Fetch previous revision (save or approve operations can only modify the latest revision so if we check
                // TODO: against the previous revision we should always be valid) then check if the current value is higher than (or equal to if we support value
                // TODO: staying the same for multiple revisions) previous value.
                return true;
                case DECREASING:
                // TODO: Fetch previous revision (save or approve operations can only modify the latest revision so if we check
                // TODO: against the previous revision we should always be valid) then check if the current value is lower than (or equal to if we support value
                // TODO: staying the same for multiple revisions) previous value.*/
                return true;
            } else if(t.getParent() != null && d.getParent() instanceof DataRow) {
                /*case INCREASING:
                // TODO: Fetch the immediate parent (if it exists) and go through all the rows making sure the value increases
                // TODO: (equal value is not supported in rows) between rows
                return true;
                case DECREASING:
                // TODO: Fetch the immediate parent (if it exists) and go through all the rows making sure the value decreases
                // TODO: (equal value is not supported in rows) between rows*/
                return true;
            } else if((t.getParent() == null && d.getParent() instanceof DataRow)
                    || (t.getParent() != null && d.getParent() instanceof RevisionData)) {
                logger.error("Parent informations don't match between field and target.");
                return false;
            } else {
                logger.error("Weird condition between field and taret in change checking.");
                return false;
            }
        }
    }
}
