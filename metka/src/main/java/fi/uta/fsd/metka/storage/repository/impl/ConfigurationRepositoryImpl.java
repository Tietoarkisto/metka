package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.entity.ConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.GUIConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public void insert(Configuration configuration) {
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
        } else if(list.size() > 1) {
            // TODO: Log error since something has inserted multiple instances of same configuration to database
            return;
        } else {
            entity = list.get(0);
        }
        entity.setData(json.serialize(configuration));
        em.merge(entity);
    }

    @Override
    public void insert(GUIConfiguration configuration) {
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
        } else if(list.size() > 1) {
            // TODO: Log error since something has inserted multiple instances of same configuration to database
            return;
        } else {
            entity = list.get(0);
        }
        entity.setData(json.serialize(configuration));
        em.merge(entity);
    }

    @Override
    public void insertDataConfig(String text) {
        Configuration config = json.deserializeDataConfiguration(text);
        insert(config);
    }

    @Override
    public void insertGUIConfig(String text) {
        GUIConfiguration config = json.deserializeGUIConfiguration(text);
        insert(config);
    }

    @Override
    public Configuration findConfiguration(String type, Integer version) {
        return findConfiguration(ConfigurationType.fromValue(type), version);
    }

    @Override
    public Configuration findConfiguration(ConfigurationType type, Integer version) {
        return findConfiguration(new ConfigurationKey(type, version));
    }

    @Override
    public Configuration findConfiguration(ConfigurationKey key) {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
                .setParameter("type", key.getType())
                .setParameter("version", key.getVersion())
                .getResultList();

        if(list.size() > 1) {
            // TODO: Log error
            return null;
        } else if(list.size() == 0) {
            return null;
        }
        ConfigurationEntity entity = list.get(0);

        Configuration configuration = json.deserializeDataConfiguration(entity.getData());
        return configuration;
    }

    @Override
    public Configuration findLatestConfiguration(ConfigurationType type) {
        List<ConfigurationEntity> list =
                em.createQuery(
                    "SELECT c FROM ConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                    ConfigurationEntity.class)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
        if(list.size() == 0) {
            return null;
        }
        ConfigurationEntity entity = list.get(0);

        Configuration configuration = json.deserializeDataConfiguration(entity.getData());
        return configuration;
    }

    @Override
    public GUIConfiguration findLatestGUIConfiguration(ConfigurationType type) {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                        GUIConfigurationEntity.class)
                        .setParameter("type", type)
                        .setMaxResults(1)
                        .getResultList();
        if(list.size() == 0) {
            return null;
        }
        GUIConfigurationEntity entity = list.get(0);

        GUIConfiguration configuration = json.deserializeGUIConfiguration(entity.getData());
        return configuration;
    }

    @Override
    public Configuration findLatestByRevisionableId(Long id) {
        RevisionableEntity rev = em.find(RevisionableEntity.class, id);
        if(rev == null) {
            return null;
        }
        List<ConfigurationEntity> list =
                em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type ORDER BY c.version DESC", ConfigurationEntity.class)
                .setParameter("type", ConfigurationType.valueOf(rev.getType()))
                .setMaxResults(1)
                .getResultList();

        if(list.size() == 0) {
            return null;
        }
        ConfigurationEntity entity = list.get(0);

        Configuration configuration = json.deserializeDataConfiguration(entity.getData());
        return configuration;
    }

    @Override
    public Configuration findConfigurationForRevision(Long id, Integer revision) {
        RevisionData rev = generalRepository.getRevision(id, revision);
        Configuration config = null;
        if(rev != null) {
            List<ConfigurationEntity> list =
                    em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type AND c.version = :version", ConfigurationEntity.class)
                            .setParameter("type", rev.getConfiguration().getType())
                            .setParameter("version", rev.getConfiguration().getVersion())
                            .setMaxResults(1)
                            .getResultList();

            if(list.size() == 0) {
                return null;
            }
            ConfigurationEntity entity = list.get(0);
            config = json.deserializeDataConfiguration(entity.getData());
        }
        return config;
    }
}
