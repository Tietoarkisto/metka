<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="upperContainer">
    <form:form id="revisionSearchForm" method="post" action="/series/search" modelAttribute="searchData.query">
        <table class="formTable">
            <tr>
                <td class="labelColumn"><spring:message code="general.search.state"/></td>

                <td class="rightAlignCell"><form:label path="searchApproved"><spring:message code="general.search.state.APPROVED"/></form:label></td>
                <td><form:checkbox path="searchApproved" /></td>

                <td class="rightAlignCell"><form:label path="searchDraft"><spring:message code="general.search.state.DRAFT"/></form:label></td>
                <td><form:checkbox path="searchDraft" /></td>

                <td class="rightAlignCell"><form:label path="searchRemoved"><spring:message code="general.search.state.REMOVED"/></form:label></td>
                <td><form:checkbox path="searchRemoved" /></td>
            </tr>
        </table>
        <table class="formTable">
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesno" />
                    <jsp:param name="singlecolumn" value="false" />
                </jsp:include>
            </tr>
            <tr><c:set var="field" value="seriesabbr" />
                <td class="labelColumn"><form:label path="values['${field}']"><spring:message code="SERIES.field.${field}"/></form:label></td>
                <td><form:select path="values['${field}']" items="${searchData.abbreviations}" /></td>
            </tr>
            <tr>
                <jsp:include page="../../inc/inputs/formText.jsp">
                    <jsp:param name="field" value="seriesname" />
                    <jsp:param name="singlecolumn" value="false" />
                </jsp:include>
            </tr>
        </table>
    </form:form>
</div>
<div class="buttonsHolder">
    <!-- TODO: Fix this reset button
    <input type="reset" class="button" value="Tyhjennä">-->
    <input type="button" id="addNewSeriesBtn" class="button" value="<spring:message code='general.buttons.addSeries'/>"
           onclick="MetkaJS.PathBuilder().add('series').add('add').navigate()"/>
    <input id="revisionSearchFormSearch" type="submit" class="button" value="<spring:message code='general.buttons.search'/>">
</div>
<c:if test="${not empty searchData.results}">
    <div class="searchResult">
        <h1 class="pageTitle"><spring:message code="general.searchResult"/><span class="floatRight normalText"><spring:message code="general.searchResult.amount"/> ${fn:length(searchData.results)}</span> </h1>
        <table class="dataTable">
            <thead>
                <tr>
                    <th><spring:message code="SERIES.field.seriesno"/></th>
                    <th><spring:message code="SERIES.field.seriesabb"/></th>
                    <th><spring:message code="SERIES.field.seriesname"/></th>
                    <th><spring:message code="general.search.result.state"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${searchData.results}">
                    <tr class="pointerClass" onclick="MetkaJS.view(${r.id},${r.revision})">
                        <td>${r.values['seriesno']}</td>
                        <td>${r.values['seriesabb']}</td>
                        <td>${r.values['seriesname']}</td>
                        <td><spring:message code="general.search.result.state.${r.state}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <!-- TODO: implement search result csv-export
        <div class="searchTableActionLinkHolder"><input type="submit" class="button" value="Lataa CSV"/></div>-->
    </div>
</c:if>