<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_deposit_agreement">

    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="termsofuse" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <c:set var="field" value="agreementdate" />
            <jsp:include page="../../../inc/inputs/dateField.jsp">
                <jsp:param name="field" value="${field}" />
                <jsp:param name="readonly" value="${readonly or configuration[context].fields[field].editable == false}" />
            </jsp:include>

            <c:set var="field" value="triggerdate" />
            <jsp:include page="../../../inc/inputs/dateField.jsp">
                <jsp:param name="field" value="${field}" />
                <jsp:param name="readonly" value="${readonly or configuration[context].fields[field].editable == false}" />
            </jsp:include>
        </tr>
        <tr>
            <c:set var="field" value="termsofusechangedate" />
            <jsp:include page="../../../inc/inputs/dateField.jsp">
                <jsp:param name="field" value="${field}" />
                <jsp:param name="readonly" value="${readonly or configuration[context].fields[field].editable == false}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="depositortype" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="triggerpro" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="newtermsofuse" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/l10nSelect.jsp">
                <jsp:param name="field" value="agreementtype" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="triggerlabel" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <table class="formTable">
        <%-- TODO: change to CONTAINER --%>
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="agreement" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
    <table class="formTable">
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="agreementnotes" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="permission" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="specialtermsofuse" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
            <jsp:include page="../../../inc/inputs/formText.jsp">
                <jsp:param name="field" value="agreementfsdnotes" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
    </table>
</div>