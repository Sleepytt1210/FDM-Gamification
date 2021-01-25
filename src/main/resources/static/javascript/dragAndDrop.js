import {btnToggle, showScore} from "./repeatedCode.js";

const local = window.localStorage;
$(function () {

    const url = window.location.href;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    const qid = urlSplit[2];
    const qidString = 'q' + qid;
    const btn = $("#submit");
    const cids0Name = qidString + "_score0";
    const cids1Name = qidString + "_score1";
    const cids2Name = qidString + "_score2";
    const scoreHTML = $("#score").attr("value");
    const score = local.getItem(qidString);

    btnToggle(btn);

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
        showScore(score,qidString,scoreHTML,local);
        move(cids0, "score0");
        move(cids1, "score1");
        move(cids2, "score2");

        const allIDs = (cids0 + cids1 + cids2).split(",");
        btnToggle(btn);
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
            btnToggle(btn);
        }
    });



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
