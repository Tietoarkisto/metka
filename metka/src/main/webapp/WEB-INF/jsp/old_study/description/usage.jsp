<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.usage"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="dataRow containsTranslations">
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.weightFactors"/>&nbsp;&nbsp;(<input id="weightCoefficientToggle" type="checkbox" checked="checked"/><spring:message code="study.view.description.usage.noWeightFactors"/>)</label>
			<textarea class="weightCoefficient"></textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.weightFactors"/></label>
			<textarea class="weightCoefficient"></textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.weightFactors"/></label>
			<textarea class="weightCoefficient"></textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.contentModification"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.contentModification"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.contentModification"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
	</div>
	<div class="dataRow containsTranslations">
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.files"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.files"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.files"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.completeness"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.completeness"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.completeness"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
	</div>
	<div class="dataRow containsTranslations">
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.additionalReservation"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.additionalReservation"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.additionalReservation"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<label><spring:message code="study.view.description.usage.noticeable"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label><spring:message code="study.view.description.usage.noticeable"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label><spring:message code="study.view.description.usage.noticeable"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
	</div>
	<div class="dataRow containsTranslations">
		<div class="studyLevelDataSetTextareaContainer translated translationFi">
			<c:forEach items="${study.data.description.appraisals}" var="appraisal" varStatus="i">
				<c:choose>
					<c:when test="${i == 0}">
						<label id="studyLevelAppraisal"><spring:message code="study.view.description.usage.appraisal"/><img title="<spring:message code='general.buttons.add'/>" class="addRow" id="addAppraisal" src="../images/add.png"/></label>
						<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
					</c:when>
					<c:otherwise>
						<label id="studyLevelAppraisal"><spring:message code="study.view.description.usage.appraisal"/></label>
						<textarea>Foo bar lorem ipsum dolor sit amet</textarea><img class="removeRow" style="display:none;" title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationSv">
			<label id="studyLevelAppraisal"><spring:message code="study.view.description.usage.appraisal"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
		<div class="studyLevelDataSetTextareaContainer translated translationEn">
			<label id="studyLevelAppraisal"><spring:message code="study.view.description.usage.appraisal"/></label>
			<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
		</div>
	</div>
</div>