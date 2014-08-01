<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="handler" value="${empty param.handler ? 'generalReferenceHandler' : param.handler}" />
<c:set var="container" value="fileManagement" />
<c:set var="context" value="STUDY_ATTACHMENT" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<!--<script src="${pageContext.request.contextPath}/js/handlers/studyFilesHandler.js"></script>-->
<div class="largePopupContainer" id="fileManagementDialog" title="<spring:message code="${context}.dialog.fileManagement"/>" >
    <input type="hidden" id="fileManagementRowId" />

    <table id="fileManagementDialogTable" class="formTable">
        <tbody>
            <jsp:include page="inc/dialogHidden.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="field" value="fileno" />
            </jsp:include>
            <jsp:include page="inc/dialogTextRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="file" />
            </jsp:include>
            <jsp:include page="inc/dialogTextRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filelabel" />
            </jsp:include>
            <jsp:include page="inc/dialogTextRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filedescription" />
            </jsp:include>
            <jsp:include page="inc/dialogTextRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filecomment" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filecategory" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="fileaip" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filelanguage" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="fileoriginal" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filepublication" />
            </jsp:include>
            <jsp:include page="inc/dialogSelectionRow.jsp">
                <jsp:param name="container" value="${container}" />
                <jsp:param name="context" value="${context}" />
                <jsp:param name="field" value="filedip" />
            </jsp:include>
        </tbody>
    </table>

    <div class="popupButtonsHolder">
        <input type="button" class="button" onclick="MetkaJS.dialogClose('fileManagementDialog')" value="<spring:message code='general.buttons.close'/>" />
        <c:if test="${readonly == false}">
        <input type="button" class="button generalDialogAdd" onclick="MetkaJS.DialogHandlers['${handler}'].process()" value="<spring:message code="general.buttons.ok"/>"/>
        </c:if>
    </div>
</div>