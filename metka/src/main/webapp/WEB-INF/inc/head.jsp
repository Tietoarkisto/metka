<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">

<title><spring:message code="page.title"/></title>
<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/styles.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/form.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/table.css">

<%-- Include javascript libraries --%>
<script src="${contextPath}/js/jquery/jquery-1.10.2.js"></script>
<script src="${contextPath}/js/jquery/jquery-ui.js"></script>
<script src="${contextPath}/js/jquery/jquery.fileupload.js"></script>
<%--
<script src="${contextPath}/js/jquery/jquery.tablesorter.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.tablesorter.pager.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.rowReordering.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.filter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>--%>

<%-- Include namespace javascript. These should not modify dom or use document.ready --%>
<script src="${contextPath}/js/namespace/metkaJS.js"></script>
<script src="${contextPath}/js/namespace/enums.js"></script>
<script src="${contextPath}/js/namespace/l10n.js"></script>
<script src="${contextPath}/js/namespace/errorManager.js"></script>
<script src="${contextPath}/js/namespace/reference.js"></script>
<script src="${contextPath}/js/namespace/tableHandler.js"></script>
<script src="${contextPath}/js/namespace/jsConfigUtil.js"></script>
<script src="${contextPath}/js/namespace/eventManager.js"></script>

<jsp:include page="MetkaJSInit.jsp" />

<%-- Include general table handlers for container and reference container tables --%>
<script src="${contextPath}/js/handlers/generalHandlers.js"></script>

<%-- Include dom modifying javascript and page specific javascript.
    Namescpace should be excisting and initialized for these.
    These scripts generally initialise click handlers for DOM objects,
    replace general functionality with modified implementations or
    run automated commands after document has loaded.
    Generally everything inside these scripts should be inside
    $(document).ready() function. --%>
<script src="${contextPath}/js/custom/dialogs.js"></script>
<script src="${contextPath}/js/custom/general.js"></script>
<script src="${contextPath}/js/custom/${page}.js"></script>
<script src="${contextPath}/js/custom/table.js"></script>