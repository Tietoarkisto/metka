package fi.uta.fsd.metka.data.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 11:06 AM
 */
@Transactional(readOnly = true)
public interface CRUDRepository<T,E> {
    @Transactional(readOnly = false) public T create(T entity);
    public T read(E id);
    @Transactional(readOnly = false) public T update(T entity);
    @Transactional(readOnly = false) public void delete(E id);
    public List<T> listAll();
}
