<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">

<title><spring:message code="page.title"/></title>
<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/styles.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/form.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/css/table.css">
<script>
    // Define global variables for javascript wrapped in MetkaGlobals object.
    // As many as possible of global variables should be set here.
    MetkaGlobals = function() {
        return {
            page: "${page}",
            contextPath: "${pageContext.request.contextPath}",
            strings: new Array(),
            containerConfig: null,
            errorArray: new Array()
        };
    }();
    // Insert default confirmation dialog title
    MetkaGlobals.strings["general.confirmation.title.confirm"] = "<spring:message code='general.confirmation.title.confirm' />";
    // Insert default error title
    MetkaGlobals.strings["general.errors.title.notice"] = "<spring:message code='general.errors.title.notice' />";

<c:forEach items="${displayableErrors}" var="errorObject">
    <c:if test="${not empty errorObject.title}">MetkaGlobals.strings["${errorObject.title}"] = "<spring:message code='${errorObject.title}' />";</c:if>
    MetkaGlobals.strings["${errorObject.msg}"] = "<spring:message code='${errorObject.msg}' />";
    MetkaGlobals.errorArray.push({
        title: "${errorObject.title}",
        message: "${errorObject.msg}",
        data: new Array()
    });
    <c:forEach items="${errorObject.data}" var="dataStr" varStatus="errorDataStatus">
    MetkaGlobals.errorArray[MetkaGlobals.errorArray.length - 1].data[${errorDataStatus.index}] = "<spring:message code='${dataStr}' />";
    </c:forEach>
</c:forEach>

    // If containerConfig JSON is provided insert it to globals. Otherwise MetkaGlobals.containerConfig will remain null
    <c:if test="${not empty containerConfig}">MetkaGlobals.containerConfig = JSON.parse('${containerConfig}');</c:if>
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
<script src="${contextPath}/js/custom/table.js"></script>
<script src="${contextPath}/js/studies.js"></script>
<script src="${contextPath}/js/publications.js"></script>
<script src="${contextPath}/js/binders.js"></script>
<script src="${contextPath}/js/tables.js"></script>