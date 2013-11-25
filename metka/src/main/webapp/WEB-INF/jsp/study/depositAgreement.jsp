<div id="materialFilingContract" class="tabs2 depositAgreement">
	<div class="upperContainer">
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainer translated translationFi"><label>Ehto 1: käyttöoikeus</label>
				<select><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>	
			<div class="materialDataSetContainer translated translationSv"><label>Ehto 1: käyttöoikeus (sv)</label>
				<select disabled="true"><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>	
			<div class="materialDataSetContainer translated translationEn"><label>Ehto 1: käyttöoikeus (en)</label>
				<select disabled="true"><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>								
			<div class="materialDataSetContainer"><label>Arkistointisopimuksen pvm</label><input type="text" class="datepicker" name="ark.sop.pvm"/></div>
			<div class="materialDataSetContainer"><label>Jos heräte, ilmoituspvm</label><input type="text" class="datepicker" name="ilmoituspvm" /></div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer"><label>Käyttöehdon muutospvm</label><input type="text" class="datepicker" name="kayttoehdonmuutospvm" /></div>
			<div class="materialDataSetContainer"><label>Luovuttajan tyyppi</label>
				<select><option>Ei tietoa</option><option>Yrit.yht.</option><option>Tutkija(t)</option></select>
			</div>
			<div class="materialDataSetContainer"><label>Herätteen saaja</label><input type="text" class="" name="kayttoehdonmuutospvm" /></div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer"><label>Käyttöehto muutospvm jälkeen</label>
				<select><option>Avoin</option><option>opiskelu,opetus,tutkimus</option><option>Tutkimus</option><option>Lupa</option><option>Embargo</option></select>
			</div>

		<div class="materialDataSetContainer"><label>Arkistointisopimuksen tapa</label>
			<select><option>Ei tietoa</option><option>Ei tarvita sopimusta</option><option>Könttäsopimus</option><option>Norm+puite</option><option>On sopimus</option></select>
		</div>
			<div class="materialDataSetContainer"><label>Herätteen selite</label><input type="text" class="" name="herateselite" /></div>
		</div>						
			<div class="materialDataSetContainer">
				<label class="link">Arkistointisopimustiedosto&nbsp;<img title="Lisää" class="addRow" id="addFilingContractFile" src="../images/add.png"/></label><input type="text" class="" name="kayttoehto" />
			</div>
			<div id="additionalFilingContractFile" style="display: hidden;" class="materialDataSetContainer">
				<label class="link">Arkistointisopimustiedosto</label><input type="text" name="kayttoehto" /><img id="removeAdditionalFilingContractFile" title="Poista" src="../images/cancel.png"/>
			</div>
		<div class="rowContainer">
		</div>
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetTextareaContainer">
	           <label>Lisätiedot koskien arkistointisopimusta</label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer">
	           <label>Menettely, jos luvanantajaa ei tavoiteta tai hän ei itse voi antaa lupaa</label>
	           <textarea></textarea>
	      </div>      
	
	      <div class="materialDataSetTextareaContainer translated translationFi">
	           <label>Erityisehdot</label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer translated translationSv">
	           <label>Erityisehdot (sv)</label>
	           <textarea></textarea>
	      </div>        
	      <div class="materialDataSetTextareaContainer translated translationEn">
	           <label>Erityisehdot (en)</label>
	           <textarea></textarea>
	      </div>
	      <div class="materialDataSetTextareaContainer">
	           <label>Muuta kommentoitavaa</label>
	           <textarea></textarea>
	      </div>
		</div>

	</div>
	
	<jsp:include page="buttons.jsp"/>
	
</div>