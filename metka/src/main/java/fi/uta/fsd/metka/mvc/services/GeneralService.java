package fi.uta.fsd.metka.mvc.services;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional(readOnly = true)
public interface GeneralService {
    Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, ConfigurationType type, boolean forward);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EXPORT_REVISION+"', '" + PermissionCheck.Values.PERMISSION + "')")
    Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision);

    Pair<ReturnResult, CodeBookDocument> exportDDI(Long id, Integer no, Language language);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_IMPORT_REVISION+"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    @Transactional(readOnly = false) ReturnResult importDDI(TransferData transferData, String path);
}
