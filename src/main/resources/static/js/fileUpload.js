$(document).ready(function(){
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var exchange_code = getParameterByName("id");
    if(exchange_code == '7010272') alert("Please send 8 cup of Doi");
    $("#exchangeCode").val(exchange_code);
    $('form').on('submit',function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        $.ajax({
            url: "/utils/upload",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            $("#up-form").hide();
            $("#up-data").show();
            $("#up-data").html(resp);
            $(".card-header").html("");
            $(".order-last h3").html("Uploaded File Statistics");
            view_data(resp);
        }).fail(function(){
            alert("Error Getting from server");
        });
   
    });

    function view_data(resp){
        var tbl = "#error_data_tbl";
        if($(tbl).length){
            var uid = $('#id').val();
            var reportColumns = $("#reportColumns").val();
            var cols = JSON.parse(reportColumns);
            var url = "/errorReport?id=" + uid;
            var sfun = [error_report_ui];
            get_dynamic_dataTable(tbl, url, cols, sfun);

            function error_report_ui(resp){
                edit_error_data(tbl);
                delete_error(tbl,csrf_token,csrf_header);
            }
        }
    }
});