package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;

class NamedTargetHandler {

    /**
     * Fetches the named Target from configuration. If it is found copies it, sets the copy's parent to target's parent
     * and calls validateTarget with the new target. So simply replaces the NAMED target with a 'template' and handles it like it was always present.
     * @param target    NAMED type Target
     * @return Boolean if the Target validates to true
     */
    static boolean handle(Target target, Configuration configuration, DataFieldContainer context, RevisionValidator validator) {
        Target named = configuration.getNamedTargets().get(target.getContent());
        if(named == null) {
            return false;
        }
        named = named.copy();
        named.setParent(target.getParent());
        named.initParents();
        return validator.validateTarget(named, context);
    }
}
