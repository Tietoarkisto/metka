(function () {
    'use strict';

    MetkaJS.EventManager = (function() {
        var eventListeners = {};

        return {
            /**
             * Adds a listener function to listener queue.
             * Listener is separated by provided arguments
             * @param type Listened event type. Should come from MetkaJS.E.Event enum.
             * @param listener Some sort of unique identifier for each listener. If there already exists an entry with certain listener attribute then it is overwritten.
             * @param target Some sort of unique identifier for the target that is listened to. This is provided again by the triggered event and callbacks are called for only listeners with given target.
             * @param callback Function that is called by the triggered event. Should accept an object as its parameter and this object contains information on the triggered event, such as target.
             */
            listen: function (type, listener, target, callback) {
                var typed = eventListeners[type];
                if (!typed) {
                    typed = [];
                    eventListeners[type] = typed;
                }
                var targeted = typed[target];
                if (!targeted) {
                    targeted = [];
                    typed[target] = targeted;
                }
                var found = false;
                var i, length;
                for(i = 0, length = targeted.length; i < length; i++) {
                    if (targeted[i].listener === listener) {
                        targeted[i] = {
                            type: type,
                            listener: listener,
                            target: target,
                            callback: callback
                        };
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    targeted.push({
                        type: type,
                        listener: listener,
                        target: target,
                        callback: callback
                    });
                }
            },

            /**
             * Calls all applicable event listeners.
             * Searches for listeners with certain target parameter and type and then calls their callback function providing
             * the notifier information as a parameter
             * @param type Event type from MetkaJS.E.Event enum. Only listeners of certain event are notified.
             * @param notifier Information of the event thrower. Must contain at least target-attribute since this is used to find listeners.
             */
            notify: function (type, notifier) {
                if (!eventListeners[type] || !notifier.target || !eventListeners[type][notifier.target]) {
                    // no listeners or notifier didn't give target
                    return;
                }
                var listeners = eventListeners[type][notifier.target];
                if (!listeners) {
                    return;
                }
                var i, length;
                for (i = 0, length = listeners.length; i < length; i++) {
                    var listener = listeners[i];
                    listener.callback(notifier);
                }
            }
        };
    }());
}());