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
</body>
</html>
