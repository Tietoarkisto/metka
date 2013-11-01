package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.entity.MaterialEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 9:53 AM
 */
public interface MaterialRepository {
    public MaterialEntity update(MaterialEntity material);
    public Long create(MaterialEntity material);
    public void delete(MaterialEntity material);

    public List<MaterialEntity> findAll();
    public MaterialEntity findById(Long id);

}
