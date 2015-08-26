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

package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.*;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.ReportRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    /**
     * Generates simple XML-report of all studies and returns it in string form
     * @return  String
     */
    @Override
    public String gatherGeneralReport() {
        List<StudyEntity> studies = em.createQuery("SELECT s FROM StudyEntity s", StudyEntity.class).getResultList();
        List<GeneralStudyReportObject> reportStudies = new ArrayList<>();

        for(StudyEntity study : studies) {
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(study.getId(), false, ConfigurationType.STUDY);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Did not find revision for study with id " + study.getId() + ". Returned result was " + dataPair.getLeft());
                continue;
            }

            RevisionData revision = dataPair.getRight();
            GeneralStudyReportObject reportStudy = new GeneralStudyReportObject();
            // StudyId
            Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.STUDYID));
            reportStudy.studyId = (field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));

            // State including removed
            reportStudy.state = (study.getRemoved() ? UIRevisionState.REMOVED : UIRevisionState.fromRevisionState(revision.getState())).name();

            // Title
            field = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
            reportStudy.title = (field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));

            // Terms of use value (it's easy to fetch the title if required)
            field = revision.dataField(ValueDataFieldCall.get(Fields.TERMSOFUSE));
            reportStudy.termsofuse = (field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));

            // Relpubl from publications referenced in study

            Pair<StatusCode, ReferenceContainerDataField> references = revision.dataField(ReferenceContainerDataFieldCall.get(Fields.PUBLICATIONS));
            if(references.getLeft() == StatusCode.FIELD_FOUND && !references.getRight().getReferences().isEmpty()) {
                for(ReferenceRow reference : references.getRight().getReferences()) {
                    dataPair = revisions.getLatestRevisionForIdAndType(Long.parseLong(reference.getActualValue()), false, ConfigurationType.PUBLICATION);
                    if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                        continue;
                    }
                    field = dataPair.getRight().dataField(ValueDataFieldCall.get(Fields.PUBLICATIONRELPUBL));
                    if(field.getLeft() != StatusCode.FIELD_FOUND || !field.getRight().hasValueFor(Language.DEFAULT)) {
                        continue;
                    }
                    reportStudy.relpubls.add(field.getRight().getActualValueFor(Language.DEFAULT));
                }
            }

            // Varquantity
            ContainerDataField variablesCon = revision.dataField(ContainerDataFieldCall.get(Fields.STUDYVARIABLES)).getRight();
            if(variablesCon == null) {
                continue;
            }

            DataRow row = variablesCon.getRowWithFieldValue(Language.DEFAULT, Fields.VARIABLESLANGUAGE, new Value(Language.DEFAULT.toValue())).getRight();
            if(row == null) {
                continue;
            }
            field = row.dataField(ValueDataFieldCall.get(Fields.VARIABLES));
            if(field.getLeft() == StatusCode.FIELD_FOUND && field.getRight().hasValueFor(Language.DEFAULT)) {
                dataPair = revisions.getRevisionData(field.getRight().getActualValueFor(Language.DEFAULT));
                if(dataPair.getLeft() == ReturnResult.REVISION_FOUND) {
                    field = dataPair.getRight().dataField(ValueDataFieldCall.get(Fields.VARQUANTITY));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        reportStudy.varquantity = field.getRight().getActualValueFor(Language.DEFAULT);
                    }
                }
            }

            reportStudies.add(reportStudy);
        }
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element root = doc.createElement("Report");
            doc.appendChild(root);

            for(GeneralStudyReportObject reportStudy : reportStudies) {
                Element study = doc.createElement("Study");

                Element studyId = doc.createElement("StudyId");
                studyId.appendChild(doc.createTextNode(reportStudy.studyId));
                study.appendChild(studyId);

                Element state = doc.createElement("State");
                state.appendChild(doc.createTextNode(reportStudy.state));
                study.appendChild(state);

                Element title = doc.createElement("Title");
                title.appendChild(doc.createTextNode(reportStudy.title));
                study.appendChild(title);

                Element terms = doc.createElement("TermsOfUse");
                terms.appendChild(doc.createTextNode(reportStudy.termsofuse));
                study.appendChild(terms);

                Element vars = doc.createElement("VarQuantity");
                vars.appendChild(doc.createTextNode(reportStudy.varquantity));
                study.appendChild(vars);

                Element rels = doc.createElement("Relpubls");
                for(String s : reportStudy.relpubls) {
                    Element rel = doc.createElement("Relpubl");
                    rel.appendChild(doc.createTextNode(s));
                    rels.appendChild(rel);
                }
                study.appendChild(rels);

                root.appendChild(study);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            transformer.transform(source, result);

            return sw.toString();
        } catch(Exception e) {
            return "Exception while forming report";
        }
    }

    private static class GeneralStudyReportObject {
        public String studyId = "";
        public String state = "";
        public String title = "";
        public String termsofuse = "";
        public final List<String> relpubls = new ArrayList<>();
        public String varquantity = "";
    }
}
