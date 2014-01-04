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
                <h1 class="pageTitle"><spring:message code="series.search.title"/></h1>

                <div class="searchFormContainer">
                    <form:form method="post" action="/series/search" modelAttribute="info.query">
                        <div class="searchFormRowHolder">
                            <form:label path="id"><spring:message code="series.form.id"/></form:label>
                            <form:input path="id" cssClass="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="name"><spring:message code="series.form.name"/></form:label>
                            <form:input path="name" cssClass="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="abbreviation"><spring:message code="series.form.abbreviation"/></form:label>
                            <form:select path="abbreviation" class="formSelect" items="${info.abbreviations}" />
                        </div>
                        <div class="searchFormButtonsHolder">
                            <!-- TODO: translation -->
                            <input type="submit" class="searchFormInput doSearch" value="Tee haku">
                            <!-- TODO: Fix this reset button
                            <input type="reset" class="searchFormInput" value="Tyhjennä">-->
                        </div>

                        <div id="addNewButton">
                            <!-- TODO: translation -->
                            <input type="button" id="addNewSeriesBtn" class="searchFormInput" value="Lisää uusi"
                                    onclick="location.href='/series/add'"/>
                        </div>
                    </form:form>
                </div>
                <c:if test="${not empty info.results}">
                    <div class="searchResult">
                        <h1 class="pageTitle"><spring:message code="general.searchResult"/></h1>
                        <div class="searchResultDataContainer">
                            <table id="myTable" class="metkaTable sortableTable">
                                <thead>
                                    <tr>
                                        <th><spring:message code="series.form.id"/></th>
                                        <th><spring:message code="series.form.abbreviation"/></th>
                                        <th><spring:message code="series.form.name"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${info.results}">
                                        <tr class="seriesSearchResultRow" onclick="location.href='/series/view/${r.id}'">
                                            <td>${r.id}</td>
                                            <td>${r.abbreviation}</td>
                                            <td>${r.name}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <!-- TODO: implement search result csv-export
                            <div class="searchTableActionLinkHolder"><input type="submit" class="searchFormInput" value="Lataa CSV"/></div>-->
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </body>
</html>