<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<div class="tabs tab_deposit_agreement">

    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="termsofuse" />
            </jsp:include>

            <c:set var="field" value="agreementdate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${configuration[context].fields[field].editable == false}" /></td>

            <c:set var="field" value="triggerdate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${configuration[context].fields[field].editable == false}" /></td>
        </tr>
        <tr><c:set var="field" value="termsofusechangedate" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input class="datepicker" path="values['${field}']" readonly="${configuration[context].fields[field].editable == false}" /></td>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="depositortype" />
            </jsp:include>

            <c:set var="field" value="triggerpro" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']"/></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="newtermsofuse" />
            </jsp:include>

            <jsp:include page="../../../inc/l18nSelect.jsp">
                <jsp:param name="field" value="agreementtype" />
            </jsp:include>

            <c:set var="field" value="triggerlabel" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']"/></td>
        </tr>
        <tr>
            <c:set var="field" value="agreement" />
            <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label></td>
            <td><form:input path="values['${field}']"/></td>
            <td colspan="4"></td>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="agreementnotes" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="permission" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="specialtermsofuse" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="agreementfsdnotes" />
                <jsp:param name="colspan" value="3" />
            </jsp:include>
        </tr>
    </table>
</div>