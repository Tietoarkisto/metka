<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.otherStudies"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="rowContainer containsTranslations">
		<%-- relatedStudyDialog --%>
		<div class="studyLevelDataSetContainer translated translationFi">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.relatedStudy"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.relatedStudy"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="relatedMaterialRow">
						<td>foo</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>
		<div class="studyLevelDataSetContainer translated translationSv">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.relatedStudy"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.relatedStudy"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="relatedMaterialRow">
						<td>bar</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link"><spring:message code='general.buttons.add'/></a></div>
		</div>
		<div class="studyLevelDataSetContainer translated translationEn">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.relatedStudy"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.relatedStudy"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="relatedMaterialRow">
						<td>foo</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>
	</div>
	<div class="rowContainer containsTranslations">
		<%-- otherStudyDialog --%>
		<div class="studyLevelDataSetContainer translated translationFi">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.otherStudies"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.otherStudies.shortDescription"/></th>
					<th><spring:message code="study.view.description.otherStudies.exactDescription"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="otherMaterialRow">
						<td>http://vesa.lib.helsinki.fi/</td>
						<td>foo bar</td>
						<td>g in odnibh euismod.</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>
		<div class="studyLevelDataSetContainer translated translationSv">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.otherStudies"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.otherStudies.shortDescription"/></th>
					<th><spring:message code="study.view.description.otherStudies.exactDescription"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="otherMaterialRow">
						<td>http://vesa.lib.helsinki.fi/</td>
						<td>foo bar</td>
						<td>g in odio in, co nunc pulvinar, ac hendrerit nibh euismod.</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>
		<div class="studyLevelDataSetContainer translated translationEn">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.otherStudies"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.otherStudies.shortDescription"/></th>
					<th><spring:message code="study.view.description.otherStudies.exactDescription"/></th>
					<th></th></tr>
				</thead>
				<tbody>
					<tr class="otherMaterialRow">
						<td>http://vesa.lib.helsinki.fi/</td>
						<td>foo bar</td>
						<td>g in odc hendrerit nibh euismod.</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>
	</div>
	<div class="rowContainer containsTranslations">
		<%-- remarksDialog (not done) --%>
		<div class="studyLevelDataSetContainer translated translationFi">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.remarksOnPublishing"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.notices"/></th><th></th></tr>
				</thead>
				<tbody>
					<tr class="otherRefsRow">
						<td>foo</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>	
		<div class="studyLevelDataSetContainer translated translationSv">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.remarksOnPublishing"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.notices"/></th><th></th></tr>
				</thead>
				<tbody>
					<tr class="otherRefsRow">
						<td>bar</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>	
		<div class="studyLevelDataSetContainer translated translationEn">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.otherStudies.remarksOnPublishing"/></div>
			<table class="metkaTable">
				<thead>
					<tr><th><spring:message code="study.view.description.otherStudies.notices"/></th><th></th></tr>
				</thead>
				<tbody>
					<tr class="otherRefsRow">
						<td>foo</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
		</div>								
	</div>
</div>		