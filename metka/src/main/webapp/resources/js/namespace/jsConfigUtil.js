MetkaJS.JSConfigUtil = {
    /**
     * Returns a field configuration for the given key and context.
     * If no context is given uses current page (from Globals).
     * Returns null if field can not be found with given parameters.
     * @param key Field key of the required field
     * @param context Desired configuration context, can be omitted
     * @returns Field configuration
     */
    getField: function(key, context) {
        if(context == null) {
            context = MetkaJS.Globals.page.toUpperCase();
        }
        if(key != null && context != null && MetkaJS.JSConfig[context] != null) {
            return MetkaJS.JSConfig[context].fields[key];
        }
        return null;
    },
    /**
     * Returns a choicelist configuration for the given key and context.
     * If no context is given uses current page (from Globals).
     * Returns null if no choicelist can be found with given parameters.
     * Returns the root configuration for choicelist, meaning the actual
     * configuration containing type, options etc. not the configuration
     * given in field configuration necessarily.
     * @param key Choicelist key used as a starting point for search.
     * @param context Desired configuration context, can be omitted
     * @returns Choicelist configuration
     */
    getRootChoicelist: function(key, context) {
        if(context == null) {
            context = MetkaJS.Globals.page.toUpperCase();
        }
        if(key != null && context != null && MetkaJS.JSConfig[context] != null) {
            // TODO: Implement loop protection
            var choicelist = MetkaJS.JSConfig[context].choicelists[key];
            while(choicelist.key != key) {
                key = choicelist.key;
                choicelist = MetkaJS.JSConfig[context].choicelists[key];
            }
            return choicelist;
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
    getReference: function(key, context) {
        if(context == null) {
            context = MetkaJS.Globals.page.toUpperCase();
        }
        if(key != null && context != null && MetkaJS.JSConfig[context] != null) {
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
    getConfigurationKey: function(context) {
        if(context == null) {
            context = MetkaJS.Globals.page.toUpperCase();
        }
        if(context != null && MetkaJS.JSConfig[context] != null) {
            return MetkaJS.JSConfig[context].key;
        }
        return null;
    }
};