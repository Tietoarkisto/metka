<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page session="false" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="type" value="SERIES" />
<!DOCTYPE HTML>
<html lang="fi">
	<head>
        <jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content study">
                <h1 class="pageTitle"><spring:message code="STUDY"/> ${single.values['id']} - <spring:message code="general.revision"/> ${single.revision} - <spring:message code="general.title.DRAFT"/></h1>
                <div class="tabNavi">
                    <a id="basic_information" class="selected" href="#basic_information"><spring:message code="STUDY.section.basic_information"/></a>
                    <a id="deposit_agreement" href="#deposit_agreement"><spring:message code="STUDY.section.deposit_agreement"/></a>
                    <a id="study_description" href="#study_description"><spring:message code="STUDY.section.study_description"/></a>
                    <a id="variables" href="#variables"><spring:message code="STUDY.section.variables"/></a>
                    <a id="file_management" href="#file_management"><spring:message code="STUDY.section.file_management"/></a>
                    <a id="codebook" href="#codebook"><spring:message code="STUDY.section.codebook"/></a>
                    <a id="study_errors" href="#study_errors"><spring:message code="STUDY.section.study_errors"/></a>
                    <a id="identifiers" href="#identifiers"><spring:message code="STUDY.section.identifiers"/></a>
                    <a id="import_export" href="#import_export"><spring:message code="STUDY.section.import_export"/></a>
                </div>
				<div class="upperContainer">
                    <form:form id="modifyForm" method="post" action="/study/save" modelAttribute="single">
                        <form:hidden path="id" />
                        <form:hidden path="revision" />
                        <form:hidden path="values['${configuration.idField}']" />
                        <jsp:include page="sub/basic_information.jsp"/>
                    </form:form>
                    <%--<h1 class="pageTitle">
                        <div class="floatLeft">${study.id}&nbsp;-&nbps;${study.data.name}&nbsp;-&nbsp;</div>
                        <div class="floatLeft draftInfo">${study.data.state}</div>
                        <div class="floatLeft publishedInfo"><spring:message code="general.version"/>&nbsp;${study.data.version}</div>
                        <!--<div class="floatLeft publishedInfo smallFont">&nbsp;(julkaistu 12.3.2013)</div>-->
                        <div class="floatRight handlerInfo">${study.data.handler}</div>
                        <div class="floatRight handlerInfo"><spring:message code="general.handler"/>&nbsp;</div>
                    </h1>
                    <div class="materialPrevNextContainer">
                        <div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>
                    </div>

                    <jsp:include page="basicInformation.jsp"/>

                    <jsp:include page="depositAgreement.jsp"/>

                    <jsp:include page="description/description.jsp"/>

                    <jsp:include page="variables/variables.jsp"/>

                    <jsp:include page="fileManagement.jsp"/>

                    <jsp:include page="codebook.jsp"/>

                    <jsp:include page="errors.jsp"/>

                    <jsp:include page="identifiers.jsp"/>--%>
                </div>
                <div class="buttonsHolder">
                    <input type="button" id="studySave" class="button" value="<spring:message code="general.buttons.save" />">
                    <input type="button" id="studyApprove" class="button" value="<spring:message code='general.buttons.approve'/>" />
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