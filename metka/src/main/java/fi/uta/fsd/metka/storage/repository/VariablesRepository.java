package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VariablesRepository {
    @Transactional(readOnly = true) public RevisionData getVariablesByStudyId(Long id);
    @Transactional(readOnly = true) public RevisionData getVariablesById(Long id);
    @Transactional(readOnly = true) public RevisionData getVariablesRevision(Long id, Integer no);
    @Transactional(readOnly = true) public RevisionData getVariableById(Long id);
    @Transactional(readOnly = true) public RevisionData getVariableRevision(Long id, Integer no);

    public RevisionData getEditableVariablesData(Long id);
}
