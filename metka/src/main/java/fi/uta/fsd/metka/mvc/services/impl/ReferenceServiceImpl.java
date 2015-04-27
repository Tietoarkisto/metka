package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.enums.SelectionListType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.collecting.ReferenceCollector;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.reference.*;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Contains communication pertaining to Reference objects
 */
@Service
public class ReferenceServiceImpl implements ReferenceService {
    private static Logger logger = LoggerFactory.getLogger(ReferenceService.class);

    @Autowired
    private ReferenceCollector references;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    /**
     * Returns the current option depicting the value and title of given reference.
     * This is mainly used for indexing to get the actual text that needs to be indexed.
     * This works only for reference fields and reference selections, reference containers
     * are handled differently.
     * @param data
     * @param path
     * @return
     */
    @Override public ReferenceOption getCurrentFieldOption(Language language, RevisionData data, String path) {
        // TODO: Gather dependencies, form request and return single result

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Couldn't find configuration for "+data.toString());
            return null;
        }
        Configuration config = configPair.getRight();
        String[] splits = path.split("\\.");
        if(splits.length == 0) {
            splits = new String[1];
            splits[0] = path;
        }
        // Check that the final path element points to a field that can be a reference with a value, deal with reference containers separately with a different call
        Field field = config.getField(splits[splits.length-1]);
        if(field == null) {
            return null;
        }
        if(!(field.getType() == FieldType.REFERENCE || field.getType() == FieldType.SELECTION)) {
            return null;
        }
        if(field.getType() == FieldType.SELECTION) {
            SelectionList list = config.getRootSelectionList(field.getSelectionList());
            if(list.getType() != SelectionListType.REFERENCE) {
                return null;
            }
        }

        ReferenceOptionsRequest request = null;

        List<String> dependencyStack = formDependencyStack(field, config);

        // Let's form a request that we can use to fetch a reference option
        request = formReferenceOptionsRequest(language, splits, dependencyStack, data, config);

        // Perform the request
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();

        // Return first option or null if no options were found
        return options.isEmpty() ? null : options.get(0);
    }

    @Override public ReferenceOption getCurrentFieldOption(ReferencePathRequest request) {
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();
        return options.isEmpty() ? null : options.get(0);
    }

    @Override public List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request) {
        Pair<ReturnResult, List<ReferenceOption>> optionsPair = references.handleReferenceRequest(request);
        return optionsPair.getRight();
    }

    @Override
    public List<ReferenceOption> collectReferenceOptions(ReferencePathRequest request) {
        Pair<ReturnResult, List<ReferenceOption>> optionsPair = references.handleReferenceRequest(request);
        return optionsPair.getRight();
    }

    @Override public ReferenceRowResponse getReferenceRow(ReferenceRowRequest request) {
        ReferenceRowResponse response = new ReferenceRowResponse();
        Pair<ReturnResult, ReferenceRow> pair = references.getReferenceRow(request);

        response.setResult(pair.getLeft());
        if(pair.getLeft() == ReturnResult.REFERENCE_FOUND) {
            response.setRow(TransferRow.buildFromContainerRow(pair.getRight()));
        }

        return response;
    }

    @Override
    public ReferenceStatusResponse getReferenceStatus(Long id) {
        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(id);
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ReferenceStatusResponse(ReturnResult.REVISIONABLE_NOT_FOUND.name(), false, null, null, null);
        }

        return getReferenceStatus(id, info.getRight().getCurrent(), info);
    }

    @Override
    public ReferenceStatusResponse getReferenceStatus(Long id, Integer no) {
        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(id);
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ReferenceStatusResponse(ReturnResult.REVISIONABLE_NOT_FOUND.name(), false, null, null, null);
        }

        return getReferenceStatus(id, no, info);
    }

    private ReferenceStatusResponse getReferenceStatus(Long id, Integer no, Pair<ReturnResult, RevisionableInfo> info) {
        if(id == null || no == null || info == null) {
            return new ReferenceStatusResponse(ReturnResult.PARAMETERS_MISSING.name(), false, null, null, null);
        }

        DateTimeUserPair removed = null;
        DateTimeUserPair saved = null;
        Map<Language, ApproveInfo> approved = null;

        if(info.getRight().getRemoved()) {
            removed = new DateTimeUserPair(info.getRight().getRemovedAt(), info.getRight().getRemovedBy());
        }

        Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(id, no);
        if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
            return new ReferenceStatusResponse(ReturnResult.REVISION_FOUND.name(), true, removed, revPair.getRight().getSaved(), revPair.getRight().getApproved());
        } else {
            return new ReferenceStatusResponse(ReturnResult.REVISION_NOT_FOUND.name(), true, removed, null, null);
        }
    }

    private List<String> formDependencyStack(Field field, Configuration config) {
        List<String> stack = new ArrayList<>();
        Reference reference;

        do {
            if(field == null) {
                break;
            }

            // If the field is not writable then there's no point in adding it to the stack. It will either be an end point or function as a forwarder
            if(field.getWritable()) {
                stack.add(field.getKey());
            }
            reference = getFieldReference(field, config);
            if(reference != null) {
                if(reference.getType() == ReferenceType.DEPENDENCY) {
                    field = config.getField(reference.getTarget());
                } else {
                    reference = null;
                }
            }
        } while(reference != null);

        return stack;
    }

    private Reference getFieldReference(Field field, Configuration config) {
        if(field == null) {
            return null;
        }
        Reference reference = null;
        if(field.getType() == FieldType.REFERENCE || field.getType() == FieldType.REFERENCECONTAINER) {
            reference = config.getReference(field.getReference());
        } else if(field.getType() == FieldType.SELECTION) {
            SelectionList list = config.getRootSelectionList(field.getSelectionList());
            if(list.getType() == SelectionListType.REFERENCE) {
                reference = config.getReference(list.getReference());
            }
        }
        return reference;
    }

    private ReferenceOptionsRequest formReferenceOptionsRequest(Language language, String[] path, List<String> stack, RevisionData data, Configuration config) {
        ReferenceOptionsRequest request = new ReferenceOptionsRequest();
        request.setLanguage(language);
        request.setConfType(data.getConfiguration().getType().toValue());
        request.setConfVersion(data.getConfiguration().getVersion());
        request.setKey(path[path.length-1]);
        List<String> pathList = new ArrayList<>();
        Collections.addAll(pathList, path);
        parsePath(request, pathList, stack, data.getFields(), config);

        return request;
    }

    private void parsePath(ReferenceOptionsRequest request, List<String> path, List<String> stack, Map<String, DataField> fieldMap, Configuration config) {
        if(path.isEmpty() || stack.isEmpty()) {
            return;
        }
        DataField field = fieldMap.get(path.get(0));
        if(field != null && field instanceof ContainerDataField) {
            path.remove(0);
            if(!path.isEmpty()) {
                Integer rowId = Integer.parseInt(path.get(0));
                path.remove(0);
                if(!path.isEmpty()) {
                    // Path doesn't terminate so let's try to continue
                    ContainerDataField container = (ContainerDataField) field;
                    DataRow row = container.getRowWithId(rowId).getRight();
                    if (row != null && !row.getFields().isEmpty()) {
                        parsePath(request, path, stack, row.getFields(), config);
                    }
                }
            }
        } else if(field != null && field instanceof ReferenceContainerDataField) {
            path.remove(0);
            if(!path.isEmpty()) {
                Integer rowId = Integer.parseInt(path.get(0));
                path.remove(0);
                ReferenceContainerDataField ref = (ReferenceContainerDataField)field;
                ReferenceRow row = ref.getReferenceWithId(rowId).getRight();
                if(row != null && stack.get(0).equals(ref.getKey())) {
                    request.getFieldValues().put(ref.getKey(), row.getReference().getValue());
                    stack.remove(0);
                }
            }
        }
        while(!stack.isEmpty()) {
            String key = stack.get(0);
            field = fieldMap.get(key);
            if(field != null) {
                if(field instanceof ValueDataField) {
                    stack.remove(0);
                    Field fieldConf = config.getField(field.getKey());
                    Language l = fieldConf.getTranslatable() ? request.getLanguage() : Language.DEFAULT;
                    request.getFieldValues().put(key, ((ValueDataField)field).getActualValueFor(l));
                } else {
                    // In this case we will naturally fail since the same key can't be found on earlier steps in the hierarchy
                    // This is however what should happen since if we found a nonsensical situation and there's no point in continuing.
                    return;
                }
            } else {
                return;
            }
        }
    }
}
