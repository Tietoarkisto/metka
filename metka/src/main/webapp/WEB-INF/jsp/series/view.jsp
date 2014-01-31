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
           	<h1 class="pageTitle"><spring:message code="SERIES"/> ${info.single.id} - <spring:message code="general.revision"/> ${info.single.revision}</h1>
                <jsp:include page="../../inc/prevNext.jsp">
                    <jsp:param name="id" value="${info.single.id}" />
                </jsp:include>
                <div class="upperContainer">
                    <table class="formTable">
                        <tr>
                            <td class="labelColumn"><label><spring:message code="SERIES.field.id"/></label></td>
                            <td><input type="text" value="${info.single.id}" name="seriesId" readonly="readonly" /></td>
                        </tr>
                        <tr>
                            <td class="labelColumn"><label><spring:message code="SERIES.field.abbreviation"/></label></td>
                            <td><input type="text" value="${info.single.abbreviation}" name="seriesAbbr" readonly="readonly" /></td>
                        </tr>
                        <tr>
                            <td class="labelColumn"><label><spring:message code="SERIES.field.name"/></label></td>
                            <td><input type="text" name="seriesNameFi" value="${info.single.name}" readonly="readonly" /></td>
                            <%-- TODO: Implement translatiopn functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameSv" /></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.name"/></label><input type="text" name="seriesNameEn" /></div>--%>
                        </tr>
                        <tr>
                            <td class="labelColumn"><label><spring:message code="SERIES.field.description"/></label></td>
                            <td><textarea id="seriesDescriptionFi" name="seriesDescrFi" readonly="readonly" >${info.single.description}</textarea></td>
                            <%-- TODO: Implement translation functionality
                            <div class="seriesDataSetContainer translationSv"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrSv"></textarea></div>
                            <div class="seriesDataSetContainer translationEn"><label><spring:message code="series.view.description"/></label><textarea name="seriesDescrEn"></textarea></div>--%>
                        </tr>
                        <%--<div class="rowContainer containsTranslations">
                            <div class="seriesDataSetContainer">

                            </div>
                        </div>
                        <div class="rowContainer containsTranslations">
                            <div class="seriesDataSetContainer">


                            </div>
                        </div>
                        <div class="rowContainer containsTranslations">
                            <div class="seriesDataSetContainer translated translationFi">


                            </div>

                        </div>
                        <div class="rowContainer containsTranslations">
                            <div class="seriesDataSetContainer translated translationFi">


                            </div>

                        </div>--%>
                    </table>
                </div>

				<div class="viewFormButtonsHolder">
                    <div class="buttonsGroup">
                        <jsp:include page="../../inc/revHistory.jsp">
                            <jsp:param name="id" value="${info.single.id}"></jsp:param>
                            <jsp:param name="isDraft" value="false"></jsp:param>
                            <jsp:param name="type" value="series"></jsp:param>
                        </jsp:include>

                        <input type="button" class="previewButton button"
                               value="<spring:message code='general.buttons.edit'/>"
                               onclick="location.href='${contextPath}/series/edit/${info.single.id}'"/>

                        <jsp:include page="../../inc/removeButton.jsp">
                            <jsp:param name="id" value="${info.single.id}" />
                            <jsp:param name="isDraft" value="false" />
                        </jsp:include>
                    </div>
				</div>
                <div class="spaceClear"></div>
            </div>
        </div>
    </body>
</html>