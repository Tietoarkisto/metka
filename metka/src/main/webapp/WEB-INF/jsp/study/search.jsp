<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
        <h1 class="pageTitle"><spring:message code="STUDY.search.title"/></h1>
        <div class="tabNavi">
            <a id="search" class="selected" href="#"><spring:message code="STUDY.search.title"/></a>
            <a id="erroneous" href="#"><spring:message code="STUDY.erroneous.title"/></a>
        </div>
        <div class="tabs tab_search">
            <div class="upperContainer">
                <%--<form action="#">
                    <div class="searchFormRowHolder"><label><spring:message code="study.search.form.studyNumber"/></label><input type="text" class="searchInput" name="studyNumber" /></div>
                    <div class="searchFormRowHolder"><label><spring:message code="study.search.form.studyName"/></label><input type="text" class="searchInput" name="studyName" /></div>
                    <div class="searchFormRowHolder">
                        <label><spring:message code="study.search.form.contributor.lastName"/></label><input type="text" class="shortSearchInput" name="contributorLastName" />
                        <label class="shortLabel"><spring:message code="study.search.form.contributor.firstName"/></label><input type="text" class="shortSearchInput" name="contributorFirstName" /><div style="clear:both;"></div></div>
                    <div class="searchFormRowHolder"><label><spring:message code="study.search.form.contributor.organization"/></label><select class="formSelect" name="contributorOrganization"><option></option></select></div>
                    <div class="searchFormRowHolder"><label><spring:message code="study.search.form.contributor.institution"/></label><input type="text" class="searchInput" name="contributorInstitution" /></div>
                    <div class="searchFormRowHolder">
                        <label><spring:message code="study.search.form.producer.lastName"/></label><input type="text" class="shortSearchInput" name="producerLastName" />
                        <label class="shortLabel"><spring:message code="study.search.form.producer.firstName"/></label><input type="text" class="shortSearchInput" name="producerFirstName" /><div style="clear:both;"></div></div>
                    <div class="searchFormRowHolder"><label><spring:message code="study.search.form.seriesName"/></label><select class="formSelect" name="seriesName"><option></option></select></div>

                    <div class="searchFormButtonsHolder">
                        <input type="button" class="searchFormInput doSearch" value="<spring:message code='general.buttons.search'/>" />
                        <input class="searchFormInput" type="reset" value="<spring:message code='general.buttons.clear'/>" />
                    </div>
                </form>--%>
                <form:form id="studySearchForm" method="post" action="/study/search" modelAttribute="searchData.query">
                    <table class="formTable">
                        <tr>
                            <td class="labelColumn"><spring:message code="general.search.state"/></td>
                            <td>
                                <form:label path="searchApproved"><spring:message code="general.search.state.approved"/></form:label>
                                <form:checkbox path="searchApproved" />
                            </td>
                            <td>
                                <form:label path="searchDraft"><spring:message code="general.search.state.draft"/></form:label>
                                <form:checkbox path="searchDraft" />
                            </td>
                            <td>
                                <form:label path="searchRemoved"><spring:message code="general.search.state.removed"/></form:label>
                                <form:checkbox path="searchRemoved" />
                            </td>
                        </tr>
                        <jsp:include page="../../inc/fullRowFormText.jsp">
                            <jsp:param name="field" value="id" />
                            <jsp:param name="colspan" value="3" />
                        </jsp:include>
                        <jsp:include page="../../inc/fullRowFormText.jsp">
                            <jsp:param name="field" value="title" />
                            <jsp:param name="colspan" value="3" />
                        </jsp:include>
                        <tr><c:set var="field" value="seriesid" />
                            <td class="labelColumn">
                                <form:label path="values['${field}']"><spring:message code="STUDY.field.${field}"/></form:label>
                            </td>
                            <td colspan="3"><form:select path="values['${field}']" items="${searchData.series}" itemLabel="name" itemValue="id" /></td>
                        </tr>
                    </table>
                </form:form>
            </div>
            <div class="buttonsHolder">
                <!-- TODO: Fix this reset button
                <input type="reset" class="button" value="Tyhjennä">-->
                <input id="studySearchSubmit" type="submit" class="button" value="<spring:message code='general.buttons.search'/>">
            </div>
            <c:if test="${not empty searchData.results}">
                <div class="searchResult">
                    <h1 class="pageTitle"><spring:message code="general.searchResult"/><span class="floatRight normalText"><spring:message code="general.searchResult.amount"/> ${fn:length(searchData.results)}</span> </h1>
                    <table class="dataTable">
                        <thead>
                        <tr>
                            <th><spring:message code="STUDY.field.study_number"/></th>
                            <th><spring:message code="STUDY.field.study_name"/></th>
                                <%--<th><spring:message code="STUDY.field.publication_references"/></th>--%><%--TODO: Julkaisuja voi olla monta, näytetäänkö tätä.--%>
                            <th><spring:message code="STUDY.field.series_reference"/></th>
                            <th><spring:message code="STUDY.field.acquisition_number"/></th>
                            <%--<th><spring:message code="STUDY.field.fsd_contributes"/></th>--%><%-- TODO: What is this --%>
                            <th><spring:message code="general.search.result.state"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${searchData.results}">
                            <tr class="pointerClass" onclick="location.href='${contextPath}/series/view/${r.id}/${r.revision}'">
                                <td>${r.study_number}</td>
                                <td>${r.study_name}</td>
                                    <%--<td>${r.publication_references}</td>--%><%-- TODO: See above--%>
                                <td>${r.series_reference}</td>
                                <td>${r.acquisition_number}</td>
                                <td>${r.fsd_contributes}</td>
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
    </div>
</div>
</body>
</html>