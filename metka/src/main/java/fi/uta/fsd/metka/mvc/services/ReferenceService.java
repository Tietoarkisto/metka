package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.reference.ReferenceOptionsRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceRowRequest;
import fi.uta.fsd.metka.transfer.reference.ReferenceRowResponse;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional(readOnly = true)
public interface ReferenceService {
    ReferenceOption getCurrentFieldOption(Language language, RevisionData data, String path);

    List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request);

    ReferenceRowResponse getReferenceRow(ReferenceRowRequest request);
}
