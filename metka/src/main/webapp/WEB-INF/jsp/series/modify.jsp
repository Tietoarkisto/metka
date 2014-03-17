<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="upperContainer">
    <form:form id="revisionModifyForm" method="post" modelAttribute="single">
        <table class="formTable">
            <form:hidden path="id" />
            <form:hidden path="revision" />
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesno" />
                <jsp:param name="readOnly" value="true" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesabb" />
                <jsp:param name="readOnly" value="${not empty single.values['seriesabb']}" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesname" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesdesc" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesnotes" />
            </jsp:include>
        </table>
    </form:form>
</div>