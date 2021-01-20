$(function () {
    function readURL(input) {
        if (input.files && input.files[0] && (input.files[0].type === "image/jpeg" || input.files[0].type === "image/png")  ) {

            const fileLimit = 256*1024; //256kiB
            const fileLimitString = "256kiB";
            if(input.files[0].size <= fileLimit) {

                var reader = new FileReader();
                $("#preview").show();
                reader.onload = function (e) {
                    document.getElementById('preview').src = e.target.result;
                }

                reader.readAsDataURL(input.files[0]); // convert to base64 string
            }else{
                alert(`Image is too large! File size must not exceed ${fileLimitString}`)
            }
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