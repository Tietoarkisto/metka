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

package fi.uta.fsd.metka.ddi.reader;

import codebook25.CodeBookType;
import codebook25.OtherMatType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.Map;

class DDIReadOtherMaterialDescription extends DDIReadSectionBase {
    DDIReadOtherMaterialDescription(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        super(revision, language, codeBook, info, configuration);
    }

    @Override
    ReturnResult read() {
        /*
         * We know that language has to be DEFAULT and that description tab should be clear so we can just insert the new data in
         */
        if(!hasContent(codeBook.getOtherMatArray())) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerResult = getContainer(Fields.OTHERMATERIALS);
        if(containerResult.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerResult.getLeft();
        }
        Pair<ContainerDataField, Map<String, Change>> container = containerResult.getRight();
        for(OtherMatType other : codeBook.getOtherMatArray()) {
            Pair<StatusCode, DataRow> row = container.getLeft().insertNewDataRow(language, container.getRight());
            if(row.getLeft() != StatusCode.ROW_INSERT) {
                continue;
            }
            if(StringUtils.hasText(other.getURI())) {
                valueSet(row.getRight(), Fields.OTHERMATERIALURI, other.getURI());
            }
            if(hasContent(other.getTxtArray()) && StringUtils.hasText(getText(other.getTxtArray(0)))) {
                valueSet(row.getRight(), Fields.OTHERMATERIALTEXT, getText(other.getTxtArray(0)));
            }
            if(hasContent(other.getLablArray()) && StringUtils.hasText(getText(other.getLablArray(0)))) {
                valueSet(row.getRight(), Fields.OTHERMATERIALLABEL, getText(other.getLablArray(0)));
            }
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }
}
