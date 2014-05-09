(function () {
	'use strict';
	MetkaJS.JSConfigUtil = {
		/**
		 * Returns a field configuration for the given key and context.
		 * If no context is given uses current page (from Globals).
		 * Returns null if field can not be found with given parameters.
		 * @param key Field key of the required field
		 * @param context Desired configuration context, can be omitted
		 * @returns Field configuration
		 */
		getField: function (key, context) {
			if (!context) {
				context = MetkaJS.Globals.page.toUpperCase();
			}
			if (key && context && MetkaJS.JSConfig[context]) {
				return MetkaJS.JSConfig[context].fields[key];
			}
			return null;
		},
		/**
		 * Returns a selectionList configuration for the given key and context.
		 * If no context is given uses current page (from Globals).
		 * Returns null if no selection list can be found with given parameters.
		 * Returns the root configuration for selection list, meaning the actual
		 * configuration containing type, options etc. not the configuration
		 * given in field configuration necessarily.
		 * @param key SelectionList key used as a starting point for search.
		 * @param context Desired configuration context, can be omitted
		 * @returns SelectionList configuration
		 */
		getRootSelectionList: function (key, context) {
			if (!context) {
				context = MetkaJS.Globals.page.toUpperCase();
			}
			if (key && context && MetkaJS.JSConfig[context]) {
				// TODO: Implement loop protection
				var selectionList = MetkaJS.JSConfig[context].selectionLists[key];
				while (selectionList.key !== key) {
					key = selectionList.key;
					selectionList = MetkaJS.JSConfig[context].selectionLists[key];
				}
				return selectionList;
			}
			return null;
		},
		/**
		 * Returns a reference configuration for the given key and context.
		 * If no context is given uses current page (from Globals).
		 * Returns null if no reference can be found with given parameters.
		 * @param key Reference key of the requested reference.
		 * @param context Desired configuration context, can be omitted
		 * @returns Reference configuration.
		 */
		getReference: function (key, context) {
			if (!context) {
				context = MetkaJS.Globals.page.toUpperCase();
			}
			if (key && context && MetkaJS.JSConfig[context]) {
				return MetkaJS.JSConfig[context].references[key];
			}
			return null;
		},
		/**
		 * Returns configuration key for the given context.
		 * If no context is given uses current page (from Globals).
		 * @param context Desired configuration context, can be omitted
		 * @returns Configuration key
		 */
		getConfigurationKey: function (context) {
			if (!context) {
				context = MetkaJS.Globals.page.toUpperCase();
			}
			if (context && MetkaJS.JSConfig[context]) {
				return MetkaJS.JSConfig[context].key;
			}
			return null;
		}
	};
}());
