(function () {
	'use strict';

	/**
	 * Define ErrorManager object.
	 * This knows how to handle and display error messages, it can save a group of error messages
	 * and display all of them simultaneously.
	 * TODO: Sequential display of error messages where new message is shown every time user closes the previous message.
	 */

	var errors = [];
	function showError(error) {
		var str = MetkaJS.L10N.get(error.message);
		var i;
		for (i = 0; i < error.data.length; i++) {
			str = str.replace('{' + i + '}', error.data[i]);
		}
		alert(str, error.title);
	}

	MetkaJS.ErrorManager = {
		/**
		 * Define new ErrorMessage that contains a title, message and data for a single ErrorMessage.
		 * All attributes are public but there are two convinience methods, pushData and setData for
		 * specifically manipulating ErrorMessage data.
		 */
		ErrorMessage: function (title, message) {
			return {
				title: title,
				message: message,
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
		 * Push a new ErrorMessage to the queue.
		 * @param error ErrorMessage to be pushed to queue.
		 */
		push: function (error) {
			errors[errors.length] = error;
		},
		/**
		 * Display all errors in queue.
		 * Errors are removed from queue as part of displaying.
		 */
		showAll: function () {
			while (errors.length > 0) {
				showError(errors.pop());
			}
		},
		/**
		 * Show given ErrorMessage.
		 * @param error ErrorMessage to be shown
		 */
		show: function (error) {
			showError(error);
		},
		/**
		 * Returns the current top error in queue.
		 * Convenient for avoiding unnecessary variables during error manipulation.
		 * @returns Current top error in queue
		 */
		topError: function () {
			return errors[errors.length - 1];
		}
	};
}());