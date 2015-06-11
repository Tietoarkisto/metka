package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Handles PARENT typed targets.
 * Context is moved to one level higher and validation is continued with the subtargets of the PARENT type target.
 */
class ParentTargetCascader {

    static boolean cascade(CascadeInstruction instruction, Target t, DataFieldContainer context,
            Configuration configuration, Cascader.RepositoryHolder repositories) {
        if(context.getParent() == null) {
            // Configuration error, return false
            return false;
        }

        // We have to check whether we're crossing a reference line, if so then get the old configuration.
        RevisionKey old = context.getRevisionKey();
        context = context.getParent();
        if(!context.getRevisionKey().equals(old)) {
            Pair<ReturnResult, Configuration> confPair = repositories.getConfigurations().findConfiguration(context.getConfigurationKey());
            if(confPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                // We've found a revision but can't find configuration for it, we can't accept this as a success
                Logger.error(ParentTargetCascader.class, "Could not find configuration for revision " + context.getRevisionKey().toString());
                return false;
            }
            configuration = confPair.getRight();
        }

        return DataFieldCascader.cascade(instruction, t.getTargets(), context, configuration, repositories);
    }
}