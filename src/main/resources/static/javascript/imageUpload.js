$(function () {
    function readURL(input) {
        if (input.files && input.files[0]){
            if(input.files[0].type === "image/jpeg" || input.files[0].type === "image/png") {

                const fileLimit = 256 * 1024; //256kiB
                const fileLimitString = "256kiB";
                if (input.files[0].size <= fileLimit) {

                    var reader = new FileReader();
                    $("#preview").show();
                    reader.onload = function (e) {
                        document.getElementById('preview').src = e.target.result;
                    }

                    reader.readAsDataURL(input.files[0]); // convert to base64 string
                    return true;
                } else {
                    alert(`Image is too large! File size must not exceed ${fileLimitString}`);
                    $(input).val('');
                    return false;
                }
            }else {
                alert('File must be png or jpeg!');
                $(input).val('');
                return false;
            }
        }
    }

    $("#thumbnail").change(function () {
        return readURL(this);
    });

    $("#image-upload-wrapper").click(function () {
        $("#thumbnail").click();
    })
});