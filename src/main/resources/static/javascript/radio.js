import {btnToggle, showScore} from "./repeatedCode.js";

const local = window.localStorage;

$(function () {


    const url = window.location.href;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    const qid = urlSplit[2];
    const qidString = 'q' + qid;
    const btn = $("#submit");
    const cidName = qidString + "score";
    const scoreHTML = $("#score").attr("value");
    const score = local.getItem(qidString);

    btnToggle(btn);

    btn.click(function () {


        const cid = ($('input[name=choices]:checked')).val();

        // Set qidString to temp before server side calculation
        local.setItem(qidString, "temp");

        // Set answer in local storage
        local.setItem(cidName, cid);


    });

    // If score exists in local storage indicates that a submission is made.
    if (score){
        const cid = local.getItem(cidName);
        showScore(score,qidString,scoreHTML,local);
        $('#' + cid).prop('checked', true);
        btnToggle(btn);
    }


}

)