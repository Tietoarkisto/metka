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

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "API_USER")
public class APIUserEntity {
    @Id
    @SequenceGenerator(name="API_USER_ID_SEQ", sequenceName="API_USER_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="API_USER_ID_SEQ")
    @Column(name = "API_USER_ID", updatable = false, insertable = false, unique = true)
    private Long apiUserId;

    @Column(name = "SECRET", unique = true, updatable = false)
    private String secret;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LAST_ACCESS")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastAccess;

    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    public Long getApiUserId() {
        return apiUserId;
    }

    public void setApiUserId(Long apiUserId) {
        this.apiUserId = apiUserId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        if(this.secret == null) this.secret = secret;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
