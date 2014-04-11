MetkaJS.EventManager = (function() {
    var eventListeners = new Array();

    EventListener = function(type, listener, target, callback) {
        this.type = type;
        this.listener = listener;
        this.target = target;
        this.callback = callback;
    }

    function registerListener(type, listener, target, callback) {
        var typed = eventListeners[type];
        if(typed == null) {
            typed = new Array();
            eventListeners[type] = typed;
        }
        var targeted = typed[target];
        if(targeted == null) {
            targeted = new Array();
            typed[target] = targeted;
        }
        var found = false;
        for(var i= 0, length = targeted.length; i<length; i++) {
            if(targeted[i].listener == listener) {
                targeted[i] = new EventListener(type, listener, target, callback);
                found = true;
                break;
            }
        }
        if(!found) {
            targeted.push(new EventListener(type, listener, target, callback));
        }
    }

    function notifyListeners(type, notifier) {
        var listeners = eventListeners[type][notifier];
        if(listeners == null) {
            return;
        }
        for(var i = 0, length = listeners.length; i<length; i++) {
            var listener = listeners[i];
            listener.callback();
        }
    }

    return {
        listen: registerListener,
        notify: notifyListeners
    }
})();