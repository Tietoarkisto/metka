package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.entity.GUIConfigurationEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
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

    @Autowired
    private GeneralRepository generalRepository;

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

    @Override
    public void insert(GUIConfiguration configuration) throws IOException {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        GUIConfigurationEntity.class)
                        .setParameter("type", configuration.getKey().getType())
                        .setParameter("version", configuration.getKey().getVersion())
                        .getResultList();

        GUIConfigurationEntity entity = null;

        if(list.size() == 0) {
            entity = new GUIConfigurationEntity();
            entity.setVersion(configuration.getKey().getVersion());
            entity.setType(configuration.getKey().getType());
        } else {
            entity = list.get(0);
        }
        entity.setData(json.serialize(configuration));
        em.merge(entity);
    }

    @Override
    public void insertDataConfig(String text) throws IOException {
        Configuration config = json.readDataConfigurationFromString(text);
        insert(config);
    }

    @Override
    public void insertGUIConfig(String text) throws IOException {
        GUIConfiguration config = json.readGUIConfigurationFromString(text);
        insert(config);
    }

    @Override
    public Configuration findConfiguration(String type, Integer version) throws IncorrectResultSizeDataAccessException, IOException {
        return findConfiguration(ConfigurationType.fromValue(type), version);
    }

    @Override
    public Configuration findConfiguration(ConfigurationType type, Integer version) throws IncorrectResultSizeDataAccessException, IOException {
        return findConfiguration(new ConfigurationKey(type, version));
    }

    @Override
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

        Configuration configuration = json.readDataConfigurationFromString(entity.getData());
        return configuration;
    }

    @Override
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

        Configuration configuration = json.readDataConfigurationFromString(entity.getData());
        return configuration;
    }

    @Override
    public GUIConfiguration findLatestGUIConfiguration(ConfigurationType type)
            throws IncorrectResultSizeDataAccessException, IOException {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                        GUIConfigurationEntity.class)
                        .setParameter("type", type)
                        .setMaxResults(1)
                        .getResultList();
        GUIConfigurationEntity entity = null;
        try {
            entity = DataAccessUtils.requiredSingleResult(list);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }

        GUIConfiguration configuration = json.readGUIConfigurationFromString(entity.getData());
        return configuration;
    }

    @Override
    public Configuration findLatestByRevisionableId(Integer id)
            throws IncorrectResultSizeDataAccessException, IOException {
        List<RevisionableEntity> revs =
                em.createQuery("SELECT r FROM RevisionableEntity r WHERE r.id=:id", RevisionableEntity.class)
                .setParameter("id", id)
                .getResultList();
        RevisionableEntity rev = null;
        try {
            rev = DataAccessUtils.requiredSingleResult(revs);
        } catch(EmptyResultDataAccessException ex) {
            // No revisionable
            return null;
        }
        if(rev == null) {
            // no revisionable
            return null;
        }
        List<ConfigurationEntity> list =
                em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type ORDER BY c.version DESC", ConfigurationEntity.class)
                .setParameter("type", ConfigurationType.fromValue(rev.getType()))
                .setMaxResults(1)
                .getResultList();

        ConfigurationEntity entity = null;
        try {
            entity = DataAccessUtils.requiredSingleResult(list);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }

        Configuration configuration = json.readDataConfigurationFromString(entity.getData());
        return configuration;
    }

    @Override
    public Configuration findConfigurationForRevision(Integer id, Integer revision)
            throws IncorrectResultSizeDataAccessException, IOException {
        RevisionData rev = generalRepository.getRevision(id, revision);
        Configuration config = null;
        if(rev != null) {
            List<ConfigurationEntity> list =
                    em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type AND c.version = :version", ConfigurationEntity.class)
                            .setParameter("type", rev.getConfiguration().getType())
                            .setParameter("version", rev.getConfiguration().getVersion())
                            .setMaxResults(1)
                            .getResultList();

            ConfigurationEntity entity = null;
            try {
                entity = DataAccessUtils.requiredSingleResult(list);
            } catch(EmptyResultDataAccessException ex) {
                return null;
            }
            config = json.readDataConfigurationFromString(entity.getData());
        }
        return config;
    }
}
