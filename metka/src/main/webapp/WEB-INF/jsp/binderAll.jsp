<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
    <head>
    	<jsp:include page="../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
				<h1 class="pageTitle"><spring:message code="binders.title"/></h1>
				<div id="binderList" class="upperContainer">
					<table class="metkaTable sortableTable" id="binderTable">
						<thead>
							<tr>
								<th><spring:message code="binders.table.study.number"/></th>
								<th><spring:message code="binders.table.study.name"/></th>
								<th><spring:message code="binders.table.study.handler"/></th>
								<th><spring:message code="binders.table.binder.number"/></th>
								<th><spring:message code="binders.table.study.bindedMaterial"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${bindedStudies}" var="study">
								<tr class="binderRow">
									<td>${study.number}</td>
									<td>${study.title}</td>
									<td>${study.handler}</td>
									<td class="binderNumber link">${study.data.binder}</td>
									<td>${study.data.bindedMaterial}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>	
					<div class="binderButtonsHolder">
						<input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" />
						<a href="dialogs/addMaterialToBinderDialog.html" class="button fancyboxpopup fancybox.ajax"><spring:message code="binders.buttons.addStudy"/></a>
					</div>
				</div>
				
				<div id="binderInfo" class="upperContainer" style="display: none;">
					<spring:message code="binders.binder.contents" arguments="${selectedBinder.number}"/>
					<table class="metkaTable sortableTable" id="binderTable">
						<thead>
							<tr>
								<th><spring:message code="binders.table.study.number"/></th>
								<th><spring:message code="binders.table.study.name"/></th>
								<th><spring:message code="binders.table.study.handler"/></th>
								<th><spring:message code="binders.table.study.bindedMaterial"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${bindedStudies}" var="study">
								<tr class="binderRow">
									<td>${study.number}</td>
									<td>${study.title}</td>
									<td>${study.handler}</td>
									<td>${study.data.bindedMaterial}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="binderButtonsHolder">
						<input type="submit" id="backToBinderList" class="searchFormInput" value="<spring:message code='binders.buttons.return'/>" />
						<input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" />
						<a href="dialogs/versionHistoryDialog.html" class="button fancyboxpopup fancybox.ajax"><spring:message code='general.buttons.versionHistory'/></a>
						<a href="dialogs/addMaterialToBinderDialog.html" class="button fancyboxpopup fancybox.ajax"><spring:message code="binders.buttons.addStudy"/></a>
					</div>
				</div>
            </div>
        </div>
    </body>
</html>