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

package fi.uta.fsd.metka.storage.util;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import org.springframework.util.StringUtils;

import java.util.Map;

public final class JSONChecker {
    private JSONChecker() {}

    public static void performNullKeyCheck(RevisionData revision) {
        loopThroughChanges(revision.getChanges(), "");
        loopThroughFields(revision.getFields(), "");
    }

    private static void loopThroughChanges(Map<String, Change> changes, String root) {
        if(StringUtils.hasText(root)) root+=".";
        for(String key : changes.keySet()) {
            if(key == null) {
                System.err.println("Null key in "+root);
            } else {
                Change change = changes.get(key);
                if(change instanceof ContainerChange) {
                    String changeRoot = root + change.getKey();

                    for(Integer i : ((ContainerChange)change).getRows().keySet()) {
                        if(i == null) {
                            System.err.println("Null rowid in "+changeRoot);
                        } else {
                            RowChange rowChange = ((ContainerChange)change).getRows().get(i);
                            loopThroughChanges(rowChange.getChanges(), changeRoot);
                        }
                    }
                }
            }
        }
    }

    private static void loopThroughFields(Map<String, DataField> fields, String root) {
        if(StringUtils.hasText(root)) root += ".";
        for(String key : fields.keySet()) {
            if(key == null) {
                System.err.println("Null key in fields "+root);
            } else {
                DataField field = fields.get(key);
                if(field instanceof ContainerDataField) {
                    checkContainerDataField((ContainerDataField)field, root);
                } else if(field instanceof ValueDataField) {
                    checkValueDataField((ValueDataField) field, root);
                }
            }
        }
    }

    private static void checkContainerDataField(ContainerDataField field, String root) {
        root += field.getKey();

        for(Language language : field.getRows().keySet()) {
            if(language == null) {
                System.err.println("Null language in "+root);
            } else {
                String rowLangRoot = root+"."+language;
                for(DataRow row : field.getRowsFor(language)) {
                    loopThroughFields(row.getFields(), rowLangRoot+"."+row.getRowId());
                }
            }
        }
    }

    private static void checkValueDataField(ValueDataField field, String root) {
        root += field.getKey();
        String tempPath = root + ".original.";
        checkValueDataFieldMap(field.getOriginal(), tempPath);
        tempPath = root + ".current.";
        checkValueDataFieldMap(field.getOriginal(), tempPath);
    }

    private static void checkValueDataFieldMap(Map<Language, ValueContainer> values, String root) {
        for(Language language : values.keySet()) {
            if(language == null) {
                System.err.println("Null language in "+root);
            }
        }
    }
}
