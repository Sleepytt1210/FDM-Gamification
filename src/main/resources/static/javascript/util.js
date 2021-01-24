const local = window.localStorage;


function minusChallengeTotal(challengeScore, chidString, score) {
    // If no challenge score indicates that this is the first submission of this challenge.
    if(!challengeScore) {
        local.setItem(chidString, "0");
    } else if(challengeScore && score) {
        // Else if a submission has been made for this question, minus the old one and add a new one later.
        let scoreNum = Number(score);
        local.setItem(chidString, ""+(Number(challengeScore) - scoreNum));
    }
}