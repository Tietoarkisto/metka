<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Include page specific and other javascript that doesn't go within namespace.
    All namespaces should be excisting and initialized for these.
    These scripts generally initialise click handlers for DOM objects,
    replace general functionality with modified implementations or
    run automated commands after document has loaded.
    Generally everything inside these scripts should be inside
    $(document).ready() function. --%>
<script src="${contextPath}/js/custom/uiLocalization.js"></script>
<script src="${contextPath}/js/custom/dialogs.js"></script>
<script src="${contextPath}/js/custom/general.js"></script>
<script src="${contextPath}/js/custom/${page}.js"></script>
<script src="${contextPath}/js/custom/table.js"></script>