package fi.uta.fsd.metka.storage.variables;

import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is used to merge study variables from a variables file to a study structure (study_variables and study_variable).
 * There is a possibility of changes being made to provided STUDY revision data in which case caller is usually
 * notified to persist the changed RevisionData.
 * Methods in this repository will deal strictly with parsing and merging variable data as provided by variable files,
 * separate repository should be made for fetching that data for display and modifying that data through user interface.
 */
@Transactional
public interface StudyVariablesParser {
    public ParseResult parse(RevisionData attachment, VariableDataType type);
    /*public ParseResult merge(RevisionData study, VariableDataType type, Configuration studyConfig);*/
}
