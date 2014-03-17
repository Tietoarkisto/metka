<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.otherNames"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="dataRow containsTranslations">
		<c:forEach items="${study.data.description.altTitles}" var="altTitle" varStatus="i">
			<div class="studyLevelDataSetContainer translated translationFi">
				<label id="studyLevelAltTitle"><spring:message code="study.view.description.concurrentName"/>
					<c:if test="${varStatus == 0}">
						&nbsp;<img title="<spring:message code='general.buttons.add'/>" class="addRow" id="addAltTitle" src="../images/add.png"/>
					</c:if>
				</label>
				<input type="text" name="" value="${altTitle.title}" />
				<c:if test="${varStatus > 0}">
					<img class="removeRow" id="removeAltTitle" style="display:none;" title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/>
				</c:if>
			</div>
			<div class="studyLevelDataSetContainer translated translationSv">
				<label id="studyLevelAltTitle"><spring:message code="study.view.description.concurrentName"/></label>
				<input type="text" name="" value="toinen nimi" />
			</div>
			<div class="studyLevelDataSetContainer translated translationEn">
				<label id="studyLevelAltTitle"><spring:message code="study.view.description.concurrentName"/></label>
				<input type="text" name="" value="toinen nimi" />
			</div>
		</c:forEach>
	</div>
	<div class="dataRow containsTranslations">
		<%-- parTitleDialog --%>
		<div class="studyLevelDataSetContainer translated translationFi">
			<div class="studyLevelTableTitle"><spring:message code="study.view.description.parTitles"/></div>
			<table class="metkaTable studyLevelTwoHeadersTable">
				<thead>
					<tr><th><spring:message code="study.view.description.parTitle.name"/></th><th><spring:message code="study.view.description.parTitle.language"/></th><th></th></tr>
				</thead>
				<tbody>
					<c:forEach items="${study.data.description.otherLanguageTitles}" var="title">
						<tr class="parTitleRow">
							<td>${title.name}</td>
							<td>${title.language}</td>
							<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>	
		</div>
	</div>							
</div>