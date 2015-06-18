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

package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.TargetType;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_restrictions.graphml
 */
public class Target {
    private final TargetType type;
    private final String content;
    private final List<Target> targets = new ArrayList<>();
    private final List<Check> checks = new ArrayList<>();

    @JsonIgnore
    private Target parent;

    @JsonCreator
    public Target(@JsonProperty("type")TargetType type, @JsonProperty("content")String content) {
        this.type = type;
        this.content = content;
    }

    public TargetType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public Target getParent() {
        return parent;
    }

    public void setParent(Target parent) {
        this.parent = parent;
    }

    public void initParents(Target parent) {
        setParent(parent);
        for(Target t : targets) {
            t.parent = this;
            t.initParents(this);
        }
        for(Check c : checks) {
            c.setParent(this);
            c.initParents();
        }
    }

    public void initParents() {
        initParents(null);
    }

    public Target copy() {
        Target target = new Target(type, content);
        for(Target t : targets) {
            target.targets.add(t.copy());
        }
        for(Check c : checks) {
            target.checks.add(c.copy());
        }
        return target;
    }
}
