<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="series.search.title"/></h1>
                <form:form method="post" action="/series/add" modelAttribute="Series">
                    <table>
                        <tr>
                            <td><form:label path="abbreviation"><spring:message code="series.search.form.abbreviation" /></form:label></td>
                            <td><form:input path="abbreviation" /></td>
                        </tr>
                        <tr>
                            <td><form:label path="name"><spring:message code="series.search.form.name" /></form:label></td>
                            <td><form:input path="name" /></td>
                        </tr>
                        <tr>
                            <td><form:label path="description"><spring:message code="series.search.form.description" /></form:label></td>
                            <td><form:input path="description" /></td>
                        </tr>
                        <tr>
                            <td colspan="2"><input type="submit" value="<spring:message code='general.buttons.add' />"></td>
                        </tr>
                    </table>
                </form:form>

                <div class="searchFormContainer">
                    <form:form method="post" action="/series/search" modelAttribute="Series">
                        <div class="searchFormRowHolder">
                            <form:label path="id"><spring:message code="series.search.form.id"/></form:label>
                            <form:input path="id" cssClass="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="name"><spring:message code="series.search.form.name"/></form:label>
                            <form:input path="name" cssClass="searchInput" name="name"/>
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="abbreviation"><spring:message code="series.search.form.abbreviation"/></form:label>
                            <form:select path="abbreviation" class="formSelect" name="abbreaviation" items="${abbreviations}" />
                        </div>
                        <div class="searchFormButtonsHolder">
                            <input type="submit" class="searchFormInput doSearch" value="<spring:message code="general.search"/>" />
                            <input type="reset" class="searchFormInput" value="<spring:message code="general.clear"/>" />
                        </div>

                        <div id="addNewButton"><input type="button" id="addNewSeriesButton" class="searchFormInput" value="<spring:message code="general.buttons.getCSV"/>" /></div>
                    </form:form>
                </div>
            </div>
        </div>
    </body>
</html>