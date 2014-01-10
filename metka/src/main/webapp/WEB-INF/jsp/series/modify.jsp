<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="general.series"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision} - <spring:message code="general.draft"/></h1>
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
                            <form:label path="id"><spring:message code="series.form.id"/></form:label>
                            <form:input path="id" readonly="true"/>
                        </div>
                        <div class="seriesDataSetContainer">
                            <form:label path="abbreviation"><spring:message code="series.form.abbreviation"/></form:label>
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
                            <form:label path="name"><spring:message code="series.form.name"/></form:label>
                            <form:input path="name" />
                            <%-- TODO: Implement translatiopn functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
                        </div>
                        <div class="seriesDataSetContainer translated translationFi">
                            <form:label path="description"><spring:message code="series.form.description"/></form:label>
                            <form:textarea path="description" />
                            <%-- TODO: Implement translation functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
                        </div>
                        <div class="viewFormButtonsHolder">
                            <input type="button" id="seriesSave" class="searchFormInput" value="<spring:message code="general.buttons.save" />">
                            <input type="button" id="seriesApprove" class="searchFormInput" value="<spring:message code='general.buttons.approve'/>" />
                            <!-- TODO: Fix this reset button
                            <input type="reset" class="searchFormInput" value="Tyhjennä">-->
                        </div>
                    </form:form>
				</div>

				<%--<div class="viewFormButtonsHolder" style="margin-bottom:20px;width: 630px;">--%>
					<%-- TODO: Implement series remove functionality
					<input type="button" class="ediButton searchFormInput" value="<spring:message code='general.buttons.remove'/>" />--%>
					<%-- TODO: Implement series version history display functionality
					<a href="dialogs/versionHistoryDialog.html" class="versionHistoryButton fancyboxpopup fancybox.ajax button"><spring:message code='general.buttons.versionHistory'/></a>--%>
				<%--</div>--%>
            </div>
        </div>
    </body>
</html>