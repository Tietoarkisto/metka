package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VariablesRepository {
    @Transactional(readOnly = true) public Pair<ReturnResult, RevisionData> getVariablesByStudyId(Long id);

    public Pair<ReturnResult, RevisionData> getEditableVariablesData(Long id);
    public Pair<ReturnResult, RevisionData> getEditableVariableData(Long id);

    /*public void saveVariablesData(); // TODO: Set return type and parameters
    public void saveVariableData(); // TODO: Set return type and parameters*/
}
