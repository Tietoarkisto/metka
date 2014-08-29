package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', 'PERMISSION')")
// TODO: Move to RevisionService
public interface GeneralService {
    Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, String type, boolean forward);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EXPORT_REVISION+"', 'PERMISSION')")
    Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision);
}
