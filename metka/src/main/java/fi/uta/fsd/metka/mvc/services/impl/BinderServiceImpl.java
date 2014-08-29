package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.mvc.services.BinderService;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderListResponse;
import fi.uta.fsd.metka.transfer.binder.BinderPageListEntry;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinderServiceImpl implements BinderService {
    @Autowired
    private BinderRepository binder;

    @Override public BinderListResponse saveBinderPage(SaveBinderPageRequest request) {
        Pair<ReturnResult, BinderPageListEntry> result = binder.saveBinderPage(request);
        BinderListResponse response = new BinderListResponse();
        response.setResult(result.getLeft());
        if(result.getRight() != null) {
            response.getPages().add(result.getRight());
        }
        return response;
    }

    @Override public ReturnResult removePage(Long pageId) {
        return binder.removePage(pageId);
    }

    @Override public BinderListResponse listStudyBinderPages(Long id) {
        return formBinderListResponse(binder.listStudyBinderPages(id));
    }

    @Override public BinderListResponse listBinderPages() {
        return formBinderListResponse(binder.listBinderPages());
    }

    @Override public BinderListResponse binderContent(Long binderId) {
        return formBinderListResponse(binder.binderContent(binderId));
    }

    private BinderListResponse formBinderListResponse(Pair<ReturnResult, List<BinderPageListEntry>> pair) {
        BinderListResponse response = new BinderListResponse();
        response.setResult(pair.getLeft());
        if(pair.getRight() != null && !pair.getRight().isEmpty()) {
            response.getPages().addAll(pair.getRight());
        }
        return response;
    }
}
