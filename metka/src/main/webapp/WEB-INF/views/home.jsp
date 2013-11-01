<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	<spring:message code="hello.world"/>
</h1>

<P>  <spring:message code="time.is"/> ${serverTime}. </P>
<h1>
    <spring:message code="materials.title" />
</h1>
<table>
    <tbody>
        <c:forEach var="material" items="${materials}">
            <tr>
                <td>${material.id}</td>
                <td>${material.name}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</body>
</html>
