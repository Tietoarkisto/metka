<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="singlecolumn" value="${empty param.singlecolumn ? true : param.singlecolumn}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="colspan" value="${colspan > 1 and singlecolumn == false ? colspan - 1 : colspan}" />
<c:if test="${singlecolumn == false}">
    <td class="labelColumn">
        <form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label>
    </td>
</c:if>
<td colspan="${colspan}">
    <c:if test="${singlecolumn == true}">
        <div class="singleCellTitle"><form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label></div>
    </c:if>
    <form:input path="values['${param.field}']" />
    <script>
        $(document).ready(function() {
            require(['./modules/referenceHandler'], function (referenceHandler) {
                //referenceHandler.handleReference("${param.field}", "${context}");
            });
        });
    </script>
</td>