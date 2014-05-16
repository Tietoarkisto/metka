// TODO: clean this script to include only needed functionality
$(document).ready(function () {
	'use strict';

	function changeToTab(tab) {
		if (!tab.hasClass('selected')) {
			$('.tabNavi a').removeClass('selected');
			tab.addClass('selected');
			var selectedId = tab.attr('id');
			$('.tabs').hide();
			$('.tab_' + selectedId).show();
			sessionStorage.setItem('currentTab', selectedId);
		}
	}

	function toggleAccordion(accordionTitle) {
		accordionTitle.next().toggle();
		accordionTitle.toggleClass('selected');
	}

	function toggleFinnishTranslations(hide) {
		/*$('.translationFi').find('input').attr('disabled', hide);
		$('.translationFi').find('textarea').attr('disabled', hide);
		$('.translationFi').find('select').attr('disabled', hide);
		if ( hide ) {
		$('.rowContainer:not(.containsTranslations)').hide();
		$('.materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)').hide();
		$('.studyLevelDataSetContainer:not(.translated), .studyLevelDataSetContainer:not(.translated)').hide();
		$('#normalDesktop').hide();
		$('#translatorDesktop').show();
		$('.translationBorder').removeClass('translationEnBorder');
		$('.translationBorder').removeClass('translationSvBorder');
		$('#studyLevelData').find('.translationFi').find('a').hide();
		} else {
		$('.rowContainer:not(.containsTranslations)').show();
		$('.materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)').show();
		$('.studyLevelDataSetContainer:not(.translated), .studyLevelDataSetTextareaContainer:not(.translated)').show();
		$('#normalDesktop').show();
		$('#translatorDesktop').hide();
		$('.translationBorder').removeClass('translationSvBorder');
		$('.translationBorder').removeClass('translationEnBorder');
		$('#studyLevelData').find('.translationFi').find('a').show();
		}*/
	}

	/*$('.sortableTable').tablesorter();*/

	// TODO: localize calendar texts
	$(document).ready(function () {
		$.datepicker.regional.fi = {
			closeText: 'Sulje',
			prevText: '&laquo;Edellinen',
			nextText: 'Seuraava&raquo;',
			currentText: 'T&auml;n&auml;&auml;n',
			monthNames: ['Tammikuu', 'Helmikuu', 'Maaliskuu', 'Huhtikuu', 'Toukokuu', 'Kesäkuu', 'Heinäkuu', 'Elokuu', 'Syyskuu', 'Lokakuu', 'Marraskuu', 'Joulukuu'],
			monthNamesShort: ['Tammi', 'Helmi', 'Maalis', 'Huhti', 'Touko', 'Kesä', 'Heinä', 'Elo', 'Syys', 'Loka', 'Marras', 'Joulu'],
			dayNamesShort: ['Su', 'Ma', 'Ti', 'Ke', 'To', 'Pe', 'Su'],
			dayNames: ['Sunnuntai', 'Maanantai', 'Tiistai', 'Keskiviikko', 'Torstai', 'Perjantai', 'Lauantai'],
			dayNamesMin: ['Su', 'Ma', 'Ti', 'Ke', 'To', 'Pe', 'La'],
			weekHeader: 'Vk',
			dateFormat: 'yy-mm-dd',
			firstDay: 1,
			isRTL: false,
			showMonthAfterYear: false,
			yearSuffix: ''
		};
		$.datepicker.setDefaults($.datepicker.regional.fi).setDefaults({beforeShow: function (i) {
			if ($(i).attr('readonly')) {
				return false;
			}
		}});
	});

	$('input[type=radio][name=language]').on('click', function () {
		var language = $(this).val();
		$('.translationSv').hide();
		$('.translationEn').hide();

		if (language === 'fi') {
			toggleFinnishTranslations(false);
		} else if (language === 'en') {
			$('.translationEn').show();
			toggleFinnishTranslations(true);
			$('.translationBorder').addClass('translationEnBorder');
			$('#materialNameEnInput').attr('disabled', false);
		} else if (language === 'sv') {
			$('.translationSv').show();
			toggleFinnishTranslations(true);
			$('.translationBorder').addClass('translationSvBorder');
		}
	});

	/*$('.helpImage').on('click', function() {
		window.open('help.html');
	});*/

	/**************
	 * Clean code *
	 **************/

    document.title = MetkaJS.L10N.get("page.title");

	$('.datepicker').datepicker();

	// Init tab navigation

	$('.tabNavi a').click(function (e) {
		e.preventDefault();
		changeToTab($(this));
	});

	changeToTab((function () {
		if (location.hash && location.hash.length > 0) {
			return $(location.hash);
		}
		if (sessionStorage.getItem('currentTab')) {
			var $resume = $('#' + sessionStorage.getItem('currentTab'));
			if ($resume.length) {
				return $resume;
			}
		}
		return $('.tabNavi a').first();
	}()));

	$('.accordionContent').hide();
	$('.accordionTitle').click(function () {
		toggleAccordion($(this));
	});

	$('#revisionSearchFormSearch').click(function () {
		$('#revisionSearchForm').submit();
	});

	/*$('#revisionModifyFormSave').click(function () {
		MetkaJS.SingleObject.formAction(MetkaJS.E.Form.SAVE);
	});

	$('#revisionModifyFormApprove').click(function () {
		MetkaJS.SingleObject.formAction(MetkaJS.E.Form.APPROVE);
	});*/

	/**
	 * Display controller provided errors that are present at page load time.
	 */
	MetkaJS.MessageManager.showAll();
});
