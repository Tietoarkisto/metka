package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookType;
import codebook25.DataDscrType;
import codebook25.VarType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;

class DDIDataDescription {
    static void readDataDescription(RevisionData revisionData, Language language, CodeBookType codeBookType, RevisionRepository revisions) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = revisionData.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
        // This operation is so large that it's cleaner just to return than to wrap everything inside this one IF
        if(valueFieldPair.getLeft() != StatusCode.FIELD_FOUND || !valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            return;
        }

        // Get variables data since it contains most of the information needed for this. Some additional data is also needed from the actual file but very little.
        Pair<ReturnResult, RevisionData> revisionDataPair = revisions.getLatestRevisionForIdAndType(
                valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger(), false, ConfigurationType.STUDY_VARIABLES);
        if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            Logger.error(DDIDataDescription.class,
                    "Couldn't find expected variables revision with id: " + valueFieldPair.getRight().getValueFor(Language.DEFAULT).valueAsInteger());
            return;
        }
        RevisionData variables = revisionDataPair.getRight();

        // Add data description
        DataDscrType dataDscrType = codeBookType.addNewDataDscr();

        readVariableGroups(language, revisions, variables, dataDscrType);

        valueFieldPair = variables.dataField(ValueDataFieldCall.get(Fields.VARFILEID));
        String fileID = (valueFieldPair.getLeft() == StatusCode.FIELD_FOUND) ? valueFieldPair.getRight().getActualValueFor(Language.DEFAULT) : "";

        Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = variables.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES));
        if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
            for(ReferenceRow reference : referenceContainerPair.getRight().getReferences()) {
                if(reference.getRemoved()) {
                     continue;
                }
                revisionDataPair = revisions.getLatestRevisionForIdAndType(reference.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    Logger.error(DDIDataDescription.class, "Referenced study variable with id: " + reference.getReference().asInteger() + " could not be found with result " + revisionDataPair.getLeft());
                    continue;
                }
                RevisionData variable = revisionDataPair.getRight();
                Pair<StatusCode, ContainerDataField> containerPair = variable.dataField(ContainerDataFieldCall.get(Fields.TRANSLATIONS));
                if(containerPair.getLeft() != StatusCode.FIELD_FOUND || !containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                    continue;
                }
                boolean translated = false;
                for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                    if(row.getRemoved()) {
                        continue;
                    }
                    valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.TRANSLATION));
                    if(valueFieldPair.getLeft() != StatusCode.FIELD_FOUND || !valueFieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                        continue;
                    }
                    if(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT).equals(language.toValue())) {
                        translated = true;
                        break;
                    }
                }
                if(!translated) {
                    continue;
                }
                VarType var = dataDscrType.addNewVar();
                readVar(variable, language, var, fileID);
            }
        }*/
    }

    private static void readVariableGroups(Language language, RevisionRepository revisions, RevisionData variables, DataDscrType dataDscr) {
        /*Pair<ReturnResult, RevisionData> revisionDataPair;Pair<StatusCode, ContainerDataField> containerPair = variables.dataField(ContainerDataFieldCall.get(Fields.VARGROUPS));
        ContainerDataField vargroups = containerPair.getRight();
        if(containerPair.getLeft() == StatusCode.FIELD_FOUND && vargroups.hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : vargroups.getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VARGROUPTITLE));
                // TODO: Should variable group title be translatable?
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    VarGrpType varGrpType = fillTextType(dataDscrType.addNewVarGrp(), valueFieldPair, Language.DEFAULT);

                    // TODO: do we want variable names or variable id:s?
                    Pair<StatusCode, ReferenceContainerDataField> referenceContainerPair = row.dataField(ReferenceContainerDataFieldCall.get(Fields.VARGROUPVARS));
                    if(referenceContainerPair.getLeft() == StatusCode.FIELD_FOUND && !referenceContainerPair.getRight().getReferences().isEmpty()) {
                        String vars = "";
                        for(ReferenceRow reference : referenceContainerPair.getRight().getReferences()) {
                            if(reference.getRemoved()) {
                                 continue;
                            }
                            revisionDataPair = revisions.getLatestRevisionForIdAndType(reference.getReference().asInteger(), false, ConfigurationType.STUDY_VARIABLE);
                            if(revisionDataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                                Logger.error(DDIDataDescription.class, "Referenced study variable with id: " + reference.getReference().asInteger() + " could not be found with result " + revisionDataPair.getLeft());
                                continue;
                            }
                            if(StringUtils.hasText(vars)) {
                                vars += " ";
                            }
                            valueFieldPair = revisionDataPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARNAME));
                            if(!hasValue(valueFieldPair, language)) {
                                vars += "-";
                            } else {
                                vars += valueFieldPair.getRight().getActualValueFor(language);
                            }
                        }
                        varGrpType.setVar(Lists.asList(vars, new String[0]));
                    }

                    containerPair = row.dataField(ContainerDataFieldCall.get(Fields.VARGROUPTEXTS));
                    if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(language)) {
                        for(DataRow vartextrow : containerPair.getRight().getRowsFor(language)) {
                            if(vartextrow.getRemoved()) {
                                continue;
                            }
                            valueFieldPair = vartextrow.dataField(ValueDataFieldCall.get(Fields.VARGROUPTEXT));
                            if(hasValue(valueFieldPair, language)) {
                                fillTextType(varGrpType.addNewTxt(), valueFieldPair, language);
                            }

                        }
                    }
                }
            }
        }*/
    }

    private static void readVar(RevisionData variable, Language language, VarType var, String fileID) {
        // TODO: Reverse process
        /*Pair<StatusCode, ValueDataField> valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARID));
        if(hasValue(valueFieldPair, Language.DEFAULT)) {
            var.setID(valueFieldPair.getRight().getActualValueFor(Language.DEFAULT));
        }
        valueFieldPair = variable.dataField(ValueDataFieldCall.get(Fields.VARNAME));
        if(hasValue(valueFieldPair, language)) {
            var.setName(valueFieldPair.getRight().getActualValueFor(language));
        }
        var.setFiles(Lists.asList(fileID, new String[0]));

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
        if(hasValue(valueFieldPair, language)) {
            fillTextType(var.addNewLabl(), valueFieldPair, language);
        }

        readVarSecurities(variable, language, var);

        readVarQstn(variable, language, var);

        readVarTexts(variable, language, var);

        readNotes(variable, language, var);

        readStatistics(variable, var);

        readCategories(variable, var);*/
    }

    private static void readCategories(RevisionData variable, VarType var) {
        // TODO: Reverse process
        /*Pair<StatusCode, ContainerDataField> categories = variable.dataField(ContainerDataFieldCall.get(Fields.CATEGORIES));
        if(categories.getLeft() == StatusCode.FIELD_FOUND && categories.getRight().hasRowsFor(Language.DEFAULT)) {
            for(DataRow row : categories.getRight().getRowsFor(Language.DEFAULT)) {
                if(row.getRemoved()) {
                    continue;
                }
                Pair<StatusCode, ValueDataField> valueFieldPair = row.dataField(ValueDataFieldCall.get(Fields.VALUE));
                if(hasValue(valueFieldPair, Language.DEFAULT)) {
                    CatgryType catgry = var.addNewCatgry();
                    fillTextType(catgry.addNewCatValu(), valueFieldPair, Language.DEFAULT);

                    // TODO: Should cat labels be translatable?
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
        }*/
    }

    private static void readStatistics(RevisionData variable, VarType var) {
        // TODO: Reverse process
        /*Pair<StatusCode, ContainerDataField> statistics = variable.dataField(ContainerDataFieldCall.get(Fields.STATISTICS));
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
        }*/
    }

    private static void readVarSecurities(RevisionData variable, Language language, VarType var) {
        // TODO: Reverse process
        /*List<ValueDataField> fields = gatherFields(variable, Fields.VARSECURITIES, Fields.VARSECURITY, language, language);
        for(ValueDataField field : fields) {
            fillTextAndDateType(var.addNewSecurity(), field, language);
        }*/
    }

    private static void readVarQstn(RevisionData variable, Language language, VarType var) {
        // TODO: Reverse process
        /*// TODO: Do we want to use single qstn type with multiple texts for each container or do we want to have one qstn type per set of texts so that each qstn contains only one preq etc.?
        QstnType qstn = var.addNewQstn();

        List<ValueDataField> fields = gatherFields(variable, Fields.PREQTXTS, Fields.PREQTXT, language, language);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewPreQTxt(), field, language);
        }

        fields = gatherFields(variable, Fields.QSTNLITS, Fields.QSTNLIT, language, language);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewQstnLit(), field, language);
        }

        fields = gatherFields(variable, Fields.POSTQTXTS, Fields.POSTQTXT, language, language);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewPostQTxt(), field, language);
        }

        fields = gatherFields(variable, Fields.IVUINSTRS, Fields.IVUINSTR, language, language);
        for(ValueDataField field : fields) {
            fillTextType(qstn.addNewIvuInstr(), field, language);
        }*/
    }

    private static void readVarTexts(RevisionData variable, Language language, VarType var) {
        // TODO: Reverse process
        /*List<ValueDataField> fields = gatherFields(variable, Fields.VARTEXTS, Fields.VARTEXT, language, language);
        for(ValueDataField field : fields) {
            fillTextType(var.addNewTxt(), field, language);
        }*/
    }

    private static void readNotes(RevisionData variable, Language language, VarType var) {
        // TODO: Reverse process
        /*NotesType notes = var.addNewNotes();
        // TODO: Notes is not a repeatable field in DDI
        // TODO: How is this supposed to be used?
        */
    }
}
