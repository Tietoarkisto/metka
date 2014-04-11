$(document).ready(function() {
    // Init autobuild tables
    $(".autobuild").each(function(index) {
        var id = $(this).attr("id");
        MetkaJS.TableHandler.build(id, $(this).data("context"));
    });
});