package fi.uta.fsd.metka.storage.collecting;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.ReferenceRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

// TODO: It should be possible to jump from revision to misc json but not the other way. So if one field references a revisionable and a field inside that references a JSON and a third field depends on that then the dependency should rebase itself to json instead of terminating
@Repository
public class ReferencePathHandler {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ReferenceRepository references;

    @Autowired
    private ConfigurationRepository configurations;

    public Pair<ReturnResult, List<ReferenceOption>> handleReferencePath(ReferencePath path, Configuration configuration,
                                                                         List<ReferenceOption> options, Language language) {
        if(path == null || configuration == null) {
            return new ImmutablePair<>(ReturnResult.PARAMETERS_MISSING, options);
        }
        // We want to start from the root
        while(path.getPrev() != null) {
            path = path.getPrev();
        }

        referencePathStep(path, configuration, options, language);

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, options);
    }

    // This is basically the first step which might lead to other steps or might cause options collecting depending on parameters
    private void referencePathStep(ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language) {
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Cuurrent step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }
        switch(step.getReference().getType()) {
            case DEPENDENCY:
                Logger.error(ReferencePathHandler.class, "First step reference was a DEPENDENCY. Can not proceed");
                return;
            case REVISIONABLE:
                handleRevisionableStep(step, configuration, options, language);
                break;
            case JSON:
                handleJsonStep(step, configuration, options, language);
                break;
        }
    }

    private void referencePathStep(Map<String, DataField> fieldMap, ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language) {
        // We should not arrive here if this is not a dependency step
        if(step.getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(ReferencePathHandler.class, "Tried to parse DEPENDENCY step with a reference that is not a DEPENDENCY");
            return;
        }
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Cuurrent step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }

        DataFieldPathParser parser = new DataFieldPathParser(fieldMap, step.getReference().getValuePathParts(), configuration, language);
        if(StringUtils.hasText(step.getValue())) {
            // Step has value, either continue on or add a single option
            fieldMap = parser.findRootObjectWithTerminatingValue(step.getValue());
            if(step.getNext() != null) {
                referencePathStep(fieldMap, step.getNext(), configuration, options, language);
            } else {
                ReferenceOption option = parser.getOption(fieldMap, step.getReference());
                if(option != null) {
                    options.add(option);
                }
            }
        } else {
            // Add all terminating values as options
            List<Map<String, DataField>> fieldMaps = parser.findTermini();
            for(Map<String, DataField> terminus : fieldMaps) {
                ReferenceOption option = parser.getOption(terminus, step.getReference());
                if(option != null) {
                    options.add(option);
                }
            }
        }
    }

    private void referencePathStep(JsonNode node, ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language) {
        // We should not arrive here if this is not a dependency step
        if(step.getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(ReferencePathHandler.class, "Tried to parse DEPENDENCY step with a reference that is not a DEPENDENCY");
            return;
        }
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(ReferencePathHandler.class, "Malformed path. Cuurrent step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }

        JsonPathParser parser = new JsonPathParser(node, step.getReference().getValuePathParts());
        if(StringUtils.hasText(step.getValue())) {
            // There's a value, we either add a single option or then we continue on with the next step
            node = parser.findRootObjectWithTerminatingValue(step.getValue());
            if(step.getNext() != null) {
                referencePathStep(node, step.getNext(), configuration, options, language);
            } else {
                ReferenceOption option = parser.getOption(node, step.getReference(), language);
                if(option != null) {
                    options.add(option);
                }
            }
        } else {
            // We add all possible options starting from the given JsonNode
            List<JsonNode> termini = parser.findTermini();
            for(JsonNode termNode : termini) {
                // Get node containing value, Has to be ValueNode due to JsonParser only returning objects containing terminating value node.
                ReferenceOption option = parser.getOption(termNode, step.getReference(), language);
                if(option != null) {
                    options.add(option);
                }
            }
        }

    }

    private void handleRevisionableStep(ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language) {
        if(StringUtils.hasText(step.getValue())) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(step.getValue()),
                    false, ConfigurationType.fromValue(step.getReference().getTarget()));
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(pair.getRight().getConfiguration());
                if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                    Logger.error(ReferencePathHandler.class, "Could not found configuration for "+pair.getRight().toString());
                    return;
                }
                if(step.getNext() == null) {
                    // Terminating step, gather option from revision data
                    DataFieldPathParser parser = new DataFieldPathParser(pair.getRight().getFields(),
                            step.getReference().getValuePathParts(), configPair.getRight(), language);
                    Map<String, DataField> fieldMap = parser.findRootObjectWithTerminatingValue(step.getValue());
                    ReferenceOption option = parser.getOption(fieldMap, step.getReference());
                    if(option != null) {
                        options.add(option);
                    }
                } else {
                    referencePathStep(pair.getRight().getFields(), step.getNext(), configPair.getRight(), options, language);
                }
            }
        } else {
            List<RevisionableEntity> revisionables = em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.type=:type", RevisionableEntity.class)
                    .setParameter("type", step.getReference().getTarget())
                    .getResultList();
            // Collect options
            for(RevisionableEntity revisionable : revisionables) {
                Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(revisionable.getId(),
                    false, ConfigurationType.fromValue(step.getReference().getTarget()));
                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    // Terminating step, gather option from revision data
                    Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(pair.getRight().getConfiguration());
                    if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                        Logger.error(ReferencePathHandler.class, "Could not found configuration for "+pair.getRight().toString());
                        continue;
                    }
                    DataFieldPathParser parser = new DataFieldPathParser(pair.getRight().getFields(),
                            step.getReference().getValuePathParts(), configPair.getRight(), language);
                    List<Map<String, DataField>> fieldMaps = parser.findTermini();
                    for(Map<String, DataField> fieldMap : fieldMaps) {
                        ReferenceOption option = parser.getOption(fieldMap, step.getReference());
                        if(option != null) {
                            options.add(option);
                        }
                    }
                }
            }
        }
    }

    private void handleJsonStep(ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language) {
        Pair<ReturnResult, JsonNode> nodePair = references.getMiscJson(step.getReference().getTarget());
        if(nodePair.getLeft() != ReturnResult.MISC_JSON_FOUND) {
            // No misc json, can't continue
            Logger.error(ReferencePathHandler.class, "No Misc JSON file found with key "+ step.getReference().getTarget());
            return;
        }

        String[] path = step.getReference().getValuePathParts();
        JsonPathParser pathParser = new JsonPathParser(nodePair.getRight().get("data"), path);

        if(StringUtils.hasText(step.getValue())) {
            JsonNode node = pathParser.findRootObjectWithTerminatingValue(step.getValue());
            if(node != null) {
                if(step.getNext() == null) {
                    // Get option from this node
                    ReferenceOption option = pathParser.getOption(node, step.getReference(), language);
                    options.add(option);
                } else {
                    // Path has not terminated, continue option collecting
                    referencePathStep(node, step.getNext(), configuration, options, language);
                }
            }
        } else {
            List<JsonNode> termini = pathParser.findTermini();
            for(JsonNode node : termini) {
                // Get node containing value, Has to be ValueNode due to JsonParser only returning objects containing terminating value node.
                ReferenceOption option = pathParser.getOption(node, step.getReference(), language);
                if(option != null) {
                    options.add(option);
                }
            }
        }
    }
}
