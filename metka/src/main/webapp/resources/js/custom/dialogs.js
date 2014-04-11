$(document).ready(function(){
	$(".popupContainer").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        width: 'auto',
        height: 'auto'
    });

    $(".largePopupContainer").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        width: '60%',
        height: 'auto'
    });

    /**
     * Replace native alert with jQuery dialog.
     * This provides non blocking alert dialog that can be styled.
     *
     * @param message - Message to be shown to user
     * @param title - Title for this alert
     */
    window.alert = function(message, title){
        if(title == undefined || title == null || title == "") {
            title = "general.errors.title.notice";
        }
        var alertDlg = $("#alertDialog").clone();
        alertDlg.dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: 'auto',
            height: 'auto'
        });
        alertDlg.dialog("option", "title", MetkaJS.L10N.get(title));
        alertDlg.find("#alertContent").empty();
        alertDlg.find("#alertContent").text(message);
        alertDlg.find("#alertCloseBtn").click(function(alertDlg) {
            return function() {
                alertDlg.dialog("close");
            };
        }(alertDlg));
        alertDlg.dialog("open");
    };

    /**
     * Replace native confirm with jQuery dialog.
     * This gives better styling options as well as better control.
     * @param message - Message to be shown to user
     * @param title - Title for this dialog
     * @param execute - Callback to be executed if user confirms action
     */
    window.confirm = function confirmation(message, title, execute) {
        if(title == undefined || title == null || title == "") {
            title = "general.confirmation.title.confirm";
        }
        var confirm = $("#confirmationDialog").clone();
        confirm.dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: 'auto',
            height: 'auto'
        });
        confirm.dialog("option", "title", MetkaJS.L10N.get(title));
        confirm.find("#confirmationContent").empty();
        confirm.find("#confirmationContent").text(message);
        if(execute==undefined || execute==null) {
            confirm.find("#confirmationYesBtn").click(function(confirm) {
                return function() {
                    confirm.dialog("close");
                };
            }(confirm));
        } else {
            confirm.find("#confirmationYesBtn").click(execute);
        }
        confirm.find("#confirmationNoBtn").click(function(confirm) {
            return function() {
                confirm.dialog("close");
            };
        }(confirm));

        confirm.dialog("open");
    }
});