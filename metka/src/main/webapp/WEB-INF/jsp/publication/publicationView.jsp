<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
            	<h1 class="page-header"><spring:message code="publication.view.title"/></h1>
				<div class="upperContainer">
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.publicationYear"/></label><input type="text" name="publicationYear" value="" /></div>
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.publishable"/></label><select name="publishable"><option>Kyll�</option></select></div>
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.id"/></label><input type="text" class="unModifiable" readonly="readonly" name="publicationId" value="" /></div>
					</div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.firstSaveDay"/></label><input type="text" class="unModifiable" readonly="readonly" name="firstSaveDay" value="" /></div>
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.noticeType"/></label><select name="noticeType"><option>Ei tietoa</option></select></div>
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.publicationLanguage"/></label><select name="language"><option>Suomi</option></select></div>
					</div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.latestModificationDay"/></label><input type="text" name="latestChangeDay" value="" /></div>
						<div class="publicationDataSetContainer"><label><spring:message code="general.handler"/></label><input type="text" name="handler" value="" /></div>
					</div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.publicationTitle"/></label><textarea name="title"></textarea></div>
					</div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.relPubl"/></label><textarea name="relPubl"></textarea></div>
					</div>
					<div class="dataRow">
						<div class="publicationDataSetContainer"><label><spring:message code="publication.view.publicationNotifications"/></label><textarea name="notifications"></textarea></div>
					</div>
				</div>
				<div class="dataRow" style="display: inline-block; margin-top: 10px;">
					<div class="publicationRowTitle"><spring:message code="publication.view.relatedPeople"/>t&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
					<table id="publicationPersonTable" class="metkaTable sortableTable">
						<thead>
							<tr><th><spring:message code="general.lastName"/></th><th><spring:message code="general.firstName"/></th><th></th></tr>
						</thead>
						<tbody>
							<c:forEach items="${publication.data.relatedPeople}" var="person">
								<tr>
									<td>${person.lastName}</td>
									<td>${person.firstName}</td>
									<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>	
					<div class="publicationTableActionLinkHolder"><a href="dialogs/attachPersonPublicationDialog.html" class="addRow fancyboxpopup fancybox.ajax"><spring:message code='general.buttons.add'/></a></div>						
				</div>
				<div class="dataRow">
					<div class="publicationRowTitle"><spring:message code="publication.view.pids"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
					<table id="publicationIdentificationTable" class="metkaTable sortableTable">
						<thead>
							<tr><th><spring:message code="publication.view.pid"/></th><th><spring:message code="publication.view.pidType"/></th><th></th></tr>
						</thead>
						<tbody>
							<c:forEach items="${publication.data.identifications}" var="identification">
								<tr>
									<td>${identification.name}</td>
									<td>${identification.type}</td>
									<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>	
					<div class="publicationTableActionLinkHolder"><a href="dialogs/attachIndentificationDialog.html" class="addRow fancyboxpopup fancybox.ajax"><spring:message code='general.buttons.add'/></a></div>						
				</div>		
				<div class="dataRow">
					<div class="publicationRowTitle"><spring:message code="publication.view.relatedStudies"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
					<table id="publicationMaterialTable" class="metkaTable sortableTable">
						<thead>
							<tr><th><spring:message code="study.search.form.studyNumber"/></th><th><spring:message code="study.search.form.studyName"/></th><th></th></tr>
						</thead>
						<tbody>
							<c:forEach items="${publication.data.relatedStudies}" var="study">
								<tr class="publicationMaterialRow">
									<td>${study.id}</td>
									<td>${study.data.name}</td>
									<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>	
					<div class="publicationTableActionLinkHolder"><a href="dialogs/attachMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax"><spring:message code='general.buttons.add'/></a></div>						
				</div>					
				<div class="dataRow">
					<div class="publicationRowTitle"><spring:message code="publication.view.relatedSeries"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
					<table id="publicationSeriesTable" class="metkaTable sortableTable">
						<thead>
							<tr><th><spring:message code="series.view.abbreviation"/></th><th><spring:message code="series.view.name"/></th><th></th></tr>
						</thead>
						<tbody>
							<c:forEach items="${publication.data.series}" var="series">
								<tr class="publicationSeriesRow">
									<td>${series.abbreviation}</td>
									<td>${series.name}</td>
									<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
								</tr>
							</c:forEach>
						</tbody>

					</table>	
					<div class="publicationTableActionLinkHolder"><a href="dialogs/attachSeriesDialog.html" class="addRow fancyboxpopup fancybox.ajax"><spring:message code='general.buttons.add'/></a></div>						
				</div>
				
				<div class="buttonsHolder" style="margin-bottom:20px;">
					<input type="button" class="editButton searchFormInput" value="<spring:message code='general.buttons.remove'/>" />
					<a href="dialogs/versionHistoryDialog.html" class="versionHistoryButton fancyboxpopup fancybox.ajax button"><spring:message code="general.buttons.versionHistory"/></a>
					<input type="button" id="approvePublcationButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.approve'/>" />
					<input type="button" id="savePublicationChangesButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.save'/>" />
					<input type="button" id="editPublicationButton" class="previewButton searchFormInput" value="<spring:message code='general.buttons.edit'/>" />		
				</div>
            </div>
        </div>
    </body>
</html>