package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.APIUserEntry;
import fi.uta.fsd.metka.transfer.settings.NewAPIUserRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface APIUserRepository {

    public Pair<ReturnResult, List<APIUserEntry>> listAPIUsers();

    public ReturnResult removeAPIUser(String key);

    public Pair<ReturnResult, APIUserEntry> newAPIUser(NewAPIUserRequest request);
}