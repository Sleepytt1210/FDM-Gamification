import {btnToggle, showScore} from "./repeatedCode";

const local = window.localStorage;

$(function () {


    const url = window.location.href;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    const qid = urlSplit[2];
    const qidString = 'q' + qid;
    const btn = $("#submit");
    const answerName = qidString + "score";
    const scoreHTML = $("#score").attr("value");
    const score = local.getItem(qidString);

    btnToggle();

    btn.click(function () {


        const answer = ($('input[type=text]')).val();
        console.log(answer);


        // Set qidString to temp before server side calculation
        local.setItem(qidString, "temp");

        // Set answer in local storage
        local.setItem(answerName, answer);


    });

    if (score){
        const answer = local.getItem(answerName);
        showScore(score,qidString,scoreHTML,local);
        $('#answer').val(answer);
        btnToggle();
    }


})
