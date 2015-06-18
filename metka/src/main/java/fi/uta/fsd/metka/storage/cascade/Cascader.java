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

package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Cascader {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionApproveRepository approve;

    @Autowired
    private RevisionRemoveRepository remove;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionHandlerRepository handler;

    public boolean cascade(CascadeInstruction instruction, RevisionData revision, List<Target> targets, Configuration configuration) {
        initParents(revision, targets);
        return DataFieldCascader.cascade(instruction, targets, revision, configuration, new RepositoryHolder(configurations, revisions, save, approve, remove, edit, handler));
    }

    /**
     * Calls init parent on all targets in list. Can be redundant but shouldn't take that much time.
     * This is required since we need to be able to navigate upwards in target tree to check the context of conditions.
     * @param targets   List of Targets
     */
    private void initParents(RevisionData revision, List<Target> targets) {
        revision.initParents();
        for(Target t : targets) {
            t.initParents();
        }
    }

    static class RepositoryHolder {
        private final ConfigurationRepository configurations;
        private final RevisionRepository revisions;
        private final RevisionSaveRepository save;
        private final RevisionApproveRepository approve;
        private final RevisionRemoveRepository remove;
        private final RevisionEditRepository edit;
        private final RevisionHandlerRepository handler;

        public RepositoryHolder(
                ConfigurationRepository configurations, RevisionRepository revisions, RevisionSaveRepository save, RevisionApproveRepository approve,
                RevisionRemoveRepository remove, RevisionEditRepository edit, RevisionHandlerRepository handler) {
            this.configurations = configurations;
            this.revisions = revisions;
            this.save = save;
            this.approve = approve;
            this.remove = remove;
            this.edit = edit;
            this.handler = handler;
        }

        public ConfigurationRepository getConfigurations() {
            return configurations;
        }

        public RevisionRepository getRevisions() {
            return revisions;
        }

        public RevisionSaveRepository getSave() {
            return save;
        }

        public RevisionApproveRepository getApprove() {
            return approve;
        }

        public RevisionRemoveRepository getRemove() {
            return remove;
        }

        public RevisionEditRepository getEdit() {
            return edit;
        }

        public RevisionHandlerRepository getHandler() {
            return handler;
        }
    }
}