$( function() {
    $("#question-accordion").accordion({
        active: false,
        collapsible: true,
        header: '.question-row-one'
    });

    $(".remove-question-button").each(function () {
        $(this).click(function (e) {
            e.stopPropagation();
            return true;
        })
    })
} );