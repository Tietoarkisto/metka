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

package fi.uta.fsd.metka.storage.entity;

import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.enums.RevisionState;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "REVISION",
        indexes = {
                @Index(name = "revision_index_status", columnList = "INDEX_STATUS"),
                @Index(name = "revision_index_handled", columnList = "INDEXING_HANDLED"),
                @Index(name = "revision_index_requested", columnList = "INDEXING_REQUESTED")
        })
/*@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)*/
public class RevisionEntity {
    @EmbeddedId
    private RevisionKey key;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private RevisionState state;

    @Lob
    @Column(name = "DATA")
    @Type(type="org.hibernate.type.StringClobType")
    private String data;

    @Column(name = "INDEXING_REQUESTED")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime indexingRequested;

    @Column(name = "INDEXING_HANDLED")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime indexingHandled;

    @Column(name = "INDEX_STATUS")
    private String indexStatus;

    @Column(name = "LATEST")
    private String latest;

    public RevisionEntity() {
    }

    public RevisionEntity(RevisionKey key) {
        this.key = key;
    }

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getIndexingRequested() {
        return indexingRequested;
    }

    public void setIndexingRequested(LocalDateTime indexingRequested) {
        this.indexingRequested = indexingRequested;
    }

    public LocalDateTime getIndexingHandled() {
        return indexingHandled;
    }

    public void setIndexingHandled(LocalDateTime indexingHandled) {
        this.indexingHandled = indexingHandled;
    }

    public String getIndexStatus() {
        return indexStatus;
    }

    public void setIndexStatus(String indexStatus) {
        this.indexStatus = indexStatus;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionEntity that = (RevisionEntity) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    public enum IndexStatus {
        INDEXED    // Revision has been indexed
    }
}
