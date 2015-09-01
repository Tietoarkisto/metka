/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.names.Lists;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains functionality related to RevisionData model and specifically to revision data related to Study.
 */
public class StudyFactory extends DataFactory {
    private static final String URN_BASE = "urn:nbn:fi:fsd:M-";

    private static Map<Language, String> version = new HashMap<>();
    private static Map<Language, String> compFile = new HashMap<>();
    private static Map<Language, String> collector = new HashMap<>();
    private static Map<Language, String> producer = new HashMap<>();
    private static Map<Language, String> distributor = new HashMap<>();

    static {

        version.put(Language.DEFAULT, "Versio");
        version.put(Language.EN, "Version");
        version.put(Language.SV, "Version");

        compFile.put(Language.DEFAULT, "[elektroninen aineisto]");
        compFile.put(Language.EN, "[computer file]");
        compFile.put(Language.SV, "[datafil]");

        collector.put(Language.DEFAULT, "[aineistonkeruu]");
        collector.put(Language.EN, "[data collection]");
        collector.put(Language.SV, "[datainsamling]");

        producer.put(Language.DEFAULT, "[tuottaja]");
        producer.put(Language.EN, "[producer]");
        producer.put(Language.SV, "[producent]");

        distributor.put(Language.DEFAULT, "[jakaja]");
        distributor.put(Language.EN, "[distributor]");
        distributor.put(Language.SV, "[distributör]");
    }

    /**
     * Construct a new RevisionData for STUDY.
     * Sets the following fields:
     *   studyid_prefix - Letter part of the generated studyid
     *   studyid_number - number part of the generated studyid
     *   studyid - concatenation of the previous two fields
     *   submissionid - number associated with the study and provided as part of the request
     *   dataarrivaldate - date associated with the study and provided as part of the request
     * @param id
     * @param no
     * @param configuration
     * @param studyNumber
     * @param submissionid
     * @param arrivalDate
     * @return
     */
    public Pair<ReturnResult, RevisionData> newData(Long id, Integer no, Configuration configuration, String studyNumber, String submissionid, String arrivalDate) {
        if(configuration.getKey().getType() != ConfigurationType.STUDY) {
            Logger.error(getClass(), "Called StudyFactory with type " + configuration.getKey().getType() + " configuration");
            return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        RevisionData data = createDraftRevision(id, no, configuration.getKey());

        SelectionList list;
        Field confField;

        // Create studyid field. studyid_prefix and studyid_number were redundant and were removed
        list = configuration.getRootSelectionList(Lists.ID_PREFIX_LIST);
        data.dataField(ValueDataFieldCall.set(Fields.STUDYID, new Value(list.getDef()+studyNumber), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // submissionid, this is required information for creating a new study
        data.dataField(ValueDataFieldCall.set("submissionid", new Value(submissionid), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        // Set dataarrivaldate
        data.dataField(ValueDataFieldCall.set("dataarrivaldate", new Value(arrivalDate), Language.DEFAULT).setConfiguration(configuration).setInfo(info));

        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, data);
    }

    public ReturnResult formUrnAndBiblCit(RevisionData data, DateTimeUserPair info, ReferenceService references, Configuration configuration, MutablePair<Boolean, Boolean> changesAndErrors) {
        // Form urns here so that they are correct when needed for biblcit
        formUrns(data, info, changesAndErrors);

        // Form biblCit for all languages if there is a title for that language.
        Pair<StatusCode, ValueDataField> titlePair = data.dataField(ValueDataFieldCall.get(Fields.TITLE));
        if(titlePair.getLeft() != StatusCode.FIELD_FOUND) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        ValueDataField title = titlePair.getRight();
        for(Language l : Language.values()) {
            if(!title.hasValueFor(l)) {
                continue;
            }
            String biblcit = "";

            // Person authors separated with ampersand
            String authors = "";
            Pair<StatusCode, ContainerDataField> containerPair = data.dataField(ContainerDataFieldCall.get(Fields.AUTHORS));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                    Pair<StatusCode, ValueDataField> typePair = row.dataField(ValueDataFieldCall.get(Fields.AUTHORTYPE));
                    if(typePair.getLeft() != StatusCode.FIELD_FOUND || !typePair.getRight().valueForEquals(Language.DEFAULT, "1")) {
                        // Type needs to be set and equal value "1" (person author)
                        continue;
                    }
                    Pair<StatusCode, ValueDataField> authorPair = row.dataField(ValueDataFieldCall.get(Fields.AUTHOR));
                    if(authorPair.getLeft() == StatusCode.FIELD_FOUND && authorPair.getRight().hasValueFor(Language.DEFAULT)) {
                        if(StringUtils.hasText(authors)) {
                            authors += " & ";
                        }
                        authors += authorPair.getRight().getActualValueFor(Language.DEFAULT);
                    }
                }
            }
            biblcit += authors;
            if(StringUtils.hasText(biblcit)) {
                biblcit += ": ";
            }

            // Title on language, append with compFile.get(language)
            biblcit += title.getActualValueFor(l);

            // dataversions.version+"."+descversions.version
            String ver = version.get(l)+" ";
            containerPair = data.dataField(ContainerDataFieldCall.get(Fields.DATAVERSIONS));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                DataRow row = containerPair.getRight().getRowsFor(Language.DEFAULT).get(containerPair.getRight().getRowsFor(Language.DEFAULT).size()-1);
                Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DATAVERSION));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.DEFAULT)) {
                    ver += versionPair.getRight().getActualValueFor(Language.DEFAULT);
                } else {
                    ver += "0.0";
                }
            } else {
                ver += "0.0";
            }

            ver += ".";

            containerPair = data.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(l)) {
                DataRow row = containerPair.getRight().getRowsFor(l).get(containerPair.getRight().getRowsFor(l).size()-1);
                Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(l)) {
                    ver += versionPair.getRight().getActualValueFor(l);
                } else {
                    ver += "0.0";
                }
            } else {
                ver += "0.0";
            }

            if(StringUtils.hasText(ver)) {
                if(StringUtils.hasText(biblcit)) {
                    biblcit += ". ";
                }

                biblcit += ver;
            }

            // first collector, append with collector.get(language)
            String colls = "";
            containerPair = data.dataField(ContainerDataFieldCall.get(Fields.COLLECTORS));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                    String coll = "";
                    Pair<StatusCode, ValueDataField> typePair = row.dataField(ValueDataFieldCall.get(Fields.AUTHORTYPE));
                    // Need type to continue
                    if(typePair.getLeft() == StatusCode.FIELD_FOUND && typePair.getRight().valueForEquals(Language.DEFAULT, "1")) {
                        // Person collector
                        Pair<StatusCode, ValueDataField> collectorPair = row.dataField(ValueDataFieldCall.get(Fields.COLLECTOR));
                        if(collectorPair.getLeft() == StatusCode.FIELD_FOUND && collectorPair.getRight().hasValueFor(Language.DEFAULT)) {
                            coll = collectorPair.getRight().getActualValueFor(Language.DEFAULT);
                        }
                    } else if(typePair.getLeft() == StatusCode.FIELD_FOUND && typePair.getRight().valueForEquals(Language.DEFAULT, "2")) {
                        // Organisation collector
                        ReferenceOption org = references.getCurrentFieldOption(l, data, configuration, "collectors."+row.getRowId()+"."+Fields.COLLECTORORGANISATION, true);
                        ReferenceOption ag = references.getCurrentFieldOption(l, data, configuration, "collectors."+row.getRowId()+"."+Fields.COLLECTORAGENCY, true);
                        ReferenceOption sec = references.getCurrentFieldOption(l, data, configuration, "collectors."+row.getRowId()+"."+Fields.COLLECTORSECTION, true);

                        if(org != null && org.getTitle() != null && StringUtils.hasText(org.getTitle().getValue())) {
                            coll += org.getTitle().getValue();
                        }
                        if(ag != null && ag.getTitle() != null && StringUtils.hasText(ag.getTitle().getValue())) {
                            if(StringUtils.hasText(coll)) {
                                coll += ". ";
                            }
                            coll += ag.getTitle().getValue();
                        }
                        if(sec != null && sec.getTitle() != null && StringUtils.hasText(sec.getTitle().getValue())) {
                            if(StringUtils.hasText(coll)) {
                                coll += ". ";
                            }
                            coll += sec.getTitle().getValue();
                        }
                    }
                    if(StringUtils.hasText(coll)) {
                        if(StringUtils.hasText(colls)) {
                            colls += " & ";
                        }
                        colls += coll;
                    }
                }
            }
            if(StringUtils.hasText(colls)) {
                if(StringUtils.hasText(biblcit)) {
                    biblcit += ". ";
                }
                colls += " "+collector.get(l);
                biblcit += colls;
            }

            // producers separated with ampersand, append with producer.get(language)
            String prods = "";
            containerPair = data.dataField(ContainerDataFieldCall.get(Fields.PRODUCERS));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                    String prod = "";
                    ReferenceOption org = references.getCurrentFieldOption(l, data, configuration, "producers."+row.getRowId()+"."+Fields.PRODUCERORGANISATION, true);
                    ReferenceOption ag = references.getCurrentFieldOption(l, data, configuration, "producers."+row.getRowId()+"."+Fields.PRODUCERAGENCY, true);
                    ReferenceOption sec = references.getCurrentFieldOption(l, data, configuration, "producers."+row.getRowId()+"."+Fields.PRODUCERSECTION, true);

                    if(org != null && org.getTitle() != null && StringUtils.hasText(org.getTitle().getValue())) {
                        prod += org.getTitle().getValue();
                    }
                    if(ag != null && ag.getTitle() != null && StringUtils.hasText(ag.getTitle().getValue())) {
                        if(StringUtils.hasText(prod)) {
                            prod += ". ";
                        }
                        prod += ag.getTitle().getValue();
                    }
                    if(sec != null && sec.getTitle() != null && StringUtils.hasText(sec.getTitle().getValue())) {
                        if(StringUtils.hasText(prod)) {
                            prod += ". ";
                        }
                        prod += sec.getTitle().getValue();
                    }
                    if(StringUtils.hasText(prod)) {
                        if(StringUtils.hasText(prods)) {
                            prods += " & ";
                        }
                        prods += prod;
                    }
                }
            }
            if(StringUtils.hasText(prods)) {
                if(StringUtils.hasText(biblcit)) {
                    biblcit += ". ";
                }
                prods += " "+producer.get(l);
                biblcit += prods;
            }

            // Yhteiskuntatieteellinen tietoarkisto, append with distributor.get(language)
            if(StringUtils.hasText(biblcit)) {
                biblcit += ". ";
                biblcit += " Yhteiskuntatieteellinen tietoarkisto "+distributor.get(l);
            }

            // URN from DIP package (this is still unclear how it's formed
            containerPair = data.dataField(ContainerDataFieldCall.get(Fields.PACKAGES));
            if(containerPair.getLeft() == StatusCode.FIELD_FOUND && containerPair.getRight().hasRowsFor(Language.DEFAULT)) {
                for(DataRow row : containerPair.getRight().getRowsFor(Language.DEFAULT)) {
                    Pair<StatusCode, ValueDataField> type = row.dataField(ValueDataFieldCall.get(Fields.PACKAGE));
                    if(type.getLeft() == StatusCode.FIELD_FOUND && type.getRight().valueForEquals(Language.DEFAULT, "DIP")) {
                        Pair<StatusCode, ValueDataField> urn = row.dataField(ValueDataFieldCall.get(Fields.PACKAGEURN));
                        if(urn.getLeft() == StatusCode.FIELD_FOUND && urn.getRight().hasValueFor(Language.DEFAULT)) {
                            if(StringUtils.hasText(biblcit)) {
                                biblcit += ". ";
                            }
                            biblcit += urn.getRight().getActualValueFor(Language.DEFAULT);
                        }
                        break;
                    }
                }
            }

            if(StringUtils.hasText(biblcit)) {
                checkSave(data.dataField(ValueDataFieldCall.set(Fields.BIBLCIT, new Value(biblcit), l).setInfo(info)), changesAndErrors);
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    private void formUrns(RevisionData revision, DateTimeUserPair info, MutablePair<Boolean, Boolean> changesAndErrors) {
        Pair<StatusCode, ValueDataField> pair = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
            return;
        }

        Pair<StatusCode, ContainerDataField> descVersionsPair = revision.dataField(ContainerDataFieldCall.get(Fields.DESCVERSIONS));
        Pair<StatusCode, ContainerDataField> dataVersionsPair = revision.dataField(ContainerDataFieldCall.get(Fields.DATAVERSIONS));

        String base = URN_BASE+pair.getRight().getActualValueFor(Language.DEFAULT)+"-";

        Pair<StatusCode, ContainerDataField> packagesPair = revision.dataField(ContainerDataFieldCall.set(Fields.PACKAGES));
        if(packagesPair.getRight() == null) {
            // Something bad happened
            Logger.error(getClass(), "Failed to create packages container during URN creation with message "+packagesPair.getLeft());
            return;
        }
        ContainerDataField packages = packagesPair.getRight();
        Pair<StatusCode, DataRow> rowPair = packages.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.PACKAGE, new Value("DDI-xml-fi"), revision.getChanges(), info);
        if(rowPair.getRight() != null) {
            // Finnish package
            String descVersion = null;
            LocalDateTime modifyMoment = null;
            if(descVersionsPair.getLeft() == StatusCode.FIELD_FOUND && descVersionsPair.getRight().hasRowsFor(Language.DEFAULT)) {
                DataRow row = descVersionsPair.getRight().getRows().get(Language.DEFAULT).get(descVersionsPair.getRight().getRows().get(Language.DEFAULT).size()-1);
                Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.DEFAULT)) {
                    descVersion = versionPair.getRight().getActualValueFor(Language.DEFAULT);
                    modifyMoment = versionPair.getRight().getValueFor(Language.DEFAULT).getSaved().getTime();
                }
            }
            if(descVersion == null) {
                descVersion = "0.0";
            }
            if(modifyMoment == null) {
                modifyMoment = new LocalDateTime();
            }
            String ddi = base+"DDIC.fi-"+descVersion+"-"+modifyMoment.toString("yyyyMMdd");
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEURN, new Value(ddi), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEVERSION, new Value(descVersion), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
        }

        rowPair = packages.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.PACKAGE, new Value("DDI-xml-en"), revision.getChanges(), info);
        if(rowPair.getRight() != null) {
            // English package
            String descVersion = null;
            LocalDateTime modifyMoment = null;
            if(descVersionsPair.getLeft() == StatusCode.FIELD_FOUND && descVersionsPair.getRight().hasRowsFor(Language.EN)) {
                DataRow row = descVersionsPair.getRight().getRows().get(Language.EN).get(descVersionsPair.getRight().getRows().get(Language.EN).size()-1);
                Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.EN)) {
                    descVersion = versionPair.getRight().getActualValueFor(Language.EN);
                    modifyMoment = versionPair.getRight().getValueFor(Language.EN).getSaved().getTime();
                }
            }
            if(descVersion == null) {
                descVersion = "0.0";
            }
            if(modifyMoment == null) {
                modifyMoment = new LocalDateTime();
            }
            String ddi = base+"DDIC.en-"+descVersion+"-"+modifyMoment.toString("yyyyMMdd");
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEURN, new Value(ddi), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEVERSION, new Value(descVersion), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
        }

        rowPair = packages.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.PACKAGE, new Value("DDI-xml-sv"), revision.getChanges(), info);
        if(rowPair.getRight() != null) {
            // Swedish package
            String descVersion = null;
            LocalDateTime modifyMoment = null;
            if(descVersionsPair.getLeft() == StatusCode.FIELD_FOUND && descVersionsPair.getRight().hasRowsFor(Language.SV)) {
                DataRow row = descVersionsPair.getRight().getRows().get(Language.SV).get(descVersionsPair.getRight().getRows().get(Language.SV).size()-1);
                Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
                if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.SV)) {
                    descVersion = versionPair.getRight().getActualValueFor(Language.SV);
                    modifyMoment = versionPair.getRight().getValueFor(Language.SV).getSaved().getTime();
                }
            }
            if(descVersion == null) {
                descVersion = "0.0";
            }
            if(modifyMoment == null) {
                modifyMoment = new LocalDateTime();
            }
            String ddi = base+"DDIC.sv-"+descVersion+"-"+modifyMoment.toString("yyyyMMdd");
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEURN, new Value(ddi), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEVERSION, new Value(descVersion), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
        }

        String dataVersion = null;
        LocalDateTime modifyMoment = null;
        if(dataVersionsPair.getLeft() == StatusCode.FIELD_FOUND && dataVersionsPair.getRight().hasRowsFor(Language.DEFAULT)) {
            DataRow row = dataVersionsPair.getRight().getRows().get(Language.DEFAULT).get(dataVersionsPair.getRight().getRows().get(Language.DEFAULT).size()-1);
            Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DATAVERSION));
            if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.DEFAULT)) {
                dataVersion = versionPair.getRight().getActualValueFor(Language.DEFAULT);
                modifyMoment = versionPair.getRight().getValueFor(Language.DEFAULT).getSaved().getTime();
            }
        }
        if(dataVersion == null) {
            dataVersion = "0.0";
        }
        String descVersion = null;
        if(descVersionsPair.getLeft() == StatusCode.FIELD_FOUND && descVersionsPair.getRight().hasRowsFor(Language.DEFAULT)) {
            DataRow row = descVersionsPair.getRight().getRows().get(Language.DEFAULT).get(descVersionsPair.getRight().getRows().get(Language.DEFAULT).size()-1);
            Pair<StatusCode, ValueDataField> versionPair = row.dataField(ValueDataFieldCall.get(Fields.DESCVERSION));
            if(versionPair.getLeft() == StatusCode.FIELD_FOUND && versionPair.getRight().hasValueFor(Language.DEFAULT)) {
                descVersion = versionPair.getRight().getActualValueFor(Language.DEFAULT);
                if(modifyMoment == null) {
                    modifyMoment = versionPair.getRight().getValueFor(Language.DEFAULT).getSaved().getTime();
                } else {
                    if(versionPair.getRight().getValueFor(Language.DEFAULT).getSaved().getTime().compareTo(modifyMoment) > 0) {
                        modifyMoment = versionPair.getRight().getValueFor(Language.DEFAULT).getSaved().getTime();
                    }
                }
            }
        }
        if(descVersion == null) {
            descVersion = "0.0";
        }
        if(modifyMoment == null) {
            modifyMoment = new LocalDateTime();
        }

        rowPair = packages.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.PACKAGE, new Value("AIP"), revision.getChanges(), info);
        if(rowPair.getRight() != null) {
            // AIP package
            String aip = base+"AIP-"+dataVersion+"."+descVersion+"-"+modifyMoment.toString("yyyyMMdd");
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEURN, new Value(aip), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEVERSION, new Value(dataVersion+"."+descVersion), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
        }

        rowPair = packages.getOrCreateRowWithFieldValue(Language.DEFAULT, Fields.PACKAGE, new Value("DIP"), revision.getChanges(), info);
        if(rowPair.getRight() != null) {
            // DIP package
            String dip = base+"DIP-"+dataVersion+"."+descVersion+"-"+modifyMoment.toString("yyyyMMdd");
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEURN, new Value(dip), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
            checkSave(rowPair.getRight().dataField(ValueDataFieldCall.set(Fields.PACKAGEVERSION, new Value(dataVersion+"."+descVersion), Language.DEFAULT).setInfo(info).setChangeMap(revision.getChanges())), changesAndErrors);
        }
    }

    private void checkSave(Pair<StatusCode, ValueDataField> pair, MutablePair<Boolean, Boolean> changesAndErrors) {
        if(pair.getLeft() == StatusCode.FIELD_INSERT || pair.getLeft() == StatusCode.FIELD_UPDATE) {
            changesAndErrors.setLeft(true);
        }
    }
}
