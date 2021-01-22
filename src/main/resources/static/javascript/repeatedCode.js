

export function btnToggle() {
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

export function showScore(score, qidString, scoreHTML, local){
    if (score === "temp") {
        local.setItem(qidString, scoreHTML);
    } else {
        $("#score").html('Score: ' + score);
        $(".result").show();
    }
}