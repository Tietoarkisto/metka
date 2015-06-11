package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
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
class ChildrenTargetCascader {

    static boolean cascade(CascadeInstruction instruction, Target t, DataFieldContainer context,
            Configuration configuration, Cascader.RepositoryHolder repositories) {
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
        // Cascader only supports CONTAINER type children. If we want to leave the current revision then the actual cascade operation should be performed,
        // cascade should not jump over revisionables
        switch (field.getType()) {
            case CONTAINER:
                d = context.dataField(ContainerDataFieldCall.get(field.getKey())).getRight();
                break;
            default:
                // Field can not have children and so no context switching can take place. Configuration mistake
                return false;
        }

        if(d == null) {
            // No field data found, from the point of CHILDREN target the cascade is a success
            // since no context switching can take place and all further calls would be made to an empty context.
            // The point of CHILDREN target is not to require children but just to move cascade down a level.
            return true;
        }

        // Check for the existence of children
        // TODO: How to define languages for context change?
        switch(field.getType()) {
            case CONTAINER:
                if(!((ContainerDataField)d).hasRows()) {
                    // No children, can't change context. Everything in this target is valid
                    return true;
                } else {
                    return changeContext(instruction, (ContainerDataField)d, t, configuration, repositories);
                }
        }
        return true;
    }

    private static boolean changeContext(CascadeInstruction instruction, ContainerDataField d, Target t, Configuration configuration,
            Cascader.RepositoryHolder repositories) {
        // TODO: For now cascade all languages, fix this when language support is added to cascade
        boolean result = true;
        for(Language l : Language.values()) {
            if(!d.hasRowsFor(l)) {
                continue;
            }
            for(DataRow row : d.getRowsFor(l)) {
                if(row.getRemoved()) {
                    continue;
                }
                if(!DataFieldCascader.cascade(instruction, t.getTargets(), row, configuration, repositories)) {
                    result = false;
                }
            }
        }
        return result;
    }
}
