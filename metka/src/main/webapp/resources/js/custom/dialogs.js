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
    $("#confirmationNoBtn").click(function() {
        $("#confirmationDialog").dialog("close");
    });

    window.alert = function(message, title){
        if(!title) {
            title = "general.errors.title.notice";
        }
        var alertDlg = $("#alertDialog");
        alertDlg.dialog("option", "title", strings[title]);
        alertDlg.find("#alertContent").empty();
        alertDlg.find("#alertContent").text(message);
        alertDlg.dialog("open");
    };
});

function confirmation(message, title, execute){
    if(!title) {
        title = strings["general.confirmation.title.confirm"];
    }
    var confirm = $("#confirmationDialog");
    confirm.dialog("option", "title", title);
    confirm.find("#confirmationContent").empty();
    confirm.find("#confirmationContent").text(message);

    confirm.find("#confirmationYesBtn").click(execute);

    confirm.dialog("open");
}

function dialogClose(id) {
    $("#"+id).dialog("close");
}