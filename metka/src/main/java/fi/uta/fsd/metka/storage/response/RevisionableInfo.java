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

package fi.uta.fsd.metka.storage.response;

import fi.uta.fsd.metka.enums.ConfigurationType;
import org.joda.time.LocalDateTime;

public class RevisionableInfo {
    public static final RevisionableInfo FALSE = new RevisionableInfo();

    private final Long id;
    private final ConfigurationType type;
    private final Integer approved;
    private final Integer current;
    private final Boolean removed;
    private final LocalDateTime removedAt;
    private final String removedBy;

    public RevisionableInfo() {
        id = null;
        type = null;
        approved = null;
        current = null;
        removed = false;
        removedAt = null;
        removedBy = null;
    }

    public RevisionableInfo(Long id, ConfigurationType type, Integer approved, Integer current,
                            Boolean removed, LocalDateTime removedAt, String removedBy) {
        this.id = id;
        this.type = type;
        this.approved = approved;
        this.current = current;
        this.removed = removed;
        this.removedAt = removedAt;
        this.removedBy = removedBy;
    }

    public Long getId() {
        return id;
    }

    public ConfigurationType getType() {
        return type;
    }

    public Integer getApproved() {
        return approved;
    }

    public Integer getCurrent() {
        return current;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public String getRemovedBy() {
        return removedBy;
    }
}
