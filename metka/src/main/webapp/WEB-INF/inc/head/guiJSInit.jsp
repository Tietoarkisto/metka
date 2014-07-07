<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<script src="${contextPath}/js/gui/widgets/metka.js"></script>
<script src="${contextPath}/js/gui/widgets/metka/gridItem.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaUI.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaButton.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaButtonContainer.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaContainer.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaField.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaInput.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaLabel.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaRow.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaCell.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaTabTitle.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaTabContent.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaSection.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaColumn.js"></script>
<script src="${contextPath}/js/gui/containers/tab.js"></script>
<script src="${contextPath}/js/gui/containers/section.js"></script>
<script src="${contextPath}/js/gui/containers/column.js"></script>

<script>
    <c:if test="${not empty jsGUIConfig}">$.metka.metkaUI.prototype.options = ${jsGUIConfig}[MetkaJS.Globals.page.toUpperCase()];</c:if>
</script>
