<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_variables">
    <table class="formTable">
        <c:set var="field" value="variables" />
        <tr>
            <jsp:include page="../../../inc/inputs/referenceWithLabel.jsp">
                <jsp:param name="field" value="${field}" />
            </jsp:include>
        </tr>
        <c:set var="field" value="variablefile" />
        <tr>
            <jsp:include page="../../../inc/inputs/referenceWithLabel.jsp">
                <jsp:param name="field" value="${field}" />
            </jsp:include>
        </tr>
    </table>
</div>