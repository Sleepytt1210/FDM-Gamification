const local = window.localStorage;

$(function () {

    const url = new URL(window.location.href).pathname;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    const qid = urlSplit[2];
    const qidString = 'q' + qid;
    const challengeId = urlSplit[1];
    const chidString = 'ch' + challengeId;
    const btn = $("#submit");
    const cids0Name = qidString + "_score0";
    const cids1Name = qidString + "_score1";
    const cids2Name = qidString + "_score2";
    const scoreHTML = $("#score").attr("value");
    const score = local.getItem(qidString);
    const curChalQuesIds = $(".sidebar-menu-list > li > a").map(function () {
        return $(this).attr("id");
    }).get();

    console.log(curChalQuesIds)

    btn.click(function () {
        // Gets id of choice for each column
        const cids0 = $("#score0 li input").map(function () {
            return $(this).val();
        }).get().join(",");
        const cids1 = $("#score1 li input").map(function () {
            return $(this).val();
        }).get().join(",");
        const cids2 = $("#score2 li input").map(function () {
            return $(this).val();
        }).get().join(",");

        // Adds to general completion
        if(!score){
            const completion = local.getItem(chidString);
            if(completion) {
                local.setItem(chidString, String(Number(completion)+1));
            }else{
                local.setItem(chidString, "1");
            }
            $("<input type='hidden' name='compInc' value='1'/>").appendTo($(".user-question-form"));
        }


        // Set qidString to temp before server side calculation
        local.setItem(qidString, "temp");

        // Save answers in local storage.
        local.setItem(cids0Name, cids0);
        local.setItem(cids1Name, cids1);
        local.setItem(cids2Name, cids2);
    });

    // If score exists in local storage indicates that a submission is made.
    if (score) {
        const cids0 = local.getItem(cids0Name);
        const cids1 = local.getItem(cids1Name);
        const cids2 = local.getItem(cids2Name);
        if (score === "temp") {
            local.setItem(qidString, scoreHTML);
        } else {
            $("#score").html('Score: ' + score);
            $(".result").show();
        }
        move(cids0, "score0");
        move(cids1, "score1");
        move(cids2, "score2");

        const allIDs = (cids0 + cids1 + cids2).split(",");
        // Remove everything in choice list after migration.
        $("#choices > li").each(function () {
            if (allIDs.includes($(this).attr("id")))$(this).remove();
        });
        btnToggle();
    }

    $("ul.droptrue").sortable({
        connectWith: ".droptrue",
        placeholder: "placeholder",
        dropOnEmpty: true,
        start: function (event, ui) {
            // Explicitly set placeholder height following the dragged elements height.
            ui.placeholder.height(ui.item.height());
            $(".droppable .droptrue").addClass("highlight");
        },
        stop: function (event, ui) {
            $(".droppable .droptrue").removeClass("highlight");
            const cur = ui.item;
            // Set request parameter of input according to table column
            cur.find("input").attr("name", cur.parent()[0].id);
            btnToggle();
        }
    });

    function btnToggle() {
        if ($('#choices li').length !== 0) {
            btn.prop("disabled", true);
            btn.css("cursor", "not-allowed");
            btn.prop("title", "Please fill in the tables first");
        } else {
            btn.prop("disabled", false);
            btn.removeAttr("title");
            btn.css("cursor", "pointer");
        }
    }

    function move(cids, name) {
        cids.split(",").forEach((cid) => {
            // Id of li element.
            const liId = '#ch' + cid;
            const li = $(liId);
            // Set the request name of input to corresponding column id.
            li.find('input').attr("name", name);
            // Adds the list to column id.
            $("#" + name).append($(liId));
        });
    }
});
