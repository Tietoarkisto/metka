package fi.uta.fsd.metka.ddi.builder;

import codebook25.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

class DDIWriteDataDescription extends DDIWriteSectionBase {
    DDIWriteDataDescription(RevisionData revision, Language language, CodeBookType codeBook, Configuration configuration, RevisionRepository revisions, ReferenceService references) {
        super(revision, language, codeBook, configuration, revisions, references);
    }

    void write() {
        Pair<StatusCode, ValueDataField> valueFieldPair = revision.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(valueFieldPair.getLeft() != StatusCode.FIELD_FOUND || !valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            return;
        }

        // Get variables data for given language
        Pair<ReturnResult, RevisionData> revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(language).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(getClass(),
                    "Couldn't find expected variables revision with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
            return;
        }
        RevisionData variables = revisionDataPair.getRight();

        // Add data description
        DataDscrType dataDscrType = codeBook.addNewDataDscr();

        setVariableGroups(variables, dataDscrType);

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILEID));
        String fileID = (valueFieldPair.getLeft() == StatusCode.FIELD_FOUND) ? valueFieldPair.getRight().getActualValueFor(Language.DEFAULT) : "";

        Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && referenceContainerPair.getRight().hasValidRows()) {
            for(ReferenceRow reference : referenceContainerPair.getRight().getReferences()) {
                if(reference.getRemoved()) {
                     continue;
                }
                revisionDataPair = revisions.getLatestRevisionForIdAndType(reference.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(getClass(), "Referenced study variable with id: " + reference.getReference().asInteger() + " could not be found with result " + revisionDataPair.getLeft());
                    continue;
                }
                RevisionData variable = revisionDataPair.getRight();

                VarType var = dataDscrType.addNewVar();
                setVar(variable, var, fileID);
            }
        }
    }

    private void setVariableGroups(RevisionData variables, DataDscrType dataDscrType) {
        Pair<ReturnResult, RevisionData> revisionDataPair;Pair<StatusCode, ContainerDataField> containerPair = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS));
        ContainerDataField vargroups = containerPair.getRight();
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && vargroups.hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VARGROUPTITLE));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    //VarGrpType varGrpType = fillTextType(dataDscrType.addNewVarGrp(), valueFieldPair, Language.DEFAULT);
                    VarGrpType varGrpType = dataDscrType.addNewVarGrp();

                    // TODO: do we want variable names or variable id:s?
                    Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = row.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS));
                    if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && referenceContainerPair.getRight().hasValidRows()) {
                        String vars = "";
                        for(ReferenceRow reference : referenceContainerPair.getRight().getReferences()) {
                            if(reference.getRemoved()) {
                                 continue;
                            }
                            revisionDataPair = revisions.getLatestRevisionForIdAndType(reference.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                            if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                                Logger.error(getClass(), "Referenced study variable with id: " + reference.getReference().asInteger() + " could not be found with result " + revisionDataPair.getLeft());
                                continue;
                            }
                            if(StringUtils.hasText(vars)) {
                                vars += " ";
                            }
                            valueFieldPair = revisionDataPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARNAME));
                            if(!hasValue(valueFieldPair, Language.DEFAULT)) {
                                vars += "-";
                            } else {
                                vars += valueFieldPair.getRight().getActualValueFor(Language.DEFAULT);
                            }
                        }
                        varGrpType.setVar(Arrays.asList(vars));
                    }

                    containerPair = row.dataField(ContainerDataFieldCall.get(Fields.VARGROUPTEXTS));
                    if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                        for(DataRow vartextrow : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                            if(vartextrow.getRemoved()) {
                                continue;
                            }
                            valueFieldPair = vartextrow.dataField(ValueDataFieldCall.get(Fields.VARGROUPTEXT));
                            if(hasValue(valueFieldPair, Language.DEFAULT)) {
                                fillTextType(varGrpType.addNewTxt(), valueFieldPair, Language.DEFAULT);
                            }

                        }
                    }
                }
            }
        }
    }

    private void setVar(RevisionData variable, VarType var, String fileID) {
        Pair<StatusCode, ValueDataField> valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            var.setID(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
        valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARNAME));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            var.setName(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
        var.setFiles(Arrays.asList(fileID));

        valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARINTERVAL));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            switch(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)) {
                case "contin":
                    var.setIntrvl(VarType.Intrvl.CONTIN);
                    break;
                case "discrete":
                    var.setIntrvl(VarType.Intrvl.DISCRETE);
                    break;
            }
        }

        valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARLABEL));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            fillTextType(var.addNewLabl(), valueFieldPair, Language.DEFAULT);
        }

        setVarSecurities(variable, var);

        setVarQstn(variable, var);

        setVarTexts(variable, var);

        setNotes(variable, var);

        setStatistics(variable, var);

        setCategories(variable, var);
    }

    private void setCategories(RevisionData variable, VarType var) {
        Pair<StatusCode, ContainerDataField> categories = variable.dataField(ContainerDataFieldCall.get(Fields.CATEGORIES));
        if(categories.getLeft() == StatusCode.FIELD_FOUND && categories.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : categories.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VALUE));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    CatgryType catgry = var.addNewCatgry();
                    fillTextType(catgry.addNewCatValu(), valueFieldPair, Language.DEFAULT);

                    valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.LABEL));
                    if(hasValue(valueFieldPair, Language.DEFAULT)) {
                        fillTextType(catgry.addNewLabl(), valueFieldPair, Language.DEFAULT);
                    }

                    valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.STAT));
                    if(hasValue(valueFieldPair, Language.DEFAULT)) {
                        fillTextType(catgry.addNewCatStat(), valueFieldPair, Language.DEFAULT);
                    }

                    valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.MISSING));
                    if(hasValue(valueFieldPair, Language.DEFAULT)) {
                        catgry.setMissing(CatgryType.Missing.Y);
                    }
                }
            }
        }
    }

    private void setStatistics(RevisionData variable, VarType var) {
        Pair<StatusCode, ContainerDataField> statistics = variable.dataField(ContainerDataFieldCall.get(Fields.STATISTICS));
        if(statistics.getLeft() == StatusCode.FIELD_FOUND && statistics.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : statistics.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.STATISTICSVALUE));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    SumStatType sumStat = fillTextType(var.addNewSumStat(), valueFieldPair, Language.DEFAULT);

                    valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.STATISTICSTYPE));
                    if(hasValue(valueFieldPair, Language.DEFAULT)) {
                        switch(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT)) {
                            case "vald":
                                sumStat.setType(SumStatType.Type.VALD);
                                break;
                            case "min":
                                sumStat.setType(SumStatType.Type.MIN);
                                break;
                            case "max":
                                sumStat.setType(SumStatType.Type.MAX);
                                break;
                            case "mean":
                                sumStat.setType(SumStatType.Type.MEAN);
                                break;
                            case "stdev":
                                sumStat.setType(SumStatType.Type.STDEV);
                                break;

                        }
                    }
                }
            }
        }
    }

    private void setVarSecurities(RevisionData variable, VarType var) {
        List<ValueDataField> fields = gatherFields(variable, Fields.VARSECURITIES, Fields.VARSECURITY);
        for(ValueDataField field : fields) {
            fillTextAndDateType(var.addNewSecurity(), field, Language.DEFAULT);
        }
    }

    private void setVarQstn(RevisionData variable, VarType var) {
        QstnType qstn = var.addNewQstn();

        List<ValueDataField> fields = gatherFields(variable, Fields.PREQTXTS, Fields.PREQTXT);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewPreQTxt(), field, Language.DEFAULT);
        }

        fields = gatherFields(variable, Fields.QSTNLITS, Fields.QSTNLIT);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewQstnLit(), field, Language.DEFAULT);
        }

        fields = gatherFields(variable, Fields.POSTQTXTS, Fields.POSTQTXT);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewPostQTxt(), field, Language.DEFAULT);
        }

        fields = gatherFields(variable, Fields.IVUINSTRS, Fields.IVUINSTR);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewIvuInstr(), field, Language.DEFAULT);
        }
    }

    private void setVarTexts(RevisionData variable, VarType var) {
        List<ValueDataField> fields = gatherFields(variable, Fields.VARTEXTS, Fields.VARTEXT);
        for(ValueDataField field : fields) {
            fillTextType(var.addNewTxt(), field, Language.DEFAULT);
        }
    }

    private void setNotes(RevisionData variable, VarType var) {
        List<ValueDataField> fields = gatherFields(variable, Fields.VARNOTES, Fields.VARNOTE);
        for(ValueDataField field : fields) {
            fillTextType(var.addNewNotes(), field, Language.DEFAULT);
        }
    }
}
