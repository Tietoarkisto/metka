package fi.uta.fsd.metka.storage.collecting_old;


import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
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
@Deprecated
public class ReferenceCollecting {
    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionableReferenceHandler revisionableHandler;
    @Autowired
    private JsonReferenceHandler jsonHandler;
    @Autowired
    private DependencyReferenceHandler dependencyHandler;

    /**
     * This analysis a given request and provides relevant options.
     *
     * TODO: This contains a huge deal of functionality that will be implemented as it is needed and so the method signature will change over time.
     * @param reference Reference of the field currently being analyzed
     * @param field Field containing the reference
     * @param request Request containing all required information to provide reference options
     * @return List of ReferenceOption objects (size >= 0) defining values and titles (if reference doesn't define a title then value is copied to title).
     */
    public List<ReferenceOption> referenceOptionCollecting(Reference reference, Field field, Configuration config, ReferenceOptionsRequest request) {
        List<ReferenceOption> options = new ArrayList<>();
        if(reference == null) {
            Logger.warning(ReferenceCollecting.class, "Reference option collecting_old request made with null reference");
            // Return the empty list since we can not find values for a non existing reference
            return options;
        }

        // It's assumed that given field configuration is correct for given reference.

        // Distinguish between reference types and forward the request to separate handlers.
        switch(reference.getType()) {
            case REVISIONABLE:
                revisionableHandler.collectOptions(reference, options);
                break;
            case JSON:
                jsonHandler.collectOptions(reference, options);
                break;
            case DEPENDENCY:
                dependencyHandler.collectOptions(field, reference, config, request.getFieldValues().get(reference.getTarget()), options);
                break;
        }

        return options;
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
            Logger.error(ReferenceCollecting.class, "Configuration was not found for revision " + dataPair.getRight().toString() + " with result " + configPair.getLeft());
            return new ImmutablePair<>(configPair.getLeft(), null);
        }

        RevisionData data = dataPair.getRight();
        Configuration config = configPair.getRight();

        String[] path = request.getPath().split(".");
        if(path.length == 0) {
            path = new String[]{request.getPath()};
        }

        Field field = config.getField(path[path.length-1]);
        if(field == null || field.getType() != FieldType.REFERENCECONTAINER) {
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        // TODO: recursion, for now checks only top level fields

        Pair<StatusCode, ReferenceContainerDataField> fieldPair = data.dataField(ReferenceContainerDataFieldCall.get(path[path.length-1]).setConfiguration(config));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || fieldPair.getRight().getReferences().isEmpty()) {
            return new ImmutablePair<>(ReturnResult.REFERENCE_MISSING, null);
        }

        Pair<StatusCode, ReferenceRow> rowPair = fieldPair.getRight().getReferenceWithValue(request.getReference());
        if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
            return new ImmutablePair<>(ReturnResult.REFERENCE_MISSING, null);
        }

        return new ImmutablePair<>(ReturnResult.REFERENCE_FOUND, rowPair.getRight());
    }
}
