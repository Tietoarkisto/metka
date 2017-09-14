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

package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Operation;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.cascade.CascadeInstruction;
import fi.uta.fsd.metka.storage.cascade.Cascader;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.restrictions.RestrictionValidator;
import fi.uta.fsd.metka.storage.restrictions.ValidateResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Restores removed revisionables back to usage.
 * Needs special permissions to use but those are checked on the interface
 * Successful result is SUCCESS_RESTORE, all other results are failures of some sort
 */
@Repository
public class RevisionRestoreRepositoryImpl implements RevisionRestoreRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private Messenger messenger;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private Cascader cascader;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionApproveRepository approve;

    @Autowired
    private EhCacheCacheManager cacheManager;

    @Override
    @CacheEvict(value="info-cache", key="#id")
    public RemoveResult restore(Long id) {
        return this.restore(id,null);
    }

    @Override
    @CacheEvict(value="info-cache", key="#id")
    public RemoveResult restore(Long id, LocalDateTime dt) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);

        if(entity == null) {
            return RemoveResult.NOT_FOUND;
        }
        if(!entity.getRemoved()) {
            return RemoveResult.NOT_REMOVED;
        }

        if (dt != null) {
            if (!dt.equals(entity.getRemovalDate())) {
                return RemoveResult.NOT_REMOVED;
            }
        } else {
            dt = entity.getRemovalDate();
        }

        entity.setRemoved(false);
        entity.setRemovedBy(null);
        entity.setRemovalDate(null);

        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(entity.getId().toString());
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        RevisionData data = dataPair.getRight();

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        for(Operation operation : configPair.getRight().getCascade()) {
            if(!(operation.getType() == OperationType.RESTORE || operation.getType() == OperationType.ALL)) {
                continue;
            }
            cascader.cascade(CascadeInstruction.build(OperationType.RESTORE, DateTimeUserPair.build(dt)), data, operation.getTargets(), configPair.getRight());
        }

        finalizeRestore(data, DateTimeUserPair.build());

        List<Integer> nos = revisions.getAllRevisionNumbers(id);
        for(Integer no : nos) {
            Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(id, no);
            if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
                revisions.indexRevision(revPair.getRight().getKey());
            }
        }

        // We need to clear the cache in order to keep cache vs db integrity over large transactions.
        cacheManager.getCache("revision-cache").clear();

        messenger.sendAmqpMessage(messenger.FD_RESTORE, new RevisionPayload(data));

        return RemoveResult.SUCCESS_RESTORE;
    }

    private void finalizeRestore(RevisionData data, DateTimeUserPair info) {
        switch(data.getConfiguration().getType()) {
            case STUDY_VARIABLES:
                finalizeStudyVariablesRestore(data, info);
                break;
            case STUDY_VARIABLE:
                finalizeStudyVariableRestore(data, info);
                break;
            case PUBLICATION:
                finalizePublicationRestore(data, info);
                break;
        }
    }

    private void finalizeStudyVariablesRestore(RevisionData data, DateTimeUserPair info) {
        checkStudyVariablesStudy(data);

        checkStudyVariablesAttachment(data, info);
    }

    private void checkStudyVariablesAttachment(RevisionData data, DateTimeUserPair info) {
        ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight();
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            // Something weird has happened but this is not the place to react to it, just return
            return;
        }

        RevisionData attachment = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT).split("-")[0]).getRight();
        if(attachment == null || attachment.getState() != RevisionState.DRAFT) {
            return;
        }

        field = attachment.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(field == null || field.hasValueFor(Language.DEFAULT)) {
            // Something weird has happened but this is not the place to react to it, just return
            // If there already is a value then it doesn't matter if it equals this variables-file or not, we can't overwrite it even if it doesn't
            return;
        }

        attachment.dataField(ValueDataFieldCall.set(Fields.VARIABLES, new Value(data.getKey().getId().toString()), Language.DEFAULT).setInfo(info).setChangeMap(attachment.getChanges()));
        revisions.updateRevisionData(attachment);
    }

    private void checkStudyVariablesStudy(RevisionData data) {
        RevisionData study = revisions.getRevisionData(data.dataField(ValueDataFieldCall.get(Fields.STUDY)).getRight().getActualValueFor(Language.DEFAULT)).getRight();
        if(study == null) {
            Logger.error(getClass(), "Tried to finalize study variables restore but could not find study for study variables " + data.toString());
            return;
        }

        String language = data.dataField(ValueDataFieldCall.get(Fields.LANGUAGE)).getRight().getActualValueFor(Language.DEFAULT);

        ContainerDataField variables = study.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES)).getRight();
        for(DataRow row : variables.getRowsFor(Language.DEFAULT)) {
            if(language.equals(row.dataField(ValueDataFieldCall.get(Fields.VARIABLESLANGUAGE)).getRight().getActualValueFor(Language.DEFAULT))) {
                if(row.getRemoved()) {
                    row.setRemoved(false);
                    revisions.updateRevisionData(study);
                }
                break;
            }
        }
    }

    private void finalizeStudyVariableRestore(RevisionData data, DateTimeUserPair info) {
        ValueDataField field = data.dataField(ValueDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(field == null || !field.hasValueFor(Language.DEFAULT)) {
            // Something weird has happened but this is not the place to react to it, just return
            return;
        }

        RevisionData variablesData = revisions.getRevisionData(field.getActualValueFor(Language.DEFAULT)).getRight();
        if(variablesData == null || variablesData.getState() != RevisionState.DRAFT) {
            return;
        }

        ReferenceContainerDataField variables = variablesData.dataField(ReferenceContainerDataFieldCall.get(Fields.VARIABLES)).getRight();
        if(variables == null || !variables.hasValidRows()) {
            return;
        }

        ReferenceRow row = variables.getReferenceIncludingValue(data.getKey().asPartialKey()).getRight();
        if(row == null || row.valueEquals(data.getKey().asCongregateKey())) {
            return;
        }

        variables.replaceRow(row.getRowId(), ReferenceRow.build(variables, new Value(data.getKey().asCongregateKey()), info), variablesData.getChanges());
    }

    private void finalizePublicationRestore(RevisionData revision, DateTimeUserPair info) {
        List<Long> handled = new ArrayList<>();
        // Iterate through related studies
        ReferenceContainerDataField studies = (ReferenceContainerDataField) revision.getField("studies");
        for (ReferenceRow reference : studies.getReferences()){
            if (!handled.contains(Long.parseLong(reference.getActualValue()))) {
                Pair<ReturnResult, RevisionData> studyPair = revisions.getRevisionData(reference.getActualValue());
                if (!studyPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                    continue;
                }
                handleDescVersion(studyPair.getRight(), info);
                handled.add(studyPair.getRight().getKey().getId());
            }
        }

        // Iterate through related series
        ReferenceContainerDataField series = (ReferenceContainerDataField) revision.getField("series");
        for (ReferenceRow reference : series.getReferences()){
            Pair<ReturnResult, RevisionData> seriesPair = revisions.getRevisionData(reference.getReference().getValue());
            if (!seriesPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                continue;
            }
            // Query for studies with the found series
            ExpertRevisionSearchCommand command = null;
            try {
                command = ExpertRevisionSearchCommand.build("+key.configuration.type:STUDY +series:"+reference.getReference().getValue()+" +state.removed:FALSE", configurations);
            } catch (QueryNodeException e) {
                continue;
            }
            ResultList<RevisionResult> seriesStudiesResult = searcher.executeSearch(command);
            for (RevisionResult result: seriesStudiesResult.getResults()){
                if (!handled.contains(result.getId())){
                    Pair<ReturnResult, RevisionData> studyPair = revisions.getRevisionData(result.getId().toString());
                    if (!studyPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                        continue;
                    }
                    handleDescVersion(studyPair.getRight(), info);
                    handled.add(studyPair.getRight().getKey().getId());
                }
            }
        }
    }

    /**
     * Adds a new descversion for a revision given as the parameter. If the revision is
     * APPROVED, create a new DRAFT, add a descrevision and approve the revision.
     * if the revision is already a DRAFT, only add a descrevision and save.
     * @param revision RevisionData of the revision being handled
     * @param info Date and User information for the ongoing publication approval operation.
     */
    private void handleDescVersion(RevisionData revision, DateTimeUserPair info){
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(revision.getKey().getId());
        if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
            return;
        }
        if (revision.getState().equals(RevisionState.DRAFT)){
            TransferData transferData = TransferData.buildFromRevisionData(revision, infoPair.getRight());
            TransferField descversions = null;
            if (transferData.hasField("descversions")){
                descversions = transferData.getField("descversions");
            } else {
                descversions = new TransferField("descversions", TransferFieldType.CONTAINER);
                transferData.getFields().put("descversions", descversions);
            }
            TransferRow newRow = new TransferRow("descversions");

            TransferField newField = new TransferField("versionlabeldesc", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent("23");
            newRow.addField(newField);

            newField = new TransferField("versionpro", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getUser());
            newRow.addField(newField);

            Double newVersionNo = 0.0;
            Double currVersionNo;
            if (descversions.hasRows()) {
                for (TransferRow row : descversions.getRows().get(Language.DEFAULT)) {
                    if (row.getField("descversion") == null){
                        continue;
                    }
                    try {
                        currVersionNo = Double.parseDouble(row.getField("descversion").getValueFor(Language.DEFAULT).getValue());
                    } catch (NumberFormatException ex) {
                        currVersionNo = 1.0;
                    }
                    if (currVersionNo > newVersionNo) {
                        newVersionNo = currVersionNo;
                    }
                }
            }
            newVersionNo = newVersionNo + 0.1;
            newField = new TransferField("descversion", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(BigDecimal.valueOf(newVersionNo).setScale(1, RoundingMode.DOWN).toString());
            newRow.addField(newField);

            newField = new TransferField("versiondate", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            newRow.addField(newField);

            descversions.addRowFor(Language.DEFAULT, newRow);
            save.saveRevision(transferData, info);
        } else if (revision.getState().equals(RevisionState.APPROVED)) {
            Pair<OperationResponse, RevisionData> editPair = edit.edit(revision.getKey(), info);
            TransferData transferData = TransferData.buildFromRevisionData(editPair.getRight(), infoPair.getRight());
            TransferField descversions = null;
            if (transferData.hasField("descversions")){
                descversions = transferData.getField("descversions");
            } else {
                descversions = new TransferField("descversions", TransferFieldType.CONTAINER);
                transferData.getFields().put("descversions", descversions);
            }
            TransferRow newRow = new TransferRow("descversions");

            TransferField newField = new TransferField("versionlabeldesc", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent("23");
            newRow.addField(newField);

            newField = new TransferField("versionpro", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getUser());
            newRow.addField(newField);

            Double newVersionNo = 0.0;
            Double currVersionNo;
            if (descversions.hasRows()) {
                for (TransferRow row : descversions.getRows().get(Language.DEFAULT)) {
                    if (row.getField("descversion") == null){
                        continue;
                    }
                    try {
                        currVersionNo = Double.parseDouble(row.getField("descversion").getValueFor(Language.DEFAULT).getValue());
                    } catch (NumberFormatException ex) {
                        currVersionNo = 1.0;
                    }
                    if (currVersionNo > newVersionNo) {
                        newVersionNo = currVersionNo;
                    }
                }
            }
            newVersionNo = newVersionNo + 0.1;
            newField = new TransferField("descversion", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(BigDecimal.valueOf(newVersionNo).setScale(1, RoundingMode.DOWN).toString());
            newRow.addField(newField);

            newField = new TransferField("versiondate", TransferFieldType.VALUE);
            newField.getValues().put(Language.DEFAULT, new TransferValue());
            newField.getValueFor(Language.DEFAULT).setCurrent(info.getTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            newRow.addField(newField);

            descversions.addRowFor(Language.DEFAULT, newRow);
            save.saveRevision(transferData, info);
            approve.approve(transferData, info);
        }
    }
}
