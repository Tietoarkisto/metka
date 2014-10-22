// entry point for JS application

(function () {
    var version;

    // When new release: remove comment and increase version number
    //version = 1;

    require.config({
        urlArgs: 'v=' + (version || Date.now())
    });
})();

require(['./modules/page']);
