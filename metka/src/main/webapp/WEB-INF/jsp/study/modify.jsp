<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="tabNavi">
    <a id="basic_information" href="#basic_information"><spring:message code="STUDY.section.basic_information"/></a>
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
    <jsp:include page="sub/file_upload.jsp">
        <jsp:param name="targetField" value="files" />
    </jsp:include>
    <form:form id="revisionModifyForm" method="post" action="/study/save" modelAttribute="single">
        <form:hidden path="id" />
        <form:hidden path="revision" />
        <form:hidden path="values['${configuration.idField}']" />
        <jsp:include page="sub/basic_information.jsp"/>
        <jsp:include page="sub/file_management.jsp"/>
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