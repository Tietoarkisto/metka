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
				<h1 class="page-header"><spring:message code="publication.search.title"/></h1>
				<div class="searchFormContainer">					
					<form action="#">
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.id"/></label><input type="text" class="shortSearchInput" name="publicationId" /> 
							<label class="shortLabel">&nbsp;<spring:message code="publication.search.form.studyNumbers"/></label><input type="text" class="shortSearchInput" name="studyNumbers" />
							<div style="clear:both;"></div>
						</div>	
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.additionDate"/></label><input type="text" class="shortSearchInput datepicker" name="additionStart" />
							<label class="shortLabel">&nbsp;-</label><input type="text" class="shortSearchInput datepicker" name="additionEnd" />
							<div style="clear:both;"></div></div>	
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.publicationYear"/></label><input type="text" class="shortSearchInput" name="publishYear" />
							<div style="clear:both;"></div></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.studyName"/></label><input type="text" class="searchInput" name="studyName" /></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.seriesName"/></label><select class="formSelect" name="seriesName"><option></option></select></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.lastName"/></label><input type="text" class="shortSearchInput" name="firstName" />
							<label class="shortLabel"><spring:message code="publication.search.form.firstName"/></label><input type="text" class="shortSearchInput" name="firstName" />
							<div style="clear:both;"></div></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.publicationName"/></label><input type="text" class="searchInput" name="publicationName" /></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.relPubl"/></label><input type="text" class="searchInput" name="relPubl" /></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.publicationLanguage"/></label><select  class="formSelect" name="publicationLanguage"><option></option></select></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.publishable"/></label><select  class="formSelect" name="publishable"><option></option></select></div>
						<div class="searchFormRowHolder">
							<label><spring:message code="publication.search.form.handler"/></label><input type="text" class="searchInput" name="handler" /></div>
						<div class="searchFormButtonsHolder">
							<input type="button" class="searchFormInput doSearch" value="<spring:message code='general.buttons.search'/>" />
							<input class="searchFormInput" type="reset" value="<spring:message code='general.buttons.clear'/>" />
						</div>
					</form>
					<div id="addNewButton">
						<input type="button" id="addNewPublicationButton" class="searchFormInput" value="<spring:message code='general.buttons.addNew'/>" />
					</div>

				</div>
				<div class="searchResult">
					<h1 class="page-header"><spring:message code="general.searchResult"/></h1>
					<div class="searchResultDataContainer">					
						<table id="publicationSearchResultTable" class="metkaTable sortableTable"> 
							<thead> 
							<tr> 
							    <th><spring:message code="publication.search.table.id"/></th> 
							    <th><spring:message code="publication.search.table.name"/></th> 
							</tr> 
							</thead> 
							<tbody> 
								<c:forEach items="${publications}" var="publication">
									<tr class="publicationSearchResultRow"> 
									    <td>${publication.id}</td> 
									    <td>${publication.data.name}</td> 
									</tr> 
								</c:forEach>
							</tbody> 
						</table>
						<div class="searchTableActionLinkHolder">
							<input type="submit" class="searchFormInput" value="<spring:message code='general.buttons.getCSV'/>" />
						</div>	
					</div>							
				</div>
            </div>
        </div>
    </body>
</html>