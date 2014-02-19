<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesno" />
                                <jsp:param name="type" value="input" />
                                <jsp:param name="colspan" value="3" />
                            </jsp:include>
                            <tr><c:set var="field" value="seriesabb" />
                                <td class="labelColumn"><form:label path="seriesabb"><spring:message code="SERIES.field.${field}"/></form:label></td>
                                <td colspan="3"><form:select path="${field}" items="${info.abbreviations}" /></td>
                            </tr>
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesname" />
                                <jsp:param name="type" value="input" />
                                <jsp:param name="colspan" value="3" />
                            </jsp:include>
                        </table>
                    </form:form>
                </div>
                <div class="buttonsHolder">
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
                        <h1 class="pageTitle"><spring:message code="general.searchResult"/><span class="floatRight normalText"><spring:message code="general.searchResult.amount"/> ${fn:length(info.results)}</span> </h1>
                        <table class="dataTable">
                            <thead>
                                <tr>
                                    <th><spring:message code="SERIES.field.seriesno"/></th>
                                    <th><spring:message code="SERIES.field.seriesabb"/></th>
                                    <th><spring:message code="SERIES.field.seriesname"/></th>
                                    <th><spring:message code="general.search.result.state"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${info.results}">
                                    <tr class="pointerClass" onclick="location.href='${contextPath}/series/view/${r.seriesno}/${r.revision}'">
                                        <td>${r.seriesno}</td>
                                        <td>${r.seriesabb}</td>
                                        <td>${r.seriesname}</td>
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