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

import com.ctc.wstx.util.StringUtil;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

/**
 * Handles CHILDREN type targets.
 * Context is checked for possibility of children. If no children can exist the process is stopped immediately, otherwise each child is checked separately
 */
class ChildrenTargetHandler {

    static ValidateResult handle(DataFieldValidator validator, Target t, DataFieldContainer context, Configuration configuration
            , RevisionRepository revisions, ConfigurationRepository configurations) {
        Target parent = t.getParent();
        if(parent == null) {
            // We can't process children of null
            return new ValidateResult(false, "CONFIG", null);
        }

        if(parent.getType() != TargetType.FIELD) {
            // At the moment only Fields can have children and so any other parent is a configuration problem
            return new ValidateResult(false, "CONFIG", null);
        }

        Field field = configuration.getField(parent.getContent());
        if(field == null) {
            // If we can't find the parent from the configuration then no further validation can take place
            return new ValidateResult(false, "CONFIG", null);
        }

        DataField d;
        Reference reference = null;
        switch (field.getType()) {
            case CONTAINER:
                d = context.dataField(ContainerDataFieldCall.get(field.getKey())).getRight();
                break;
            case REFERENCECONTAINER:
                reference = configuration.getReference(field.getReference());
                if(reference == null) {
                    // Reference not found, configuration error
                    return new ValidateResult(false, "CONFIG", null);
                }
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return new ValidateResult(false, "CONFIG", null);
                }
                d = context.dataField(ReferenceContainerDataFieldCall.get(field.getKey())).getRight();
                break;
            case SELECTION:
                SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                if(list.getType() != SelectionListType.REFERENCE) {
                    // Only REFERENCE type SELECTION fields can have children
                    return new ValidateResult(false, "CONFIG", null);
                }
                reference = configuration.getReference(list.getReference());
                if(reference == null) {
                    // Reference not found, configuration error
                    return new ValidateResult(false, "CONFIG", null);
                }
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return new ValidateResult(false, "CONFIG", null);
                }
                d = context.dataField(ValueDataFieldCall.get(field.getKey())).getRight();
                break;
            case REFERENCE:
                reference = configuration.getReference(field.getReference());
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return new ValidateResult(false, "CONFIG", null);
                }
                if(reference == null) {
                    // Reference not found, configuration error
                    return new ValidateResult(false, "CONFIG", null);
                }
                d = context.dataField(ValueDataFieldCall.get(field.getKey())).getRight();
                break;
            default:
                // Field can not have children and so no context switching can take place. Configuration mistake
                return new ValidateResult(false, "CONFIG", null);
        }

        if(d == null) {
            // No field data found, from the point of CHILDREN target the validation is a success
            // since no context switching can take place and all further calls would be made to an empty context.
            // The point of CHILDREN target is not to require children but just to move validation down a level.
            // If chidlren are required then a separate NOT_EMPTY validation has to be made.
            return new ValidateResult(true);
        }

        // Check for the existence of children
        // TODO: How to define languages for context change?
        switch(field.getType()) {
            case CONTAINER:
                if(!((ContainerDataField)d).hasRows()) {
                    // No chidlren, can't change context. Everything in this target is valid
                    return new ValidateResult(true);
                } else {
                    return changeContext((ContainerDataField)d, t, validator, configuration);
                }
            case REFERENCECONTAINER:
                if(!((ReferenceContainerDataField)d).hasRows()) {
                    // No chidlren, can't change context. Everything in this target is valid
                    return new ValidateResult(true);
                } else {
                    if(reference == null) {
                        // This should have been found earlier
                        return new ValidateResult(false, "CONFIG", null);
                    }
                    return changeContext((ReferenceContainerDataField)d, t, reference, context, configuration, revisions, validator, configurations);
                }
            default:
                if(!((ValueDataField)d).hasAnyValue()) {
                    return new ValidateResult(true);
                } else {
                    if(reference == null) {
                        // This should have been found earlier
                        return new ValidateResult(false, "CONFIG", null);
                    }
                    return changeContext((ValueDataField)d, t, reference, context, configuration, revisions, validator, configurations);
                }
        }
    }

    private static ValidateResult changeContext(ContainerDataField d, Target t, DataFieldValidator validator, Configuration configuration) {
        // TODO: For now validates all languages, fix this when language support is added to validation
        for(Language l : Language.values()) {
            if(!d.hasRowsFor(l)) {
                continue;
            }
            for(DataRow row : d.getRowsFor(l)) {
                if(row.getRemoved()) {
                    continue;
                }
                ValidateResult vr = validator.validate(t.getTargets(), row, configuration);
                if(!vr.getResult()) {
                    return vr;
                }
            }
        }
        return new ValidateResult(true);
    }

    private static ValidateResult changeContext(ReferenceContainerDataField d, Target t, Reference reference, DataFieldContainer context, Configuration configuration
            , RevisionRepository revisions, DataFieldValidator validator, ConfigurationRepository configurations) {
        Field field = configuration.getField(d.getKey());

        if(d != null && d.hasRows()) {
            for(ReferenceRow row : d.getReferences()) {
                if(row.getRemoved()) {
                    continue;
                }
                if(!row.hasValue()) {
                    Logger.error(ChildrenTargetHandler.class, "Encountered a reference row without value in "+context.toString());
                    continue;
                }
                ValidateResult vr = changeContext(row.getActualValue(), t, context, validator, reference, revisions, configurations);
                if(!vr.getResult()) {
                    // Failed validation
                    return vr;
                }
            }
        }
        return new ValidateResult(true);
    }

    private static ValidateResult changeContext(ValueDataField d, Target t, Reference reference, DataFieldContainer context, Configuration configuration
            , RevisionRepository revisions, DataFieldValidator validator, ConfigurationRepository configurations) {
        Field field = configuration.getField(d.getKey());
        for(Language l : Language.values()) {
            if(!d.hasValueFor(l)) {
                continue;
            }
            ValidateResult vr = changeContext(d.getActualValueFor(l), t, context, validator, reference, revisions, configurations);
            if(!vr.getResult()) {
                // Failed validation
                return vr;
            }
        }
        return new ValidateResult(true);
    }

    private static ValidateResult changeContext(String value, Target t, DataFieldContainer context, DataFieldValidator validator, Reference reference
            , RevisionRepository revisions, ConfigurationRepository configurations) {
        Pair<ReturnResult, RevisionData> pair = fetchRevisionData(reference, value, revisions);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // If we don't find the revision data then return true by default, existence should have been checked earlier if this is required
            return new ValidateResult(true);
        }

        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(pair.getRight().getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            // We've found a revision but can't find configuration for it, we can't accept this as a success
            Logger.error(FieldTargetHandler.class, "Could not find configuration for revision "+pair.getRight().toString());
            return new ValidateResult(false, "CONFIG", null);
        }

        RevisionData rev = pair.getRight();
        rev.initParents(context);
        return validator.validate(t.getTargets(), rev, confPair.getRight());
    }

    private static Pair<ReturnResult, RevisionData> fetchRevisionData(Reference reference, String value, RevisionRepository revisions) {
        if(!StringUtils.hasText(value)) {
            Logger.error(ChildrenTargetHandler.class, "Value was missing during fetchRevisionData operation");
            return new ImmutablePair<>(ReturnResult.PARAMETERS_MISSING, null);
        }
        String id;
        String no = null;
        if(reference.getType() == ReferenceType.REVISION) {
            String[] splits = value.split("-");
            if(splits == null || splits.length < 2) {
                id = value;
            } else {
                id = splits[0];
                no = splits[1];
            }
        } else {
            id = value;
        }
        if(no == null) {
            return revisions.getLatestRevisionForIdAndType(Long.parseLong(id), false, ConfigurationType.fromValue(reference.getTarget()));
        } else {
            return revisions.getRevisionData(Long.parseLong(id), Integer.parseInt(no));
        }
    }
}