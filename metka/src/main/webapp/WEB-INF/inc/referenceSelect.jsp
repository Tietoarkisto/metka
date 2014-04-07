<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<td class="labelColumn">
    <form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label>
</td>
<td colspan="${colspan}">
    <c:choose>
        <c:when test="${readonly}">
            <c:set var="choicelist" value="${configuration[context].fields[param.field].choicelist}" />
            <c:set var="optionslist" value="${configuration[context].choicelists[choicelist].key}" />
            <input type="text" readonly="${readonly}" value="" />
            <%-- TODO: get reference title value with ajax--%>
        </c:when>
        <c:otherwise>
            <form:input path="values['${param.field}']" autocomplete="off"  />
            <script>
                $(document).ready(function() {
                    MetkaJS.ReferenceHandler.handleRevisionableReferenceModelInput("${param.field}", "${context}");
                })
            </script>
        </c:otherwise>
    </c:choose>
</td>