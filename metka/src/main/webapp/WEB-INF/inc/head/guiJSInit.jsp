<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Include GUI parser namespace and all sub components --%>
<script src="${contextPath}/js/gui/guiParser.js"></script>
<script src="${contextPath}/js/gui/gridInterpreter.js"></script>

<%-- Button handlers--%>
<script src="${contextPath}/js/gui/buttons/generalButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/saveButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/approveButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/editButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/removeButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/historyButtonHandler.js"></script>

<%-- UI Components --%>
<script src="${contextPath}/js/gui/components/viewButton.js"></script>