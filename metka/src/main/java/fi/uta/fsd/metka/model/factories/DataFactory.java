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

package fi.uta.fsd.metka.model.factories;

import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;


/**
 * Provides functionality common for all RevisionData factories
 */
public abstract class DataFactory {
    /**
     * Creates an empty draft RevisionData.
     *
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param config Configuration that the new revision data should use
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, ConfigurationKey config) {
        RevisionData data = new RevisionData(new RevisionKey(id, no), config);
        data.setState(RevisionState.DRAFT);
        return data;
    }

    /**
     * Used to create new draft revision based on old revision data
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param oldData Old data to which the new data is to be based on
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, RevisionData oldData) {
        return createDraftRevision(id, no, oldData, oldData.getConfiguration());
    }

    /**
     * Used to create new draft reivion based on old revision but using a specific configuration.
     * @param id Id for the new revision data
     * @param no Revision number for the new revision data
     * @param oldData Old data to which the new data is to be based on
     * @param config Configuration key for the new data
     * @return
     */
    public static RevisionData createDraftRevision(Long id, Integer no, RevisionData oldData, ConfigurationKey config) {
        RevisionData data = createDraftRevision(id, no, config);

        // Copies fields from old data to new data using Copy and then normalizes them
        copyDataToNewRevision(oldData, data);

        return data;
    }

    private static void copyDataToNewRevision(RevisionData oldData, RevisionData newData) {

        for(DataField field : oldData.getFields().values()) {
            newData.getFields().put(field.getKey(), field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }
    }
}
