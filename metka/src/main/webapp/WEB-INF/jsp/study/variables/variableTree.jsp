<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="basicVariableTreeContainer" class="variablesTree">
	<div id="variableFilter"><input id="variableFilterInput" placeholder="<spring:message code='study.view.variables.filter'/>" ></div>
	<ul id="variablesListBasic">
		<c:forEach items="${study.data.variables}" var="variable">
			<li>${variable.description}</li>
		</c:forEach>
	</ul>
</div>