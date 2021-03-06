/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.util.StringUtils;

import java.util.regex.*;

class FieldTargetHandler {

    static ValidateResult handle(Target target, DataFieldContainer context, DataFieldValidator validator,
                          Configuration configuration, SearcherComponent searcher, RevisionRepository revisions, ConfigurationRepository configurations) {
        Field field = configuration.getField(target.getContent());
        if(field == null) {
            // Configuration error, no field with provided name
            return new ValidateResult(false, "CONFIG - NULL FIELD", target.getContent());
        }
        if(field.getSubfield() && target.getParent() == null) {
            // Something is wrong. Field is marked as a subfield but target doesn't have a parent
            return new ValidateResult(false, "CONFIG - SUBFIELD NULL PARENT", field.getKey());
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
            if(validator.validate(check.getRestrictors(), context, configuration).getResult()) {
                if(!checkConditionForField(revisions, field, d, check.getCondition(), context, configuration, searcher)) {
                    return new ValidateResult(false, target.getType().name(), target.getContent());
                }
            }
        }

        return validator.validate(target.getTargets(), context, configuration);
    }

    // CONDITION HANDLERS
    // *************************
    private static boolean checkConditionForField(RevisionRepository revisions, Field field, DataField d, Condition condition, DataFieldContainer context,
                                                  Configuration configuration, SearcherComponent searcher) {
        switch(field.getType()) {
            case CONTAINER:
                return checkConditionForContainerField((ContainerDataField) d, condition);
            case REFERENCECONTAINER:
                return checkConditionForReferenceContainerField((ReferenceContainerDataField) d, condition);
            default:
                return checkConditionForValueField(revisions, (ValueDataField) d, condition, configuration, context, searcher);
        }
    }

    private static boolean checkConditionForContainerField(ContainerDataField d, Condition condition) {
        switch(condition.getType()) {
            case TRUE:
                return true;
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
            case TRUE:
                return true;
            case IS_EMPTY:
                return IsEmptyCheck.isEmpty(d);
            case NOT_EMPTY:
                return NotEmptyCheck.notEmpty(d);
            default:
                return true;
        }
    }

    /**
     * @param d            ValueDataField
     * @param condition    Condition to be checked
     * @return boolean telling if validation is successful
     */
    private static boolean checkConditionForValueField(RevisionRepository revisions, ValueDataField d, Condition condition, Configuration configuration,
                                                       DataFieldContainer context, SearcherComponent searcher) {
        switch(condition.getType()) {
            case TRUE:
                return true;
            case IS_EMPTY:
                return IsEmptyCheck.isEmpty(d, condition.getTarget());
            case NOT_EMPTY:
                return NotEmptyCheck.notEmpty(d, condition.getTarget());
            case UNIQUE:
                return UniqueCheck.unique(d, condition.getParent().getParent(), context, searcher, configuration);
            case INCREASING:
                return ChangeCheck.change(revisions, true, d, condition.getParent().getParent());
            case DECREASING:
                return ChangeCheck.change(revisions, false, d, condition.getParent().getParent());
            case EQUALS:
                if(condition.getTarget() == null) {
                    // Condition must have a target for equality checking
                    Logger.error(FieldTargetHandler.class, "Condition "+condition.toString()+" didn't have a target ");
                    return false;
                }
                return EqualsCheck.equals(d, condition.getTarget(), configuration);
            case NOT_EQUALS:
                if(condition.getTarget() == null) {
                    // Condition must have a target for non equality checking
                    Logger.error(FieldTargetHandler.class, "Condition "+condition.toString()+" didn't have a target ");
                    return false;
                }
                return EqualsCheck.notEquals(d, condition.getTarget(), configuration);
            case FREE_TEXT:
                return FreeTextCheck.freeText(d,configuration);
            case REGEX:
                return RegexCheck.regex(d, condition.getTarget());
            default:
                return true;
        }
    }

    private static class UniqueCheck {
        private static boolean unique(ValueDataField d, Target t, DataFieldContainer context, SearcherComponent searcher, Configuration configuration) {
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
                                        configuration.getKey().getType().toValue()+" lang:"+l.toValue()+" -key.id:" + context.getRevision().getId() + " +" + d.getKey() + ":\"" + d.getActualValueFor(l) + "\""
                                        , configuration);
                        ResultList<RevisionResult> result = searcher.executeSearch(command);
                        if (!result.getResults().isEmpty()) {
                            allValuesUnique = false;
                        }
                    } catch (QueryNodeException e) {
                        Logger.error(FieldTargetHandler.class, "QRE during expert search creation.", e);
                        allValuesUnique = false;
                    }
                }
                return (hasSomeValue && allValuesUnique);
            } else if(t.getParent() != null && d.getParent() instanceof DataRow) {
                // value is subfield, check uniqueness inside container
                // TODO: Implement
                return true;
            } else if(((t.getParent() == null) != (d.getParent() == null))) {
                Logger.error(FieldTargetHandler.class, "Parent informations don't match between field and target.");
                return false;
            }
            return false;
        }
    }

    private static class EqualsCheck {
        private static boolean equals(ValueDataField d, Target t, Configuration configuration) {
            if(d == null) {
                // Null value is by definition not equal to anything
                return false;
            }
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

        public static boolean notEquals(ValueDataField d, Target t, Configuration configuration) {
            if(d == null) {
                // Null is by definition not equal to everything
                return true;
            }
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
                    return named != null && notEquals(d, named, configuration);
                case VALUE:
                    // If target has an empty content then return false, empty values should be checked with IS_EMPTY
                    if (!StringUtils.hasText(t.getContent())) {
                        return false;
                    }
                    // TODO: When language support for restrictions is added then fix this.
                    // Checks values in all languages for equality, if one of them equals then the whole value equals and this check fails
                    for (Language l : Language.values()) {
                        if (d.hasValueFor(l) && d.valueForEquals(l, t.getContent()))
                            return false;
                    }
                    // Value did not equal
                    return true;
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
                return field != null && field.hasValidRows();
            } else {
                return field != null && field.hasValidRowsFor(Language.fromValue(t.getContent()));
            }
        }

        private static boolean notEmpty(ReferenceContainerDataField field) {
            return field != null && field.hasValidRows();
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
                return field == null || !field.hasValidRows();
            } else {
                return field == null || !field.hasValidRowsFor(Language.fromValue(t.getContent()));
            }
        }

        private static boolean isEmpty(ReferenceContainerDataField field) {
            return field == null || !field.hasValidRows();
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
        private static boolean change(RevisionRepository revisions, boolean increase, ValueDataField d, Target t) {
            if(d == null) {
                return true;
            }
            // regex for checking whether the string value is numeric or not
            String regexDouble = "^-?\\d+(\\.\\d+)?$";

            if (t.getParent() == null && d.getParent() instanceof RevisionData) {
                DataField df = d.getParent().getField(t.getContent());
                DataFieldContainer dfc = d.getParent();
                long idNo = dfc.getRevision().getId();
                int revNo = dfc.getRevision().getNo();
                // current revision data
                RevisionData current = revisions.getRevisionData(idNo, revNo).getRight();

                if(revNo == 1){
                    return true;
                } else {
                    revNo = revNo - 1;
                }

                // get previous revision data
                Pair<ReturnResult, RevisionData> previous = revisions.getRevisionData(idNo, revNo);

                while(previous.getLeft() != ReturnResult.REVISION_FOUND) {
                    revNo = revNo - 1;
                    previous = revisions.getRevisionData(idNo, revNo);
                    if(revNo <= 1){
                        return true;
                    }
                }
                // get actual field values
                Pair<StatusCode, ValueDataField> previousFieldPair = previous.getRight().dataField(ValueDataFieldCall.get(t.getContent()));
                Pair<StatusCode, ValueDataField> currentFieldPair = current.dataField(ValueDataFieldCall.get(t.getContent()));
                String previousValue = previousFieldPair.getRight().getActualValueFor(Language.DEFAULT).replace(",",".");
                String currentValue =  currentFieldPair.getRight().getActualValueFor(Language.DEFAULT).replace(",",".");

                if(increase){
                    // Compare values if they are numeric
                    if(previousValue.matches(regexDouble) && currentValue.matches(regexDouble)) {
                        double prevValue = Double.parseDouble(previousValue);
                        double currValue = Double.parseDouble(currentValue);
                        if (currValue <= prevValue) {
                            Logger.error(FieldTargetHandler.class, "The Field value is not increasing.");
                            return false;
                        }
                    } else if(previousValue.matches(regexDouble) && !currentValue.matches(regexDouble)){
                        Logger.error(FieldTargetHandler.class, "The Field value is not increasing.");
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    // Compare values if they are numeric
                    if(previousValue.matches(regexDouble) && currentValue.matches(regexDouble)) {
                        double prevValue = Integer.parseInt(previousValue);
                        double currValue = Integer.parseInt(currentValue);
                        if (currValue >= prevValue) {
                            Logger.error(FieldTargetHandler.class, "The Field value is Not decreasing.");
                            return false;
                        }
                    } else if(previousValue.matches(regexDouble) && !currentValue.matches(regexDouble)){
                        Logger.error(FieldTargetHandler.class, "The Field value is Not decreasing.");
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            } else if(t.getParent() != null && d.getParent() instanceof DataRow) {
                /*case INCREASING:
                // TODO: Fetch the immediate parent (if it exists) and go through all the rows making sure the value increases
                // TODO: (equal value is not supported in rows) between rows
                revturn true;
                case DECREASING:
                // TODO: Fetch the immediate parent (if it exists) and go through all the rows making sure the value decreases
                // TODO: (equal value is not supported in rows) between rows*/
                return true;
            } else if((t.getParent() == null && d.getParent() instanceof DataRow)
                    || (t.getParent() != null && d.getParent() instanceof RevisionData)) {
                Logger.error(FieldTargetHandler.class, "Parent information does not match between field and target.");
                return false;
            } else {
                Logger.error(FieldTargetHandler.class, "Weird condition between field and target in change checking.");
                return false;
            }
        }
    }

    private static class FreeTextCheck {
        private static boolean freeText(ValueDataField field, Configuration configuration) {
            if(field == null) {
                // Empty field can not be free text so that solves that
                return false;
            }
            Field fieldConf = configuration.getField(field.getKey());
            if(fieldConf.getType() != FieldType.SELECTION) {
                // Not a selection, check returns false. We can't just return true when people make a mistake in the configuration
                return false;
            }
            SelectionList list = configuration.getRootSelectionList(fieldConf.getSelectionList());
            if(list == null) {
                // Mistake in configuration, return false
                return false;
            }
            if(list.getFreeText().size() == 0) {
                // Field can't be in free text state if no free text values are defined
                return false;
            }
            // Since selection values are always saved on DEFAULT language we can just check default language against free text values
            return (list.getFreeText().contains(field.getActualValueFor(Language.DEFAULT)));
        }
    }

    // TODO: At the moment functions only for default language. This and the whole language handling within restrictions needs reworking
    // TODO: Add support for FIELD targets that are values, i.e. read target from configuration.
    // TODO: Target needs to be able to travel in context so that both references and fields in parent can be checked. The target traveling needs to be diversified away from just pre-validation.
    private static class RegexCheck {
        private static boolean regex(ValueDataField field, Target t) {
            if(t == null || t.getType() != TargetType.VALUE || !StringUtils.hasText(t.getContent())) {
                // Regular expression must be provided in a VALUE
                return false;
            }
            Pattern p = null;
            try {
                p = Pattern.compile(t.getContent());
            } catch (PatternSyntaxException e) {
                // Pattern vas not valid regex
                Logger.error(RegexCheck.class, "Given regex was not a valid pattern");
                return false;
            }

            String match = "";
            if(field != null) {
                match = field.getActualValueFor(Language.DEFAULT);
            }

            Matcher m = p.matcher(match);

            return m.matches();
        }
    }
}
