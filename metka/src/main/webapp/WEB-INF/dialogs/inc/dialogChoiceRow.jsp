<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<tr>
    <td class="labelColumn"><label for="dialogField_${param.field}"><spring:message code="${context}.field.${param.field}"/></label></td>
    <td>
        <select id="${param.container}Field${param.field}" class="dialogValue" autocomplete="off">
            <c:set var="choicelist" value="${configuration[context].fields[param.field].choicelist}" />
            <c:set var="optionslist" value="${configuration[context].choicelists[choicelist].key}" />
            <c:if test="${configuration[context].choicelists[choicelist].includeEmpty == true or configuration[context].choicelists[optionslist].includeEmpty == true}">
                <option value="${null}"><spring:message code="general.list.empty"/></option>
            </c:if>
            <c:forEach var="option" items="${configuration[context].choicelists[optionslist].options}">
                <option value="${option.value}">
                    <c:choose>
                        <c:when test="${configuration[context].choicelists[optionslist].type == 'VALUE'}">
                            <script>MetkaJS.L18N.put("${context}.${optionslist}.choices.${option.value}", "<spring:message code="${context}.${optionslist}.choices.${option.value}"/>")</script>
                            <spring:message code="${context}.${optionslist}.choices.${option.value}"/>
                        </c:when>
                        <c:otherwise>
                            <script>MetkaJS.L18N.put("${context}.${optionslist}.choices.${option.value}", "${option.title}")</script>
                            ${option.title}
                        </c:otherwise>
                    </c:choose>
                </option>
            </c:forEach>
        </select>
    </td>
</tr>