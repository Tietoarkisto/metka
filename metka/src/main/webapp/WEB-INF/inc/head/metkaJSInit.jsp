<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Include MetkaJS namespace javascript. --%>
<script src="${contextPath}/js/metkaJS/metkaJS.js"></script>
<script src="${contextPath}/js/metkaJS/l10n.js"></script>


<script>
    <c:if test="${not empty configurationType}">
        MetkaJS.configurationType = "${configurationType}";
    </c:if>
    <c:if test="${not empty revisionId}">
        MetkaJS.revisionId = ${revisionId};
    </c:if>
    <c:if test="${not empty revisionNo}">
        MetkaJS.revisionNo = ${revisionNo};
    </c:if>

    MetkaJS.contextPath = "${contextPath}";

    // This is used to test different users in client code
    // These values are never returned to server so they don't enable any shenanigans
    <c:if test="${not empty uUserName}">
    MetkaJS.User.userName = "${uUserName}";
    </c:if>

    <c:if test="${not empty uDisplayName}">
    MetkaJS.User.displayName = "${uDisplayName}";
    </c:if>

    /* User */
    /*MetkaJS.User.userName = "user";
    MetkaJS.User.displayName = "Perus Pena";*/

    /* Admin */
    /*MetkaJS.User.userName = "admin";
    MetkaJS.User.displayName = "Admin Pena";*/

    <c:if test="${not empty uRole}">
    MetkaJS.User.role = ${uRole};
    </c:if>
</script>