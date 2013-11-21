package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.entity.SeriesEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/21/13
 * Time: 9:23 AM
 */
public interface SeriesRepository extends CRUDRepository<SeriesEntity, Integer> {
    public List<String> listAllAbbreviations();
}
