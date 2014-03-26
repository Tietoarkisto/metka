<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<div class="tabs tab_basic_information">

    <table class="formTable">
        <jsp:include page="../../../inc/fullRowFormText.jsp">
            <jsp:param name="field" value="title" />
        </jsp:include>
        <jsp:include page="../../../inc/fullRowFormText.jsp">
            <jsp:param name="field" value="partitle" />
        </jsp:include>
    </table>
    <table class="formTable">
        <tr><c:set var="field" value="id" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="true"/></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="datakind" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="public" />
            </jsp:include>
        </tr>
        <tr><c:set var="field" value="submissionid" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="true"/></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="anonymization" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="descpublic" />
            </jsp:include>
        </tr>
        <tr><c:set var="field" value="dataarrivaldate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${configuration[context].fields[field].editable == false}" /></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="securityissues" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="varpublic" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="seriesid" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
            <c:set var="field" value="aipcomplete" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${configuration[context].fields[field].editable == false}" /></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="originallocation" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="processingnotes" />
                <jsp:param name="colspan" value="3" />
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