package fi.uta.fsd.metka.storage.collecting_old;

import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: Actual language handling, for now only collects for DEFAULT language
@Service
@Deprecated
class RevisionableReferenceHandler extends ReferenceHandler {
    private static Logger logger = LoggerFactory.getLogger(RevisionableReferenceHandler.class);

    @Autowired
    private RevisionRepository revisions;

    /**
     * Analyses a revisionable reference and collects the values defined by that reference.
     *
     * TODO: At the moment handles only titlePaths of top level non container fields. When others are handled it should be made sure that the path actually terminates on a value, not an object of collection
     * @param reference Reference to be processed
     * @param options List where found values are placed as ReferenceOption objects
     */
    void collectOptions(Reference reference, List<ReferenceOption> options) {

        /*List<RevisionableEntity> entities = repository.getRevisionablesForReference(reference);
        if(entities == null) {
            return;
        }

        for(RevisionableEntity entity : entities) {
            if(reference.getApprovedOnly() && entity.getCurApprovedNo() == null) {
                // No approved revision, not applicable.
                continue;
            }

            ReferenceOptionTitle title = null;
            if(StringUtils.hasText(reference.getTitlePath())) {
                // Title is requested
                Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(entity.getId(), reference.getApprovedOnly(), null);
                if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    continue;
                }
                Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(dataPair.getRight().getConfiguration());
                if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                    logger.error("Couldn't find configuration for "+dataPair.getRight().toString());
                    continue;
                }
                RevisionData data = dataPair.getRight();
                Configuration config = configPair.getRight();
                // TODO: Fetch value based on path, not just assumption that it's a top level field
                ValueDataField saved = data.dataField(ValueDataFieldCall.get(reference.getTitlePath())).getRight();
                if(saved != null) {
                    TranslationObject to = new TranslationObject();
                    to.getTexts().put(Language.DEFAULT.toValue(), saved.getActualValueFor(Language.DEFAULT));
                    if(config.getField(reference.getTitlePath()).getType() == FieldType.SELECTION) {
                        // TODO: Fix if different languages are needed
                        title = new ReferenceOptionTitle(ReferenceTitleType.VALUE, to);
                    } else {
                        title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to);
                    }
                }
            }
            if(title == null) {
                TranslationObject to = new TranslationObject();
                to.getTexts().put(Language.DEFAULT.toValue(), entity.getId().toString());
                title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to);
            }
            options.add(new ReferenceOption(entity.getId().toString(), title));
        }*/
    }
}
