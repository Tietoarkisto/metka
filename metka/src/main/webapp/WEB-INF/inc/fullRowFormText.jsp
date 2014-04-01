<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="colspan" value="${empty param.colspan ? 1 : param.colspan}" />
<c:set var="multiline" value="${empty configuration[context].fields[param.field].multiline ? false : configuration[context].fields[param.field].multiline}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<tr>
    <td class="${multiline == true ? 'textAreaLabel' : 'labelColumn'}"><form:label path="values['${param.field}']"><spring:message code="${context}.field.${param.field}"/></form:label></td>
<c:choose>
    <c:when test="${multiline == false}">
    <td colspan="${colspan}"><form:input path="values['${param.field}']" readonly="${readonly}"/></td>
        <%-- TODO: Implement translation functionality
        <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
        <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
    </c:when>
    <c:when test="${multiline == true}">
    <td colspan="${colspan}"><form:textarea path="values['${param.field}']" readonly="${readonly}"/></td>
        <%-- TODO: Implement translation functionality
        <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
        <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
    </c:when>
</c:choose>
</tr>
