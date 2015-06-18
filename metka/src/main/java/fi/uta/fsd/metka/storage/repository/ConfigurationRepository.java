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

package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface ConfigurationRepository {
    @Transactional(readOnly = false) public ReturnResult insert(Configuration configuration);
    @Transactional(readOnly = false) public ReturnResult insert(GUIConfiguration configuration);
    @Transactional(readOnly = false) public ReturnResult insertDataConfig(String text);
    @Transactional(readOnly = false) public ReturnResult insertGUIConfig(String text);
    public Pair<ReturnResult, Configuration> findConfiguration(String type, Integer version);
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationType type, Integer version);
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, Configuration> findLatestConfiguration(ConfigurationType type);
    public Pair<ReturnResult, Configuration> findLatestByRevisionableId(Long id);
    public Pair<ReturnResult, Configuration> findConfigurationForRevision(Long id, Integer revision);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(String type, Integer version);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationType type, Integer version);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, GUIConfiguration> findLatestGUIConfiguration(ConfigurationType type);

    public List<ConfigurationKey> getDataKeys();
    public List<ConfigurationKey> getGUIKeys();
    public Pair<ReturnResult, String> getDataConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, String> getGUIConfiguration(ConfigurationKey key);
}
