package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;

/**
 * Factory related to study variables.
 * Provides initial data methods for study variables and a study variable
 */
public class VariablesFactory extends DataFactory {

    public Pair<ReturnResult, RevisionData> newStudyVariables(Long id, Integer no, Configuration configuration, String studyId, String fileId, String varfileid, String varfiletype) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLES) {
            Logger.error(getClass(), "Called StudyVariablesFactory with type " + configuration.getKey().getType() + " configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build(new LocalDateTime());

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(ValueDataFieldCall.set("study", new Value(studyId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("file", new Value(fileId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfileid", new Value(varfileid), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfiletype", new Value(varfiletype), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varfileno", new Value("F1"), Language.DEFAULT).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    public Pair<ReturnResult, RevisionData> newVariable(Long id, Integer no, Configuration configuration, String variablesId, String studyId, String varName, String varId) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY_VARIABLE) {
            Logger.error(getClass(), "Called StudyVariablesFactory with type "+configuration.getKey().getType()+" configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build(new LocalDateTime());

        RevisionData data = createDraftRevision(id, no, configuration.getKey());
        data.dataField(ValueDataFieldCall.set("variables", new Value(variablesId), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("study", new Value(studyId), Language.DEFAULT).setInfo(info));
        // Set varid field
        data.dataField(ValueDataFieldCall.set("varname", new Value(varName), Language.DEFAULT).setInfo(info));
        data.dataField(ValueDataFieldCall.set("varid", new Value(varId), Language.DEFAULT).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    /**
     * This checks all translatable fields in study variable for content of all languages and updates the TRANSLATIONS container to match with current data.
     * This does not save the given revision data so if no save happens after this then all of the information is lost
     * @param variable
     */
    public void checkVariableTranslations(RevisionData variable, DateTimeUserPair info) {
        if(variable.getConfiguration().getType() != ConfigurationType.STUDY_VARIABLE) {
            return;
        }
        Pair<StatusCode, ContainerDataField> translationsPair = variable.dataField(ContainerDataFieldCall.set(Fields.TRANSLATIONS));
        if(!(translationsPair.getLeft() == StatusCode.FIELD_FOUND || translationsPair.getLeft() == StatusCode.FIELD_INSERT)) {
            Logger.error(getClass(), "Could not get translations container for setting study variable translations. Result: "+translationsPair.getLeft());
            // Can't set translations since could not get translations container
            return;
        }
        ContainerDataField translations = translationsPair.getRight();
        for(Language l : Language.values()) {
            checkVariableForLanguage(translations, variable, l, info);
        }
    }

    private void checkVariableForLanguage(ContainerDataField translations, RevisionData variable, Language l, DateTimeUserPair info) {
        boolean hasContent = doesValueHaveTranslation(variable, Fields.VARLABEL, l);
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.QSTNLITS, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.PREQTXTS, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.POSTQTXTS, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.IVUINSTRS, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.VARNOTES, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.VARTEXTS, l);
        }
        if(!hasContent) {
            hasContent = doesContainerHaveTranslation(variable, Fields.VARSECURITIES, l);
        }
        if(!hasContent) {
            Pair<StatusCode, ContainerDataField> container = variable.dataField(ContainerDataFieldCall.get(Fields.VALUELABELS));
            if(container.getLeft() == StatusCode.FIELD_FOUND && container.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : container.getRight().getRowsFor(Language.DEFAULT)) {
                    if(row.getRemoved()) {
                        continue;
                    }
                    hasContent = doesValueHaveTranslation(row, Fields.LABEL, l);
                    if(hasContent) {
                        break;
                    }
                }
            }
        }
        if(!hasContent) {
            Pair<StatusCode, ContainerDataField> container = variable.dataField(ContainerDataFieldCall.get(Fields.CATEGORIES));
            if(container.getLeft() == StatusCode.FIELD_FOUND && container.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : container.getRight().getRowsFor(Language.DEFAULT)) {
                    if(row.getRemoved()) {
                        continue;
                    }
                    hasContent = doesValueHaveTranslation(row, Fields.LABEL, l);
                    if(hasContent) {
                        break;
                    }
                }
            }
        }
        if(hasContent) {
            // Let's just assume that this succeeds, if it doesn't then we can't really do much about it.
            translations.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.TRANSLATION, new Value(l.toValue()), variable.getChanges(), info);
        } else {
            // If we find a row then remove it.
            Pair<StatusCode, DataRow> row = translations.getRowWithFieldValue(Language.DEFAULT, Fields.TRANSLATION, new Value(l.toValue()));
            if(row.getLeft() == StatusCode.FOUND_ROW) {
                translations.removeRow(row.getRight().getRowId(), variable.getChanges(), info);
            }
        }
    }

    private boolean doesContainerHaveTranslation(DataFieldContainer revision, String key, Language l) {
            Pair<StatusCode, ContainerDataField> container = revision.dataField(ContainerDataFieldCall.get(key));
            if(container.getLeft() == StatusCode.FIELD_FOUND && container.getRight().hasRowsFor(l)) {
                for(DataRow row : container.getRight().getRowsFor(l)) {
                    if(!row.getRemoved()) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean doesValueHaveTranslation(DataFieldContainer revision, String key, Language l) {
            Pair<StatusCode, ValueDataField> value = revision.dataField(ValueDataFieldCall.get(key));
            return value.getLeft() == StatusCode.FIELD_FOUND && value.getRight().hasValueFor(l);
        }
}
