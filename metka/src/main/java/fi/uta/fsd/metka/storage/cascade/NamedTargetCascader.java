package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;

class NamedTargetCascader {

    /**
     * Fetches the named Target from configuration. If it is found copies it, sets the copy's parent to target's parent
     * and calls cascade with the new target. So simply replaces the NAMED target with a 'template' and handles it like it was always present.
     * @param target    NAMED type Target
     * @return Boolean if the Target cascades successfully
     */
    static boolean cascade(CascadeInstruction instruction, Target target, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        Target named = configuration.getNamedTargets().get(target.getContent());
        if(named == null) {
            return false;
        }
        named = named.copy();
        named.initParents(target.getParent());
        return DataFieldCascader.cascade(instruction, named, context, configuration, repositories);
    }
}