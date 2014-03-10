<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<tr>
    <td class="labelColumn"><label for="dialogField_${param.field}"><spring:message code="${fn:toUpperCase(page)}.field.${param.field}"/></label></td>
    <td>
        <select id="${param.container}Field${param.field}" class="dialogValue" autocomplete="off"> <%-- TODO: implement readOnly --%>
            <c:set var="choicelist" value="${configuration.fields[param.field].choicelist}" />
            <c:set var="optionslist" value="${configuration.choicelists[choicelist].key}" />
            <c:if test="${configuration.choicelists[choicelist].includeEmpty == true or configuration.choicelists[optionslist].includeEmpty == true}">
                <option value="${null}"><spring:message code="general.list.empty"/></option>
            </c:if>
            <c:forEach var="option" items="${configuration.choicelists[optionslist].options}">
                <option value="${option.value}">
                    <c:choose>
                        <c:when test="${configuration.choicelists[optionslist].type == 'VALUE'}">
                            <script>MetkaGlobals.strings["${fn:toUpperCase(page)}.${optionslist}.choices.${option.value}"] = "<spring:message code="${fn:toUpperCase(page)}.${optionslist}.choices.${option.value}"/>"</script>
                            <spring:message code="${fn:toUpperCase(page)}.${optionslist}.choices.${option.value}"/>
                        </c:when>
                        <c:otherwise>
                            <script>MetkaGlobals.strings["${fn:toUpperCase(page)}.${optionslist}.choices.${option.value}"] = "${option.title}"</script>
                            ${option.title}
                        </c:otherwise>
                    </c:choose>
                </option>
            </c:forEach>
        </select>
    </td>
</tr>