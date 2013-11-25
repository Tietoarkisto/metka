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
           	<h1 class="pageTitle"><spring:message code="series.view.title"/></h1>
				<div class="upperContainer">
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>		
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer"><label><spring:message code="series.view.id"/></label><input type="text" value="16" class="unModifiable" name="seriesId" readonly="readonly" /></div>
					</div>						
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer"><label><spring:message code="series.view.abbreviation"/></label><input type="text" class="unModifiable" name="seriesAbbr" readonly="readonly" /></div>
					</div>
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer translated translationFi"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameFi" /></div>
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer translated translationFi"><label><spring:message code="series.view.description"/></label><textarea id="seriesDescriptionFi" name="seriesDescrFi"></textarea></div>
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>
					</div>		
				</div>

				<div class="viewFormButtonsHolder" style="margin-bottom:20px;width: 630px;">
					<input type="button" class="ediButton searchFormInput" value="<spring:message code='general.buttons.remove'/>" />
					<a href="dialogs/versionHistoryDialog.html" class="versionHistoryButton fancyboxpopup fancybox.ajax button"><spring:message code='general.buttons.versionHistory'/></a>
					<input type="button" id="approvePublcationButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.approve'/>" />
					<input type="button" id="saveSeriesChangesButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.save'/>" />
					<input type="button" id="editSeriesButton" class="previewButton searchFormInput" value="<spring:message code='general.buttons.edit'/>" />
				</div>
            </div>
        </div>
    </body>
</html>