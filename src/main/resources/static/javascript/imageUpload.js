$(function () {
    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            $("#preview").show();

            reader.onload = function (e) {
                document.getElementById('preview').src = e.target.result;
            }

            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }

    $("#thumbnail").change(function () {
        readURL(this);
    });

    $("#image-upload-wrapper").click(function () {
        $("#thumbnail").click();
        return true;
    })
});