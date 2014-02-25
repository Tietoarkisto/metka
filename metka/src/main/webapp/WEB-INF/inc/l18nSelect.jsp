<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<td class="labelColumn">
    <form:label path="values['${param.field}']"><spring:message code="${fn:toUpperCase(page)}.field.${param.field}"/></form:label>
</td>
<td>
    <form:select path="values['${param.field}']" autocomplete="off">
        <c:set var="choicelist" value="${configuration.fields[param.field].choicelist}" />
        <c:set var="optionslist" value="${configuration.choicelists[choicelist].key}" />
        <c:if test="${configuration.choicelists[choicelist].includeEmpty == true or configuration.choicelists[optionslist].includeEmpty == true}">
            <form:option value="${null}"><spring:message code="general.list.empty"/></form:option>
        </c:if>
        <c:forEach var="option" items="${configuration.choicelists[optionslist].options}">
            <form:option value="${option.value}">
                <c:choose>
                    <c:when test="${configuration.choicelists[optionslist].type == 'VALUE'}"><spring:message code="${fn:toUpperCase(page)}.${optionslist}.choices.${option.value}"/></c:when>
                    <c:otherwise>${option.title}</c:otherwise>
                </c:choose>
            </form:option>
        </c:forEach>
    </form:select>
</td>