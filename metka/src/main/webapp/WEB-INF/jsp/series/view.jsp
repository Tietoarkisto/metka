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
        <!--    Initialization for revision history viewing and comparison. All of these things have to be included when this
                functionality is required including a component with id: showRevisions. -->
        <script>
            var revisionableId = ${info.single.id};
            var isDraft = false;
            var contextPath = "${pageContext.request.contextPath}";
            var type = "SERIES";
        </script>
        <c:set var="type" value="SERIES" />
        <!--    jsInit.jsp contains javascript initialisation actions such as initialising strings array for localization.
                Include as needed. Usually it is required as part of revision history comparison. JSP variable type
                has to be set before including this file. -->
        <%@include file="../../inc/jsInit.jsp"%>
        <script src="${contextPath}/js/custom/history.js"></script>
        <!--    End of revision history component requirements. -->
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <jsp:include page="../../dialogs/revisionHistoryDialog.jsp">
            <jsp:param name="isDraft" value="false" />
        </jsp:include>
        <div class="wrapper">
            <div class="content">
           	<h1 class="pageTitle"><spring:message code="SERIES"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision}</h1>
				<div class="upperContainer">
					<jsp:include page="../../inc/prevNext.jsp">
                        <jsp:param name="prevLink" value="${contextPath}/prev/series/${info.single.id}" />
                        <jsp:param name="nextLink" value="${contextPath}/next/series/${info.single.id}" />
					</jsp:include>
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer">
                            <label><spring:message code="SERIES.field.id"/></label>
                            <input type="text" value="${info.single.id}" name="seriesId" readonly="readonly" />
                        </div>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer">
                            <label><spring:message code="SERIES.field.abbreviation"/></label>
                            <input type="text" value="${info.single.abbreviation}" name="seriesAbbr" readonly="readonly" />
                        </div>
					</div>
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="SERIES.field.name"/></label>
                            <input type="text" name="seriesNameFi" value="${info.single.name}" readonly="readonly" />
                        </div>
						<%-- TODO: Implement translatiopn functionality
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="SERIES.field.description"/></label>
                            <textarea id="seriesDescriptionFi" name="seriesDescrFi" readonly="readonly" >${info.single.description}</textarea>
                        </div>
						<%-- TODO: Implement translation functionality
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
					</div>		
				</div>

				<div class="viewFormButtonsHolder" style="margin-bottom:20px;width: 630px;">
					<%-- TODO: Implement series remove functionality
					<input type="button" class="ediButton searchFormInput" value="<spring:message code='general.buttons.remove'/>" />--%>

					<input type="button" id="showRevisions" class="searchFormInput"
                           value="<spring:message code='general.buttons.revisionHistory'/>" />

                    <input type="button" class="previewButton searchFormInput"
                            value="<spring:message code='general.buttons.edit'/>"
                            onclick="location.href='${contextPath}/series/edit/${info.single.id}'"/>
				</div>
            </div>
        </div>
    </body>
</html>