<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="materialGeneral" class="tabs2 general">						
	<div class="upperContainer">
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainerTopRow">
			</div>
		</div>
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainerTopRow translated translationFi">
				<label id="materialName" class="required leftSide"><spring:message code="study.view.basic.name"/></label>
				<input tabIndex="1" type="text" value="Eduskuntavaalitutkimukset 2003-2011: yhdistetty aineisto." name="nameFi" />
			</div>
			<!-- Not actually fi but must be seen on the finnish section -->
			<div class="materialDataSetContainerTopRow translated translationFi">
				<label id="materialNameEn" class="rightSide"><spring:message code="study.view.basic.name"/></label>
				<input id="materialNameEnInput" tabIndex="2" type="text" name="nameEn" />
			</div>
			<div class="materialDataSetContainerTopRow translationSv">
				<label id="materialNameSv" class="rightSide"><spring:message code="study.view.basic.name"/></label>
				<input tabIndex="2" type="text" name="nameSv" />
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer">
				<label id="materialNumber" class="required"><spring:message code="study.view.basic.number"/></label>
				<input readonly="readonly" class="unModifiable" type="text" name="number" value="FSD2556" />
			</div>													
			<div class="materialDataSetContainer translated translationFi">
				<label class="materialQuality" class="required"><spring:message code="study.view.basic.quality"/></label>
				<select id="materialQualitySelect"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&amp;Kvali</option></select>
			</div>
			<div class="materialDataSetContainer translated translationSv">
				<label class="materialQuality" class="required"><spring:message code="study.view.basic.quality"/></label>
				<select disabled="disabled" class="unModifiable"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&amp;Kvali</option></select>
			</div>
			<div class="materialDataSetContainer translated translationEn">
				<label class="materialQuality" class="required"><spring:message code="study.view.basic.quality"/></label>
				<select disabled="disabled" class="unModifiable"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&amp;Kvali</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialPublishing"><spring:message code="study.view.basic.publishing"/></label>
				<select><option>Kyllä</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer">

			<div class="materialDataSetContainer">
				<label id="acquisitiedMaterialNumber" class="required"><spring:message code="study.view.basic.acquisitionStudyNumber"/></label>
				<input type="text" readonly="readonly" class="unModifiable" name="hank.ain.nro" value="1465"/>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialAnonymized"><spring:message code="study.view.basic.anonymisation"/></label>
				<select><option>Anonymisoidaan FSD:ssä</option><option>Ei vaadi anonymisointia</option><option>Ei tietoa</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialDepictionPublishing"><spring:message code="study.view.basic.descriptionPublishing"/></label>
				<select><option>Kyllä</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer containsTranslations">

			<div class="materialDataSetContainer">
				<label id="materialReadyDate"><spring:message code="study.view.basic.readyDate"/></label>
				<input type="text" readonly="readonly" class="unModifiable" name="valmispvm" value=""/>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialAnonymized"><spring:message code="study.view.basic.dataProtection"/></label>
				<select><option>Kyllä</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialVariableDepictionPublishing"><spring:message code="study.view.basic.variablePublishing"/></label>
				<select><option>Kyllä</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer">
				<label id="materialSeries"><spring:message code="study.view.basic.series"/></label>
				<select id="materialSeriesSelect"><option>Valitse...</option><option>Yksittäiset aineistot</option><option>Sosiaalibarometrit</option><option>Evan kansalliset asennetutkimukset foo bar lorem</option></select>
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetTextareaContainer">
                 <label><spring:message code="study.view.basic.originalLocation"/></label>
                 <textarea></textarea>
            </div>  
			<div class="materialDataSetTextareaContainer">
                 <label><spring:message code="study.view.basic.notesForProcess"/></label>
                 <textarea></textarea>
            </div>      
		</div>
	</div>
	<br/>
	<div class="rowContainer">
		<%-- notificationDialog --%>
		<div class="materialRowTitle"><spring:message code="study.view.basic.notifications"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable" id="materialNotificationTable">
			<thead>
				<tr><th><spring:message code="study.view.basic.notification"/></th>
				<th><spring:message code="general.date"/></th>
				<th><spring:message code="study.view.basic.notificationMaker"/></th><th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.notifications}" var="notification">
					<tr class="materialNotificationRow"><td>${notification.notificationText}</td>
					<td>${notification.date}</td>
					<td>${notification.modifier}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td></tr>
				</c:forEach>
			</tbody>

		</table>		
		<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>		
	</div>
	<div class="rowContainer containsTranslations">
		<%-- versionInfoDialog (not done)--%>
		<div class="materialRowTitle"><spring:message code="study.view.basic.dataVersions"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable materialVersionTable">
			<thead>
				<tr><th><spring:message code="general.versions.versionNumber"/></th>
				<th><spring:message code="general.date"/></th>
				<th><spring:message code="general.handler"/></th>
				<th><spring:message code="general.versions.shortDescription"/></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.dataVersions}" var="version">
					<tr class="versionRow"><td>${version.number}</td>
					<td>${version.date}</td>
					<td>${version.handler}</td>
					<td>${version.shortDescription}</td></tr>
				</c:forEach>
			</tbody>
		</table>			
	</div>
	<div class="rowContainer containsTranslations">
		<%-- versionInfoDialog (not done)--%>
		<div class="materialRowTitle"><spring:message code="study.view.basic.descriptionVersions"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable materialVersionTable">
			<thead>
				<tr><th><spring:message code="general.versions.versionNumber"/></th>
				<th><spring:message code="general.date"/></th>
				<th><spring:message code="general.handler"/></th>
				<th><spring:message code="general.versions.shortDescription"/></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.descriptionVersions}" var="version">
					<tr class="versionRow"><td>${version.number}</td>
					<td>${version.date}</td>
					<td>${version.handler}</td>
					<td>${version.shortDescription}</td></tr>
				</c:forEach>
			</tbody>
		</table>			
	</div>
	
	<div class="rowContainer">
		<%-- relatedPublicationDialog --%>
		<div class="materialRowTitle"><spring:message code="study.view.basic.relatedPublications"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialPublicationTable">
			<thead>
				<tr><th><spring:message code="study.view.basic.publicationNumber"/></th>
				<th><spring:message code="study.view.basic.publicationName"/></th><th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.publications}" var="publication">
					<tr class="materialPublicationRow"><td>${publication.number}</td>
					<td>${publication.name}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code="general.buttons.remove"/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>

		</table>
		<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>					
	</div>
	<div class="rowContainer">
		<%-- relatedStudyDialog --%>
		<div class="materialRowTitle"><spring:message code="study.view.basic.relatedStudies"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialMaterialTable">
			<thead>
				<tr><th><spring:message code="study.view.basic.number"/></th>
				<th><spring:message code="study.view.basic.name"/></th><th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.relatedMateria}" var="relatedStudy">
					<tr class="materialMaterialRow"><td>${relatedStudy.number}</td>
					<td>${relatedStudy.name}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code="general.buttons.remove"/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>

		</table>
		<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>					
	</div>
	<div class="rowContainer">
		<div class="materialRowTitle"><spring:message code="study.view.basic.relatedBinders"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialBinderTable">
			<thead>
				<tr><th><spring:message code="study.view.basic.binderNumber"/></th>
				<th><spring:message code="study.view.basic.bindedMaterial"/></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.binders}" var="binder">
					<tr class="materialBinderRow">
						<td>${binder.id}</td>
						<td>${binder.bindedMaterial}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>	
	</div>
	
	<jsp:include page="buttons.jsp"/>	

</div>