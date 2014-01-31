<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<title><spring:message code="page.title"/></title>
<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/styles.css">
<script>
    // Set global variable for contextPath to be used in javascript.
    var contextPath = "${pageContext.request.contextPath}";
    // Initialise the global translation strings array for javascript. When translations are needed they should be found from here.
    var strings = new Array();
</script>
<%@include file="errorInit.jsp"%>
<script src="${contextPath}/js/jquery/jquery-1.10.2.js"></script>
<script src="${contextPath}/js/jquery/jquery-ui.js"></script>
<script src="${contextPath}/js/jquery/jquery.tablesorter.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.tablesorter.pager.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.rowReordering.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.filter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>
<script src="${contextPath}/js/custom/dialogs.js"></script>
<script src="${contextPath}/js/custom/general.js"></script>
<script src="${contextPath}/js/custom/${page}.js"></script>
<script src="${contextPath}/js/studies.js"></script>
<script src="${contextPath}/js/publications.js"></script>
<script src="${contextPath}/js/binders.js"></script>
<script src="${contextPath}/js/tables.js"></script>