<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<tr>
    <td class="labelColumn"><label for="dialogField_${param.field}"><spring:message code="${fn:toUpperCase(page)}.field.${param.field}"/></label></td>
    <td><input type="text" class="datepicker" id="${param.container}Field${param.field}" class="dialogValue"/></td> <%-- TODO: implement readOnly --%>
</tr>