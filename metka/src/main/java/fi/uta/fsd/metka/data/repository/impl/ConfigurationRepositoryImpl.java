package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

@Repository("configurationRepository")
public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;
    @Autowired
    private JSONUtil json;

    @Override
    public void insert(Configuration configuration) throws IOException {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
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
        entity.setData(json.serialize(configuration));
        em.merge(entity);
    }

    public Configuration findConfiguration(ConfigurationKey key)
            throws IncorrectResultSizeDataAccessException, IOException {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
                .setParameter("type", key.getType())
                .setParameter("version", key.getVersion())
                .getResultList();
        ConfigurationEntity entity = null;
        try {
            entity = DataAccessUtils.requiredSingleResult(list);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }

        Configuration configuration = json.readConfigurationFromString(entity.getData());
        return configuration;
    }

    public Configuration findLatestConfiguration(ConfigurationType type)
            throws IncorrectResultSizeDataAccessException, IOException {
        List<ConfigurationEntity> list =
                em.createQuery(
                    "SELECT c FROM ConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                    ConfigurationEntity.class)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
        ConfigurationEntity entity = null;
        try {
            entity = DataAccessUtils.requiredSingleResult(list);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }

        Configuration configuration = json.readConfigurationFromString(entity.getData());
        return configuration;
    }
}
