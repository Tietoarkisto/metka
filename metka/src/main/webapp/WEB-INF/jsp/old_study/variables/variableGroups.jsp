<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="groupedVariableTreeContainer" class="variablesTree">
	<ul id="variableList">
		<c:forEach items="${variableGroups}" var="variableGroup">
			<li class="parentVariableGroup folder">${variableGroup.name}
				<ul class="childVariableGroupList">
					<c:forEach items="${variableGroup.variables}" var="variable">
						<li>${variable.description}</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>		
	</ul>
</div>