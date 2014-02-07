<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="materialFilingContract" class="tabs2 depositAgreement">
	<div class="upperContainer">
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainer translated translationFi"><label><spring:message code="study.view.depositAgreement.license"/></label>
				<select><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>	
			<div class="materialDataSetContainer translated translationSv"><label><spring:message code="study.view.depositAgreement.license"/></label>
				<select disabled="disabled"><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>	
			<div class="materialDataSetContainer translated translationEn"><label><spring:message code="study.view.depositAgreement.license"/></label>
				<select disabled="disabled"><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>								
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.depositAgreementDate"/></label><input type="text" class="datepicker" name="ark.sop.pvm"/></div>
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.notificationDate"/></label><input type="text" class="datepicker" name="ilmoituspvm" /></div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.modificationDate"/></label><input type="text" class="datepicker" name="kayttoehdonmuutospvm" /></div>
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.contributorType"/></label>
				<select><option>Ei tietoa</option><option>Yrit.yht.</option><option>Tutkija(t)</option></select>
			</div>
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.triggerReceiver"/></label><input type="text" class="" name="kayttoehdonmuutospvm" /></div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.termsOfUseAfterModificationDate"/></label>
				<select><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>

		<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.type"/></label>
			<select><option>Ei tietoa</option><option>Ei tarvita sopimusta</option><option>Könttäsopimus</option><option>Norm+puite</option><option>On sopimus</option></select>
		</div>
			<div class="materialDataSetContainer"><label><spring:message code="study.view.depositAgreement.triggerDescription"/></label><input type="text" class="" name="herateselite" /></div>
		</div>						
			<div class="materialDataSetContainer">
				<label class="link"><spring:message code="study.view.depositAgreement.depositAgreementFile"/>&nbsp;<img title="Lisää" class="addRow" id="addFilingContractFile" src="../images/add.png"/></label><input type="text" class="" name="kayttoehto" />
			</div>
			<div id="additionalFilingContractFile" style="display: hidden;" class="materialDataSetContainer">
				<label class="link"><spring:message code="study.view.depositAgreement.depositAgreementFile"/></label><input type="text" name="kayttoehto" /><img id="removeAdditionalFilingContractFile" title="Poista" src="../images/cancel.png"/>
			</div>
		<div class="rowContainer">
		</div>
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetTextareaContainer">
	           <label><spring:message code="study.view.depositAgreement.additionalInformation"/></label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer">
	           <label><spring:message code="study.view.depositAgreement.procedure"/></label>
	           <textarea></textarea>
	      </div>      
	
	      <div class="materialDataSetTextareaContainer translated translationFi">
	           <label><spring:message code="study.view.depositAgreement.specialConditions"/></label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer translated translationSv">
	           <label><spring:message code="study.view.depositAgreement.specialConditions"/></label>
	           <textarea></textarea>
	      </div>        
	      <div class="materialDataSetTextareaContainer translated translationEn">
	           <label><spring:message code="study.view.depositAgreement.specialConditions"/></label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer">
	           <label><spring:message code="study.view.depositAgreement.otherComments"/></label>
	           <textarea></textarea>
	      </div>
		</div>

	</div>
	
	<jsp:include page="buttons.jsp"/>
	
</div>