<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="multiline" value="${empty configuration[context].fields[param.field].multiline ? false : configuration[context].fields[param.field].multiline}" />
<tr>
    <td class="${multiline == true ? 'textAreaLabel' : 'labelColumn'}"><label for="dialogField_${param.field}"><spring:message code="${context}.field.${param.field}"/></label></td>
<c:choose>
    <c:when test="${multiline == false}">
    <td><input type="text" id="${param.container}Field${param.field}" class="dialogValue"/></td> <%-- TODO: implement readOnly --%>
        <%-- TODO: Implement translatiopn functionality
        <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
        <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
    </c:when>
    <c:when test="${multiline == true}">
    <td><textarea id="${param.container}Field${param.field}" class="dialogValue"></textarea></td> <%-- TODO: implement readOnly --%>
        <%-- TODO: Implement translation functionality
        <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
        <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
    </c:when>
</c:choose>
</tr>