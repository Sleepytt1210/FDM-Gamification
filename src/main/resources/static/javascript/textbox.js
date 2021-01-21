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
        if (score === "temp") {
            local.setItem(qidString, scoreHTML);
        } else {
            $("#score").html('Score: ' + score);
            $(".result").show();
        }
        $('answer').val(answer);
        btnToggle();
    }


    function btnToggle() {
        if ($('btn:checked').length) {
            btn.prop("disabled", true);
            btn.css("cursor", "not-allowed");
            btn.prop("title", "Please choose an answer first");
        } else {
            btn.prop("disabled", false);
            btn.removeAttr("title");
            btn.css("cursor", "pointer");
        }
    }

})
