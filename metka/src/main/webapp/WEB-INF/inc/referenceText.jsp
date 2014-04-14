<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<c:set var="multiline" value="${empty configuration[context].fields[param.field].multiline ? false : configuration[context].fields[param.field].multiline}" />
<td colspan="${colspan}">
    <c:choose>
        <c:when test="${readonly}">
            <c:set var="choicelist" value="${configuration[context].fields[param.field].choicelist}" />
            <c:set var="optionslist" value="${configuration[context].choicelists[choicelist].key}" />
            <input type="text" readonly="${readonly}" value="" />
            <%-- TODO: get reference title value with ajax--%>
        </c:when>
        <c:otherwise>
            <form:hidden path="values['${param.field}']" autocomplete="off"  />
                <div class="singleCellTitle"><spring:message code="${context}.field.${param.field}"/></div>
                <c:choose><c:when test="${multiline == false}">
                    <input id="${param.field}_text" ${readonly ? 'readonly="readonly"': ''}/>
                </c:when><c:when test="${multiline == true}">
                    <textarea id="${param.field}_text" ${readonly ? 'readonly="readonly"': ''}></textarea>
                </c:when></c:choose>
            <script>
                $(document).ready(function() {
                    MetkaJS.ReferenceHandler.handleReference("${param.field}", "${context}");
                })
            </script>
        </c:otherwise>
    </c:choose>
</td>