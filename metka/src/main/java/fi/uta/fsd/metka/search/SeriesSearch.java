package fi.uta.fsd.metka.search;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface SeriesSearch {
    public List<String> findAbbreviations();
}
