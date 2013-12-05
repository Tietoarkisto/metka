<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="vocabularyDialog">
	<h1 class="pageTitle"><spring:message code="settings.vocabularies.editTitle" arguments="${selectedVocabulary.name}"/></h1>
	<div class="popupRowHolder">
		<div class="languageSelectPopup">
			<input type="radio" name="language" value="fi"/><spring:message code="topMenu.language.finnish"/>
			<input type="radio" name="language" value="en"/><spring:message code="topMenu.language.english"/>
			<input type="radio" name="language" value="sv"/><spring:message code="topMenu.language.swedish"/>
		</div>
	</div>
	<br/>
	<div class="popupRowHolder translationFi">
		<label class="inputRowLabel"><spring:message code="settings.vocabularies.name"/></label><input type="text" value="Havaintoyksiköt"/>
	</div>
	<div class="popupRowHolder translationFi">
		<label class="inputRowLabel"><spring:message code="settings.vocabularies.uri"/></label><input type="text" value=""/>
	</div>
	<table id="vocabularyDialogTable"> 
		<thead> 
		<tr> 
		    <th align="middle"><spring:message code="settings.vocabularies.key"/></th> 
		    <th align="middle"><spring:message code="settings.vocabularies.value"/></th> 
		    <th align="middle"><spring:message code="settings.vocabularies.additionalTextField"/></th>
		    <th align="middle"><spring:message code="general.buttons.remove"/></th>
		</tr> 
		</thead> 
		<tbody id="vocabularyBody"> 
			<c:forEach items="${vocabularies}" var="vocabulary">
				<tr class="translationFi"> 
				    <td align="middle"><span>${vocabulary.fi.key}</span></td> 
				    <td align="middle"><input type="text" value="${vocabulary.fi.value}"></td> 
				    <td align="middle"><input type="checkbox"/></td>
				    <td align="middle"><a href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../css/images/cancel.png"/></a></td>
				</tr> 	
				<tr class="translationEn"> 
				    <td align="middle"><span>${vocabulary.en.key}</span></td> 
				    <td align="middle"><input type="text" value="${vocabulary.en.key}"></td> 
				    <td align="middle"></td>
				</tr> 		
				<tr class="translationSv"> 
				    <td align="middle"><span>${vocabulary.sv.key}</span></td> 
				    <td align="middle"><input type="text" value="${vocabulary.sv.key}"></td> 
				    <td align="middle"></td>
				</tr> 	
			</c:forEach>					
		</tbody> 
	</table>
	<div class="popupButtonsHolder translationFi">
		<div style="margin-bottom: 10px;">
			<input type="checkbox" id="checkbox1" checked="checked" /><label for="checkbox1"><spring:message code="settings.vocabularies.newValuesToOldStudies"/><label>
		</div>
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" id="addTermToVocabulary" value="<spring:message code='settings.vocabularies.addWord'/>">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>">
	</div>

</div>	