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
				<h1 class="pageTitle"><spring:message code="study.search.title"/></h1>
				<div class="searchFormContainer">
					<div class="tabsContainer">
                           <div class="tabNavi">
                               <ul>
                                   <li><a id="search" class="selected" href="#"><spring:message code="study.search.title"/></a></li>
                                   <li><a id="errorneous" href="#"><spring:message code="study.errorneous.title"/></a></li>
                               </ul>
                           </div>
                           <div class="tabs search">
							<form action="#">
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
							</form>
						</div>
						<div class="tabs errorneous">
							<div class="searchResultDataContainer">	
								<table class="metkaTable sortableTable">
									<thead>
										<tr>
											<th><spring:message code="study.errorneous.table.studyNumber"/></th>
									    	<th><spring:message code="study.errorneous.table.studyName"/></th> 
									    	<th><spring:message code="study.errorneous.table.errorPointCount"/></th>
									    </tr>
									</thead>
									<tbody>
										<c:forEach items="${errorneousStudies}" var="study">
											<tr class="errorneousMaterialRow">
												<td>${study.number}</td>
												<td>${study.data.name}</td>
												<td>${study.data.errors.pointCount}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<div class="searchTableActionLinkHolder"><input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" /></div>	
							</div>
						</div>
					</div>
				</div>
				<div class="searchResult">
					<h1 class="pageTitle"><spring:message code="general.searchResult"/></h1>
					<div class="searchResultDataContainer">					
						<table id="studySearchResultTable" class="metkaTable sortableTable"> 
							<thead> 
							<tr> 
							    <th><spring:message code="study.search.table.studyNumber"/></th> 
							    <th><spring:message code="study.search.table.studyName"/></th> 
							    <th><spring:message code="study.search.table.publication"/></th> 
							    <th><spring:message code="study.search.table.series"/></th> 
							    <th><spring:message code="study.search.table.acquisitionStudyNumber"/></th> 
							    <th><spring:message code="study.search.table.FSDContributes"/></th> 
							</tr> 
							</thead> 
							<tbody> 
								<c:forEach items="${studies}" var="study">
									<tr class="materialSearchResultRow"> 
									    <td>${study.number}</td> 
									    <td>${study.data.title}</td> 
									    <td>${study.data.publication.title}</td> 
									    <td>${study.data.series.title}</td> 
									    <td>${study.data.acquisitionStudyNumber}</td> 
									    <td>${study.data.fsdContributes}</td> 
									</tr> 
								</c:forEach>
							</tbody> 
						</table>
						<div class="searchTableActionLinkHolder"><input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" /></div>	
					</div>							
				</div>
            </div>
        </div>
    </body>
</html>