<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="tabNavi">
    <a id="search" href="#"><spring:message code="STUDY.search.title"/></a>
    <a id="erroneous" href="#"><spring:message code="STUDY.erroneous.title"/></a>
</div>
<div class="tabs tab_search">
    <div class="upperContainer">
        <form:form id="revisionSearchForm" method="post" action="/study/search" modelAttribute="searchData.query">
            <form:hidden path="all" />
            <%--<table class="formTable">
                <tr>
                    <td class="labelColumn"><spring:message code="general.search.state"/></td>
                    <td>
                        <form:label path="searchApproved"><spring:message code="general.search.state.APPROVED"/></form:label>
                        <form:checkbox path="searchApproved" />
                    </td>
                    <td>
                        <form:label path="searchDraft"><spring:message code="general.search.state.DRAFT"/></form:label>
                        <form:checkbox path="searchDraft" />
                    </td>
                    <td>
                        <form:label path="searchRemoved"><spring:message code="general.search.state.REMOVED"/></form:label>
                        <form:checkbox path="searchRemoved" />
                    </td>
                </tr>
                <tr>
                    <jsp:include page="../../inc/inputs/formText.jsp">
                        <jsp:param name="field" value="id" />
                        <jsp:param name="colspan" value="3" />
                    </jsp:include>
                </tr>
                <tr>
                    <jsp:include page="../../inc/inputs/formText.jsp">
                        <jsp:param name="field" value="title" />
                        <jsp:param name="colspan" value="3" />
                    </jsp:include>
                </tr>
                <tr><c:set var="field" value="seriesid" />
                    <td class="labelColumn">
                        <form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label>
                    </td>
                    <td colspan="3"><form:select path="values['${field}']" items="${searchData.series}" itemLabel="name" itemValue="id" /></td>
                </tr>
            </table>--%>
        </form:form>
    </div>
    <div class="buttonsHolder">
        <!-- TODO: Fix this reset button
        <input type="reset" class="button" value="Tyhjennä">-->
        <input id="revisionSearchFormSearch" type="submit" class="button" value="<spring:message code='general.buttons.search'/>">
    </div>
    <c:if test="${not empty searchData.results}">
        <div class="searchResult">
            <h1 class="page-header"><spring:message code="general.searchResult"/><span class="pull-right normalText"><spring:message code="general.searchResult.amount"/> ${fn:length(searchData.results)}</span> </h1>
            <table class="dataTable">
                <thead>
                <tr>
                    <th><spring:message code="STUDY.field.id"/></th>
                    <th><spring:message code="STUDY.field.title"/></th>
                    <th><spring:message code="general.search.result.state"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="r" items="${searchData.results}">
                    <tr class="pointerClass" onclick="MetkaJS.view(${r.id},${r.revision})">
                        <td>${r.values["studyid"]}</td>
                        <td>${r.values["title"]}</td>
                        <td><spring:message code="general.search.result.state.${r.state}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <!-- TODO: implement search result csv-export
            <div class="searchTableActionLinkHolder"><input type="submit" class="button" value="Lataa CSV"/></div>-->
        </div>
    </c:if>
</div>
<div class="tabs tab_erroneous">
    <div class="upperContainer">
        <table class="dataTable">
            <thead>
                <tr>
                    <th><spring:message code="STUDY.erroneous.table.study_number"/></th>
                    <th><spring:message code="STUDY.erroneous.table.study_name"/></th>
                    <th><spring:message code="STUDY.erroneous.table.errorPointCount"/></th>
                </tr>
            </thead>
            <tbody>
            <c:forEach items="${searchData.erroneous}" var="study">
                <tr>
                    <td>${study.study_number}</td>
                    <td>${study.study_name}</td>
                    <td>${study.point_count}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%-- TODO: Implement csv-export
        <div class="searchTableActionLinkHolder"><input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" /></div>--%>
    </div>
</div>