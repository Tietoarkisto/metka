<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.collecting"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="dataRow">
		<%-- collectingDateDialog --%>
		<table class="metkaTable studyLevelTwoHeadersTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.collectingDate"/></th>
					<th><spring:message code="study.view.description.collecting.collectingDateEvent"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.collectingDates}" var="date">
					<tr>
						<td>${date.date}</td>
						<td>${date.event}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectorDialog --%>
		<table class="metkaTable studyLevelOrderedTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.collector"/></th>
					<th><spring:message code="study.view.description.id"/></th>
					<th><spring:message code="study.view.description.idType"/></th>
					<th><spring:message code="study.view.description.affiliation"/></th>
					<th><spring:message code="study.view.description.affiliationId"/></th>
					<th><spring:message code="study.view.description.affiliationIdType"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.collectors}" var="collector">
					<tr>
						<td>${collector.name}</td>
						<td>
							<c:forEach items="${collector.ids}" var="id">
								${id.id}<br/>
							</c:forEach>
						</td>
						<td>
							<c:forEach items="${collector.ids}" var="id">
								${id.type}<br/>
							</c:forEach>
						</td>
						<td>${collector.affiliation}</td>
						<td>
							<c:forEach items="${collector.affiliation.ids}" var="id">
								${id.id}<br/>
							</c:forEach>
						</td>
						<td>
							<c:forEach items="${collector.affiliation.ids}" var="id">
								${id.type}<br/>
							</c:forEach>
						</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectingDataDialog --%>
		<table class="metkaTable studyLevelCollectingTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.observationUnit"/></th>
					<th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.idOrAddress"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.observationUnits}" var="obUnit">
					<tr>
						<td>${obUnit.content}</td>
						<td>${obUnit.uri}</td>
						<td>${obUnit.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectingDataDialog --%>
		<table class="metkaTable studyLevelCollectingTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.timePeriod"/></th>
					<th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.idOrAddress"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.timePeriods}" var="timePeriod">
					<tr>
						<td>${timePeriod.content}</td>
						<td>${timePeriod.uri}</td>
						<td>${timePeriod.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectingDataDialog --%>
		<table class="metkaTable studyLevelCollectingTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.collectingMethod"/></th>
					<th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.idOrAddress"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.methods}" var="method">
					<tr>
						<td>${method.content}</td>
						<td>${method.uri}</td>
						<td>${method.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectingDataDialog --%>
		<table class="metkaTable studyLevelCollectingTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.collectingTool"/></th>
					<th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.idOrAddress"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.collectingTools}" var="tool">
					<tr>
						<td>${tool.content}</td>
						<td>${tool.uri}</td>
						<td>${tool.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- collectingDataDialog --%>
		<table class="metkaTable">
			<thead>
				<tr>
					<th><spring:message code="study.view.description.collecting.sampleTool"/></th>
					<th><spring:message code="study.view.description.uri"/></th>
					<th><spring:message code="study.view.description.idOrAddress"/></th>
					<th><spring:message code="study.view.description.collecting.description"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.sampleTools}" var="tool">
					<tr>
						<td>${tool.content}</td>
						<td>${tool.uri}</td>
						<td>${tool.idOrAddress}</td>
						<td>${tool.description}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<div class="studyLevelDataSetContainer">
			<label><spring:message code="study.view.description.collecting.responseRate"/></label>
			<input type="text" name="" value="67%"/>
		</div>
	</div>
	<div class="dataRow">
		<div class="studyLevelDataSetTextareaContainer">
			<c:forEach items="${study.data.description.sourceBooks}" var="sourceBook" varStatus="i">
				<c:choose>
					<c:when test="${i == 0}">
						<label id="studyLevelDataSource"><spring:message code="study.view.description.sourceBook"/><img title="<spring:message code='general.buttons.add'/>" class="addRow" id="addDataSource" src="../images/add.png"/></label>
						<textarea></textarea>
					</c:when>
					<c:otherwise>
						<label id="studyLevelDataSource"><spring:message code="study.view.description.sourceBook"/></label>
						<textarea></textarea><img class="removeRow" style="display:none;" title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
	</div>
</div>	