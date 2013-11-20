<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
<jsp:include page="../inc/head.jsp" />
<body>
<jsp:include page="../inc/topMenu.jsp" />
<div class="wrapper"><div class="content">
<h1>
	<spring:message code="hello.world"/>
</h1>

<P>  <spring:message code="time.is"/> ${serverTime}. </P>
<h1>
    <spring:message code="studies.title" />
</h1>
<table>
    <tbody>
        <c:forEach var="study" items="${studies}">
            <tr>
                <td>${study.id}</td>
                <td>${study.name}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<h1>
    <spring:message code="series.title" />
</h1>
<form:form method="post" action="/series/add" modelAttribute="Series">
    <table>
        <tr>
            <td><form:label path="abbreviation"><spring:message code="series.abb" /></form:label></td>
            <td><form:input path="abbreviation" /></td>
        </tr>
        <tr>
            <td><form:label path="name"><spring:message code="series.name" /></form:label></td>
            <td><form:input path="name" /></td>
        </tr>
        <tr>
            <td><form:label path="description"><spring:message code="series.desc" /></form:label></td>
            <td><form:input path="description" /></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="<spring:message code='submit.add' />"></td>
        </tr>
    </table>
</form:form>

<table>
    <thead>
        <tr>
            <th><spring:message code="series.id" /></th>
            <th><spring:message code="series.abb" /></th>
            <th><spring:message code="series.name" /></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="series" items="${seriesList}">
            <tr>
                <td>${series.id}</td>
                <td>${series.abbreviation}</td>
                <td>${series.name}</td>
                <td><a href="/series/remove/${series.id}">X</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</div></div>
</body>
</html>
