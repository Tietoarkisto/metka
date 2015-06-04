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
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
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

    public Pair<ReturnResult, List<ReferenceOption>> handleReferencePath(ReferencePath path, List<ReferenceOption> options, Language language, boolean returnFirst) {
        if(path == null) {
            return new ImmutablePair<>(ReturnResult.PARAMETERS_MISSING, options);
        }
        // We want to start from the root
        while(path.getPrev() != null) {
            path = path.getPrev();
        }

        referencePathStep(path, options, language, returnFirst);

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, options);
    }

    // This is basically the first step which might lead to other steps or might cause options collecting depending on parameters
    private void referencePathStep(ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(getClass(), "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(getClass(), "Malformed path. Current step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }
        switch(step.getReference().getType()) {
            case DEPENDENCY:
                Logger.error(getClass(), "First step reference was a DEPENDENCY. Can not proceed");
                return;
            case REVISIONABLE:
                handleRevisionableStep(step, options, language, returnFirst);
                break;
            case REVISION:
                handleRevisionStep(step, options, language, returnFirst);
                break;
            case JSON:
                handleJsonStep(step, options, language, returnFirst);
                break;
        }
    }

    private void referencePathStep(Map<String, DataField> fieldMap, ReferencePath step, Configuration configuration, List<ReferenceOption> options, Language language, boolean returnFirst) {
        // We should not arrive here if this is not a dependency step
        if(step.getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(getClass(), "Tried to parse DEPENDENCY step with a reference that is not a DEPENDENCY");
            return;
        }
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(getClass(), "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(getClass(), "Malformed path. Current step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }

        DataFieldPathParser parser = new DataFieldPathParser(fieldMap, step.getReference().getValuePathParts(), configuration, language);
        if(StringUtils.hasText(step.getValue())) {
            // Step has value, either continue on or add a single option
            fieldMap = parser.findRootObjectWithTerminatingValue(step.getValue());
            if(step.getNext() != null) {
                referencePathStep(fieldMap, step.getNext(), configuration, options, language, returnFirst);
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
                    if(returnFirst) {
                        break;
                    }
                }
            }
        }
    }

    private void referencePathStep(JsonNode node, ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        // We should not arrive here if this is not a dependency step
        if(step.getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(getClass(), "Tried to parse DEPENDENCY step with a reference that is not a DEPENDENCY");
            return;
        }
        if(!StringUtils.hasText(step.getValue()) && step.getNext() != null) {
            Logger.error(getClass(), "Malformed path. Since current step does not have a selected value there should be no following steps.");
            return;
        }
        if(StringUtils.hasText(step.getValue()) && step.getNext() != null && step.getNext().getReference().getType() != ReferenceType.DEPENDENCY) {
            Logger.error(getClass(), "Malformed path. Cuurrent step has a value and there's a next step but next step is not DEPENDENCY");
            return;
        }

        JsonPathParser parser = new JsonPathParser(node, step.getReference().getValuePathParts());
        if(StringUtils.hasText(step.getValue())) {
            // There's a value, we either add a single option or then we continue on with the next step
            node = parser.findRootObjectWithTerminatingValue(step.getValue());
            if(step.getNext() != null) {
                referencePathStep(node, step.getNext(), options, language, returnFirst);
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
                    if(returnFirst) {
                        break;
                    }
                }
            }
        }

    }

    private void handleRevisionableStep(ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        if(StringUtils.hasText(step.getValue())) {
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(step.getValue()));
            if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND || (infoPair.getRight().getRemoved() && step.getReference().getIgnoreRemoved())) {
                return;
            }
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(step.getValue()),
                    step.getReference().getApprovedOnly(), ConfigurationType.fromValue(step.getReference().getTarget()));
            if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                handleRevisionStep(pair.getRight(), step, options, language, returnFirst);
            }
        } else {
            List<RevisionableEntity> revisionables = em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.type=:type", RevisionableEntity.class)
                    .setParameter("type", step.getReference().getTarget())
                    .getResultList();
            // Collect options
            for(RevisionableEntity revisionable : revisionables) {
                if(revisionable.getRemoved() && step.getReference().getIgnoreRemoved()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(revisionable.getId(),
                    step.getReference().getApprovedOnly(), ConfigurationType.fromValue(step.getReference().getTarget()));
                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    handleRevisionStep(pair.getRight(), step, options, language, returnFirst);
                    if(returnFirst && options.size() > 0) {
                        break;
                    }
                }
            }
        }
    }

    private void handleRevisionStep(ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        if(StringUtils.hasText(step.getValue())) {
            String[] splits = step.getValue().split("-");

            if(splits == null || splits.length < 2) {
                // We have only revisionable id, handle just like revisionable reference
                Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(step.getValue()));
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND || (infoPair.getRight().getRemoved() && step.getReference().getIgnoreRemoved())) {
                    return;
                }
                Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(Long.parseLong(step.getValue()),
                        step.getReference().getApprovedOnly(), ConfigurationType.fromValue(step.getReference().getTarget()));
                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    handleRevisionStep(pair.getRight(), step, options, language, returnFirst);
                }
            } else {
                // We have both revisionable id and revision number, handle accordingly
                Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(splits[0]));
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND || (infoPair.getRight().getRemoved() && step.getReference().getIgnoreRemoved())) {
                    return;
                }
                Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(Long.parseLong(splits[0]), Integer.parseInt(splits[1]));
                if(pair.getLeft() == ReturnResult.REVISION_FOUND && pair.getRight().getConfiguration().getType() == ConfigurationType.fromValue(step.getReference().getTarget())) {
                    handleRevisionStep(pair.getRight(), step, options, language, returnFirst);
                }
            }
        } else {
            // TODO: Possibly allow collecting all revision options in the future, for now we don't need this
            return;
            /*List<RevisionableEntity> revisionables = em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.type=:type", RevisionableEntity.class)
                    .setParameter("type", step.getReference().getTarget())
                    .getResultList();
            // Collect options
            for(RevisionableEntity revisionable : revisionables) {
                if(revisionable.getRemoved() && step.getReference().getIgnoreRemoved()) {
                    continue;
                }
                Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(revisionable.getId(),
                        step.getReference().getApprovedOnly(), ConfigurationType.fromValue(step.getReference().getTarget()));
                if(pair.getLeft() == ReturnResult.REVISION_FOUND) {
                    handleRevisionStep(pair.getRight(), step, options, language, returnFirst);
                    if(returnFirst && options.size() > 0) {
                        break;
                    }
                }
            }*/
        }
    }

    private void handleRevisionStep(RevisionData data, ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        if(returnFirst && options.size() > 0) {
            // We already have something to return and only first was requested, get out
            return;
        }
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(getClass(), "Could not find configuration for "+data.toString());
            return;
        }
        if(step.getNext() == null) {
            // Terminating step, gather option from revision data
            DataFieldPathParser parser = new DataFieldPathParser(data.getFields(),
                    step.getReference().getValuePathParts(), configPair.getRight(), language);
            //Map<String, DataField> fieldMap = parser.findRootObjectWithTerminatingValue(step.getValue());
            ReferenceOption option = parser.getOption(data, step.getReference());
            if(option != null) {
                options.add(option);
            }
        } else {
            referencePathStep(data.getFields(), step.getNext(), configPair.getRight(), options, language, returnFirst);
        }
    }

    private void handleJsonStep(ReferencePath step, List<ReferenceOption> options, Language language, boolean returnFirst) {
        Pair<ReturnResult, JsonNode> nodePair = references.getMiscJson(step.getReference().getTarget());
        if(nodePair.getLeft() != ReturnResult.MISC_JSON_FOUND) {
            // No misc json, can't continue
            Logger.error(getClass(), "No Misc JSON file found with key "+ step.getReference().getTarget());
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
                    referencePathStep(node, step.getNext(), options, language, returnFirst);
                }
            }
        } else {
            List<JsonNode> termini = pathParser.findTermini();
            for(JsonNode node : termini) {
                // Get node containing value, Has to be ValueNode due to JsonParser only returning objects containing terminating value node.
                ReferenceOption option = pathParser.getOption(node, step.getReference(), language);
                if(option != null) {
                    options.add(option);
                    if(returnFirst) {
                        break;
                    }
                }
            }
        }
    }
}
