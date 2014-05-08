<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<tr>
    <td class="labelColumn"><label for="dialogField_${param.field}"><spring:message code="${context}.field.${param.field}"/></label></td>
    <td>
        <select id="${param.container}Field${param.field}" class="dialogValue" autocomplete="off">
            <c:set var="selectionList" value="${configuration[context].fields[param.field].selectionList}" />
            <c:set var="optionsList" value="${configuration[context].selectionLists[selectionList].key}" />
            <c:if test="${configuration[context].selectionLists[selectionList].includeEmpty == true or configuration[context].selectionLists[optionsList].includeEmpty == true}">
                <option value="${null}"><spring:message code="general.list.empty"/></option>
            </c:if>
            <c:forEach var="option" items="${configuration[context].selectionLists[optionsList].options}">
                <option value="${option.value}">
                    <c:choose>
                        <c:when test="${configuration[context].selectionLists[optionsList].type == 'VALUE'}">
                            <script>MetkaJS.L10N.put("${context}.${optionsList}.option.${option.value}", "<spring:message code="${context}.${optionsList}.option.${option.value}"/>")</script>
                            <spring:message code="${context}.${optionsList}.option.${option.value}"/>
                        </c:when>
                        <c:otherwise>
                            <script>MetkaJS.L10N.put("${context}.${optionsList}.option.${option.value}", "${option.title}")</script>
                            ${option.title}
                        </c:otherwise>
                    </c:choose>
                </option>
            </c:forEach>
        </select>
    </td>
</tr>