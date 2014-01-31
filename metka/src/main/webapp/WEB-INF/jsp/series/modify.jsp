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
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="SERIES"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision} - <spring:message code="general.title.draft"/></h1>
				<div class="upperContainer">
                    <form:form id="modifyForm" method="post" action="/series/save" modelAttribute="info.single">
                        <table class="formTable">
                            <tr>
                                <td class="labelColumn"><form:label path="id"><spring:message code="SERIES.field.id"/></form:label></td>
                                <td><form:input path="id" readonly="true"/></td>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="abbreviation"><spring:message code="SERIES.field.abbreviation"/></form:label></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty info.single.abbreviation}">
                                            <form:input path="abbreviation" />
                                        </c:when>
                                        <c:otherwise>
                                            <form:input path="abbreviation" readonly="true" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="name"><spring:message code="SERIES.field.name"/></form:label></td>
                                <td><form:input path="name" /></td>
                                    <%-- TODO: Implement translatiopn functionality
                                    <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
                                    <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="description"><spring:message code="SERIES.field.description"/></form:label></td>
                                <td><form:textarea path="description" /></td>
                                    <%-- TODO: Implement translation functionality
                                    <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
                                    <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
                            </tr>
                        </table>
                    </form:form>
                </div>
                <div class="viewFormButtonsHolder">
                    <div class="buttonsGroup" >
                        <input type="button" id="seriesSave" class="button" value="<spring:message code="general.buttons.save" />">
                        <input type="button" id="seriesApprove" class="button" value="<spring:message code='general.buttons.approve'/>" />
                        <jsp:include page="../../inc/revHistory.jsp">
                            <jsp:param name="id" value="${info.single.id}"></jsp:param>
                            <jsp:param name="isDraft" value="true"></jsp:param>
                            <jsp:param name="type" value="series"></jsp:param>
                        </jsp:include>
                        <jsp:include page="../../inc/removeButton.jsp">
                            <jsp:param name="id" value="${info.single.id}" />
                            <jsp:param name="isDraft" value="true" />
                        </jsp:include>
                        <!-- TODO: Fix this reset button
                        <input type="reset" class="button" value="Tyhjennä">-->
                    </div>
                </div>
                <div class="spaceClear"></div>
            </div>
        </div>
    </body>
</html>