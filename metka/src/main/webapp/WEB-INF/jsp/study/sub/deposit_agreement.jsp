<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_deposit_agreement">

    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="termsofuse" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <c:set var="field" value="agreementdate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${readonly or configuration[context].fields[field].editable == false}" /></td>

            <c:set var="field" value="triggerdate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${readonly or configuration[context].fields[field].editable == false}" /></td>
        </tr>
        <tr><c:set var="field" value="termsofusechangedate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${readonly or configuration[context].fields[field].editable == false}" /></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="depositortype" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <c:set var="field" value="triggerpro" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="${readonly}"/></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="newtermsofuse" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="agreementtype" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <c:set var="field" value="triggerlabel" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="${readonly}"/></td>
        </tr>
        <tr>
            <c:set var="field" value="agreement" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']" readonly="${readonly}"/></td>
            <td colspan="4"></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="agreementnotes" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="permission" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="specialtermsofuse" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="agreementfsdnotes" />
                <jsp:param name="colspan" value="3" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
</div>