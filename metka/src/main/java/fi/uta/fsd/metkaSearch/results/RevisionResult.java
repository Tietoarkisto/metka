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

package fi.uta.fsd.metkaSearch.results;

public class RevisionResult implements SearchResult {
    private final ResultList.ResultType type = ResultList.ResultType.REVISION;
    private final Long id;
    private final Integer no;
    private final boolean draft;
    private final boolean approved;
    private final boolean removed;

    public RevisionResult(Long id, Integer no, boolean draft, boolean approved, boolean removed) {
        this.id = id;
        this.no = no;
        this.draft = draft;
        this.approved = approved;
        this.removed = removed;
    }

    @Override
    public ResultList.ResultType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public boolean isDraft() {
        return draft;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isRemoved() {
        return removed;
    }

    @Override
    public String toString() {
        return "Result is "+"ID: "+id+" | NO: "+no;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        RevisionResult that = (RevisionResult) o;

        if(!id.equals(that.id)) return false;
        return no.equals(that.no);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + no.hashCode();
        return result;
    }
}
