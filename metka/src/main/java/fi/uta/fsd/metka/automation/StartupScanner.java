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

package fi.uta.fsd.metka.automation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;

public class StartupScanner {
    @Autowired
    private ConfigurationRepository configRepo;
    @Autowired
    private MiscJSONRepository miscJsonRepo;
    @Autowired
    private JSONUtil json;

    @Value("${dir.autoload}")
    private String rootFolder;

    /**
     * Gathers data configurations from file and saves them to database
     */
    @PostConstruct
    public void scanForConfigurations() {
        Logger.debug(getClass(), "Scanning for configurations.");
        File confDir = new File(rootFolder+"configuration");

        Collection<File> files = FileUtils.listFiles(confDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<SerializationResults, Configuration> conf = json.deserializeDataConfiguration(file);
            if(conf.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                Logger.error(getClass(), "Failed at deserializing "+file.getName());
                continue;
            }
            Pair<ReturnResult, Configuration> existing = configRepo.findConfiguration(conf.getRight().getKey());
            if(existing.getLeft() != ReturnResult.CONFIGURATION_FOUND && existing.getLeft() != ReturnResult.DATABASE_DISCREPANCY) {
                configRepo.insert(conf.getRight());
            }
        }
    }

    /**
     * Gathers miscellaneous JSON-files from file and saves them to database
     */
    @PostConstruct
    public void scanForMiscJSON() {
        Logger.debug(getClass(), "Scanning for miscellaneous json files.");
        File miscDir = new File(rootFolder+"misc");

        Collection<File> files = FileUtils.listFiles(miscDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<SerializationResults, JsonNode> misc = json.deserializeToJsonTree(file);

            if(misc.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS){
                Logger.error(getClass(), "Failed at deserializing "+file.getName());
                continue;
            }
            JsonNode key = misc.getRight().get("key");
            if(key == null || key.getNodeType() != JsonNodeType.STRING) {
                // Not key or key is not text, ignore
                continue;
            }
            Pair<ReturnResult, JsonNode> existing = miscJsonRepo.findByKey(key.textValue());
            if(existing.getLeft() != ReturnResult.MISC_JSON_FOUND) {
                miscJsonRepo.insert(misc.getRight());
            }
        }
    }

    /**
     * Gathers gui-configuration from file and saves them to database
     */
    @PostConstruct
    public void scanForGUIConfigurations() {
        Logger.debug(getClass(), "Scanning for gui configurations.");
        File guiDir = new File(rootFolder+"gui");

        Collection<File> files = FileUtils.listFiles(guiDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<SerializationResults, GUIConfiguration> gui = json.deserializeGUIConfiguration(file);

            if(gui.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                Logger.error(getClass(), "Failed at deserializing "+file.getName());
                continue;
            }
            Pair<ReturnResult, GUIConfiguration> existing = configRepo.findGUIConfiguration(gui.getRight().getKey());
            if(existing.getLeft() != ReturnResult.CONFIGURATION_FOUND && existing.getLeft() != ReturnResult.DATABASE_DISCREPANCY) {
                configRepo.insert(gui.getRight());
            }
        }
    }
}