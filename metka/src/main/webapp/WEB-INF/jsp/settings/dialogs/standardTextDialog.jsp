<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="standardTextDialog">
	<h1 class="page-header"><spring:message code="settings.standardTexts.editTitle" arguments="${selectedStandardText.fi.key}"/></h1>
	<div class="popupRowHolder">
		<div class="languageSelectPopup">
			<input type="radio" name="language" value="fi"/><spring:message code="topMenu.language.finnish"/>
			<input type="radio" name="language" value="en"/><spring:message code="topMenu.language.english"/>
			<input type="radio" name="language" value="sv"/><spring:message code="topMenu.language.swedish"/>
		</div>
	</div>
	<table> 
		<thead> 
		<tr> 
		    <th><spring:message code="settings.standardTexts.key"/></th> 
		    <th><spring:message code="settings.standardTexts.description"/></th> 
		    <th><spring:message code="settings.standardTexts.standardText"/></th>
		</tr> 
		</thead> 
		<tbody> 
			<tr id="standardTextTable" class="translationFi"> 
			    <td><span><input type="text" value="${selectedStandardText.fi.key}"/></span></td> 
			    <td><textarea cols="50">${selectedStandardText.fi.description}</textarea></td> 
				<td><textarea cols="50">${selectedStandardText.fi.standardText}</textarea></td> 
			</tr> 
		</tbody> 
	</table>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>