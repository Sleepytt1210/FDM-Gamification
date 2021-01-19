$(function () {

    let form = $("#panel-form");
    const textMax = 70;

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

    $(document).on("change",".select-all",function() {
        const main = this;
        const tabName = $(main).attr("id").split("-")[0] + '-item-selection';
        $("."+tabName).map(function (){
            $(this).prop("checked", main.checked);
        })
        $(".delete-button").prop("disabled", !main.checked);
    });

    $(document).on("change", ".item-selection", function() {
        const classDiv = '.' + $(this).prop("class").split(/\s+/)[0];
        const arr = $(classDiv).map((index, obj) => {
            return obj.checked;
        }).get();
        console.log(arr);
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
})