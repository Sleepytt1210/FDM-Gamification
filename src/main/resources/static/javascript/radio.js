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
        if (score === "temp") {
            local.setItem(qidString, scoreHTML);
        } else {
            $("#score").html('Score: ' + score);
            $(".result").show();



        }
        $('#' + cid).prop('checked', true);
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
}



)