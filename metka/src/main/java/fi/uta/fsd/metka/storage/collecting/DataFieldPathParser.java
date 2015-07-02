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

package fi.uta.fsd.metka.storage.collecting;


import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches given JsonNode for various interpretations of given path.
 */
class DataFieldPathParser {
    private final DataFieldContainer context;
    private final String[] path;
    private final Configuration configuration;
    private final Language language;
    private final ReferenceService references;

    /**
     * Construct a data field path parser.
     * This object can collect mainly data field maps from given initial data field map.
     * @param context          DataFieldContainer context from where to start parsing
     * @param path             Path used to parse things
     * @param configuration    Configuration that should contain the fields in the data field map
     * @param language         Language for which options are searched
     */
    DataFieldPathParser(DataFieldContainer context, String[] path, Configuration configuration, Language language, ReferenceService references) {
        this.context = context;
        this.path = path;
        this.configuration = configuration;
        this.language = language;
        this.references = references;
    }

    /**
     * Finds all terminal DataFieldContainers containing the terminating path field.
     * Partial answers are not accepted so if the current path step terminates but there's still more path left then
     * that specific object is not included in the set.
     * @return
     */
    List<DataFieldContainer> findTermini() {
        List<DataFieldContainer> termini = new ArrayList<>();

        findTerminiPathStep(0, context, path.clone(), termini);

        return termini;
    }

    private void findTerminiPathStep(int level, DataFieldContainer context, String[] path, List<DataFieldContainer> termini) {
        if(level >= path.length) {
            // No more path, terminate
            return;
        }

        if(context == null) {
            // Current node was null, can't continue
            return;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return;
        }

        // Check non writable terminating references
        if(!field.getWritable() && field.getType() == FieldType.REFERENCE) {
            // Checks that path should terminate at this value. If so then add the current field map to termini
            if(level == path.length-1) {
                // Value is the terminating value of path,
                termini.add(context);
                return;
            }

            // TODO: Resolve non writable non terminating references
        }

        DataField dataField = context.getField(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return;
        }

        // Checks that path should terminate at this value. If so then add the current field map to termini
        if(level == path.length-1) {
            // Value is the terminating value of path,
            termini.add(context);
            return;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    findTerminiPathStep(level+1, row, path, termini);
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    findTerminiPathStep(level+1, row, path, termini);
                }
            }
        }
    }

    /**
     * Returns the first DataField object that terminates a path
     * @return
     */
    DataField findFirstTerminatingValue() {
        return findFirstTerminatingValuePathStep(0, context, path.clone());
    }

    private DataField findFirstTerminatingValuePathStep(int level, DataFieldContainer context, String[] path) {
        if(level >= path.length) {
            // No more path, terminate
            return null;
        }

        if(context == null) {
            // Current node was null, can't continue
            return null;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return null;
        }

        DataField dataField = context.getField(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return null;
        }

        // Checks that path should terminate at this value. If so then return the current data field
        if(level == path.length-1) {
            // Value is the terminating value of path,
            return dataField;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return null;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    DataField result = findFirstTerminatingValuePathStep(level+1, row, path);
                    if(result != null) {
                        return result;
                    }
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    DataField result = findFirstTerminatingValuePathStep(level+1, row, path);
                    if(result != null) {
                        return result;
                    }
                }
            }
        }

        // Default is to return null and assume that recursion has ended.
        return null;
    }

    /**
     * Almost the same as first terminating object but instead of only a matching path the value at terminating path
     * has to equal given value.
     *
     * @param terminatingValue
     * @return
     */
    DataFieldContainer findRootObjectWithTerminatingValue(String terminatingValue) {
        return findRootObjectWithTerminatingValueStep(0, context, path.clone(), terminatingValue);
    }

    private DataFieldContainer findRootObjectWithTerminatingValueStep(int level, DataFieldContainer context, String[] path, String terminatingValue) {
        if(level >= path.length) {
            // No more path, terminate
            return null;
        }

        if(context == null) {
            // Current node was null, can't continue
            return null;
        }

        Field field = configuration.getField(path[level]);
        if(field == null) {
            return null;
        }

        DataField dataField = context.getField(field.getKey());
        if(dataField == null) {
            // There's no field in the current map, terminate
            return null;
        }

        // Checks that path should terminate at this value. If so then return the current data field
        if(level == path.length-1) {
            if(field.getType().isContainer() || !(dataField instanceof ValueDataField)) {
                // Don't accept container types for value checking
                return null;
            }
            ValueDataField valueField = (ValueDataField)dataField;
            // Value is the terminating value of path,
            Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
            if(valueField.valueForEquals(optionLang, terminatingValue)) {
                return context;
            }

            return null;
        }

        // If we are not in a terminating path but we have no where else to go then terminate and don't add field map
        // TODO: Reference fields could be parsed further but terminate for now
        if(field.getType() != FieldType.CONTAINER) {
            return null;
        }

        // Path doesn't terminate. Check that data field is a container and continue on depending on parameters
        if(dataField instanceof ContainerDataField) {
            ContainerDataField container = (ContainerDataField)dataField;
            if(field.getTranslatable()) {
                // Container is translatable, continue on with correct language rows
                for(DataRow row : container.getRowsFor(language)) {
                    DataFieldContainer result = findRootObjectWithTerminatingValueStep(level + 1, row, path, terminatingValue);
                    if(result != null) {
                        return result;
                    }
                }
            } else {
                // Container is not translatable continue on with default language rows
                for(DataRow row : container.getRowsFor(Language.DEFAULT)) {
                    DataFieldContainer result = findRootObjectWithTerminatingValueStep(level + 1, row, path, terminatingValue);
                    if(result != null) {
                        return result;
                    }
                }
            }
        }

        // Default is to return null and assume that recursion has ended.
        return null;
    }

    /**
     * Returns ReferenceOption from given revision data
     * @param revision Revision data from which to return reference option
     * @param reference Reference that is used to extract value and title from given JsonNode
     * @return Single ReferenceOption containing value and title as per specification
     */
    ReferenceOption getOption(RevisionData revision, Reference reference) {

        if(revision == null) {
            // Needs a non empty data field map as field map
            return null;
        }

        if(reference.getType() == ReferenceType.JSON) {
            // Sanity check, reference not valid for this parser
            return null;
        }

        if(reference.getType() == ReferenceType.DEPENDENCY) {
            // Sanity check, return the DataFieldContainer version instead
            return getOption((DataFieldContainer)revision, reference);
        }

        // This path should be relative to the given field map
        String[] path = reference.getTitlePathParts();

        String valueStr = revision.getKey().getId().toString();
        if(!StringUtils.hasText(valueStr)) {
            return null;
        }
        String titleStr = getTitleString(revision, path);
        if(titleStr == null) {
            titleStr = valueStr;
        }

        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, titleStr));
        return option;
    }

    /**
     * Returns ReferenceOption from given data field map
     * @param context DataFieldContainer containing value field, title field is relative to this container
     * @param reference Reference that is used to extract value and title from given JsonNode
     * @return Single ReferenceOption containing value and title as per specification
     */
    ReferenceOption getOption(DataFieldContainer context, Reference reference) {

        if(context == null || !context.hasFields()) {
            // Needs a non empty data field map as field map
            return null;
        }
        String[] path = reference.getValuePathParts();
        // This should be the field key found from the field map
        String valueKey = path[path.length-1];

        // This path should be relative to the given field map
        path = reference.getTitlePathParts();

        Field field = configuration.getField(valueKey);
        if(field == null) {
            return null;
        }

        Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;

        if(!field.getWritable() && field.getType() == FieldType.REFERENCE) {
            return references.getCurrentFieldOption(optionLang, context.getContainingRevision(), configuration, reference.getValuePath());
        }

        DataField dataField = context.getField(valueKey);
        if(dataField == null) {
            // No value to get
            return null;
        }
        if(dataField instanceof ContainerDataField || dataField instanceof ReferenceContainerDataField) {
            // Can't return a single option
            // TODO: It should be possible to basically make a read only copy of a container with references, but not yet.
            return null;
        }

        ValueDataField valueField = (ValueDataField)dataField;
        if(!valueField.hasValueFor(optionLang)) {
            // Field does not have a value for the needed language
            return null;
        }

        String valueStr = valueField.getActualValueFor(optionLang);
        if(!StringUtils.hasText(valueStr)) {
            return null;
        }
        String titleStr = getTitleString(context, path);
        if(titleStr == null && field.getType() == FieldType.SELECTION) {
            titleStr = getSelectionTitle(field, valueStr);
        } else if(titleStr == null && field.getType() == FieldType.REFERENCE) {
            titleStr = getReferenceTitle(field);
        }
        if(titleStr == null) {
            titleStr = valueStr;
        }

        ReferenceOption option = new ReferenceOption(valueStr, new ReferenceOptionTitle(ReferenceTitleType.LITERAL, titleStr));
        return option;
    }

    private String getTitleString(DataFieldContainer context, String[] path) {
        String titleStr = null;

        if(path != null) {
            // Let's try to find first terminating data field matching the title path
            DataFieldPathParser titleParser = new DataFieldPathParser(context, path, configuration, language, references);
            DataField titleField = titleParser.findFirstTerminatingValue();
            if(titleField != null && titleField instanceof ValueDataField) {
                ValueDataField titleValueField = (ValueDataField)titleField;
                Field field = configuration.getField(titleField.getKey());
                Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
                if(titleValueField.hasValueFor(optionLang)) {
                    // Field does not have a value for the needed language
                    titleStr = titleValueField.getActualValueFor(optionLang);
                }
                if(titleStr != null && field.getType() == FieldType.SELECTION) {
                    titleStr = getSelectionTitle(field, titleStr);
                }
            }
        }

        return titleStr;
    }

    private String getSelectionTitle(Field field, String value) {
        String titleStr = null;

        Language optionLang = field.getTranslatable() ? language : Language.DEFAULT;
        SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
        if(list.getType() == SelectionListType.LITERAL || list.getType() == SelectionListType.VALUE) {
            Option option = list.getOptionWithValue(value);
            if(option != null) {
                titleStr = list.getType() == SelectionListType.VALUE ? option.getTitleFor(optionLang) : option.getValue();
            }
        } else if(list.getType() == SelectionListType.REFERENCE) {
            titleStr = getReferenceTitle(field);
        }

        return titleStr;
    }

    private String getReferenceTitle(Field field) {
        String titleStr = null;

        ReferenceOption option = references.getCurrentFieldOption(language, context.getContainingRevision(), configuration, field.getKey());
        if(option != null) {
            titleStr = option.getTitle().getValue();
        }

        return titleStr;
    }
}
