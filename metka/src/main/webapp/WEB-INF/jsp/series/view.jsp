<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="upperContainer">
    <form:form modelAttribute="single">
        <table class="formTable">
            <form:hidden path="id" />
            <form:hidden path="revision" />
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesid" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesabbr" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesname" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesdesc" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesnotes" />
                    <jsp:param name="readonly" value="true" />
                </jsp:include>
            </tr>
        </table>
    </form:form>
</div>