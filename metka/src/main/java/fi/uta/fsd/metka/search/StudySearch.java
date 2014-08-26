package fi.uta.fsd.metka.search;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudySearch {

    public Pair<ReturnResult, List<RevisionSearchResult>> getStudiesWithVariables();
    public Pair<ReturnResult, RevisionData> getLatestRevisionWithStudyId(String studyId);

    public Pair<ReturnResult,List<RevisionSearchResult>> collectAttachmentHistory(Long attachmentId);
}
