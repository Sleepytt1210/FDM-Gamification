$( function() {

    const url = new URL(window.location.href);
    const quesOp = url.searchParams.has('addQuestion') || url.searchParams.has('removeQuestion');
    const qIdx = url.searchParams.get('addChoice') || url.searchParams.get('removeChoice');

    $("#question-accordion").accordion({
        active: qIdx === null ? false : Number(qIdx),
        collapsible: true,
        header: '.question-row-one'
    });

    if(quesOp || qIdx){
        $('html, body').animate({
            scrollTop: $(".question-section").offset().top
        }, 500, 'swing');
    }

    $(".remove-question-button").each(function () {
        $(this).click(function (e) {
            e.stopPropagation();
            return true;
        })
    })
} );