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
            <div class="content">
           	<h1 class="pageTitle"><spring:message code="series.view.title"/></h1>
				<div class="upperContainer">
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>		
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer">
                            <label><spring:message code="series.view.id"/></label>
                            <input type="text" value="${info.single.id}" name="seriesId" readonly="readonly" />
                        </div>
					</div>						
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer">
                            <label><spring:message code="series.view.abbreviation"/></label>
                            <input type="text" value="${info.single.abbrevation}" name="seriesAbbr" readonly="readonly" />
                        </div>
					</div>
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="series.view.name"/></label>
                            <input type="text" name="seriesNameFi" value="${info.single.name}" readonly="readonly" />
                        </div>
						<%-- TODO: Implement translatiopn functionality
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="series.view.description"/></label>
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
					<%-- TODO: Implement series version history display functionality
					<a href="dialogs/versionHistoryDialog.html" class="versionHistoryButton fancyboxpopup fancybox.ajax button"><spring:message code='general.buttons.versionHistory'/></a>--%>
					<%-- TODO: Only applicable when editing draft, move there
					<input type="button" id="approvePublcationButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.approve'/>" />--%>
					<%-- TODO: Only applicable when editing draft, move there
					<input type="button" id="saveSeriesChangesButton" class="editButton searchFormInput" value="<spring:message code='general.buttons.save'/>" />--%>
					<%-- TODO: Implement add new draft and draft modification for series --%>
                    <input type="button" id="editSeriesButton" class="previewButton searchFormInput" value="<spring:message code='general.buttons.edit'/>" />
				</div>
            </div>
        </div>
    </body>
</html>