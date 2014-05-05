<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="upperContainer">
    <form:form id="revisionModifyForm" method="post" modelAttribute="single">
        <table class="formTable">
            <form:hidden path="id" />
            <form:hidden path="revision" />
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesno" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesabb" />
                    <jsp:param name="readonly" value="${not empty single.values['seriesabb']}" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesname" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesdesc" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesnotes" />
                </jsp:include>
            </tr>
        </table>
    </form:form>
</div>