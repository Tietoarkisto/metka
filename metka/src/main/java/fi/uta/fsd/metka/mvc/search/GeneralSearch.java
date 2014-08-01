package fi.uta.fsd.metka.mvc.search;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface GeneralSearch {
    // TODO: Replace this with actual study search
    public List<RevisionDataRemovedContainer> tempFindAllStudies();
}
