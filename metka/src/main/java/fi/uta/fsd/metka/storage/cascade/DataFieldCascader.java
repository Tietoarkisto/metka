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

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.restrictions.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.hibernate.annotations.Cascade;

import java.util.List;

class DataFieldCascader {
    private DataFieldCascader() {}

    static boolean cascade(CascadeInstruction instruction, List<Target> targets, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        boolean result = true;
        for(Target target : targets) {
            if(!cascade(instruction, target, context, configuration, repositories)) {
                result = false;
            }
        }
        return result;
    }

    static boolean cascade(CascadeInstruction instruction, Target target, DataFieldContainer context, Configuration configuration, Cascader.RepositoryHolder repositories) {
        switch(target.getType()) {
            case QUERY:
                return QueryTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case NAMED:
                return NamedTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case PARENT:
                return ParentTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case CHILDREN:
                return ChildrenTargetCascader.cascade(instruction, target, context, configuration, repositories);
            case FIELD:
                // This is the only one that will actually cascade
                return FieldTargetCascader.cascade(instruction, target, context, configuration, repositories);
            default:
                // This catches types that are invalid for this point of cascade operation such as VALUE
                Logger.error(DataFieldCascader.class, "Reached TargetType " + target.getType());
                return false;

        }
    }
}