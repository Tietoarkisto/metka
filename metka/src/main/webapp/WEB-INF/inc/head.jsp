<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<title><spring:message code="page.title"/></title>
<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/styles.css">
<script>
    var contextPath = "${pageContext.request.contextPath}";
    var errorTitle = "";
    <c:if test="${not empty errorContainer}">errorTitle = "${errorContainer.title}";</c:if>
    var errorMsg = "";
    <c:if test="${not empty errorContainer}">errorMsg = "${errorContainer.msg}";</c:if>
    var errorData = new Array();
    <c:if test="${not empty errorContainer}">
        <c:forEach items="${errorContainer.data}" var="dataStr" varStatus="errorDataStatus">
            errorData[${errorDataStatus.index}] = "${dataStr}";
        </c:forEach>
    </c:if>
    var errors = new Array();
    errors["general.errors.title.notice"] = "<spring:message code='general.errors.title.notice'/>";
    errors["general.errors.title.error"] = "<spring:message code='general.errors.title.error'/>";
    errors["general.errors.move.previous"] = "<spring:message code='general.errors.move.previous'/>";
    errors["general.errors.move.next"] = "<spring:message code='general.errors.move.next'/>";
    errors["general.errors.move.series"] = "<spring:message code='general.errors.move.series'/>";
    errors["general.errors.move.study"] = "<spring:message code='general.errors.move.study'/>";
    errors["general.errors.move.publication"] = "<spring:message code='general.errors.move.publication'/>";

</script>
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