<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="variableDataContainer">
	
	<div id="variableData">
		<div id="variablesPrevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>
		<div class="variableDataTable">
			<table>
				<thead>
					<tr><th class="variableName"></th><th class="variableContent"></th></tr>
				</thead>
				<tbody>
					<tr>
						<td><spring:message code="study.view.variables.name"/></td>
						<td>${selectedVariable.name}</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.description"/></td>
						<td>${selectedVariable.description}</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.question"/></td>
						<td>
							<c:forEach items="${selectedVariable.questions}" var="question" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${question.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${question.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.preText"/></td>
						<td>
							<c:forEach items="${selectedVariable.preTexts}" var="preText" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${preText.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${preText.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.postText"/></td>
						<td>
							<c:forEach items="${selectedVariable.postTexts}" var="postText" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${postText.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${postText.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.interviewerGuide"/></td>
						<td>
							<c:forEach items="${selectedVariable.guides}" var="guide" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${guide.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${guide.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.notices"/></td>
						<td>
							<c:forEach items="${selectedVariable.notices}" var="notice" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${notice.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${notice.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.additionalInformation"/></td>
						<td>
							<c:forEach items="${selectedVariable.additionalInformations}" var="information" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${information.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${information.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td class="cellValignMiddle"><spring:message code="study.view.variables.dataProtection"/></td>
						<td>
							<c:forEach items="${selectedVariable.dataProtections}" var="dataProtection" varStatus="i">
								<c:choose>
									<c:when test="${i == 0}">
										<textarea>${dataProtection.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" />
									</c:when>
									<c:otherwise>
										<textarea>${dataProtection.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" />
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.valueDescription"/></td>
						<td>
							<c:forEach items="${selectedVariable.values}" var="value">
								${value.value}:<input class="variableNameInput" type="text" value="${value.description}"/>${value.responseRate}<br/>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.amount"/></td>
						<td>${selectedVariable.amount}</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.min"/></td>
						<td>${selectedVariable.min}</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.max"/></td>
						<td>${selectedVariable.max}</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.average"/></td>
						<td>${selectedVariable.average}</td>
					</tr>
					<tr>
						<td><spring:message code="study.view.variables.standardDeviation"/></td>
						<td>${selectedVariable.standardDeviation}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	
	<div id="variableGroupData">
		<div id="variablesPrevNextContainer"><h1 class="prev">&lt;</h1><h1 class="next">&gt;</h1></div>
		<div class="variableDataTable">
			<table>
				<thead>
					<tr><th class="variableName"></th><th class="variableContent"></th></tr>
				</thead>
				<tbody>
					<c:forEach items="${selectedGroup.groupTexts}" var="groupText" varStatus="i">
						<c:choose>
							<c:when test="${i == 0}">
								<tr>
									<td id="variableGroupName"><spring:message code="study.view.variables.groupText"/></td>
									<td><textarea cols="40" rows="5">${groupText.content}</textarea><img title="<spring:message code='general.buttons.add'/>" src="../images/add.png" /></td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<td id="variableGroupName"><spring:message code="study.view.variables.groupText"/></td>
									<td><textarea cols="40" rows="5">${groupText.content}</textarea><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></td>
								</tr>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>						
</div>