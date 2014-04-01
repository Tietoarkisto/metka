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
            <c:choose>
                <c:when test="${single.values[param.field] == null or single.values[param.field] == ''}">
                    <input type="text" readonly="${readonly}" value="" />
                </c:when>
                <c:when test="${configuration[context].choicelists[optionslist].type == 'VALUE'}"><input type="text" readonly="${readonly}" value="<spring:message code="${context}.${optionslist}.choices.${single.values[param.field]}"/>" /></c:when>
                <c:otherwise>
                    <c:forEach var="option" items="${configuration[context].choicelists[optionslist].options}">
                        <c:if test="${option.value == single.values[param.field]}">
                            <input type="text" readonly="${readonly}" value="${option.title}" />
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </input>
        </c:when>
        <c:otherwise>
            <form:select path="values['${param.field}']" autocomplete="off" >
                <c:set var="choicelist" value="${configuration[context].fields[param.field].choicelist}" />
                <c:set var="optionslist" value="${configuration[context].choicelists[choicelist].key}" />
                <c:if test="${configuration[context].choicelists[choicelist].includeEmpty == true or configuration[context].choicelists[optionslist].includeEmpty == true}">
                    <form:option value="${null}"><spring:message code="general.list.empty"/></form:option>
                </c:if>
                <c:forEach var="option" items="${configuration[context].choicelists[optionslist].options}">
                    <form:option value="${option.value}">
                        <c:choose>
                            <c:when test="${configuration[context].choicelists[optionslist].type == 'VALUE'}"><spring:message code="${context}.${optionslist}.choices.${option.value}"/></c:when>
                            <c:otherwise>${option.title}</c:otherwise>
                        </c:choose>
                    </form:option>
                </c:forEach>
            </form:select>
        </c:otherwise>
    </c:choose>
</td>