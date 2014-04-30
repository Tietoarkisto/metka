<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<tr>
    <td class="labelColumn"><label for="dialogField_${param.field}"><spring:message code="${context}.field.${param.field}"/></label></td>
    <td>
        <input id="${param.container}Field${param.field}" class="dialogValue" />
    </td>
</tr>