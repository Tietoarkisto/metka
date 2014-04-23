<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<td class="labelColumn">
    <form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label>
</td>
<td colspan="${colspan}">
    <form:input path="values['${param.field}']" autocomplete="off"  />
    <script>
        $(document).ready(function() {
            MetkaJS.ReferenceHandler.handleReference("${param.field}", "${context}");
        })
    </script>
</td>