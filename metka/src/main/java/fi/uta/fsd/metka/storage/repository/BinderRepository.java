package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderPageListEntry;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BinderRepository {
    @Transactional(readOnly = false) public Pair<ReturnResult, BinderPageListEntry> saveBinderPage(SaveBinderPageRequest request);
    @Transactional(readOnly = false) public ReturnResult removePage(Long pageId);

    public Pair<ReturnResult, List<BinderPageListEntry>> listStudyBinderPages(Long study);
    public Pair<ReturnResult, List<BinderPageListEntry>> listBinderPages();
    public Pair<ReturnResult, List<BinderPageListEntry>> binderContent(Long binderId);
}
