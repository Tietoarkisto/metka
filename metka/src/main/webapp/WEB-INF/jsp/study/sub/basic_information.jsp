<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_basic_information">

    <table class="formTable">
        <jsp:include page="../../../inc/fullRowFormText.jsp">
            <jsp:param name="field" value="title" />
            <jsp:param name="readonly" value="${readonly}" />
        </jsp:include>
        <jsp:include page="../../../inc/fullRowFormText.jsp">
            <jsp:param name="field" value="entitle" />
            <jsp:param name="readonly" value="${readonly}" />
        </jsp:include>
    </table>
    <table class="formTable">
        <tr><c:set var="field" value="id" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="true"/></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="datakind" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="public" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr><c:set var="field" value="submissionid" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="true"/></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="anonymization" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="descpublic" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr><c:set var="field" value="dataarrivaldate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${readonly or configuration[context].fields[field].editable == false}" /></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="securityissues" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="varpublic" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/referenceSelect.jsp">
                <jsp:param name="field" value="seriesid" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <c:set var="field" value="aipcomplete" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${readonly or configuration[context].fields[field].editable == false}" /></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="originallocation" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="processingnotes" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="notes" />
    </jsp:include>
    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="dataversions" />
    </jsp:include>
    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="descversions" />
    </jsp:include>
</div>