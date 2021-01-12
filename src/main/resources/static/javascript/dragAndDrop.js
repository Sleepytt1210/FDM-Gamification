var local = window.localStorage;

$(function() {

    var url = window.location.href;
    var urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    var qid = urlSplit[2];
    var btn = $("#submit");
    btnEnable();
    
    btn.click(function() {
    });

    function btnEnable() {
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

    $("ul.droptrue").sortable({
        connectWith: ".droptrue",
        placeholder: "placeholder",
        dropOnEmpty: true,
        start: function(event, ui) {
            // Explicitly set placeholder height following the dragged elements height.
            ui.placeholder.height(ui.item.height());
            $(".droppable .droptrue").addClass("highlight");
        },
        stop: function(event, ui) {
            $(".droppable .droptrue").removeClass("highlight");
            var cur = ui.item;
            // Set request parameter of input according to table column
            cur.find("input").attr("name", cur.parent()[0].id);
            btnEnable();
        }
    });
});
