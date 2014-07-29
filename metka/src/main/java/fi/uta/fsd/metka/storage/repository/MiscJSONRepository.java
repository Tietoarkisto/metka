package fi.uta.fsd.metka.storage.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MiscJSONRepository {
    /**
     * Validates given JSONObject to contain misc json data and inserts or replaces it to database.
     *
     * @param misc Misc JSON data to be inserted.
     */
    @Transactional(readOnly = false) public void insert(JsonNode misc);

    @Transactional(readOnly = false) public void insert(String text);

    /**
     * If previous data exists for given Misc JSON tree the new content is merged to the old content
     * replacing existing values with new values. If no content exist then insert as new.
     * NOTICE: TODO:
     * @param misc Misc JSON data to be merged
     */
    @Transactional(readOnly = false) public void merge(JsonNode misc);

    /**
     * Find MiscJSON data with given key.
     * @param key Requested Misc JSON key
     * @return
     */
    public JsonNode findByKey(String key);
}
