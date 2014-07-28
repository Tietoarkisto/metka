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
            <div class="content container">
                <%-- TODO: Get text from javascript somehow --%>
                <div class="pageTitle row">Eksperttihaku</div>
                <div class="upperContainer">
                    <%-- TODO: Display search field --%>
                    <table class="formTable">
                        <tr>
                            <td>
                                <div class="singleCellTitle">Hakulause</div>
                                <textarea id="expertSearchQuery" ></textarea>
                                <%-- TODO: Display search buttons --%>
                                <div class="buttonsHolder">
                                    <!-- TODO: Fix this reset button
                                    <input type="reset" class="button" value="Tyhjennä">-->
                                    <input type="button" id="performExpertSearchButton" class="button" value="<spring:message code='general.buttons.search'/>" onclick="MetkaJS.expertSearch()" />
                                </div>
                            </td>
                            <td>
                                <%-- TODO: Display saved searches --%>
                                <table class="dataTable">
                                    <thead>
                                    <tr>
                                        <%-- TODO: Get text from javascript somehow--%>
                                        <th colspan="4">Tallennetut haut</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td colspan="4">--Ei hakuja--</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <%-- TODO: Display search results --%>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <script data-main="${pageContext.request.contextPath}/js/page.js" src="${pageContext.request.contextPath}/lib/js/require.js"></script>
    </body>
</html>