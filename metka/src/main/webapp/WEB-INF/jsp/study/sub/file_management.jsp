<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_file_management">
    <jsp:include page="../../../inc/referencetableContainer.jsp">
        <jsp:param name="field" value="files" />
        <jsp:param name="addButton" value="false" />
        <jsp:param name="handler" value="studyFilesHandler" />
    </jsp:include>
    <jsp:include page="../../../dialogs/fileManagementDialog.jsp">
        <jsp:param name="handler" value="studyFilesHandler" />
        <jsp:param name="readonly" value="${reaonly}" />
    </jsp:include>
</div>