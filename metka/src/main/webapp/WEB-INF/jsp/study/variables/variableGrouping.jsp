<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="variablesGroupingContainer">
	<div id="variablesBox" class="variablesGroupingBox">
	<div id="variablesGroupingVariableFilter"><input id="variablesGroupingFilterInput" placeholder="<spring:message code='study.view.variables.filter'/>"></div>
		<ul id="variablesGroupingVariablesList">
			<c:forEach items="${ungroupedVariables}" var="variable">
				<li><input type="checkbox"/>${variable.description}</li>
			</c:forEach>
		</ul>
	</div>
	<div id="variablesToGroupArrowBox" class="">
		<div id="variablesToGroupArrow">&gt;</div>
		<br/>
		<div id="groupToVariablesArrow">&lt;</div>
	</div>
	
	<div id="variableGroupsBox" class="variablesGroupingBox fancytree-radio">
		<div id="newFolder"><input type="text" id="newFolderName"/><input type="button" id="addFolderButton" class="searchFormInput" value="<spring:message code='study.view.variables.addGroup'/>"/></div>
		<ul id="variableGroupRoot">
			<c:forEach items="${variableGroups}" var="variableGroup">
				<li class="parentVariableGroup folder">${variablaGroup.name}<img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/>
					<ul class="childVariableGroupList">
						<c:forEach items="${variableGroup.variables}" var="variable">
							<li>${variable.description}</li>
						</c:forEach>
					</ul>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>	