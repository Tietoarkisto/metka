package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ChoicelistType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.ReferenceRepository;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.requests.ReferenceOptionsRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Contains communication pertaining to Reference objects
 */
@Service
public class ReferenceService {

    @Autowired
    private GeneralService general;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private ReferenceRepository references;


    public List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request) throws IOException {
        RevisionData data = general.getRevision(request.getId(), request.getRevision());
        if(data == null) {
            return null;
        }
        Configuration config = configurations.findConfiguration(data.getConfiguration());
        if(config == null) {
            return null;
        }
        Field field = config.getField(request.getKey());
        // Add types as needed, default is to return null if type can not contain a reference
        Reference reference = null;
        switch(field.getType()) {
            case REFERENCE:
            case REFERENCECONTAINER:
                reference = config.getReferences().get(field.getReference());
                break;
            case CHOICE:
                Choicelist list = config.getChoicelists().get(field.getChoicelist());
                if(list == null || list.getType() != ChoicelistType.REFERENCE) {
                    return null;
                }
                reference = config.getReferences().get(list.getReference());
                break;
            default:
                break;
        }
        if(reference == null) {
            return null;
        }
        return references.collectReferenceOptions(field, reference);

    }

    // TODO: Get this to work
    public void getReferenceFieldChoices() {
        // Most of this needs to be done in a repository

        // Needs revision key
        // Needs configuration key, can be gained from RevisionData
        // Needs field key

        // Get revision data using revision key (or id and revision)
        // Get configuration for the revision
        // Check that request is ok and proper for this situation.

        // For REVISIONABLE reference
        //  This one should be simple
        //  Should take type and possible title field as well as approvedOnly boolean as parameters
        //  Goes through all Revisionables with given type, then collects their id's as values and title field values as titles (this is restricted to noncontainer non subfield fields in the beginning)

        // For JSON reference
        //  Little more complicated
        //  Should take target as string, valuePath and titlePath.
        //  Fetch json with target as key
        //  Parse the path for valuePath
        //  Search json for object containing the terminating attribute in path
        //  For every object containing the terminating attribute
        //  Take valuePath as value and titlePath as title

    }
}
