<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="type" value="SERIES" />
<!DOCTYPE HTML>
<html lang="fi">
	<head>
        <jsp:include page="../../inc/head.jsp" />
        <!--    Initialization for revision history viewing and comparison. All of these things have to be included when this
                functionality is required including a component with id: showRevisions. -->
        <script>
            var revisionableId = ${info.single.id};
            var isDraft = true;
            var contextPath = "${pageContext.request.contextPath}";
            var type = "SERIES";
        </script>
        <c:set var="type" value="SERIES" />
        <!--    jsInit.jsp contains javascript initialisation actions such as initialising strings array for localization.
                Include as needed. Usually it is required as part of revision history comparison. JSP variable type
                has to be set before including this file. -->
        <%@include file="../../inc/jsInit.jsp"%>
        <script src="${contextPath}/js/custom/history.js"></script>
        <!--    End of revision history component requirements. -->
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <jsp:include page="../../dialogs/revisionHistoryDialog.jsp">
            <jsp:param name="isDraft" value="true" />
        </jsp:include>
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="SERIES"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision} - <spring:message code="general.draft"/></h1>
				<div class="upperContainer">
                    <c:if test="${saveFail}">
                        <p>Tallennus epäonnistui</p>
                    </c:if>
                    <c:if test="${approveFail}">
                        <p>Hyväksyminen epäonnistui</p>
                    </c:if>
					<%-- TODO: Implement prev next functionality
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>--%>
                    <form:form id="modifyForm" method="post" action="/series/save" modelAttribute="info.single">
                        <div class="seriesDataSetContainer">
                            <form:label path="id"><spring:message code="SERIES.field.id"/></form:label>
                            <form:input path="id" readonly="true"/>
                        </div>
                        <div class="seriesDataSetContainer">
                            <form:label path="abbreviation"><spring:message code="SERIES.field.abbreviation"/></form:label>
                            <c:choose>
                                <c:when test="${empty info.single.abbreviation}">
                                    <form:input path="abbreviation" />
                                </c:when>
                                <c:otherwise>
                                    <form:input path="abbreviation" readonly="true" />
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="seriesDataSetContainer translated translationFi">
                            <form:label path="name"><spring:message code="SERIES.field.name"/></form:label>
                            <form:input path="name" />
                            <%-- TODO: Implement translatiopn functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
                        </div>
                        <div class="seriesDataSetContainer translated translationFi">
                            <form:label path="description"><spring:message code="SERIES.field.description"/></form:label>
                            <form:textarea path="description" />
                            <%-- TODO: Implement translation functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
                        </div>
                    </form:form>
                </div>
                <div class="viewFormButtonsHolder" style="margin-bottom:20px;width: 630px;">
                    <input type="button" id="seriesSave" class="searchFormInput" value="<spring:message code="general.buttons.save" />">
                    <input type="button" id="seriesApprove" class="searchFormInput" value="<spring:message code='general.buttons.approve'/>" />
                    <input type="button" id="showRevisions" class="searchFormInput" value="<spring:message code='general.buttons.revisionHistory'/>" />
                    <!-- TODO: Fix this reset button
                    <input type="reset" class="searchFormInput" value="Tyhjennä">-->
                </div>

				<%--<div class="viewFormButtonsHolder" style="margin-bottom:20px;width: 630px;">--%>
					<%-- TODO: Implement series remove functionality
					<input type="button" class="ediButton searchFormInput" value="<spring:message code='general.buttons.remove'/>" />--%>
				<%--</div>--%>
            </div>
        </div>
    </body>
</html>