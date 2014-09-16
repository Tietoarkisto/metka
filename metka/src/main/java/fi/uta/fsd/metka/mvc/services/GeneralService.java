package fi.uta.fsd.metka.mvc.services;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
// TODO: Move to RevisionService
public interface GeneralService {
    Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, String type, boolean forward);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EXPORT_REVISION+"', '" + PermissionCheck.Values.PERMISSION + "')")
    Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision);

    Pair<ReturnResult, CodeBookDocument> exportDDI(Long id, Integer no);
}
