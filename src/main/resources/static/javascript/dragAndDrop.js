$(function() {
    var btn = $("#submit");
    btn.prop("disabled", true);
    btn.css("cursor", "not-allowed");
    btn.prop("title", "Please fill in the tables first");

    $("ul.droptrue").sortable({
        connectWith: ".droptrue",
        dropOnEmpty: true,
        stop: function(event, ui) {
            var cur = ui.item;
            // Set request parameter of input according to table column
            cur.find("input").attr("name", cur.parent()[0].id);
            if ($('#choices li').length !== 0) {
                btn.prop("disabled", true);
                btn.css("cursor", "not-allowed");
                btn.prop("title", "Please fill in the tables first");
            }else {
                btn.prop("disabled", false);
                btn.removeAttr("title");
                btn.css("cursor", "pointer");
            }
        }
    });
});
