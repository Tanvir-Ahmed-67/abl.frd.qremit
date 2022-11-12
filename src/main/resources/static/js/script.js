function uploadSingleFile(file, coc) {
    alert(file)
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/upload");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>Download Url : <a href='" + response.fileDownloadUri + "' >" + response.fileDownloadUri + "</a></p>";
            singleFileUploadSuccess.style.display = "block";
        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = "<p>"+(response && response.message)+"<p>" || "Some Error Occurred";
            singleFileUploadSuccess.style.display = "block";

        }
    }

    xhr.send(formData);
}