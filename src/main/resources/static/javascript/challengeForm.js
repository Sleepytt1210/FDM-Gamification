$( function() {

    const url = new URL(window.location.href);
    const addQues = url.searchParams.has('addQuestion');
    const removeQues = url.searchParams.has('removeQuestion');
    const qIdx = url.searchParams.get('addChoice') || url.searchParams.get('removeChoice');

    $("#question-accordion").accordion({
        active: qIdx === null ? false : Number(qIdx),
        collapsible: true,
        header: '.question-row-one'
    });

    if(addQues || qIdx){
        $('html, body').animate({
            scrollTop: $(".question-section").offset().top
        }, 200, 'swing');
    }

    $(".remove-question-button").each(function () {
        $(this).click(function (e) {
            e.stopPropagation();
            return true;
        })
    })
} );