<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Bootstrap --%>
<link rel="stylesheet" type="text/css" href="${contextPath}/lib/bootstrap/3.3.0/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/lib/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/lib/bootstrap-fileinput/css/fileinput.min.css">

<%-- Font Awesome --%>
<link rel="stylesheet" type="text/css" href="${contextPath}/lib/font-awesome-4.2.0/css/font-awesome.min.css">

<%-- Metka --%>
<%-- Parameter ?v=x is just to bypass cache, when there's major css changes that must be pushed to clients --%>
<link rel="stylesheet" type="text/css" href="${contextPath}/css/styles.css?v=4">
