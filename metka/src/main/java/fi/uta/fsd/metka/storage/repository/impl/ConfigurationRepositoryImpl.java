/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.entity.ConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.GUIConfigurationEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.Logger.error;

@Repository("configurationRepository")
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;
    @Autowired
    private JSONUtil json;

    @Autowired
    private RevisionRepository revisions;

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
            error(getClass(), "There are multiple instances of " + configuration.toString() + " in the database.");
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
            error(getClass(), "Serialization of configuration failed, no merge performed for "+configuration.toString());
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    private void normalizeDataConfiguration(Configuration configuration) {
        // Set all subfields of translatable containers to translatable true
        for(Field field : configuration.getFields().values()) {
            if(field.getType() == FieldType.CONTAINER && field.getTranslatable()) {
                setSubfieldsToTranslatable(field, configuration);
            }
        }

        // Set all freeText fields to writable false if they are STRING fields
        for(SelectionList list : configuration.getSelectionLists().values()) {
            if(StringUtils.hasText(list.getFreeTextKey())) {
                Field field = configuration.getField(list.getFreeTextKey());
                if(field != null) {
                    if(field.getType().isCanBeFreeText()) {
                        field.setWritable(false);
                    } else {
                        error(getClass(), "Field "+field.getKey()+" has type "+field.getType()+" which can not be used as free text");
                    }
                } else {
                    error(getClass(), "List "+list.getKey()+" uses freeTextKey "+list.getFreeTextKey()+" but field is not in the configuration.");
                }
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
            error(getClass(), "There are multiple instances of "+configuration.toString()+" in the database.");
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
            error(getClass(), "Serialization of guiconfiguration failed, no merge performed for "+configuration.toString());
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    @Override
    public ReturnResult insertDataConfig(String text) {
        Pair<SerializationResults, Configuration> pair = json.deserializeDataConfiguration(text);
        if(pair.getLeft() == SerializationResults.DESERIALIZATION_SUCCESS) {
            return insert(pair.getRight());
        } else {
            error(getClass(), "Failed to deserialize configuration for insertion.");
            return ReturnResult.DATABASE_INSERT_FAILED;
        }
    }

    @Override
    public ReturnResult insertGUIConfig(String text) {
        Pair<SerializationResults, GUIConfiguration> pair = json.deserializeGUIConfiguration(text);
        if(pair.getLeft() == SerializationResults.DESERIALIZATION_SUCCESS) {
            return insert(pair.getRight());
        } else {
            error(getClass(), "Failed to deserialize guiconfiguration for insertion.");
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
            error(getClass(), "There are multiple instances of " + key.toString() + " in the database.");
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
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(id, no);
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
            error(getClass(), "Configuration entity "+entity.toString()+" contained no data.");
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_CONTAINED_NO_DATA, null);
        }

        Pair<SerializationResults, Configuration> pair = json.deserializeDataConfiguration(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            error(getClass(), "Deserialization failed for configuration "+entity.toString());
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
            error(getClass(), "There are multiple instances of " + key.toString() + " in the database.");
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

    @Override
    public List<ConfigurationKey> getDataKeys() {
        List<ConfigurationEntity> entities = em.createQuery("SELECT e FROM ConfigurationEntity e", ConfigurationEntity.class).getResultList();
        List<ConfigurationKey> keys = new ArrayList<>();
        for (ConfigurationEntity entity : entities) {
            keys.add(new ConfigurationKey(entity.getType(), entity.getVersion()));
        }
        return keys;
    }

    @Override
    public List<ConfigurationKey> getGUIKeys() {
        List<GUIConfigurationEntity> entities = em.createQuery("SELECT e FROM GUIConfigurationEntity e", GUIConfigurationEntity.class).getResultList();
        List<ConfigurationKey> keys = new ArrayList<>();
        for (GUIConfigurationEntity entity : entities) {
            keys.add(new ConfigurationKey(entity.getType(), entity.getVersion()));
        }
        return keys;
    }

    @Override
    public Pair<ReturnResult, String> getDataConfiguration(ConfigurationKey key) {
        List<ConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM ConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        ConfigurationEntity.class)
                        .setParameter("type", key.getType())
                        .setParameter("version", key.getVersion())
                        .getResultList();
        if(list.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_FOUND, null);
        }
        if(list.size() > 1) {
            error(getClass(), "There are multiple instances of " + key.toString() + " in the database.");
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        }
        return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, list.get(0).getData());
    }

    @Override
    public Pair<ReturnResult, String> getGUIConfiguration(ConfigurationKey key) {
        List<GUIConfigurationEntity> list =
                em.createQuery(
                        "SELECT c FROM GUIConfigurationEntity c WHERE c.type = :type AND c.version = :version",
                        GUIConfigurationEntity.class)
                        .setParameter("type", key.getType())
                        .setParameter("version", key.getVersion())
                        .getResultList();
        if(list.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_FOUND, null);
        }
        if(list.size() > 1) {
            error(getClass(), "There are multiple instances of " + key.toString() + " in the database.");
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        }
        return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, list.get(0).getData());
    }

    private Pair<ReturnResult, GUIConfiguration> deserializeGUIConfiguration(List<GUIConfigurationEntity> list) {
        if(list.size() == 0) {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_FOUND, null);
        }

        GUIConfigurationEntity entity = list.get(0);
        if(!StringUtils.hasText(entity.getData())) {
            error(getClass(), "Configuration entity "+entity.toString()+" contained no data.");
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_CONTAINED_NO_DATA, null);
        }

        Pair<SerializationResults, GUIConfiguration> pair = json.deserializeGUIConfiguration(entity.getData());
        if(pair.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
            error(getClass(), "Deserialization failed for configuration "+entity.toString());
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_NOT_VALID, null);
        } else {
            return new ImmutablePair<>(ReturnResult.CONFIGURATION_FOUND, pair.getRight());
        }
    }
}
