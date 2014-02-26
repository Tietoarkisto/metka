<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="multiline" value="${empty configuration.fields[param.field].multiline ? false : configuration.fields[param.field].multiline}" />
<c:choose>
    <c:when test="${multiline == false}">
        <tr>
            <td class="labelColumn"><form:label path="values['${param.field}']"><spring:message code="${fn:toUpperCase(page)}.field.${param.field}"/></form:label></td>
            <td colspan="${colspan}"><form:input path="values['${param.field}']" readonly="${param.readOnly}"/></td>
                <%-- TODO: Implement translatiopn functionality
                <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
                <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
        </tr>
    </c:when>
    <c:when test="${multiline == true}">
        <tr>
            <td class="textAreaLabel"><form:label path="values['${param.field}']"><spring:message code="${fn:toUpperCase(page)}.field.${param.field}"/></form:label></td>
            <td colspan="${colspan}"><form:textarea path="values['${param.field}']" readonly="${param.readOnly}"/></td>
                <%-- TODO: Implement translation functionality
                <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
                <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
        </tr>
    </c:when>
</c:choose>