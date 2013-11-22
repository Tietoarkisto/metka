<div id="materialGeneral" class="tabs2 general">						
	<div class="upperContainer">
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainerTopRow">
			</div>
		</div>
		<div class="rowContainer containsTranslations">
			<div class="materialDataSetContainerTopRow translated translationFi">
				<label id="materialName" class="required leftSide">Aineiston nimi</label>
				<input tabIndex="1" type="text" value="Eduskuntavaalitutkimukset 2003-2011: yhdistetty aineisto." name="aineistonNimi" />
			</div>
			<!-- Not actually fi but must be seen on the finnish section -->
			<div class="materialDataSetContainerTopRow translated translationFi">
				<label id="materialNameEn" class="rightSide">Aineiston nimi (en)</label>
				<input id="materialNameEnInput" tabIndex="2" type="text" name="materialnNimiEng" />
			</div>
			<div class="materialDataSetContainerTopRow translationSv">
				<label id="materialNameSv" class="rightSide">Aineiston nimi (sv)</label>
				<input tabIndex="2" type="text" name="materialnNimiEng" />
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer">
				<label id="materialNumber" class="required">Aineistonumero</label>
				<input readonly="readonly" class="unModifiable" type="text" name="materialnro" value="FSD2556" />
			</div>													
			<div class="materialDataSetContainer translated translationFi">
				<label class="materialQuality" class="required">Aineiston laatu</label>
				<select id="materialQualitySelect"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&Kvali</option></select>
			</div>
			<div class="materialDataSetContainer translated translationSv">
				<label class="materialQuality" class="required">Aineiston laatu (sv)</label>
				<select disabled="true" class="unModifiable"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&Kvali</option></select>
			</div>
			<div class="materialDataSetContainer translated translationEn">
				<label class="materialQuality" class="required">Aineiston laatu (en)</label>
				<select disabled="true" class="unModifiable"><option class="unknown">Ei tietoa</option><option class="quantitative">Kvanti</option><option class="qualitative">Kvali</option><option class="both">Kvanti&Kvali</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialPublishing">Julkaisu</label>
				<select><option>Kyll‰</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer">

			<div class="materialDataSetContainer">
				<label id="acquisitiedMaterialNumber" class="required">Hankinta-aineistonumero</label>
				<input type="text" readonly="readonly" class="unModifiable" name="hank.ain.nro" value="1465"/>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialAnonymized">Anonymisointi</label>
				<select><option>Anonymisoidaan FSD:ss‰</option><option>Ei vaadi anonymisointia</option><option>Ei tietoa</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialDepictionPublishing">Aineiston kuvailun julkaisu</label>
				<select><option>Kyll‰</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer containsTranslations">

			<div class="materialDataSetContainer">
				<label id="materialReadyDate">Valmis pvm</label>
				<input type="text" readonly="readonly" class="unModifiable" name="valmispvm" value=""/>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialAnonymized">Tietosuoja</label>
				<select><option>Kyll‰</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
			<div class="materialDataSetContainer">
				<label id="materialVariableDepictionPublishing">Muuttujakuvailun julkaisu</label>
				<select><option>Kyll‰</option><option>Ei</option><option>Ei tietoa</option></select>
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetContainer">
				<label id="materialSeries">Sarja</label>
				<select id="materialSeriesSelect"><option>Valitse...</option><option>Yksitt‰iset aineistot</option><option>Sosiaalibarometrit</option><option>Evan kansalliset asennetutkimukset foo bar lorem</option></select>
			</div>
		</div>
		<div class="rowContainer">
			<div class="materialDataSetTextareaContainer">
                            <label>Alkuper‰inen sijainti</label>
                            <textarea></textarea>
                       </div>  
			<div class="materialDataSetTextareaContainer">
                            <label>Huomautuksia prosessiin</label>
                            <textarea></textarea>
                       </div>      
		</div>
	</div>
	<br/>
	<div class="rowContainer">
		<div class="materialRowTitle">Huomautukset&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable" id="materialNotificationTable">
			<thead>
				<tr><th>Huomautus</th><th>P‰iv‰m‰‰r‰</th><th>Huomauttaja</th><th></th></tr>
			</thead>
			<tbody>
				<tr class="materialNotificationRow"><td>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce consequat elit non dui euismod elementum. Aliquam erat volutpat. Etiam feugiat diam ut urna mollis, eu sollicitudin velit tincidunt. Pr<a href="dialogs/attachNotificationDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>12.10.2013</td><td>Mikko Tanskanen</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
				<tr class="materialNotificationRow"><td>Nulla sed libero sed erat venenatis dignissim. Sed vehicula urna a egestas imperdiet. Nam a turpis nisi. Proin eget dignissim risus.<a href="dialogs/attachNotificationDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>2.10.2013</td><td>Pekka Tanskanen</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
			</tbody>

		</table>		
		<div class="materialTableActionLinkHolder"><a href="dialogs/attachNotificationDialog.html" class="addRow fancyboxpopup fancybox.ajax">Lis‰‰</a></div>		
	</div>
	<div class="rowContainer containsTranslations">
		<div class="materialRowTitle">Datan versiot&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable materialVersionTable">
			<thead>
				<tr><th>Versionumero</th><th>P‰iv‰m‰‰r‰</th><th>K‰sittelij‰</th><th>Lyhyt selite</th></tr>
			</thead>
			<tbody>
				<tr class="versionRow"><td>1.0<a style="display: hidden;" href="dialogs/showVersionInfoDialog.html" class="showVersionInfo fancyboxpopup fancybox.ajax"></a></td><td>12.10.2013</td><td>Mikko Tanskanen</td><td>Selite1</td></tr>
			</tbody>
		</table>			
	</div>
	<div class="rowContainer containsTranslations">
		<div class="materialRowTitle">Kuvailun versiot&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable materialVersionTable">
			<thead>
				<tr><th>Versionumero</th><th>P‰iv‰m‰‰r‰</th><th>K‰sittelij‰</th><th>Lyhyt selite</th></tr>
			</thead>
			<tbody>
				<tr class="versionRow"><td>1.0<a style="display: hidden;" href="dialogs/showVersionInfoDialog.html" class="showVersionInfo fancyboxpopup fancybox.ajax"></a></td><td>12.10.2013</td><td>Mikko Tanskanen</td><td>Selite1</td></tr>
			</tbody>
		</table>			
	</div>
	
	<div class="rowContainer">
		<div class="materialRowTitle">Liittyv‰t julkaisut&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialPublicationTable">
			<thead>
				<tr><th>Julkaisunumero</th><th>Julkaisun nimi</th><th></th></tr>
			</thead>
			<tbody>
				<tr class="materialPublicationRow"><td>1234</td><td>Julkaisu1</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
			</tbody>

		</table>
		<div class="materialTableActionLinkHolder"><a href="dialogs/attachPublicationDialog.html" class="addRow fancyboxpopup fancybox.ajax">Lis‰‰</a></div>					
	</div>
	<div class="rowContainer">
		<div class="materialRowTitle">Liittyv‰t aineistot&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialMaterialTable">
			<thead>
				<tr><th>Aineistonumero</th><th>Aineiston nimi</th><th></th></tr>
			</thead>
			<tbody>
				<tr class="materialMaterialRow"><td>FDS3321</td><td>Vaalimaterial1</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
			</tbody>

		</table>
		<div class="materialTableActionLinkHolder"><a href="dialogs/attachMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax">Lis‰‰</a></div>					
	</div>
	<div class="rowContainer">
		<div class="materialRowTitle">Liittyv‰t mapit&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable sortableTable" id="materialBinderTable">
			<thead>
				<tr><th>Mappinumero</th><th>Mapitetun aineiston kuvaus</th></tr>
			</thead>
			<tbody>
				<tr class="materialBinderRow">
					<td>1290</td>
					<td>5kpl haastatteludokumentteja</td>
				</tr>
			</tbody>
		</table>	
	</div>
	
	<jsp:include page="buttons.jsp"/>	

</div>