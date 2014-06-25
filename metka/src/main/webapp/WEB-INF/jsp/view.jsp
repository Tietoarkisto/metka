<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<!DOCTYPE HTML>
<html lang="fi">
<head>
    <jsp:include page="../inc/head.jsp" />
</head>
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper">
    <div class="content container">
        <div class="pageTitle row"><spring:message code="${context}"/> - <spring:message code="${context}.field.${configuration[context].displayId}"/>&nbsp;${single.id} - <spring:message code="general.revision"/>&nbsp;${single.revision}
            <div class="floatRight normalText">
                <input type="button" onclick="MetkaJS.PathBuilder().add('download').add(MetkaJS.SingleObject.id).add(MetkaJS.SingleObject.revision).navigate()" value="<spring:message code='general.buttons.download' />">
            </div>
        </div>
        <jsp:include page="../inc/prevNext.jsp" />
        <jsp:include page="${page}/view.jsp" />
        <div class="buttonsHolder">
            <jsp:include page="../inc/revHistory.jsp" />
        </div>
    </div>
    <div id="dynamicContent" class="content container"></div>
</div>
<script>
    $(document).ready(function() {
        $('#dynamicContent').metkaUI();
    });
</script>
</body>
</html>