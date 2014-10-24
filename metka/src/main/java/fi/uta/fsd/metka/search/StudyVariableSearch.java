package fi.uta.fsd.metka.search;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyVariableSearch {
    Pair<ReturnResult, RevisionData> findVariableWithId(Long studyId, String variableId);
}
