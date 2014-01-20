<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="revisionHistoryDialog" title="<spring:message code="general.revision.revisions"/>">
    <table id="revisionTable">
        <thead>
            <tr class="revisionHistoryDialogRow">
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
	
	<br/>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.close'/>" />
		<input type="button" id="compareRevisions" class="searchFormInput" value="<spring:message code="general.revision.compare"/>"/>
	</div>
</div>

<div class="popupContainer" id="revisionCompareDialog">
    <table id="revisionChangesTable">
        <thead>
            <tr class="revisionHistoryCompareRow">
                <th class="changeTableColumn"><spring:message code="general.revision.compare.property"/></th>
                <th class="changeTableColumn"><spring:message code="general.revision.compare.original"/></th>
                <th class="changeTableColumn"><spring:message code="general.revision.compare.changed"/></th>
            </tr>
        </thead>
    </table>

    <div class="popupButtonsHolder">
        <input type="button" class="searchFormInput" value="<spring:message code='general.buttons.close'/>" />
    </div>
</div>