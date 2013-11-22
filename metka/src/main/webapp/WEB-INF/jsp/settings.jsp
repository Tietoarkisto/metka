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
				<h1 class="pageTitle"><spring:message code="settings.title"/></h1>
					<div class="searchFormContainer">
						<div class="tabsContainer">
							<div class="tabNavi">
								<ul>
									<li><a id="vocabulary" class="selected" href="#vocabulary"><spring:message code="settings.navi.vocabularies"/></a></li>
									<li><a id="standardTexts" href="#standardTexts"><spring:message code="settings.navi.standardTexts"/></a></li>
									<li><a id="userInfo" href="#userInfo"><spring:message code="settings.navi.userInfo"/></a></li>
								</ul>
							</div>
							<div class="tabs vocabulary">
								<table id="vocabularyTable">
									<thead>
										<tr><th><spring:message code="settings.vocabularies.tableTitle.vocabulary"/></th><th></th></tr>
									</thead>
									<tbody>
										<c:forEach items="${vocabularies}" var="vocabulary">
											<tr><td><a href="dialogs/vocabularyDialog.html" class="fancyboxpopup fancybox.ajax" >${vocabulary.name}</a></td>
												<td><a href="#"><img title="<spring:message code='general.remove'/>" src="../css/images/cancel.png"/></a></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>

								<div class="settingsButtonsHolder">
									<input type="button" class="searchFormInput" value="<spring:message code='settings.vocabularies.import'/>"/>
									<a href="dialogs/vocabularyDialog.html" class="button fancyboxpopup fancybox.ajax" style="display: hidden;"><spring:message code="general.remove"/></a>
								</div>
							</div>
							<div class="tabs standardTexts">
								<table id="standardTextTable">
									<thead>
										<tr><th><spring:message code="settings.standardTexts.tableTitle.name"/></th>
										<th><spring:message code="settings.standardTexts.tableTitle.description"/></th>
										<th><spring:message code="settings.standardTexts.tableTitle.standardText"/></th><th></th></tr>
									</thead>
									<tbody>
										<c:forEach items="${standardTexts}" var="standardText">
											<tr>
												<td><a href="dialogs/standardTextDialog.html" class="fancyboxpopup fancybox.ajax" >${standardText.name}</a></td>
												<td>${standardText.description}</td>
												<td>${standardText.standardText}</td>
												<td><a href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../css/images/cancel.png"/></a></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<div class="settingsTableActionLinkHolder"><a href="dialogs/standardTextDialog.html" class="fancyboxpopup fancybox.ajax"><spring:message code="general.add"/></a></div>
							</div>
							<div class="tabs userInfo">
								<div id="userInfoContainer">
									<div class="userPropertyRow">
										<div class="userProperty"><spring:message code="settings.userInfo.userName"/></div>
										<div class="userValue">${userInfo.name}</div>
									</div>
									<div class="userPropertyRow">
										<div class="userProperty"><spring:message code="settings.userInfo.userGroup"/></div>
										<div class="userValue">${userInfo.group}</div>
									</div>
								</div>
							</div>
						</div>
					</div>
            </div>
        </div>
    </body>
</html>