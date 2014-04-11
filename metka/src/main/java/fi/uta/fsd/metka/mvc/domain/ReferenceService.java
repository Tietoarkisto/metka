package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.collecting.ReferenceCollecting;
import fi.uta.fsd.metka.data.enums.ChoicelistType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.ReferenceRepository;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.mvc.domain.requests.ReferenceOptionsRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains communication pertaining to Reference objects
 */
@Service
public class ReferenceService {

    @Autowired
    private ReferenceCollecting references;

    @Autowired
    private ConfigurationRepository configurations;

    public List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request) throws IOException {

        Configuration config = configurations.findConfiguration(request.getConfType(), request.getConfVersion());
        if(config == null) {
            return null;
        }
        Field field = config.getField(request.getKey());
        // Add types as needed, default is to return null if type can not contain a reference
        Reference reference = null;
        switch(field.getType()) {
            case REFERENCE:
            case REFERENCECONTAINER:
                reference = config.getReference(field.getReference());
                break;
            case CHOICE:
                Choicelist list = config.getRootChoicelist(field.getChoicelist());
                if(list == null || list.getType() != ChoicelistType.REFERENCE) {
                    return null;
                }
                reference = config.getReference(list.getReference());
                break;
            default:
                break;
        }

        return references.referenceOptionCollecting(reference, field, config, request);

    }
}
