<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="isDraft" value="false" />
<!DOCTYPE HTML>
<html lang="fi">
	<head>
        <jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
           	<h1 class="pageTitle"><spring:message code="SERIES"/> ${info.single.seriesno} - <spring:message code="general.revision"/> ${info.single.revision}</h1>
                <jsp:include page="../../inc/prevNext.jsp">
                    <jsp:param name="id" value="${info.single.seriesno}" />
                </jsp:include>
                <div class="upperContainer">
                    <form:form modelAttribute="info.single">
                        <table class="formTable">
                            <form:hidden path="revision" />
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesno" />
                                <jsp:param name="type" value="input" />
                                <jsp:param name="readOnly" value="true" />
                            </jsp:include>
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesabb" />
                                <jsp:param name="type" value="input" />
                                <jsp:param name="readOnly" value="true" />
                            </jsp:include>
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesname" />
                                <jsp:param name="type" value="input" />
                                <jsp:param name="readOnly" value="true" />
                            </jsp:include>
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesdesc" />
                                <jsp:param name="type" value="area" />
                                <jsp:param name="readOnly" value="true" />
                            </jsp:include>
                            <jsp:include page="../../inc/fullRowFormText.jsp">
                                <jsp:param name="field" value="seriesnotes" />
                                <jsp:param name="type" value="area" />
                                <jsp:param name="readOnly" value="true" />
                            </jsp:include>
                        </table>
                    </form:form>
                </div>

				<div class="buttonsHolder">
                    <div class="buttonsGroup">
                        <jsp:include page="../../inc/revHistory.jsp">
                            <jsp:param name="id" value="${info.single.seriesno}"></jsp:param>
                            <jsp:param name="isDraft" value="false"></jsp:param>
                        </jsp:include>

                        <input type="button" class="button"
                               value="<spring:message code='general.buttons.edit'/>"
                               onclick="location.href='${contextPath}/series/edit/${info.single.seriesno}'"/>

                        <jsp:include page="../../inc/removeButton.jsp">
                            <jsp:param name="id" value="${info.single.seriesno}" />
                            <jsp:param name="isDraft" value="false" />
                        </jsp:include>
                    </div>
				</div>
                <div class="spaceClear"></div>
            </div>
        </div>
    </body>
</html>