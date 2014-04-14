package fi.uta.fsd.metka.data.collecting;


import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionsRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReferenceCollecting {
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
    public List<ReferenceOption> referenceOptionCollecting(Reference reference, Field field, Configuration config, ReferenceOptionsRequest request)
            throws IOException {
        List<ReferenceOption> options = new ArrayList<>();
        if(reference == null) {
            // TODO: Possibly needs to log an event since something has lead to a request that should not have been made
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
                dependencyHandler.collectOptions(field, reference, config, request.getDependencyValue(), options);
                break;
        }

        return options;
    }
}
