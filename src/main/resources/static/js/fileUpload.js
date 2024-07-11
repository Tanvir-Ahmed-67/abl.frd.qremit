$(document).ready(function(){
    var title = $(".order-last h3").text();

    var params = getParameterByName("id");
    $("#exName").val(params);
    
    /*
    $('.exCode').on('click',function(e){ 
        e.preventDefault();
        $("#up-form").show();
        $("#up-data").hide();
        $(".order-last h3").html("Upload the CSV File");
        console.log(params);
    });
    */
    //console.log(params);

    //console.log(title);
    /*
    $('.exCode').on('click',function(e){ 
        e.preventDefault();
        $("#up-form").show();
        $("#up-data").hide();
        $(".order-last h3").html("Upload the CSV File");
        var url = $(this).attr('href'); 
        //var params = url.replace(/#/g, "");
        var params = getParameterByName("id");
        console.log(params);
        $("#exName").val(params);
        console.log(url); 
    });
    */

    $('form').on('submit',function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        //console.log(data);
        $.ajax({
            url: "/utils/upload",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            //console.log(resp);
            //title = $(".order-last h3").text();
            
            $("#up-form").hide();
            $("#up-data").show();
            $("#up-data").html(resp);
            $(".card-header").html("");
            $(".order-last h3").html("Uploaded File Statistics");
        }).fail(function(){
            alert("Error Getting from server");
        });
   
    });
});