package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.restrictions.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.hibernate.annotations.Cascade;

import java.util.List;

class DataFieldCascader {
    private DataFieldCascader() {}

    static boolean cascade(CascadeInstruction instruction, List<Target> targets, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        boolean result = true;
        for(Target target : targets) {
            if(!cascade(instruction, target, context, configuration, repositories)) {
                result = false;
            }
        }
        return result;
    }

    static boolean cascade(CascadeInstruction instruction, Target target, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        switch(target.getType()) {
            case NAMED:
                return NamedTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case PARENT:
                return ParentTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case CHILDREN:
                return ChildrenTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case FIELD:
                // This is the only one that will actually cascade
                return FieldTargetCascader.cascade(instruction, target, context, configuration, repositories);
            default:
                // This catches types that are invalid for this point of cascade operation such as VALUE
                Logger.error(DataFieldCascader.class, "Reached TargetType " + target.getType());
                return false;

        }
    }
}