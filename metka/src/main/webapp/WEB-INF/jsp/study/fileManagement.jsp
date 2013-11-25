<div id="materialFiles" class="tabs2 files">
					
	<div class="materialRowTitle">Liitetyt tiedostot&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" id="materialFileTable">
		<thead>
			<tr><th>Tiedosto</th><th>Tallentaja</th><th>Kieli</th><th></th></tr>
		</thead>
		<tbody>
			<tr class="materialFileRow"><td class="materialFileName">x/foo/bar/data.zip<a href="dialogs/attachFileEditDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>Mikko Tanskanen</td><td>Englanti</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
			<tr class="materialFileRow"><td class="materialFileName">x/bar/foo/fooBar.docx<a href="dialogs/attachFileEditDialog.html" class="fancyboxpopup fancybox.ajax"/></td><td>Tiina Isotalo</td><td>Suomi</td><td><a class="removeRow" href="#"><img title="Poista" src="../images/cancel.png"/></a></td></tr>
		</tbody>
	</table>
	<div class="materialTableActionLinkHolder"><a href="dialogs/attachFileDialog.html" class="addRow fancyboxpopup fancybox.ajax">Lis‰‰</a></div>						
	
	<div class="materialRowTitle">Poistetut tiedostot&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" id="materialRemovedFileTable">
		<thead>
			<tr><th>Tiedosto</th><th>Tiedoston kuvaus</th><th>Selite</th></tr>
		</thead>
		<tbody>
			<tr class="materialRemovedFileRow" id="r1"><td class="materialFileName">x/foo/bar/wrongData.zip</td><td>Datapaketti</td><td>T‰m‰ lis‰ttiin vahingossa</td></tr>
			<tr class="materialRemovedFileRow" id="r2"><td class="materialFileName">x/bar/foo/bar.docx</td><td>Dokumentaatio</td><td>Aivan v‰‰r‰ tiedosto, muuttunut</td></tr>
		</tbody>
	</table>
	
	<div id="materialRemovedFileInfoRow" style="display: none;">
		<div id="materialRemovedFileInfoContent">
			<table>
				<thead><tr><th class="fileInfoLabel"></th><th class="fileInfoContent"></th></tr></thead>
				<tbody>
					<tr><td class="fileInfoLabel">Tiedosto</td><td class="fileInfoContent"><a id="" class="fileInfoContentFileName" href="#"></a></td>
					<tr><td class="fileInfoLabel">Virallinen selite</td><td class="fileInfoContent">Nullam non tempor enim. Curabitur porttitor, mauris non viverra euismod, leo lorem lobortis massa, vitae ultricies mauris massa nec ligula. Donec quis elit ut augue vehicula viverra et in tellus</td></tr>
					<tr><td class="fileInfoLabel">Erp‰virallinen Selite</td><td class="fileInfoContent">Nullam non tempor enim. Curabitur porttitor, mauris non viverra euismod, leo lorem lobortis massa, vitae ultricies mauris massa nec ligula. Donec quis elit ut augue vehicula viverra et in tellus</td></tr>
					<tr><td class="fileInfoLabel">Kommentti</td><td class="fileInfoContent">Ajattelin t‰m‰n lis‰t‰ t‰nne</td></tr>
					<tr><td class="fileInfoLabel">Tallentaja</td><td class="fileInfoContent">Mikko Tanskanen</td></tr>
					<tr><td class="fileInfoLabel">PAS</td><td class="fileInfoContent">Ei tietoa</td></tr>
					<tr><td class="fileInfoLabel">Kieli</td><td class="fileInfoContent">Suomi</td></tr>
					<tr><td class="fileInfoLabel">Alkuper‰inen kieli</td><td class="fileInfoContent">Ei tietoa</td></tr>
					<tr><td class="fileInfoLabel">Ulosluovutus</td><td class="fileInfoContent">Ei tietoa</td></tr>
					<tr><td class="fileInfoLabel">WWW</td><td class="fileInfoContent">x</td></tr>
					<tr><td class="fileInfoLabel">Muutospvm</td><td class="fileInfoContent">3.10.2013</td></tr>
				</tbody>
			</table>
		</div>
	
		<div class="materialRowTitle">Tiedostohistoria</div>
		<table class="metkaTable sortableTable" id="materialFileInfoTable">
			<thead>
				<tr><th>Muokkausp‰iv‰m‰‰r‰</th><th>Muokkaaja</th><th>Selite</th></tr>
			</thead>
			<tbody>
				<tr><td>2.10.2013</td><td>Simo H‰yh‰</td><td>Muokkasin v‰h‰n</td></tr>
				<tr><td>4.10.2013</td><td>Ville Iivonen</td><td>Lis‰sin pari kohtaa</td></tr>
			</tbody>
		</table>
	</div>
	
	<jsp:include page="buttons.jsp"/>
</div>