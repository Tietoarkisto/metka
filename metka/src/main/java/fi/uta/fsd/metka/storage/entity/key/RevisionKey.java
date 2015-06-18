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

package fi.uta.fsd.metka.storage.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RevisionKey implements Serializable {
    public static final long serialVersionUID = 1L;

    @Column(name = "REVISIONABLE_ID", updatable = false)
    private Long revisionableId;

    @Column(name = "REVISION_NO", updatable = false)
    private Integer revisionNo;

    public RevisionKey() {
    }

    public RevisionKey(Long revisionableId, Integer revisionNo) {
        this.revisionableId = revisionableId;
        this.revisionNo = revisionNo;
    }

    public Long getRevisionableId() {
        return revisionableId;
    }

    public void setRevisionableId(Long revisionableId) {
        this.revisionableId = revisionableId;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionKey that = (RevisionKey) o;

        if (!revisionNo.equals(that.revisionNo)) return false;
        if (!revisionableId.equals(that.revisionableId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = revisionableId.hashCode();
        result = 31 * result + revisionNo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={revisionableId: "+revisionableId+", revisionNo: "+revisionNo+"}]";
    }

    public static RevisionKey fromModelKey(fi.uta.fsd.metka.model.general.RevisionKey key) {
        return new RevisionKey(key.getId(), key.getNo());
    }

    public fi.uta.fsd.metka.model.general.RevisionKey toModelKey() {
        return new fi.uta.fsd.metka.model.general.RevisionKey(getRevisionableId(), getRevisionNo());
    }
}
