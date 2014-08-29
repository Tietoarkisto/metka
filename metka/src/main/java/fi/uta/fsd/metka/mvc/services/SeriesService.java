package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.revision.SeriesAbbreviationsResponse;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', 'PERMISSION') " +
        "and hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', 'PERMISSION')")
public interface SeriesService {
    SeriesAbbreviationsResponse findAbbreviations();

    RevisionSearchResponse findNames();
}
