<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<title></title>

<jsp:include page="head/cssInit.jsp" />

<script src="${contextPath}/lib/jquery/jquery-3.2.1.min.js"></script>
<script src="${contextPath}/lib/jquery/jquery-migrate-3.0.0.min.js"></script>
<script src="${contextPath}/lib/jquery/jquery.bootpag.min.js"></script>

<%-- jQuery-ui library --%>
<script src="${contextPath}/lib/jquery-ui/jquery-ui.js"></script>
<link href="${contextPath}/lib/jquery-ui/jquery-ui.css" rel="stylesheet">

<jsp:include page="head/metkaJSInit.jsp" />

<script src="${contextPath}/lib/js/moment-with-langs.min.js"></script>
<script src="${contextPath}/lib/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="${contextPath}/lib/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>

<%-- Summernote - Rich Text Editor --%>
<link href="${contextPath}/lib/summernote/summernote.css" rel="stylesheet">
<script src="${contextPath}/lib/summernote/summernote.min.js"></script>
<script src="${contextPath}/lib/summernote/summernote-fi-FI.js"></script>

<%-- FileSaver - Client Side File Creation --%>
<script src="${contextPath}/lib/js/FileSaver.min.js"></script>

<%-- JSON-editor - JSON Schema Based Editor --%>
<script src="${contextPath}/lib/js/jsoneditor.js"></script>
