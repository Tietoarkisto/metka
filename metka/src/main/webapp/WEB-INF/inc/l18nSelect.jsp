<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<form:select path="${param.selectPath}" autocomplete="off">
    <c:forEach var="option" items="${configuration.choicelists[param.list].options}">
        <form:option value="${option.value}">
            <spring:message code="${fn:toUpperCase(page)}.${param.list}.choices.${option.value}"/>
        </form:option>
    </c:forEach>
</form:select>