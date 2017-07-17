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

import codebook25.AbstractTextType;
import codebook25.CodeBookType;
import fi.uta.fsd.metka.ddi.nodevisitor.DDIReadNodeVisitor;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

abstract class DDIReadSectionBase {
    protected final RevisionData revision;
    protected final Language language;
    protected final CodeBookType codeBook;
    protected final DateTimeUserPair info;
    protected final Configuration configuration;

    protected DDIReadSectionBase(RevisionData revision, Language language, CodeBookType codeBook, DateTimeUserPair info, Configuration configuration) {
        this.revision = revision;
        this.language = language;
        this.codeBook = codeBook;
        this.info = info;
        this.configuration = configuration;
    }

    abstract ReturnResult read();

    protected StatusCode valueSet(String key, String value) {
        return valueSet(revision, key, value);
    }

    protected StatusCode valueSet(DataFieldContainer dataFields, String key, String value) {
        return valueSet(dataFields, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(String key, String value, Language language) {
        return valueSet(revision, key, value, language, revision.getChanges());
    }

    protected StatusCode valueSet(DataFieldContainer dataFields, String key, String value, Language language, Map<String, Change> changes) {
        return dataFields.dataField(ValueDataFieldCall.set(key, new Value(value), language).setInfo(info).setChangeMap(changes)).getLeft();
    }

    protected <T> boolean hasContent(T[] array) {
        return array != null && array.length > 0;
    }

    protected Pair<ReturnResult, ReferenceContainerDataField> getReferenceContainer(DataFieldContainer dataFields, String key, Map<String, Change> changes) {
        Pair<StatusCode, ReferenceContainerDataField> pair = dataFields.dataField(ReferenceContainerDataFieldCall.set(key).setChangeMap(changes).setInfo(info));
        if(!(pair.getLeft() == StatusCode.FIELD_FOUND || pair.getLeft() == StatusCode.FIELD_INSERT)) {
            return new ImmutablePair<>(ReturnResult.OPERATION_FAIL, null);
        }
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, pair.getRight());
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> getContainer(String key) {
        return getContainer(key, revision, revision.getChanges());
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> getContainer(String key, RevisionData data) {
        return getContainer(key, data, data.getChanges());
    }

    protected Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> getContainer(String key, DataFieldContainer fieldContainer, Map<String, Change> changeMap) {
        Pair<StatusCode, ContainerDataField> container = fieldContainer.dataField(ContainerDataFieldCall.set(key).setInfo(info));
        if(!(container.getLeft() == StatusCode.FIELD_FOUND || container.getLeft() == StatusCode.FIELD_INSERT)) {
            // No need to continue insert, we have a problem
            return new ImmutablePair<>(ReturnResult.OPERATION_FAIL, null);
        }
        ContainerChange change = (ContainerChange)changeMap.get(key);
        if(change == null) {
            change = new ContainerChange(key);
            changeMap.put(key, change);
        }
        Pair<ContainerDataField, Map<String, Change>> pair = new ImmutablePair<>(container.getRight(), changeMap);
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, pair);
    }

    protected ReturnResult fillSingleValueContainer(DataFieldContainer data, Map<String, Change> changeMap, String containerKey, String fieldKey, AbstractTextType[] tts) {
        if(!hasContent(tts)) {
            return ReturnResult.OPERATION_SUCCESSFUL;
        }
        Pair<ReturnResult, Pair<ContainerDataField, Map<String, Change>>> containerPair = getContainer(containerKey, data, changeMap);
        if(containerPair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            return containerPair.getLeft();
        }
        ContainerDataField container = containerPair.getRight().getLeft();
        for(AbstractTextType tt : tts) {
            if(!StringUtils.hasText(getText(tt))) {
                continue;
            }
            container.getOrCreateRowWithFieldValue(language, fieldKey, new Value(getText(tt)), changeMap, info);
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    protected <T extends XmlObject> String getText(T att) {
        if(att == null) return "";

        Document doc = Jsoup.parse(att.xmlText());
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

        List<Node> childNodes = doc.body().childNodes();

        if ( childNodes.size() == 1 && "#text".equals(childNodes.get(0).nodeName())) {
            // only text, parse as plaintext.
            XmlCursor cursor = att.newCursor();
            String value = cursor.getTextValue();
            cursor.dispose();
            return value == null ? "" : value;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Node child : childNodes) {
                child.traverse(new DDIReadNodeVisitor());
                child.html(sb);
            }
            return sb.toString();
        }
    }

}
