const local = window.localStorage;

$(function () {

    const url = new URL(window.location.href).pathname;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);

    // Challenge key
    const challengeId = urlSplit[1];
    const chidString = 'ch' + challengeId;
    const challengeScore = local.getItem(chidString);

    // Question key
    const qid = urlSplit[2];
    const qidString = 'q' + qid;

    const scoreHTML = $("#score").attr("value");
    const score = local.getItem(qidString);

    // Choice key
    const cids0Name = qidString + "_score0";
    const cids1Name = qidString + "_score1";
    const cids2Name = qidString + "_score2";

    const btn = $("#submit");

    const curChalQuesIds = $(".sidebar-menu-list > li > a").map(function () {
        return $(this).attr("id");
    }).get();

    // Drag and drop config
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

    // If score exists in local storage indicates that a submission has been made at least once.
    if (score) {
        const cids0 = local.getItem(cids0Name);
        const cids1 = local.getItem(cids1Name);
        const cids2 = local.getItem(cids2Name);

        // Means the page was redirected from a submission.
        if (score === "temp") {

            // Get the subtracted total from button click and adds new question score to it.
            const subtractedTotal = Number(local.getItem(chidString));
            local.setItem(chidString, ""+(subtractedTotal + Number(scoreHTML)));

            // Set new question score.
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

    // Once submission
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

        minusChallengeTotal(challengeScore, chidString, score);

        // Adds to general completion if first submission
        if(!score) {
            $("<input type='hidden' name='compInc' value='1'/>").appendTo($(".user-question-form"));
        }

        // Set qidString to temp before server side calculation
        local.setItem(qidString, "temp");

        // Save answers in local storage.
        local.setItem(cids0Name, cids0);
        local.setItem(cids1Name, cids1);
        local.setItem(cids2Name, cids2);
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
