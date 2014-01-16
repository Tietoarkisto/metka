<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="revisionHistoryDialog">
	<h1 class="pageTitle"><spring:message code="general.versions.versionHistory"/></h1>
    <table id="revisionTable">
        <thead>
            <tr class="versionHistoryDialogRow">
                <th class="revisionTableColumn"><spring:message code="general.revision"/></th>
                <th class="revisionTableColumn"><spring:message code="general.revision.publishDate"/></th>
                <th class="revisionTableColumn"><spring:message code="general.revision.compare.begin"/></th>
                <th class="revisionTableColumn"><spring:message code="general.revision.compare.end"/></th>
                <c:if test="${param.isDraft}">
                    <th class="revisionTableColumn"><spring:message code="general.revision.replace"/></th>
                </c:if>
            </tr>
        </thead>
    </table>
	<%--<div id="revisionTable" class="popupRowHolder">
		<div class="versionHistoryDialogRow">
			<div class="versionName" style="font-weight: bold;"><spring:message code="general.revision"/></div>
			<div class="versionPublishedDate" style="font-weight: bold;"><spring:message code="general.revision.publishDate"/></div>
			<div class="versionCompare" style="font-weight: bold;"><spring:message code="general.versions.compare"/></div>
			<div class="versionReplace" style="font-weight: bold;"><spring:message code="general.versions.replace"/></div>
		</div>
		<c:forEach items="${study.versions}" var="version">
			<div class="versionHistoryDialogRow">
				&lt;%&ndash;Ent� jos luonnos? &ndash;%&gt;
				<div class="versionName">${version.versionNumber}</div>
				<div class="versionPublishedDate">${version.publishDate}</div>
				<div class="versionCompare"><input type="checkbox" name="version"/></div>
				<div class="versionReplace"><input type="button" class="searchFormInput" value="<spring:message code='general.versions.replace'/>" /></div>
			</div>
		</c:forEach>
	</div>--%>
	
	<br/>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.close'/>" />
		<input type="button" id="compareRevisions" class="searchFormInput" value="<spring:message code="general.revision.compare"/>"/>
	</div>
</div>	