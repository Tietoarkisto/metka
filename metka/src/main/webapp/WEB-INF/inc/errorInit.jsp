<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
    var errorTitle = "";
    var errorMsg = "";
    var errorData = new Array();

    // Insert default title to translations
    strings["general.errors.title.notice"] = "<spring:message code='general.errors.title.notice' />";

    // Use the global strings array for translations
    <c:if test="${not empty errorContainer}">
        errorTitle = "${errorContainer.title}";
        strings["${errorContainer.title}"] = "<spring:message code='${errorContainer.title}' />";

        errorMsg = "${errorContainer.msg}";
        strings["${errorContainer.msg}"] = "<spring:message code='${errorContainer.msg}' />";

        <c:forEach items="${errorContainer.data}" var="dataStr" varStatus="errorDataStatus">
            errorData[${errorDataStatus.index}] = "${dataStr}";
            strings["${dataStr}"] = "<spring:message code='${dataStr}' />";
        </c:forEach>
    </c:if>

</script>