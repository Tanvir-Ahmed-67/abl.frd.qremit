$(document).ready(function(){
    get_loading();
    $('#beftnReturnUpload').on('submit', function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        $.ajax({
            url: "/processBeftnReturnUpload",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            alert(resp.msg);
            if(resp.err == 0)   $('form')[0].reset();
        });
    });
});