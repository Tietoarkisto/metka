$(document).ready(function(){
	$(".popupContainer").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        width: 'auto',
        height: 'auto'
    });

    /**
     * Replace native alert with jQuery dialog.
     * This provides non blocking alert dialog that can be styled.
     */
    $("#alertCloseBtn").click(function() {
        $("#alertDialog").dialog("close");
    });
    window.alert = function(message, title){
        title = title || "general.errors.title.notice";
        var alertDlg = $("#alertDialog");
        alertDlg.dialog("option", "title", errors[title]);
        alertDlg.find("#content").empty();
        alertDlg.find("#content").text(message);
        alertDlg.dialog("open");
    };
});
