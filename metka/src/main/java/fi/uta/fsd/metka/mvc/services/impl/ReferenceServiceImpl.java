package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.SelectionListType;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.collecting.ReferenceCollecting;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.reference.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains communication pertaining to Reference objects
 */
@Service
public class ReferenceServiceImpl implements ReferenceService {
    private static Logger logger = LoggerFactory.getLogger(ReferenceService.class);

    @Autowired
    private ReferenceCollecting references;

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
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Couldn't find configuration for "+data.toString());
            return null;
        }
        String[] splits = path.split(".");
        if(splits.length == 0) {
            splits = new String[1];
            splits[0] = path;
        }
        Configuration config = configPair.getRight();
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

        // TODO: If the RevisionData is already approved then the text should be contained in the data itself once approval lock down is completed. Check for that value

        // We are certain that the field is a field with a reference and can contain a value, next check that if the field currently has a value
        // If the field doesn't have a value then it's unnecessary to check for option since there's no selected option in any case
        Pair<StatusCode, ValueDataField> pair = null;
        if(splits.length == 1) {
            // We already have the correct field, it's a top level field and it's either a reference of a selection
            // Perform the saved data field call
            if(field.getSubfield()) {
                return null;
            }
            pair = data.dataField(ValueDataFieldCall.get(field.getKey()).setConfiguration(config));
        } else {
            // Field should be in a container (since we're not dealing with reference containers here)
            if(!field.getSubfield()) {
                return null;
            }
            for(int i = 0; i < splits.length; i++) {
                String key = splits[i];
                // TODO: This is obviously not finished
            }
        }
        if(pair == null) {
            return null;
        }
        if(pair.getLeft() != StatusCode.FIELD_FOUND) {
            // Since no value was found we have nothing to index
            return null;
        }
        ValueDataField saved = pair.getRight();
        if(!saved.hasValueFor(language)) {
            // No value, don't care
            return null;
        }
        // Get the value from the saved data field, this is used to identify the correct option from returned options
        String value = saved.getActualValueFor(language);
        // Check if the reference is a dependency and if so then collect the dependency value for request
        String dependency = null;
        // TODO: get dependency value

        // We have value, we can search for options and select one.
        ReferenceOptionsRequest request = new ReferenceOptionsRequest();
        request.setKey(saved.getKey());
        request.setConfType(config.getKey().getType().toValue());
        request.setConfVersion(config.getKey().getVersion());
        request.setDependencyValue(dependency);
        List<ReferenceOption> options = collectReferenceOptions(request);
        for(ReferenceOption option : options) {
            if(option.getValue().equals(value)) {
                return option;
            }
        }
        // Didn't find value, so much for that
        return null;
    }

    @Override public List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request) {

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(request.getConfType(), request.getConfVersion());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Couldn't find configuration with type: "+request.getConfType()+" and version: "+request.getConfVersion());
            return null;
        }
        Configuration config = configPair.getRight();
        Field field = config.getField(request.getKey());
        // Add types as needed, default is to return null if type can not contain a reference
        Reference reference = null;
        switch(field.getType()) {
            case REFERENCE:
            case REFERENCECONTAINER:
                reference = config.getReference(field.getReference());
                break;
            case SELECTION:
                SelectionList list = config.getRootSelectionList(field.getSelectionList());
                if(list == null || list.getType() != SelectionListType.REFERENCE) {
                    return null;
                }
                reference = config.getReference(list.getReference());
                break;
            default:
                break;
        }

        return references.referenceOptionCollecting(reference, field, config, request);

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
            return new ReferenceStatusResponse(false, null);
        } else {
            if(info.getRight().getRemoved()) {
                return new ReferenceStatusResponse(true, new DateTimeUserPair(info.getRight().getRemovedAt(), info.getRight().getRemovedBy()));
            } else {
                return new ReferenceStatusResponse(true, null);
            }
        }
    }
}
