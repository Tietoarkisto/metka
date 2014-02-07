// TODO: clean this script to include only needed functionality
$(document).ready(function(){
    $(".sortableTable").tablesorter();

    $( ".datepicker" ).datepicker();

    // TODO: localize calendar texts
    jQuery(function($){
        $.datepicker.regional['fi'] = {
            closeText: 'Sulje',
            prevText: '&laquo;Edellinen',
            nextText: 'Seuraava&raquo;',
            currentText: 'T&auml;n&auml;&auml;n',
            monthNames: ['Tammikuu','Helmikuu','Maaliskuu','Huhtikuu','Toukokuu','Kes&auml;kuu','Hein&auml;kuu','Elokuu','Syyskuu','Lokakuu','Marraskuu','Joulukuu'],
            monthNamesShort: ['Tammi','Helmi','Maalis','Huhti','Touko','Kes&auml;','Hein&auml;','Elo','Syys','Loka','Marras','Joulu'],
            dayNamesShort: ['Su','Ma','Ti','Ke','To','Pe','Su'],
            dayNames: ['Sunnuntai','Maanantai','Tiistai','Keskiviikko','Torstai','Perjantai','Lauantai'],
            dayNamesMin: ['Su','Ma','Ti','Ke','To','Pe','La'],
            weekHeader: 'Vk',
            dateFormat: 'dd.mm.yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''};
        $.datepicker.setDefaults($.datepicker.regional['fi']);
    });

    // TODO: add pointerClass class to all relevant dom-objects in JSP-instead of listing them all here
    /*$(".materialFileRow, .materialCodebookFileRow, .materialErrorRow, .desktopWidgetDataRow, " +
        ".errorneousMaterialRow, .materialSearchResultRow, .publicationSearchResultRow, " +
        ".materialSeriesRow, .materialPublicationRow, .materialMaterialRow, #variablesListBasic li, " +
        ".publicationSeriesRow, .publicationMaterialRow, .link, #removeAdditionalFilingContractFile, " +
        ".studyLevelIdRow, .parTitleRow, .otherMaterialRow, .relatedMaterialRow, #addAltTitle, #removeAltTitle, " +
        ".removeAddedElement, .versionRow, .materialNotificationRow, .helpImage, .binderNumber, .packagingRow").hover(function() {
            $(this).css('cursor', 'pointer');
        }, function() {
            $(this).css('cursor', 'auto');
        });*/



    $("input[type=radio][name=language]").on("click", function() {
        var language = $(this).val();
        $(".translationSv").hide();
        $(".translationEn").hide();

        if ( language == "fi" ) {
            toggleFinnishTranslations(false);
        } else if ( language == "en" ) {
            $(".translationEn").show();
            toggleFinnishTranslations(true);
            $(".translationBorder").addClass("translationEnBorder");
            $("#materialNameEnInput").attr("disabled", false);
        } else if ( language == "sv" ) {
            $(".translationSv").show();
            toggleFinnishTranslations(true);
            $(".translationBorder").addClass("translationSvBorder");
        }
    });

    function toggleFinnishTranslations(hide) {
        $(".translationFi").find("input").attr("disabled", hide);
        $(".translationFi").find("textarea").attr("disabled", hide);
        $(".translationFi").find("select").attr("disabled", hide);
        if ( hide ) {
            $(".rowContainer:not(.containsTranslations)").hide();
            $(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").hide();
            $(".studyLevelDataSetContainer:not(.translated), .studyLevelDataSetContainer:not(.translated)").hide();
            $("#normalDesktop").hide();
            $("#translatorDesktop").show();
            $(".translationBorder").removeClass("translationEnBorder");
            $(".translationBorder").removeClass("translationSvBorder");
            $("#studyLevelData").find(".translationFi").find("a").hide();
        } else {
            $(".rowContainer:not(.containsTranslations)").show();
            $(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").show();
            $(".studyLevelDataSetContainer:not(.translated), .studyLevelDataSetTextareaContainer:not(.translated)").show();
            $("#normalDesktop").show();
            $("#translatorDesktop").hide();
            $(".translationBorder").removeClass("translationSvBorder");
            $(".translationBorder").removeClass("translationEnBorder");
            $("#studyLevelData").find(".translationFi").find("a").show();
        }
    }

    $(".helpImage").on("click", function() {
        window.open("help.html");
    });

    // Clean code here

    $(".tabNavi ul li a").click(function(){
        if(!$(this).hasClass("selected")){
            $(".tabNavi ul li a.selected").attr("id");
            $(".tabNavi ul li a").removeClass("selected");
            $(this).addClass("selected");
            var selectedId = $(this).attr("id");
            $(".tabs").hide();
            $(".tab_" + selectedId).show();
        }
    });

    $(".pointerClass").hover(
        function() {
            $(this).css('cursor', 'pointer');
        },
        function() {
            $(this).css('cursor', 'auto');
        }
    );

    /**
     * Display controller provided errors.
     */
    checkForErrors();
});

function checkForErrors() {
    if(errorMsg != null && errorMsg != "") {
        var str = strings[errorMsg];
        if(errorData != null && errorData.length > 0) {
            for(var i = 0; i < errorData.length; i++) {
                str = str.replace("{"+i+"}", strings[errorData[i]]);
            }
        }
        alert(str, errorTitle);
    }
}
