<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="upperContainer">
    <form:form modelAttribute="single">
        <table class="formTable">
            <form:hidden path="id" />
            <form:hidden path="revision" />
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesno" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesabb" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesname" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesdesc" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>
            <jsp:include page="../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="seriesnotes" />
                <jsp:param name="readonly" value="true" />
            </jsp:include>
        </table>
    </form:form>
</div>