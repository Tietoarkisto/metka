<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.coverage"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="rowContainer">
		<div class="studyLevelTableTitle"><spring:message code="study.view.description.coverage.timePeriods"/></div>
		<table class="metkaTable studyLevelTwoHeadersTable">
			<thead>
				<tr><th><spring:message code="general.date"/></th>
				<th><spring:message code="study.view.description.coverage.timePeriodEvent"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.timePeriods}" var="timePeriod">
					<tr><td>${timePeriod.date}</td>
					<td>${timePeriod.event}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addTimePeriodDialog.html" class="addRow fancyboxpopup fancybox.ajax link"><spring:message code='general.buttons.add'/></a></div>
	</div>
	<div class="rowContainer">
		<table class="metkaTable studyLevelTwoHeadersTable">
			<thead>
				<tr><th><spring:message code="study.view.description.coverage.country"/></th>
				<th><spring:message code="study.view.description.coverage.country.abbreviation"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.countries}" var="country">
					<tr><td>${country.name}</td>
					<td>${country.abbreviation}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCountryDialog.html" class="addRow fancyboxpopup fancybox.ajax link"><spring:message code='general.buttons.add'/></a></div>
	</div>
	<div class="rowContainer">
		<table class="metkaTable studyLevelTwoHeadersTable">
			<thead>
				<tr><th><spring:message code="study.view.description.coverage.universe"/></th>
				<th><spring:message code="study.view.description.coverage.universe.masking"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.universes}" var="universe">
					<tr><td>${universe.name}</td>
					<td>${universe.masking}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addUniverseDialog.html" class="addRow fancyboxpopup fancybox.ajax link"><spring:message code='general.buttons.add'/></a></div>
	</div>

	<div class="rowContainer">
		<table class="metkaTable">
			<thead>
				<tr><th><spring:message code="study.view.description.coverage.geoCover"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.geoCovers}" var="geoCover">
					<tr><td>${geoCover}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addGeoCoverDialog.html" class="addRow fancyboxpopup fancybox.ajax link"><spring:message code='general.buttons.add'/></a></div>
	</div>
</div>