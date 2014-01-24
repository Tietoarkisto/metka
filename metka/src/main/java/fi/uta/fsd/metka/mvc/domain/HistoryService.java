package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.repository.HistoryRepository;
import fi.uta.fsd.metka.data.util.ModelAccessUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.Change;
import fi.uta.fsd.metka.model.data.FieldContainer;
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

        Map<String, Change> changes = new HashMap<String, Change>();
        for(RevisionData data : comparedRevisions) {
            for(Map.Entry<String, Change> change : data.getChanges().entrySet()) {
                if(change.getValue().getOperation() != ChangeOperation.UNCHANGED) {
                    ChangeSO so = changesSO.get(change.getKey());
                    FieldContainer field;
                    if(so == null) {
                        so = new ChangeSO();
                        so.setProperty(change.getKey());
                        Field cfgField = config.getFields().get(change.getKey());
                        so.setSection(cfgField.getSection());
                        so.setType(cfgField.getType());
                        field = original.getFields().get(cfgField.getKey());
                        if(field != null) {
                            ChangeSO.ValueStringBuilder.buildValueString(
                                    cfgField.getType(), original.getFields().get(cfgField.getKey()), so.getOldValue());
                        }
                        so.setMaxValues(cfgField.getMaxValues());
                        changesSO.put(cfgField.getKey(), so);
                    }
                    so.getNewValue().clear();
                    if(change.getValue().getOperation() != ChangeOperation.REMOVED) {
                        ChangeSO.ValueStringBuilder.buildValueString(
                                so.getType(), change.getValue().getNewField(), so.getNewValue());
                    }
                    so.setOperation(change.getValue().getOperation());
                }
            }
        }

        response.setChanges(changesSO.values());

        return response;
    }
}
