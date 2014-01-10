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
           	<h1 class="pageTitle"><spring:message code="general.series"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision}</h1>
				<div class="upperContainer">
					<div class="prevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>		
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer">
                            <label><spring:message code="series.form.id"/></label>
                            <input type="text" value="${info.single.id}" name="seriesId" readonly="readonly" />
                        </div>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer">
                            <label><spring:message code="series.form.abbreviation"/></label>
                            <input type="text" value="${info.single.abbreviation}" name="seriesAbbr" readonly="readonly" />
                        </div>
					</div>
					<div class="rowContainer containsTranslations">
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="series.form.name"/></label>
                            <input type="text" name="seriesNameFi" value="${info.single.name}" readonly="readonly" />
                        </div>
						<%-- TODO: Implement translatiopn functionality
						<div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
						<div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
					</div>
					<div class="rowContainer containsTranslations">							
						<div class="seriesDataSetContainer translated translationFi">
                            <label><spring:message code="series.form.description"/></label>
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

                    <input type="button" class="previewButton searchFormInput" value="<spring:message code='general.buttons.edit'/>"
                           onclick="location.href='${contextPath}/series/edit/${info.single.id}'"/>
				</div>
            </div>
        </div>
    </body>
</html>