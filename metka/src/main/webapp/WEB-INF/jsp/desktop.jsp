<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>

<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle"><spring:message code="desktop.title"/></h1>
                <!-- Tähän iffittely, että onko kääntäjä vai käsittelijä -->
                <%--
                <c:choose>
                <c:if test="${user.role.name == 'HANDLER'}"/> TMS --%>
                <div id="normalDesktop">
						<div class="desktopWidget">
							<div class="desktopWidgetHeading"><h2><spring:message code="desktop.widget.heading.allUnfinished"/></h2></div>
							<div class="desktopWidgetDataContainer">
								<c:forEach items="${allUnfinished}" var="study">
									<div class="desktopWidgetDataRow">&nbsp;
										<div class="desktopWidgetDataName">${study.data.name}</div>
										<div class="desktopWidgetDataOwner">${study.handler.name}</div>
									</div>
								</c:forEach>								
							</div>
						</div>
						
						<div class="desktopWidgetContainer">
							<div class="desktopWidgetInContainer onTheLeft">
								<div class="desktopWidgetHeading"><h2><spring:message code="desktop.widget.heading.ownUnfinished"/></h2></div>
								<div class="desktopWidgetDataContainer">
									<c:forEach items="${ownUnfinished}" var="study">
										<div class="desktopWidgetDataRow">${study.data.name}</div>
									</c:forEach>
								</div>
							</div>
							<div class="desktopWidgetInContainer onTheRight">
								<div class="desktopWidgetHeading"><h2><spring:message code="desktop.widget.heading.freeStudies"/></h2></div>
								<div class="desktopWidgetDataContainer">
									<c:forEach items="${freeStudies}" var="study">
										<div class="desktopWidgetDataRow">${study.data.name}</div>
									</c:forEach>)
								</div>
							</div>
						</div>
					</div>
					<!-- LAter both desktops to own files -->
                	<%--<c:otherwise/>--%>
					<div id="translatorDesktop">						
						<div class="desktopWidgetContainer">
							<div class="desktopWidgetInContainer onTheLeft">
								<div class="desktopWidgetHeading"><h2><spring:message code="desktop.widget.heading.ownUnfinished"/></h2></div>
								<div class="desktopWidgetDataContainer">
									<c:forEach items="${ownUnfinished}" var="study">
										<div class="desktopWidgetDataRow">${study.data.name}</div>
									</c:forEach>
								</div>
							</div>
							<div class="desktopWidgetInContainer onTheRight">
								<div class="desktopWidgetHeading"><h2><spring:message code="desktop.widget.heading.unTranslated"/></h2></div>
								<div class="desktopWidgetDataContainer">
									<div class="desktopWidgetDataRow">${study.data.name}</div>
								</div>
							</div>
						</div>
					</div>
					<%-- </c:otherwise> 
						<c:choose>--%>
            </div>
        </div>
    </body>
</html>