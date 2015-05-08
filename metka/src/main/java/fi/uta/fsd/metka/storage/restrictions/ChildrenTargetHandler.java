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

    static boolean handle(DataFieldValidator validator, Target t, DataFieldContainer context, Configuration configuration
            , RevisionRepository revisions, ConfigurationRepository configurations) {
        Target parent = t.getParent();
        if(parent == null) {
            // We can't process children of null
            return false;
        }

        if(parent.getType() != TargetType.FIELD) {
            // At the moment only Fields can have children and so any other parent is a configuration problem
            return false;
        }

        Field field = configuration.getField(parent.getContent());
        if(field == null) {
            // If we can't find the parent from the configuration then no further validation can take place
            return false;
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
                    return false;
                }
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return false;
                }
                d = context.dataField(ReferenceContainerDataFieldCall.get(field.getKey())).getRight();
                break;
            case SELECTION:
                SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                if(list.getType() != SelectionListType.REFERENCE) {
                    // Only REFERENCE type SELECTION fields can have children
                    return false;
                }
                reference = configuration.getReference(list.getReference());
                if(reference == null) {
                    // Reference not found, configuration error
                    return false;
                }
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return false;
                }
                d = context.dataField(ValueDataFieldCall.get(field.getKey())).getRight();
                break;
            case REFERENCE:
                reference = configuration.getReference(field.getReference());
                if(!(reference.getType() == ReferenceType.REVISION || reference.getType() == ReferenceType.REVISIONABLE)) {
                    // Context changing should only happen on actual REVISION or REVISIONABLE reference field.
                    // TODO: There might be some corner case where dependency field is also a field that can have children but it can be solved when that happens
                    return false;
                }
                if(reference == null) {
                    // Reference not found, configuration error
                    return false;
                }
                d = context.dataField(ValueDataFieldCall.get(field.getKey())).getRight();
                break;
            default:
                // Field can not have children and so no context switching can take place. Configuration mistake
                return false;
        }

        if(d == null) {
            // No field data found, from the point of CHILDREN target the validation is a success
            // since no context switching can take place and all further calls would be made to an empty context.
            // The point of CHILDREN target is not to require children but just to move validation down a level.
            // If chidlren are required then a separate NOT_EMPTY validation has to be made.
            return true;
        }

        // Check for the existence of children
        // TODO: How to define languages for context change?
        switch(field.getType()) {
            case CONTAINER:
                if(!((ContainerDataField)d).hasRows()) {
                    // No chidlren, can't change context. Everything in this target is valid
                    return true;
                } else {
                    return changeContext((ContainerDataField)d, t, validator, configuration);
                }
            case REFERENCECONTAINER:
                if(!((ReferenceContainerDataField)d).hasRows()) {
                    // No chidlren, can't change context. Everything in this target is valid
                    return true;
                } else {
                    if(reference == null) {
                        // This should have been found earlier
                        return false;
                    }
                    return changeContext((ReferenceContainerDataField)d, t, reference, context, configuration, revisions, validator, configurations);
                }
            default:
                if(!((ValueDataField)d).hasAnyValue()) {
                    return false;
                } else {
                    if(reference == null) {
                        // This should have been found earlier
                        return false;
                    }
                    return changeContext((ValueDataField)d, t, reference, context, configuration, revisions, validator, configurations);
                }
        }
    }

    private static boolean changeContext(ContainerDataField d, Target t, DataFieldValidator validator, Configuration configuration) {
        // TODO: For now validates all languages, fix this when language support is added to validation
        for(Language l : Language.values()) {
            if(!d.hasRowsFor(l)) {
                continue;
            }
            for(DataRow row : d.getRowsFor(l)) {
                if(row.getRemoved()) {
                    continue;
                }
                if(!validator.validate(t.getTargets(), row, configuration)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean changeContext(ReferenceContainerDataField d, Target t, Reference reference, DataFieldContainer context, Configuration configuration
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

                if(!changeContext(row.getActualValue(), t, context, validator, reference, revisions, configurations)) {
                    // Failed validation
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean changeContext(ValueDataField d, Target t, Reference reference, DataFieldContainer context, Configuration configuration
            , RevisionRepository revisions, DataFieldValidator validator, ConfigurationRepository configurations) {
        Field field = configuration.getField(d.getKey());
        for(Language l : Language.values()) {
            if(!d.hasValueFor(l)) {
                continue;
            }
            if(!changeContext(d.getActualValueFor(l), t, context, validator, reference, revisions, configurations)) {
                // Failed validation
                return false;
            }
        }
        return true;
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

    private static boolean changeContext(String value, Target t, DataFieldContainer context, DataFieldValidator validator, Reference reference
            , RevisionRepository revisions, ConfigurationRepository configurations) {
        Pair<ReturnResult, RevisionData> pair = fetchRevisionData(reference, value, revisions);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // If we don't find the revision data then return true by default, existence should have been checked earlier if this is required
            return true;
        }

        Pair<ReturnResult, Configuration> confPair = configurations.findConfiguration(pair.getRight().getConfiguration());
        if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            // We've found a revision but can't find configuration for it, we can't accept this as a success
            Logger.error(FieldTargetHandler.class, "Could not find configuration for revision "+pair.getRight().toString());
            return false;
        }

        RevisionData rev = pair.getRight();
        rev.initParents(context);
        return validator.validate(t.getTargets(), rev, confPair.getRight());
    }
}