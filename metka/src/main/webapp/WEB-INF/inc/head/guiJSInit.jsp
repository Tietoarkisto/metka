<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<script src="${contextPath}/js/gui/widgets/metka.js"></script>
<script src="${contextPath}/js/gui/widgets/metka/gridItem.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaUI.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaButtonContainer.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaContainer.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaField.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaRow.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaCell.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaTabTitle.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaTabContent.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaSection.js"></script>
<script src="${contextPath}/js/gui/widgets/metkaColumn.js"></script>
<script src="${contextPath}/js/gui/containers/tab.js"></script>
<script src="${contextPath}/js/gui/containers/section.js"></script>
<script src="${contextPath}/js/gui/containers/column.js"></script>

<%-- Include GUI parser namespace and all sub components --%>
<script src="${contextPath}/js/gui/guiParser.js"></script>
<script src="${contextPath}/js/gui/buttonParser.js"></script>

<%-- Button handlers--%>
<script src="${contextPath}/js/gui/buttons/generalButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/saveButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/approveButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/editButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/removeButtonHandler.js"></script>
<script src="${contextPath}/js/gui/buttons/historyButtonHandler.js"></script>

<%-- UI Components --%>
<script src="${contextPath}/js/gui/components/viewButton.js"></script>

<%-- UI Fields --%>
<script src="${contextPath}/js/gui/fields/container.js"></script>
<script src="${contextPath}/js/gui/fields/input.js"></script>
<script src="${contextPath}/js/gui/fields/input/datetime.js"></script>
<script src="${contextPath}/js/gui/fields/input/select.js"></script>