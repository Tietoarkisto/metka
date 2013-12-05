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
				<h1 class="pageTitle"><spring:message code="expertSearch.title"/></h1>
				<div class="searchFormContainer">
					<div class="expertSearchContainer">
						<div class="queryLibraryContainer">
							<div class="queryLibrary">
								<h1><spring:message code="expertSearch.savedQueries"/></h1>
								<table id="savedQueries">
									<thead><tr><th></th><th></th><th></th><th></th></thead>
									<tbody>
										<c:forEach items="${queries}" var="query">
											<tr>
												<td>${query.name}</td>
												<td>${query.saver}</td>
												<td>${query.savedDate}</td>
												<td align="center"><img title="<spring:message code='general.remove'/>" src="../css/images/cancel.png" /></td>
												</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							
							<div class="saveNewQuery">
								<h1><spring:message code="expertSearch.saveNewQuery"/></h1>
								<form action="#">
									<label><spring:message code="expertSearch.queryName"/></label>
									<input class="queryName" type="text" name="queryName">
									<input class="querySaveBtn" type="button" value="<spring:message code='general.buttons.save'/>" />
								</form>
								<div style="clear:both;"></div>
							</div>	
							
						</div>	
						
						<form action="#">
							<div class="expertSearchFormRowHolder"><label><spring:message code="expertSearch.query"/></label><textarea class="sqlSearch"></textarea></div>
							<div class="expertSearchFormButtonsHolder">
								<input type="button" class="searchFormInput doSearch" value="<spring:message code='general.search'/>">
								<input class="searchFormInput" type="reset" value="<spring:message code='general.clear'/>" />
								<input type="button" class="searchFormInput saveNewQueryButton" value="<spring:message code="expertSearch.saveNewQuery"/>" />
							</div>
						</form>
					</div>
					<div style="clear:both;"></div>
				</div>			
						
				<div class="searchResult">
					<h1 class="pageTitle"><spring:message code="general.searchResult"/></h1>
					<div class="searchResultDataContainer">					
						<table id="myTable" class="metkaTable sortableTable"> 
							<thead> 
								<tr> 
									<!-- Tulee tehdä eri hakutulostaulut sen mukaan mitä haetaan -->
								    <th>Sarjannro</th> 
								    <th>Sarjan lyhenne</th> 
								    <th>Kieli</th> 
								    <th>Sarjan nimi</th> 
								    <th>Sarjan URL</th> 
								</tr> 
							</thead> 
							<tbody> 
								<tr class="seriesSearchResultRow"> 
								    <td>0Lorem ipsum dolor sit amet</td> 
								    <td>0Lorem ipsum dolor sit amet</td> 
								    <td>0Lorem ipsum dolor sit amet</td> 
								    <td>0Lorem ipsum dolor sit amet</td> 
								    <td>0Lorem ipsum dolor sit amet</td> 
								</tr> 
							</tbody> 
						</table>
					</div>							
				</div>
            </div>
        </div>
    </body>
</html>