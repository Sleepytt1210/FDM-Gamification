$(function () {

    const url = new URL(window.location.href);
    const path = url.pathname;

    let form = $("#panel-form");
    const textMax = 70;

    /*
     * Initialised the dialog
     */
    const dialog = $( "#dialog-confirm" ).dialog({
        autoOpen: false,
        height: "auto",
        width: 350,
        modal: true,
        draggable: false,
        buttons: {
            "Delete": function() {
                form.append(dialog.data("button")).submit();
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        }
    });

    /*
     * Table select all
     */
    $(document).on("change",".select-all",function() {
        const main = this;
        const tabName = $(main).attr("id").split("-")[0] + '-item-selection';
        $(`.${tabName}:checkbox`).each(function (){
            $(this).prop("checked", main.checked);
        })
        $(".delete-button").prop("disabled", !main.checked);
    });

    $(document).on("change", ".item-selection", function() {
        const classDiv = '.' + $(this).prop("class").split(/\s+/)[0];
        const arr = $(classDiv).map(function() {
            return this.checked;
        }).get();
        const any = arr.some((element) => element);
        const all = arr.every((element) => element);
        $(".select-all").prop("checked", all);
        const del = $(".delete-button");
        del.prop("disabled", !any);
    })

    $(".text-column").each(function(){
        trimmer(this);
    });

    function trimmer(element) {
        const text = $(element).text();
        if(text.length > textMax) {
            $(element).text(text.substring(0, textMax) + "...");
        }
    }

    /*
     * Table configuration
     */
    $('.table-content table').DataTable({
        dom: '<"toolbar">frtip', // Enable custom toolbar, input filter, processing display, table, info summary, pagination
        columnDefs: [
            { orderable: false, targets: 0 }
        ],
        aaSorting: [[1, 'asc']]
    });

    $('div.toolbar').html('<button class="create-new-button" name="create" id="create-new" type="button">NEW</button>' +
        '                <input class="delete-button" name="delete" type="submit" value="DELETE" disabled="disabled">')

    /*
     * Move the listener below the button's creation method
     */
    $(".delete-button").click(function (ev) {
        ev.preventDefault();
        const $this = $(this);
        const hiddenVal = $('<input>', {type: "hidden", name: $this.attr("name"), val: $this.val()});
        const checkedNo = $('.item-selection:checked').length;
        dialog.data("button", hiddenVal);
        dialog.dialog("option", "title", `Deleting ${checkedNo} selected items.`);
        $(".ui-button:contains('Delete')").addClass("delete-button");
        dialog.dialog("open");
    });

    $("#create-new").click(function () {
        location.href = path+"/new";
    })
})