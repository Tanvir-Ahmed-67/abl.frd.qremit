function upload(file, coc) {
        var formData = new FormData();
        formData.append("file", file);
        formData.append("coc", coc);
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/qremit/downloadcoc");
        xhr.send(formData);
        xhr.onload = function() {
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            if(xhr.status == 200) {
                alert("success");
            } else {
                alert("fail");
            }
        }

}