package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.repository.HistoryRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.FieldChange;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.requests.ChangeCompareRequest;
import fi.uta.fsd.metka.mvc.domain.simple.history.ChangeCompareSO;
import fi.uta.fsd.metka.mvc.domain.simple.history.ChangeSO;
import fi.uta.fsd.metka.mvc.domain.simple.history.RevisionSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/14/14
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HistoryService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private HistoryRepository repository;

    public List<RevisionSO> getRevisionHistory(Integer id) {
        List<RevisionSO> revisions = new ArrayList<RevisionSO>();
        List<RevisionData> datas = null;
        try {
            datas = repository.getRevisionHistory(id);
        } catch(IOException ex) {
            // TODO: Notify user of problems with fetching revision history
            return revisions;
        }

        for(RevisionData data : datas) {
            RevisionSO revision = new RevisionSO();
            revision.setId(data.getKey().getId());
            revision.setRevision(data.getKey().getRevision());
            revision.setState(data.getState());
            revision.setApprovalDate(data.getApprovalDate());
            revisions.add(revision);
        }

        return revisions;
    }

    /**
     * Compares revision between two revision numbers (inclusive) and collects all changes that have happened after
     * the selected begin revision.
     * @param request User selected values for revision comparison.
     * @return
     */
    public ChangeCompareSO compareRevisions(ChangeCompareRequest request) {
        ChangeCompareSO response = new ChangeCompareSO();
        response.setId(request.getId());
        response.setBegin(request.getBegin());
        response.setEnd(request.getEnd());

        List<RevisionData> comparedRevisions = null;
        try {
            comparedRevisions = repository.getRevisionsForComparison(request);
        } catch(IOException ex) {
            // TODO: Notify user of problems with fetching original revision
            ex.printStackTrace();
            return null;
        }
        if(comparedRevisions.size() == 0) {
            // TODO: Notify user that couldn't find compared revisions
            return null;
        }
        Map<String, ChangeSO> changesSO = new HashMap<String, ChangeSO>();
        RevisionData original = null;
        try {
            original = repository.getRevisionByKey(request.getId(), request.getBegin());
        } catch(IOException ex) {
            // TODO: Notify user of problems with fetching original revision
            ex.printStackTrace();
            return null;
        }
        if(original == null) {
            // TODO: Notify user that couldn't find original revision
            return null;
        }
        Configuration config = configurationService.findLatestByType(request.getType());

        // TODO: Improve to take into account container fields and row containers. For now handles only ValueFields
        Map<String, FieldChange> changes = new HashMap<String, FieldChange>();
        for(RevisionData data : comparedRevisions) {
            for(Map.Entry<String, FieldChange> change : data.getChanges().entrySet()) {
                ValueFieldChange valueChange = (ValueFieldChange) change.getValue();
                if(valueChange.getOperation() != ChangeOperation.UNCHANGED) {
                    ChangeSO so = changesSO.get(change.getKey());
                    FieldContainer field;
                    if(so == null) {
                        so = new ChangeSO();
                        so.setProperty(change.getKey());
                        Field cfgField = config.getFields().get(change.getKey());
                        so.setSection(cfgField.getSection());
                        so.setType(cfgField.getType());
                        field = original.getField(cfgField);
                        if(field != null) {
                            ChangeSO.ValueStringBuilder.buildValueString(
                                    cfgField.getType(), original.getField(cfgField), so.getOldValue());
                        }
                        so.setMaxValues(cfgField.getMaxValues());
                        changesSO.put(cfgField.getKey(), so);
                    }
                    so.getNewValue().clear();
                    if(valueChange.getOperation() != ChangeOperation.REMOVED) {
                        ChangeSO.ValueStringBuilder.buildValueString(
                                so.getType(), valueChange.getNewField(), so.getNewValue());
                    }
                    so.setOperation(valueChange.getOperation());
                }
            }
        }

        response.setChanges(changesSO.values());

        return response;
    }
}
