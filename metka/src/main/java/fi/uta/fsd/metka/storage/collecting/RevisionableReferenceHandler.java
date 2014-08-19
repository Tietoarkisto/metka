package fi.uta.fsd.metka.storage.collecting;

import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceTitleType;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionTitle;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
class RevisionableReferenceHandler extends ReferenceHandler {
    private static Logger logger = LoggerFactory.getLogger(RevisionableReferenceHandler.class);

    /**
     * Analyses a revisionable reference and collects the values defined by that reference.
     *
     * TODO: At the moment handles only titlePaths of top level non container fields. When others are handled it should be made sure that the path actually terminates on a value, not an object of collection
     * @param language Language for which these references are collected. This also determines to which language the titles are placed
     * @param reference Reference to be processed
     * @param options List where found values are placed as ReferenceOption objects
     */
    void collectOptions(Language language, Reference reference, List<ReferenceOption> options) {

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
            if(StringUtils.hasText(reference.getTitlePath())) {
                // Title is requested
                RevisionEntity revision = repository.getRevisionForReference(entity, reference);
                if(revision == null || !StringUtils.hasText(revision.getData())) {
                    // TODO: There's a data problem, log event
                    continue;
                }

                Pair<ReturnResult, RevisionData> dataPair = json.deserializeRevisionData(revision.getData());
                if(dataPair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                    logger.error("Failed at deserializing "+revision.toString());
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
                    to.getTexts().put(Language.DEFAULT.toValue(), "");
                    to.getTexts().put(language.toValue(), saved.getActualValueFor(language));
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
                to.getTexts().put(Language.DEFAULT.toValue(), "");
                to.getTexts().put(language.toValue(), entity.getId().toString());
                title = new ReferenceOptionTitle(ReferenceTitleType.LITERAL, to);
            }
            options.add(new ReferenceOption(entity.getId().toString(), title));
        }
    }
}
