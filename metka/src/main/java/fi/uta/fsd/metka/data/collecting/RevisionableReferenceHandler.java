package fi.uta.fsd.metka.data.collecting;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.ReferenceTitleType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Service
class RevisionableReferenceHandler extends ReferenceHandler {

    /**
     * Analyses a revisionable reference and collects the values defined by that reference.
     *
     * TODO: At the moment handles only titlePaths of top level non container fields. When others are handled it should be made sure that the path actually terminates on a value, not an object of collection
     * @param reference Reference to be processed
     * @param options List where found values are placed as ReferenceOption objects
     */
    void collectOptions(Reference reference, List<ReferenceOption> options)
            throws IOException {

        List<RevisionableEntity> entities = repository.getRevisionablesForReference(reference);
        if(entities == null) {
            return;
        }

        for(RevisionableEntity entity : entities) {
            if(reference.getApprovedOnly() && entity.getCurApprovedNo() == null) {
                // No approved revision, not applicable.
                continue;
            }

            ReferenceOptionTitle title = null;
            if(!StringUtils.isEmpty(reference.getTitlePath())) {
                // Title is requested
                RevisionEntity revision = repository.getRevisionForReference(entity, reference);
                if(revision == null || StringUtils.isEmpty(revision.getData())) {
                    // TODO: There's a data problem, log event
                    continue;
                }

                RevisionData data = json.readRevisionDataFromString(revision.getData());
                Configuration config = configurations.findConfiguration(data.getConfiguration());
                // TODO: Fetch value based on path, not just assumption that it's a top level field
                SavedDataField saved = getSavedDataFieldFromRevisionData(data, reference.getTitlePath());
                if(saved != null) {
                    if(config.getField(reference.getTitlePath()).getType() == FieldType.CHOICE) {
                        title = new ReferenceOptionTitle(ReferenceTitleType.VALUE, saved.getActualValue());
                    } else {
                        title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL,saved.getActualValue());
                    }
                }
            }
            if(title == null) {
                title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, entity.getId().toString());
            }
            options.add(new ReferenceOption(entity.getId().toString(), title));
        }
    }
}
