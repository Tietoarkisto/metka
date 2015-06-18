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
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "REVISIONABLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 30)
public abstract class RevisionableEntity {
    @Id
    @SequenceGenerator(name="REVISIONABLE_ID_SEQ", sequenceName="REVISIONABLE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REVISIONABLE_ID_SEQ")
    @Column(name = "REVISIONABLE_ID", updatable = false)
    private Long id;

    @Column(name = "TYPE", insertable=false, updatable = false)
    private String type;

    @Column(name = "CUR_APPROVED_NO")
    private Integer curApprovedNo;

    @Column(name = "LATEST_REVISION_NO")
    private Integer latestRevisionNo;

    @Column(name = "REMOVED")
    private Boolean removed = false;

    @Column(name = "REMOVAL_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime removalDate;

    @Column(name = "REMOVED_BY")
    private String removedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCurApprovedNo() {
        return curApprovedNo;
    }

    public void setCurApprovedNo(Integer curApprovedNo) {
        this.curApprovedNo = curApprovedNo;
    }

    public Integer getLatestRevisionNo() {
        return latestRevisionNo;
    }

    public void setLatestRevisionNo(Integer latestRevisionNo) {
        this.latestRevisionNo = latestRevisionNo;
    }

    public Boolean getRemoved() {
        return (removed == null) ? false : removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null) ? false : removed;
    }

    public LocalDateTime getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(LocalDateTime removalDate) {
        this.removalDate = removalDate;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }

    public RevisionKey latestRevisionKey() {
        return new RevisionKey(getId(), getLatestRevisionNo());
    }

    public RevisionKey currentApprovedRevisionKey() {
        return new RevisionKey(getId(), getCurApprovedNo());
    }

    /**
     * Simple check to see if this revisionable has an open DRAFT.
     * Assuming that everything else works as it should then there's an open draft if and only if currentApprovedNo is null
     * or latestRevisionNo is larger than currentApprovedNo.
     * Additional check should be made to make sure that the revision actually is what it should be but if it's not
     * then it's a case of revision being out of sync with revisionable.
     * @return True if there should be a draft, false otherwise
     */
    public boolean hasDraft() {
        if(curApprovedNo == null) {
            return true;
        }
        if(latestRevisionNo > curApprovedNo) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionableEntity that = (RevisionableEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
