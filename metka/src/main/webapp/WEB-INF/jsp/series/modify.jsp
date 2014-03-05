<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
        <jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="SERIES"/> ${single.values['seriesno']} - <spring:message code="general.revision"/> ${single.revision} - <spring:message code="general.title.DRAFT"/></h1>
				<div class="upperContainer">
                    <form:form id="modifyForm" method="post" modelAttribute="single">
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
                <div class="buttonsHolder">
                    <input type="button" id="seriesSave" class="button" value="<spring:message code="general.buttons.save" />">
                    <input type="button" id="seriesApprove" class="button" value="<spring:message code='general.buttons.approve'/>" />
                    <jsp:include page="../../inc/revHistory.jsp">
                        <jsp:param name="id" value="${single.id}"></jsp:param>
                        <jsp:param name="isDraft" value="true"></jsp:param>
                    </jsp:include>
                    <jsp:include page="../../inc/removeButton.jsp">
                        <jsp:param name="id" value="${single.id}" />
                        <jsp:param name="isDraft" value="true" />
                    </jsp:include>
                    <!-- TODO: Fix this reset button
                    <input type="reset" class="button" value="Tyhjennä">-->
                </div>
            </div>
        </div>
    </body>
</html>