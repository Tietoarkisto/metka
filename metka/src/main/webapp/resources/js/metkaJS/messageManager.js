(function () {
    'use strict';
    // As an exception to the rule MessageManager contains code that is actually run when the script file is loaded.
    // This code overwrites window.alert and window.confirm with non blocking jQuery implementations that
    // provide a possibility of displaying multiple alert and confirm dialogs at once.

    /**
     * Replace native alert with jQuery dialog.
     * This provides non blocking alert dialog that can be styled.
     *
     * @param message - Message to be shown to user
     * @param titleKey - Localization key of the title for this alert
     */
    window.alert = function (message, titleKey) {
        if (!MetkaJS.isString(titleKey)) {
            titleKey = 'alert.notice.title';
        }
        var alertDlg = $('#alertDialog')
            .clone()
            .dialog({
                autoOpen: false,
                resizable: false,
                modal: true,
                width: 'auto',
                height: 'auto',
                title: MetkaJS.L10N.get(titleKey)
            });

        alertDlg.find('#alertContent').text(message);
        alertDlg.find('#alertCloseBtn').click(function () {
            alertDlg.dialog('close');
        });
        alertDlg.dialog('open');
    };

    /**
     * Replace native confirm with jQuery dialog.
     * This gives better styling options as well as better control.
     * @param message - Message to be shown to user
     * @param titleKey - Localization key of the title for this dialog
     * @param execute - Callback to be executed if user confirms action
     */
    window.confirm = function confirmation(message, titleKey, execute) {
        if (!MetkaJS.isString(titleKey)) {
            titleKey = 'confirmation.title';
        }
        var confirm = $('#confirmationDialog').clone()
            .dialog({
                autoOpen: false,
                resizable: false,
                modal: true,
                width: 'auto',
                height: 'auto',
                title: MetkaJS.L10N.get(titleKey)
            });

        confirm.find('#confirmationContent').text(message);
        if (execute) {
            confirm.find('#confirmationYesBtn').click(execute);
        } else {
            confirm.find('#confirmationYesBtn').click(function () {
                confirm.dialog('close');
            });
        }
        confirm.find('#confirmationNoBtn').click(function () {
            confirm.dialog('close');
        });

        confirm.dialog('open');
    };

    /**
     * Define MessageManager object.
     * This knows how to handle and display notice, error and confirmation messages, it can save a group of messages
     * and display all of them simultaneously.
     * Only difference between alert and confirmation dialog is the expectation for user input. If message contains
     * a callback method then confirmation is used, otherwise message is shown as an alert.
     *
     * TODO: Sequential display of messages where new message is shown every time user closes the previous message.
     */
    MetkaJS.MessageManager = (function() {
        var messages = [];

        function showMessage(message) {
            var str = MetkaJS.L10N.get(message.textKey);
            var i;
            for(i = 0; i < message.data.length; i++) {
                str = str.replace('{' + i + '}', message.data[i]);
            }
            if(MetkaJS.exists(message.callback)) {
                confirm(str, message.titleKey, message.callback);
            } else {
                alert(str, message.titleKey);
            }
        }

        return {
            /**
             * Define new Message that contains a title, message and data for a single Message as well as
             * a possible callback function. All attributes are public but there are two convenience methods,
             * pushData and setData for specifically manipulating Message data.
             *
             * @param titleKey Localization key of message title, can be null.
             * @param textKey Localization key of message text, required.
             * @param callback Callback function for message. If present will be treated as confirm dialog
             * @constructor
             */
            Message: function (titleKey, textKey, callback) {
                return {
                    titleKey: titleKey,
                    textKey: textKey,
                    callback: callback,
                    data: [],
                    pushData: function (data) {
                        this.data[this.data.length] = data;
                        return this;
                    },
                    setData: function (index, data) {
                        this.data[index] = data;
                        return this;
                    }
                };
            },
            /**
             * Push a new Message to the queue.
             * @param message Message to be pushed to queue.
             */
            push: function (message) {
                messages[messages.length] = message;
            },
            /**
             * Display all messages in queue.
             * Messages are removed from queue as part of displaying.
             */
            showAll: function () {
                while (messages.length > 0) {
                    showMessage(messages.pop());
                }
            },
            /**
             * Show given Message.
             * @param message Message to be shown
             */
            show: function (message) {
                showMessage(message);
            },
            /**
             * Returns the current top message in queue.
             * Convenient for avoiding unnecessary variables during message manipulation.
             * @returns Current top message in queue
             */
            topMessage: function () {
                return messages[messages.length - 1];
            }
        };
    }());
}());