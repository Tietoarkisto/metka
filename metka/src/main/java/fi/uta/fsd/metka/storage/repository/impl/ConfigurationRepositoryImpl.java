package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.entity.ConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.GUIConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository("configurationRepository")
public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private static Logger logger = LoggerFactory.getLogger(ConfigurationRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;
    @Autowired
    private JSONUtil json;

    @Autowired
    private GeneralRepository generalRepository;

    @Override
    public ReturnResult insert(Configuration configuration) {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
                .setParameter("type", configuration.getKey().getType())
                .setParameter("version", configuration.getKey().getVersion())
                .getResultList();

        ConfigurationEntity entity = null;

        normalizeDataConfiguration(configuration);

        if(list.size() == 0) {
            entity = new ConfigurationEntity();
            entity.setVersion(configuration.getKey().getVersion());
            entity.setType(configuration.getKey().getType());
        } else if(list.size() > 1) {
            logger.error("There are multiple instances of " + configuration.toString() + " in the database.");
            return ReturnResult.DATABASE_DISCREPANCY;
        } else {
            entity = list.get(0);
        }

        Pair<SerializationResults, String> pair = json.serialize(configuration);
        if(pair.getLeft() == SerializationResults.SERIALIZATION_SUCCESS) {
            entity.setData(pair.getRight());
            em.merge(entity);
            return ReturnResult.DATABASE_INSERT_SUCCESS;
        } else {
            logger.error("Serialization of configuration failed, no merge performed for "+configuration.toString());
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    private void normalizeDataConfiguration(Configuration configuration) {
        // Set all subfields of translatable containers to translatable true
        for(Field field : configuration.getFields().values()) {
            if(field.getType() == FieldType.CONTAINER) {
                setSubfieldsToTranslatable(field, configuration);
            }
        }
    }

    private void setSubfieldsToTranslatable(Field field, Configuration configuration) {
        for(String key : field.getSubfields()) {
            Field subfield = configuration.getField(key);
            if(subfield.getType() != FieldType.REFERENCECONTAINER) {
                subfield.setTranslatable(true);
            }
            if(subfield.getType() == FieldType.CONTAINER) {
                setSubfieldsToTranslatable(subfield, configuration);
            }
        }
    }

    @Override
    public ReturnResult insert(GUIConfiguration configuration) {
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
            logger.error("There are multiple instances of "+configuration.toString()+" in the database.");
            return ReturnResult.DATABASE_DISCREPANCY;
        } else {
            entity = list.get(0);
        }

        Pair<SerializationResults, String> pair = json.serialize(configuration);
        if(pair.getLeft() == SerializationResults.SERIALIZATION_SUCCESS) {
            entity.setData(pair.getRight());
            em.merge(entity);
            return ReturnResult.DATABASE_INSERT_SUCCESS;
        } else {
            logger.error("Serialization of guiconfiguration failed, no merge performed for "+configuration.toString());
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    @Override
    public ReturnResult insertDataConfig(String text) {
        Pair<SerializationResults, Configuration> pair = json.deserializeDataConfiguration(text);
        if(pair.getLeft() == SerializationResults.DESERIALIZATION_SUCCESS) {
            return insert(pair.getRight());
        } else {
            logger.error("Failed to deserialize configuration for insertion.");
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    @Override
    public ReturnResult insertGUIConfig(String text) {
        Pair<SerializationResults, GUIConfiguration> pair = json.deserializeGUIConfiguration(text);
        if(pair.getLeft() == SerializationResults.DESERIALIZATION_SUCCESS) {
            return insert(pair.getRight());
        } else {
            logger.error("Failed to deserialize guiconfiguration for insertion.");
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    @Override
    public Pair<ReturnResult, Configuration> findConfiguration(String type, Integer version) {
        return findConfiguration(ConfigurationType.fromValue(type), version);
    }

    @Override
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationType type, Integer version) {
        return findConfiguration(new ConfigurationKey(type, version));
    }

    @Override
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationKey key) {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
                .setParameter("type", key.getType())
                .setParameter("version", key.getVersion())
                .getResultList();

        if(list.size() > 1) {
            logger.error("There are multiple instances of " + key.toString() + " in the database.");
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        }

        return deserializeDataConfiguration(list);
    }

    @Override
    public Pair<ReturnResult, Configuration> findLatestConfiguration(ConfigurationType type) {
        List<ConfigurationEntity> list =
                em.createQuery(
                    "SELECT c FROM ConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                    ConfigurationEntity.class)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();

        return deserializeDataConfiguration(list);
    }

    @Override
    public Pair<ReturnResult, Configuration> findLatestByRevisionableId(Long id) {
        RevisionableEntity rev = em.find(RevisionableEntity.class, id);
        if(rev == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        List<ConfigurationEntity> list =
                em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type ORDER BY c.version DESC", ConfigurationEntity.class)
                .setParameter("type", ConfigurationType.valueOf(rev.getType()))
                .setMaxResults(1)
                .getResultList();

        return deserializeDataConfiguration(list);
    }

    @Override
    public Pair<ReturnResult, Configuration> findConfigurationForRevision(Long id, Integer no) {
        Pair<ReturnResult, RevisionData> pair = generalRepository.getRevisionData(id, no);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }
        RevisionData revision = pair.getRight();

        List<ConfigurationEntity> list =
                em.createQuery("SELECT c FROM ConfigurationEntity c WHERE c.type =:type AND c.version = :version", ConfigurationEntity.class)
                        .setParameter("type", revision.getConfiguration().getType())
                        .setParameter("version", revision.getConfiguration().getVersion())
                        .setMaxResults(1)
                        .getResultList();

        return deserializeDataConfiguration(list);
    }

    private Pair<ReturnResult, Configuration> deserializeDataConfiguration(List<ConfigurationEntity> list) {
        if(list.size() == 0) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_FOUND, null);
        }

        ConfigurationEntity entity = list.get(0);
        if(!StringUtils.hasText(entity.getData())) {
            logger.error("Configuration entity "+entity.toString()+" contained no data.");
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_CONTAINED_NO_DATA, null);
        }

        Pair<SerializationResults, Configuration> pair = json.deserializeDataConfiguration(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            logger.error("Deserialization failed for configuration "+entity.toString());
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_VALID, null);
        } else {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, pair.getRight());
        }
    }

    @Override
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(String type, Integer version) {
        return findGUIConfiguration(ConfigurationType.fromValue(type), version);
    }

    @Override
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationType type, Integer version) {
        return findGUIConfiguration(new ConfigurationKey(type, version));
    }

    @Override
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationKey key) {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        GUIConfigurationEntity.class)
                        .setParameter("type", key.getType())
                        .setParameter("version", key.getVersion())
                        .getResultList();

        if(list.size() > 1) {
            logger.error("There are multiple instances of " + key.toString() + " in the database.");
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        }

        return deserializeGUIConfiguration(list);
    }

    @Override
    public Pair<ReturnResult, GUIConfiguration> findLatestGUIConfiguration(ConfigurationType type) {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type ORDER BY c.version DESC",
                        GUIConfigurationEntity.class)
                        .setParameter("type", type)
                        .setMaxResults(1)
                        .getResultList();

        return deserializeGUIConfiguration(list);
    }

    private Pair<ReturnResult, GUIConfiguration> deserializeGUIConfiguration(List<GUIConfigurationEntity> list) {
        if(list.size() == 0) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_FOUND, null);
        }

        GUIConfigurationEntity entity = list.get(0);
        if(!StringUtils.hasText(entity.getData())) {
            logger.error("Configuration entity "+entity.toString()+" contained no data.");
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_CONTAINED_NO_DATA, null);
        }

        Pair<SerializationResults, GUIConfiguration> pair = json.deserializeGUIConfiguration(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            logger.error("Deserialization failed for configuration "+entity.toString());
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_VALID, null);
        } else {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, pair.getRight());
        }
    }
}
