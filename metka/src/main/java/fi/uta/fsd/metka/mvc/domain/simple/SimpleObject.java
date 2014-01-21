package fi.uta.fsd.metka.mvc.domain.simple;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/21/14
 * Time: 9:15 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleObject {

    /**
     * Used for automated validation and generally by getting a value by using a configuration key.
     * @param key - Configuration key of the given value
     * @return
     * @throws IllegalArgumentException - If the given key does not exist within this SimpleObject
     */
    public abstract Object getByKey(String key) throws IllegalArgumentException;
}
