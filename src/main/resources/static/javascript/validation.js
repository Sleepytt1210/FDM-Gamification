$(function () {

    $.validator.addMethod("pattern", function (value, element, regex) {
        return regex.test(value)
    }, "Angle brackets '<', '>' are not allowed!");

    $.validator.addMethod("valueNotEquals", function(value, element, arg){
        return arg !== value;
    }, "Please select an option!");

    $("#challenge-form").validate({
        ignore: [],
        rules: {
            stream: {
                valueNotEquals: "NONE",
                messages: {
                    valueNotEquals: "Please select a stream!",
                }
            },
            question_type: {
                valueNotEquals: "NONE",
                messages: {
                    valueNotEquals: "Please select a question type!",
                }
            }
        }
    })

    const defaultTextRule = {
        required: true,
        pattern: /^[^<>]+$/m,
        messages: {
            required:  "This field is required!",
            maxlength: $.validator.format("Only {0} characters are allowed!")
        }
    };

    const titleRule = {
        maxlength: 50
    }

    const longTextRule = {
        maxlength: 60
    }

    $.extend(titleRule, defaultTextRule);
    $.extend(longTextRule, defaultTextRule);

    $(".title-input").each(function (){
        $(this).rules('add', titleRule);
    })

    $(".description-input").each(function () {
        $(this).rules('add', longTextRule);
    })
});