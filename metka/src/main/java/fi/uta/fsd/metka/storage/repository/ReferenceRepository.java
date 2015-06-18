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

package fi.uta.fsd.metka.storage.repository;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.storage.entity.MiscJSONEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The aim of this repository is to function as a general implementation of the reference system for METKA.
 * The reference system allows arbitrary collection of values that user can choose between or simple copy
 * fields that always hold the same value that some other field in some other object.
 * These reference values can be collected from other Revisionable objects or from miscellaneous collections
 * of json-formatted data.
 * Thus it is vital that there is an interface that knows how to handle a given reference with no need
 * for spreading out the functionality.
 *
 * Any time there comes a need to collect values for references, find root objects (objects that function
 * as a starting point for reference path) for references, check current reference value or title, etc. then
 * the functionality should be found from here.
 *
 * In most cases this Repository should never have to make any changes, it should only be used to collect or
 * find values from other sources.
 */
@Transactional(readOnly = true)
public interface ReferenceRepository {
    public List<RevisionableEntity> getRevisionablesForReference(Reference reference);
    public MiscJSONEntity getMiscJsonForReference(Reference reference);
    public Pair<ReturnResult, JsonNode> getMiscJson(String key);
    /*public RevisionEntity getRevisionForReference(RevisionableEntity revisionable, Reference reference);
    public RevisionEntity getRevisionForReferencedRevisionable(Reference reference, String value);*/
}
