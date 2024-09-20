$(document).ready(function(){
    $('form').on('submit',function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        $.ajax({
            url: "/utils/uploadApiData",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            
        }).fail(function(){
            alert("Error getting from server");
        })
    });
});