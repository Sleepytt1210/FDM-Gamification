$(function () {
    $("#reset").click(function () {
        console.log('clearing local storage');
        localStorage.clear();
    })
})