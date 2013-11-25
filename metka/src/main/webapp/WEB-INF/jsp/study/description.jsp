<div id="materialStudyLevel" class="tabs2 description">
	<div id="studyLevelTopRow">
		<input type="button" id="toggleAccordion" class="searchFormInput" value="Avaa kaikki"/>
	</div>
	<div id="studyLevelData">						
		<label class="studyLevelTitle">Muut nimet&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetContainer translated translationFi">
					<label id="studyLevelAltTitle">Rinnakkainen nimi&nbsp;<img title="Lis‰‰" class="addRow" id="addAltTitle" src="../images/add.png"/></label>
					<input type="text" name="" value="toinen nimi" /><img class="removeRow" id="removeAltTitle" style="display:none;" title="Poista" src="../images/cancel.png"/>
				</div>
				<div class="studyLevelDataSetContainer translated translationSv">
					<label id="studyLevelAltTitle">Rinnakkainen nimi (sv)</label>
					<input type="text" name="" value="toinen nimi" />
				</div>
				<div class="studyLevelDataSetContainer translated translationEn">
					<label id="studyLevelAltTitle">Rinnakkainen nimi (en)</label>
					<input type="text" name="" value="toinen nimi" />
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetContainer translated translationFi">
					<div class="studyLevelTableTitle">Muunkieliset nimet</div>
					<table class="metkaTable studyLevelTwoHeadersTable">
						<thead>
							<tr><th>Nimi</th><th>Kieli</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="parTitleRow"><td>Another title<a class="removeRow" href="dialogs/studylevel/addParTitleDialog.html" class="fancyboxpopup fancybox.ajax link"/></td><td>Norja</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="parTitleRow"><td>Andra title<a class="removeRow" href="dialogs/studylevel/addParTitleDialog.html" class="fancyboxpopup fancybox.ajax link"/></td><td>Espanja</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addParTitleDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>	
				</div>
			</div>							
		</div>
		<label class="studyLevelTitle">Tekij‰t ja tuottajat&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer">
				<table class="metkaTable studyLevelOrderedTable" id="studyLevelAuthors">
					<thead>
						<tr><th>Tekij‰</th><th>Tunniste</th><th>Tyyppi</th><th>Taustaorg.</th><th>Taustaorg. tunniste</th><th>Taustaorg. tunnistetyyppi</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>									
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addAuthorDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
			<div class="rowContainer">
				<table class="metkaTable studyLevelOrderedTable" id="studyLevelOtherAuthors">
					<thead>
						<tr><th>Muu tekij‰</th><th>Tunniste</th><th>Tyyppi</th><th>Taustaorg.</th><th>Taustaorg. tunniste</th><th>Taustaorg. tunnistetyyppi</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addAuthorDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelOrderedTable" id="studyLevelProducers">
					<thead>
						<tr><th>Tuottaja</th><th>Tunniste</th><th>Tunnistetyyppi</th><th>Rooli</th><th>Projektinumero</th><th>Lyhenne</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Proactum Oy</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Rahoittaja</td><td>123-s<br/>234-r</td><td>pro</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy</td><td>123</td><td>Perus-id</td><td>Rahoittaja</td><td>123-s</td><td>pro</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addProducerDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
		</div>
		<label class="studyLevelTitle">Asiasanat ja tieteenalat&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer">
				<div class="studyLevelTableTitle">Asiasanat</div>
				<table class="metkaTable studyLevelVocabularyTable">
					<thead>
						<tr><th>Sanasto</th><th>Asiasana</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>YSA</td><td>eduskuntavaalit</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>YSA</td><td>‰‰nest‰minen</td><td>http://vesa.lib.helsinki.fi/</td><td>bar</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addKeywordDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
			<div class="rowContainer">
				<div class="studyLevelTableTitle">Tieteenalat</div>
				<table class="metkaTable studyLevelVocabularyTable">
					<thead>
						<tr><th>Sanasto</th><th>Tieteenala</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>FSD</td><td>poliittinen k‰ytt‰ytyminen</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>CESSDA</td><td>vaalit</td><td>http://vesa.lib.helsinki.fi/</td><td>bar</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addTopicDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>						
		</div>
		<label class="studyLevelTitle">Abstrakti&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>						
		<div class="accordionContent">
			<div class="rowContainer">
				<div class="studyLevelDataSetTextareaContainer">
					<label>Tiivistelm‰</label>
					<textarea>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras imperdiet tincidunt sapien nec pretium. Aliquam erat volutpat. Pellentesque faucibus velit et iaculis ullamcorper. Proin eu urna magna. Nam sed mauris posuere, bibendum nunc sed, rhoncus purus. Phasellus nisi est, blandit sit amet tristique non, posuere vel magna. Nulla pharetra, nisl nec hendrerit laoreet, neque mauris posuere justo, sed fermentum dolor lorem </textarea>
				</div>

				<div class="studyLevelDataSetTextareaContainer">
					<label>Viittaustiedot</label>
					<textarea>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras imperdiet tincidunt sapien nec pretium. Aliquam erat volutpat. Pellentesque faucibus velit et iaculis</textarea>
				</div>
			</div>
		</div>
		<label class="studyLevelTitle">Kattavuus&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer">
				<div class="studyLevelTableTitle">Ajalliset kattavuudet</div>
				<table class="metkaTable studyLevelTwoHeadersTable">
					<thead>
						<tr><th>P‰iv‰m‰‰r‰</th><th>timeperiodevent</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>12.10.2013</td><td>start</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>22.10.2013</td><td>end</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addTimePeriodDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
			<div class="rowContainer">
				<table class="metkaTable studyLevelTwoHeadersTable">
					<thead>
						<tr><th>Maa</th><th>Lyhenne</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Suomi</td><td>FIN</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Ven‰j‰</td><td>RUS</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCountryDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
			<div class="rowContainer">
				<table class="metkaTable studyLevelTwoHeadersTable">
					<thead>
						<tr><th>Perusjoukko</th><th>Rajaus</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Foo</td><td>E</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addUniverseDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable">
					<thead>
						<tr><th>Kohdealue</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Suomi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Ven‰j‰</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addGeoCoverDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>
		</div>
		<label class="studyLevelTitle">Aineistonkeruu&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer">
				<table class="metkaTable studyLevelTwoHeadersTable">
					<thead>
						<tr><th>Ajankohta</th><th>colldateevent</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>12.10.2013</td><td>start</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>22.10.2013</td><td>end</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addTimePeriodDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelOrderedTable">
					<thead>
						<tr><th>Ker‰‰j‰</th><th>Tunniste</th><th>Tunnistetyyppi</th><th>Taustaorg.</th><th>Taustaorg. tunniste</th><th>Taustaorg. tunnistetyyppi</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Mikko Tanskanen</td><td>123<br/>234</td><td>Perus-id<br/>Toinen-id</td><td>Proactum Oy</td><td>123-s</td><td>Tyyppi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Proactum Oy (pro)</td><td>123-s</td><td>Tyyppi</td><td></td><td></td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectorDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelCollectingTable">
					<thead>
						<tr><th>Havaintoyksikkˆ/Aineistoyks.kategoria</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>FooBar</td><td>http://vesa.lib.helsinki.fi/</td><td>http://vesa.lib.helsinki.fi/</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Pirkanmaa</td><td>http://vesa.lib.helsinki.fi/</td><td>http://vesa.lib.helsinki.fi/</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectionDataDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelCollectingTable">
					<thead>
						<tr><th>Aikaulottuvuus</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Pitkitt‰isaineisto</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectionDataDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelCollectingTable">
					<thead>
						<tr><th>Keruumenetelm‰/-tekniikka</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Foo</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Muu</td><td>http://vesa.lib.helsinki.fi/</td><td>bar</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectionDataDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable studyLevelCollectingTable">
					<thead>
						<tr><th>Keruuv‰line/Ohjeistu</th><th>Uri</th><th>Tunniste/osoite</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Foo</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Muu</td><td>http://vesa.lib.helsinki.fi/</td><td>bar</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectionDataDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<table class="metkaTable">
					<thead>
						<tr><th>Otantamenetelm‰/Aineiston valintatapa</th><th>Uri</th><th>Tunniste/osoite</th><th>Kuvaus</th><th></th></tr>
					</thead>
					<tbody>
						<tr><td>Foo</td><td>http://vesa.lib.helsinki.fi/</td><td>foo</td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						<tr><td>Muu</td><td>http://vesa.lib.helsinki.fi/</td><td>bar</td><td></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
					</tbody>
				</table>
				<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addCollectionDataDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
			</div>

			<div class="rowContainer">
				<div class="studyLevelDataSetContainer">
					<label>Vastausprosentti</label>
					<input type="text" name="" value="67%"/>
				</div>
			</div>
			<div class="rowContainer">
				<div class="studyLevelDataSetTextareaContainer">
					<label id="studyLevelDataSource">L‰hdeaineisto<img title="Lis‰‰" class="addRow" id="addDataSource" src="../images/add.png"/></label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea><img class="removeRow" style="display:none;" title="Poista" src="../images/cancel.png"/>
				</div>
			</div>
		</div>					
		<label class="studyLevelTitle">Aineiston k‰yttˆ&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>Painokertoimet&nbsp;&nbsp;(<input id="weightCoefficientToggle" type="checkbox" checked="true"/>ei painokertoimia)</label>
					<textarea class="weightCoefficient"></textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>Painokertoimet (sv)</label>
					<textarea class="weightCoefficient"></textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>Painokertoimet (en)</label>
					<textarea class="weightCoefficient"></textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>Sis‰llˆllinen muokkaus</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>Sis‰llˆllinen muokkaus (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>Sis‰llˆllinen muokkaus (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>Tiedostot</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>Tiedostot (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>Tiedostot (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>T‰ydellisyys</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>T‰ydellisyys (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>T‰ydellisyys (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>Lis‰varauma</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>Lis‰varauma (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>Lis‰varauma (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label>Huomioitavaa</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label>Huomioitavaa (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label>Huomioitavaa (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetTextareaContainer translated translationFi">
					<label id="studyLevelAppraisal">Arviointia<img title="Lis‰‰" class="addRow" id="addAppraisal" src="../images/add.png"/></label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea><img class="removeRow" style="display:none;" title="Poista" src="../images/cancel.png"/>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationSv">
					<label id="studyLevelAppraisal">Arviointia (sv)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
				<div class="studyLevelDataSetTextareaContainer translated translationEn">
					<label id="studyLevelAppraisal">Arviointia (en)</label>
					<textarea>Foo bar lorem ipsum dolor sit amet</textarea>
				</div>
			</div>
		</div>
		<label class="studyLevelTitle">Muut materiaalit&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
		<div class="accordionContent">
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetContainer translated translationFi">
					<div class="studyLevelTableTitle">Oheismateriaali</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Oheismateriaali</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
				<div class="studyLevelDataSetContainer translated translationSv">
					<div class="studyLevelTableTitle">Oheismateriaali (sv)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Oheismateriaali</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
				<div class="studyLevelDataSetContainer translated translationEn">
					<div class="studyLevelTableTitle">Oheismateriaali (en)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Oheismateriaali</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="relatedMaterialRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.<a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addRelatedMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetContainer translated translationFi">
					<div class="studyLevelTableTitle">Muu materiaali</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Uri</th><th>Lyhyt kuvaus</th><th>Tarkka kuvaus</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>foo bar</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>bar foo</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
				<div class="studyLevelDataSetContainer translated translationSv">
					<div class="studyLevelTableTitle">Muu materiaali (sv)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Uri</th><th>Lyhyt kuvaus</th><th>Tarkka kuvaus</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>foo bar</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>bar foo</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
				<div class="studyLevelDataSetContainer translated translationEn">
					<div class="studyLevelTableTitle">Muu materiaali (en)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Uri</th><th>Lyhyt kuvaus</th><th>Tarkka kuvaus</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>foo bar</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
							<tr class="otherMaterialRow"><td>http://vesa.lib.helsinki.fi/<a href="dialogs/studylevel/addOtherMaterialDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>bar foo</td><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherMaterialDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>
			</div>
			<div class="rowContainer containsTranslations">
				<div class="studyLevelDataSetContainer translated translationFi">
					<div class="studyLevelTableTitle">Julkaisuihin liittyvi‰ huomioita</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Huomiot</th><th></th></tr>
						</thead>
						<tbody>
							<tr class="otherRefsRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherRefsDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>	
				<div class="studyLevelDataSetContainer translated translationSv">
					<div class="studyLevelTableTitle">Julkaisuihin liittyvi‰ huomioita (sv)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Huomiot</th></th></tr>
						</thead>
						<tbody>
							<tr class="otherRefsRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherRefsDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>	
				<div class="studyLevelDataSetContainer translated translationEn">
					<div class="studyLevelTableTitle">Julkaisuihin liittyvi‰ huomioita (en)</div>
					<table class="metkaTable">
						<thead>
							<tr><th>Huomiot</th></th></tr>
						</thead>
						<tbody>
							<tr class="otherRefsRow"><td>g in odio in, condimentum gravida turpis. Mauris cursus dapibus velit vitae placerat. Vivamus consequat non orci eu pretium. Vestibulum quam justo, auctor sed venenatis at, dignissim et lacus. Aliquam ultricies lacus non nunc pulvinar, ac hendrerit nibh euismod.</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
						</tbody>
					</table>
					<div class="accordionTableActionLinkHolder"><a href="dialogs/studylevel/addOtherRefsDialog.html" class="addRow fancyboxpopup fancybox.ajax link">Lis‰‰</a></div>
				</div>								
			</div>
		</div>					
		
	</div>
	
	<jsp:include page="buttons.jsp"/>
	
</div>