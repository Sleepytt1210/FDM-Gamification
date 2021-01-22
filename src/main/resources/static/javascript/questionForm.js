$(function () {

    const url = window.location.href;
    const choiceOp = url.searchParams.has('addChoice') || url.searchParams.has('removeChoice');

    if(choiceOp) {
        $('html, body').animate({
            scrollTop: $(".choice-section").offset().top
        }, 500, 'swing');
    }

});