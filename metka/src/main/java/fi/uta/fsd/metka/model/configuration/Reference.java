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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.ReferenceType;

import java.util.Objects;

/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_reference.graphml
 * Additional specification is found from Reference specification.odt
 */
@JsonIgnoreProperties("_comment")
public class Reference {
    private final String key;
    private final ReferenceType type;
    private final String target;
    private final String valuePath;
    private String titlePath = null;
    private Boolean approvedOnly = false;
    private Boolean ignoreRemoved = false;

    @JsonCreator
    public Reference(@JsonProperty("key")String key, @JsonProperty("type")ReferenceType type, @JsonProperty("target")String target, @JsonProperty("valuePath")String valuePath, @JsonProperty("titlePath")String titlePath) {
        this.key = key;
        this.type = type;
        this.target = target;
        this.valuePath = valuePath;
        this.titlePath = titlePath;
    }

    public String getKey() {
        return key;
    }

    public ReferenceType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getValuePath() {
        return valuePath;
    }

    public String getTitlePath() {
        return titlePath;
    }

    public void setTitlePath(String titlePath) {
        this.titlePath = titlePath;
    }

    public Boolean getApprovedOnly() {
        return approvedOnly == null ? false : approvedOnly;
    }

    public void setApprovedOnly(Boolean approvedOnly) {
        this.approvedOnly = approvedOnly == null ? false : approvedOnly;
    }

    public Boolean getIgnoreRemoved() {
        return ignoreRemoved == null ? false : ignoreRemoved;
    }

    public void setIgnoreRemoved(Boolean ignoreRemoved) {
        this.ignoreRemoved = ignoreRemoved == null ? false : ignoreRemoved;
    }

    @JsonIgnore
    public String[] getValuePathParts() {
        if(valuePath == null) {
            return null;
        }
        String[] parts = valuePath.split("\\.");
        if(parts.length == 0) {
            parts = new String[1];
            parts[0] = valuePath;
        }
        return parts;
    }

    @JsonIgnore
    public String[] getTitlePathParts() {
        if(titlePath == null) {
            return null;
        }
        String[] parts = titlePath.split("\\.");
        if(parts.length > 0) {
            return parts;
        } else if(parts.length == 0 && titlePath != null && !titlePath.equals("")) {
            parts = new String[1];
            parts[0] = titlePath;
            return parts;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference = (Reference) o;

        return Objects.equals(key, reference.key) && Objects.equals(this.getValuePath(), reference.getValuePath()) && Objects.equals(this.getType(), reference.getType())
                && Objects.equals(this.getTarget(), reference.getTarget()) && Objects.equals(this.getTitlePath(), reference.getTitlePath()) && Objects.equals(this.getApprovedOnly(), reference.getApprovedOnly())
                && Objects.equals(this.getIgnoreRemoved(), reference.getIgnoreRemoved());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, valuePath, type, target, titlePath, approvedOnly, ignoreRemoved);
    }

    @JsonIgnore
    public Reference copy() {
        Reference ref = new Reference(key, type, target, valuePath, titlePath);
        ref.setApprovedOnly(approvedOnly);
        ref.setIgnoreRemoved(ignoreRemoved);

        return ref;
    }
}
