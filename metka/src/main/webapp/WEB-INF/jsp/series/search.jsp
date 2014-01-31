<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="SERIES.search.title"/></h1>

                <div class="upperContainer">
                    <form:form id="seriesSearchForm" method="post" action="/series/search" modelAttribute="info.query">
                        <table class="formTable">
                            <tr>
                                <td class="labelColumn"><spring:message code="general.search.state"/></td>
                                <td>
                                    <form:label path="searchApproved"><spring:message code="general.search.state.approved"/></form:label>
                                    <form:checkbox path="searchApproved" />
                                </td>
                                <td>
                                    <form:label path="searchDraft"><spring:message code="general.search.state.draft"/></form:label>
                                    <form:checkbox path="searchDraft" />
                                </td>
                                <td>
                                    <form:label path="searchRemoved"><spring:message code="general.search.state.removed"/></form:label>
                                    <form:checkbox path="searchRemoved" />
                                </td>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="id"><spring:message code="SERIES.field.id"/></form:label></td>
                                <td colspan="3"><form:input path="id" /></td>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="abbreviation"><spring:message code="SERIES.field.abbreviation"/></form:label></td>
                                <td colspan="3"><form:select path="abbreviation" items="${info.abbreviations}" /></td>
                            </tr>
                            <tr>
                                <td class="labelColumn"><form:label path="name"><spring:message code="SERIES.field.name"/></form:label></td>
                                <td colspan="3"><form:input path="name" /></td>
                            </tr>
                        </table>
                    </form:form>
                </div>
                <div class="viewFormButtonsHolder">
                    <div class="buttonsGroup">
                        <!-- TODO: Fix this reset button
                        <input type="reset" class="button" value="Tyhjennä">-->
                        <input type="button" id="addNewSeriesBtn" class="button" value="<spring:message code='general.buttons.addSeries'/>"
                               onclick="location.href='${contextPath}/series/add'"/>
                        <input id="seriesSearchSubmit" type="submit" class="button" value="<spring:message code='general.buttons.search'/>">
                    </div>
                </div>
                <div class="spaceClear"></div>
                <c:if test="${not empty info.results}">
                    <div class="searchResult">
                        <h1 class="pageTitle"><spring:message code="general.searchResult"/></h1>
                        <table class="dataTable">
                            <thead>
                                <tr>
                                    <th><spring:message code="SERIES.field.id"/></th>
                                    <th><spring:message code="SERIES.field.abbreviation"/></th>
                                    <th><spring:message code="SERIES.field.name"/></th>
                                    <th><spring:message code="SERIES.field.state"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${info.results}">
                                    <tr class="pointerClass" onclick="location.href='${contextPath}/series/view/${r.id}'">
                                        <td>${r.id}</td>
                                        <td>${r.abbreviation}</td>
                                        <td>${r.name}</td>
                                        <td><spring:message code="general.search.result.state.${r.state}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <!-- TODO: implement search result csv-export
                        <div class="searchTableActionLinkHolder"><input type="submit" class="button" value="Lataa CSV"/></div>-->
                    </div>
                </c:if>
            </div>
        </div>
    </body>
</html>