<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
    <head>
    	<jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content materialContent">
				<h1 class="pageTitle">
					<div class="floatLeft">${study.id}&nbsp;-&nbps;${study.data.name}&nbsp;-&nbsp;</div>
					<div class="floatLeft draftInfo">${study.data.state}</div>
					<div class="floatLeft publishedInfo"><spring:message code="general.version"/>&nbsp;${study.data.version}</div>
					<!--<div class="floatLeft publishedInfo smallFont">&nbsp;(julkaistu 12.3.2013)</div>-->
					<div class="floatRight handlerInfo">${study.data.handler}</div>
					<div class="floatRight handlerInfo"><spring:message code="general.handler"/>&nbsp;</div>
				</h1>
				<div class="materialPrevNextContainer">
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>		
					<div class="materialTabNavi">
						<ul>
							<li><a id="general" class="selected" href="#general"><spring:message code="study.view.navi.basicInformation"/></a></li>
							<li><a id="depositAgreement" href="#depositAgreement"><spring:message code="study.view.navi.depositAgreement"/></a></li>
							<li><a id="description" href="#description"><spring:message code="study.view.navi.description"/></a></li>
							<li><a id="variables" href="#variables"><spring:message code="study.view.navi.variables"/></a></li>
							<li><a id="files" href="#files"><spring:message code="study.view.navi.files"/></a></li>
							<li><a id="codebook" href="#codebook"><spring:message code="study.view.navi.codebook"/></a></li>
							<li><a id="errors" href="#errors"><spring:message code="study.view.navi.errors"/></a></li>
							<li><a id="identifiers" href="#identifiers"><spring:message code="study.view.navi.identifiers"/></a></li>
							<li><a id="importExport" href="#importExport"><spring:message code="study.view.navi.importExport"/></a></li>
						</ul>
					</div>
				</div>
				
				<jsp:include page="basicInformation.jsp"/>
				
				<jsp:include page="depositAgreement.jsp"/>
				
				<jsp:include page="description/description.jsp"/>
				
				<jsp:include page="variables/variables.jsp"/>
				
				<jsp:include page="fileManagement.jsp"/>
				
				<jsp:include page="codebook.jsp"/>
				
				<jsp:include page="errors.jsp"/>
				
				<jsp:include page="identifiers.jsp"/>
			</div>
        </div>
    </body>
</html>