<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="multiline" value="${empty configuration[context].fields[param.field].multiline ? false : configuration[context].fields[param.field].multiline}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<c:set var="singlecolumn" value="${empty param.singlecolumn ? true : param.singlecolumn}" />
<c:if test="${singlecolumn == false}">
    <td class="${multiline == true ? 'textAreaLabel' : 'labelColumn'}"><form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label></td>
</c:if>
<td colspan="${colspan}">
    <c:if test="${singlecolumn == true}">
        <div class="singleCellTitle"><spring:message code="${context}.field.${param.field}"/></div>
    </c:if>
<c:choose><c:when test="${multiline == false}">
    <form:input path="values['${param.field}']" readonly="${readonly}"/> <%-- TODO: Implement translations --%>
</c:when><c:when test="${multiline == true}">
    <form:textarea path="values['${param.field}']" readonly="${readonly}"/> <%-- TODO: Implement translations --%>
</c:when></c:choose>
</td>