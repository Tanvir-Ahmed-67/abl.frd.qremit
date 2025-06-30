$(document).ready(function(){
    get_loading();
    $('form').on('submit',function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        console.log(data);
        $.ajax({
            url: "/bbReport/uploadReport",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            alert(resp.msg);
        });
    });
});