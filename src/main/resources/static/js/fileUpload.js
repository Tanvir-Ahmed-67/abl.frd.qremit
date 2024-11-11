$(document).ready(function(){
    get_loading();
    var exchange_code = getParameterByName("id");
    //if(exchange_code == '7010272') alert("Please send 8 cup of Doi");
    $("#exchangeCode").val(exchange_code);
    $('form').on('submit',function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        $("body").addClass("loading");
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
        }).always(function() {
            // Hide loading
            $("body").removeClass("loading");
        });
   
    });

    function view_data(resp){
        var tbl = "#error_data_tbl";
        var csrf_token = $('#_csrf').val();
        var csrf_header = $('#_csrf_header').val();
        $('.view_btn').on('click', function(e){
            e.preventDefault();
            var id = $(this).attr('id');
            var exchange_code = $('#exchange_code').val();
            var url = '/user-home-page?type=2&exchangeCode=' + exchange_code + '&id=' + id;
            window.open(url, '_blank');
            /*
            var id = $(this).attr('id');
            var params = { tdiv: '.modal-body'};
            var mparams = { 'modalID': 'myModal', 'modal_wrap':'#modal_wrap','modal_class':'modal-lg', 'modal_title': 'View Data', 'sfunc': view_exchange_data };
            var url = "/viewData?type=2&exchangeCode=" + exchange_code + "&id=" + id;
            var data = {'id': id};
            gen_modal(url,params,mparams,data);
            */
        });

        function view_exchange_data(resp){
            var id = $("#fid").val();
            var exchange_code = $('#exchange_code').val();
            var viewColumn = $("#viewColumn").val();
            var cols = JSON.parse(viewColumn);
            var tbl1 = "#view_data_tbl";
            var url = "/fileReport?exchangeCode=" + exchange_code + "&id=" + id;
            get_dynamic_dataTable(tbl1, url, cols);
        }
        
        $(".apiUrl").on('click', function(e){
            e.preventDefault();
            var id = $(this).attr('id');
            var url = $("#apiUrl").val();
            var data = {'_csrf': csrf_token, '_csrf_header': csrf_header, id: id};
            var params = {'success_alert': "", "success_redirect": "true"};
            get_ajax(url,data, success_alert, fail_func,'post','json',params, 1000000);
        });

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