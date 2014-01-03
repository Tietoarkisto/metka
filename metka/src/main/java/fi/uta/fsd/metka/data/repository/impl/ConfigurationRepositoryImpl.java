package fi.uta.fsd.metka.data.repository.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.mvc.domain.model.configuration.Configuration;
import fi.uta.fsd.metka.mvc.domain.model.configuration.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository("configurationRepository")
public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Override
    public void insert(Configuration configuration) throws JsonProcessingException {
        List<ConfigurationEntity> list = em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version")
                .setParameter("type", configuration.getKey().getType())
                .setParameter("version", configuration.getKey().getVersion())
                .getResultList();

        ConfigurationEntity entity = null;

        if(list.size() == 0) {
            entity = new ConfigurationEntity();
            entity.setVersion(configuration.getKey().getVersion());
            entity.setType(configuration.getKey().getType());
        } else {
            entity = list.get(0);
        }
        entity.setData(metkaObjectMapper.writeValueAsString(configuration));
        em.merge(entity);
    }

    public Configuration findConfiguration(ConfigurationKey key)
            throws IncorrectResultSizeDataAccessException, JsonParseException, JsonMappingException, IOException {
        List<ConfigurationEntity> list = em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version")
                .setParameter("type", key.getType())
                .setParameter("version", key.getVersion())
                .getResultList();
        ConfigurationEntity entity = null;
        try {
            entity = DataAccessUtils.requiredSingleResult(list);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }

        Configuration configuration = metkaObjectMapper.readValue(entity.getData(), Configuration.class);
        return configuration;
    }
}
