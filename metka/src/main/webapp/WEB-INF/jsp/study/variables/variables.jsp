<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="materialVariables" class="tabs2 variables">
					
	<div id="variablesTreeSelection">
		<div class="variableTreeRadioSelect">
			<input type="radio" name="variableTreeType" id="basicVariableTree" checked autocomplete="off" />
			<spring:message code="study.view.variables.basicView"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/>
		</div>
		<div class="variableTreeRadioSelect">
			<input type="radio" name="variableTreeType" id="groupedVariableTree" autocomplete="off" />
			<spring:message code="study.view.variables.groupedView"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/>
		</div>
		<div class="variableTreeRadioSelect">
			<input type="radio" name="variableTreeType" id="variablesGrouping" autocomplete="off" />
			<spring:message code="study.view.variables.groupingView"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/>
		</div>
	</div>
	
	<div id="variablesMainContainer" class="translationBorder">
	
		<jsp:include page="variableTree.jsp"/>
		
		<jsp:include page="variableGroups.jsp"/>
	
		<jsp:include page="variableAndGroupData.jsp"/>
	
		<jsp:include page="variableGrouping.jsp"/>
	
		<div style="clear:both;"></div>
		
		<jsp:include page="../buttons.jsp"/>
	</div>		
</div>	