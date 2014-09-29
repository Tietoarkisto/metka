package fi.uta.fsd.metka.storage.collecting;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferencePath;
import fi.uta.fsd.metka.transfer.reference.ReferencePathRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionsRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceRowRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferenceCollector {
    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ReferencePathHandler pathHandler;

    public Pair<ReturnResult, List<ReferenceOption>> handleReferenceRequest(ReferenceOptionsRequest request) {
        List<ReferenceOption> options = new ArrayList<>();
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(ConfigurationType.fromValue(request.getConfType()), request.getConfVersion());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(ReferenceCollector.class, "Reference request made for configuration ["+request.getConfType()+","+request.getConfVersion()+"] that could not be found with result "+configPair.getLeft());
            return new ImmutablePair<>(configPair.getLeft(), options);
        }

        Configuration configuration = configPair.getRight();
        ReferencePath root = formReferencePath(request.getKey(), request, configuration, null);
        if(root != null && root.getReference() == null) {
            // Top value has no reference but has a provided value. This means that the whole stack will collapse to
            ReferenceOption option = new ReferenceOption(root.getValue(), new ReferenceOptionTitle(ReferenceTitleType.LITERAL, root.getValue()));
            options.add(option);
            return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, options);
        }

        return pathHandler.handleReferencePath(root, options, request.getLanguage());
    }

    public Pair<ReturnResult, List<ReferenceOption>> handleReferenceRequest(ReferencePathRequest request) {
        List<ReferenceOption> options = new ArrayList<>();
        return pathHandler.handleReferencePath(request.getRoot(), options, request.getLanguage());
    }

    public Pair<ReturnResult, ReferenceRow> getReferenceRow(ReferenceRowRequest request) {
        if(!StringUtils.hasText(request.getPath())) {
            return new ImmutablePair<>(ReturnResult.PARAMETERS_MISSING, null);
        }

        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionDataOfType(request.getId(), request.getNo(), request.getType());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(dataPair.getLeft(), null);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(dataPair.getRight().getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            Logger.error(ReferenceCollector.class, "Configuration was not found for revision " + dataPair.getRight().toString() + " with result " + configPair.getLeft());
            return new ImmutablePair<>(configPair.getLeft(), null);
        }

        RevisionData data = dataPair.getRight();
        Configuration config = configPair.getRight();

        String[] path = request.getPath().split("\\.");
        if(path.length == 0) {
            path = new String[]{request.getPath()};
        }

        Field field = config.getField(path[path.length-1]);
        if(field == null || field.getType() != FieldType.REFERENCECONTAINER) {
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        // TODO: recursion, for now checks only top level fields

        Pair<StatusCode, ReferenceContainerDataField> fieldPair = data.dataField(ReferenceContainerDataFieldCall.get(path[path.length - 1]).setConfiguration(config));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || fieldPair.getRight().getReferences().isEmpty()) {
            return new ImmutablePair<>(ReturnResult.REFERENCE_MISSING, null);
        }

        Pair<StatusCode, ReferenceRow> rowPair = fieldPair.getRight().getReferenceWithValue(request.getReference());
        if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
            return new ImmutablePair<>(ReturnResult.REFERENCE_MISSING, null);
        }

        return new ImmutablePair<>(ReturnResult.REFERENCE_FOUND, rowPair.getRight());
    }

    private ReferencePath formReferencePath(String key, ReferenceOptionsRequest request, Configuration configuration, ReferencePath previous) {
        if(previous != null && !StringUtils.hasText(request.getFieldValues().get(key))) {
            return null;
        }
        Field field = configuration.getField(key);
        if(field == null) {
            Logger.error(ReferenceCollector.class, "Field with key ["+key+"] was referenced but could not be found from configuration "+configuration.toString());
            return null;
        }

        Reference reference = getReference(field, configuration);
        if(reference == null && !StringUtils.hasText(request.getFieldValues().get(key))) {
            Logger.error(ReferenceCollector.class, "Field "+field.toString()+" did not contain a reference or reference was not found from "+configuration.toString());
            return null;
        }

        ReferencePath path = new ReferencePath(reference, request.getFieldValues().get(key));
        if(previous != null) {
            // We have a previous value, make sure there's no circular reference
            ReferencePath current = previous;
            // Since IDEA didn't like this as a do - while loop let's do it this way instead
            while(true) {
                if(current.getReference().getKey().equals(path.getReference().getKey())) {
                    // We have circularity, return null;
                    return null;
                }
                if(current.getPrev() == null) {
                    break;
                }
                current = current.getPrev();
            }

            // No circularity, extend path
            path.setNext(previous);
            previous.setPrev(path);
        }
        if(reference != null && reference.getType() == ReferenceType.DEPENDENCY) {
            return formReferencePath(reference.getTarget(), request, configuration, path);
        } else {
            return path;
        }
    }

    private Reference getReference(Field field, Configuration configuration) {
        String referenceKey = null;
        Reference reference = null;
        switch(field.getType()) {
            case SELECTION:
                SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                if(list != null && list.getType() == SelectionListType.REFERENCE) {
                    referenceKey = list.getReference();
                }
                break;
            case REFERENCE:
            case REFERENCECONTAINER:
                referenceKey = field.getReference();
                break;
        }
        if(referenceKey != null) {
            reference = configuration.getReference(referenceKey);
        }
        return reference;
    }
}
