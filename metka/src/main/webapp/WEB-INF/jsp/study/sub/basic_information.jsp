<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_basic_information">

    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="title" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="entitle" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="id" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="datakind" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="public" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="submissionid" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="anonymization" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="descpublic" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <%--<td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>--%>
            <c:set var="field" value="dataarrivaldate" />
            <jsp:include page="../../../inc/inputs/dateField.jsp">
                <jsp:param name="field" value="${field}" />
                <jsp:param name="readonly" value="${readonly or configuration[context].fields[field].editable == false}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="securityissues" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="varpublic" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/referenceWithLabel.jsp">
                <jsp:param name="field" value="seriesid" />
            </jsp:include>
            <c:set var="field" value="aipcomplete" />
            <jsp:include page="../../../inc/inputs/dateField.jsp">
                <jsp:param name="field" value="${field}" />
                <jsp:param name="readonly" value="${readonly or configuration[context].fields[field].editable == false}" />
            </jsp:include>
        </tr>
        <%-- TEMPORARY TEST FIELD START --%>
        <c:set var="field" value="seriesiddep1" />
        <c:if test="${configuration[context].fields[field].display == true}">
        <tr>
            <jsp:include page="../../../inc/inputs/referenceWithLabel.jsp">
                <jsp:param name="field" value="${field}" />
            </jsp:include>
            <td></td>
        </tr>
        </c:if>
        <c:set var="field" value="seriesiddep2" />
        <c:if test="${configuration[context].fields[field].display == true}">
            <tr>
                <jsp:include page="../../../inc/inputs/referenceWithLabel.jsp">
                    <jsp:param name="field" value="${field}" />
                </jsp:include>
                <td></td>
            </tr>
        </c:if>
        <%-- TEMPORARY TEST FIELD END --%>
    </table>
    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="originallocation" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="processingnotes" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <jsp:include page="../../../inc/inputs/datatableContainer.jsp">
        <jsp:param name="field" value="notes" />
    </jsp:include>
    <jsp:include page="../../../inc/inputs/datatableContainer.jsp">
        <jsp:param name="field" value="dataversions" />
    </jsp:include>
    <jsp:include page="../../../inc/inputs/datatableContainer.jsp">
        <jsp:param name="field" value="descversions" />
    </jsp:include>
</div>