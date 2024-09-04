$(document).ready(function(){
    var type = getParameterByName("type");
    var tbl = "#user_upload_tbl";
    var page_header = "";
    switch(type){
        case '1':
        default:
            var url = "/report";
            break;
        case '2':
            var exchangeCode = getParameterByName("exchangeCode");
            var pid = getParameterByName("id");
            var url = "/fileReport?exchangeCode=" + exchangeCode + "&id=" + pid;
            break;
        case '3':
            var url = "/errorReport";
            page_header = "Error Data Report";
            break;
        case '4':
            var url = "/getErrorUpdateReport";
            page_header = "Error Data Report Update for Admin";
            break;
    }
    if(page_header)  $(".page-header").html(page_header);
    var params = {'tbl': tbl,'url': url};

    function get_user_upload_report(params){
        $.ajax({
            "url" : params.url,
        }).done(function(resp){
            get_simple_dataTable(params.tbl,resp.columns,resp);
        }).fail(function(){
            alert("Eroor getting from server");
        });
    }
    get_user_upload_report(params);

    $(document).off('click',".view_exchange");
    $(document).on('click',".view_exchange",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var exCode = $("#exCode_"+ id).val();
        var val = $(exCode).val();
        var url = "/user-home-page?type=2&exchangeCode=" + exCode + "&id=" + id;
        window.location.href = url;
    });

    $(document).off('click',".edit_error");
    $(document).on('click',".edit_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var params = { tdiv: '.modal-body'};
        var mparams = { 'modalID': 'myModal', 'modal_wrap':'#modal_wrap','modal_class':'modal-md', 'modal_title': 'Edit Error Data' };
        var url = "/error/editForm/" + id;
        gen_modal(url,params,mparams);
    });
    $(document).on('submit', "#editErrorForm", function(e){
        e.preventDefault();
        var token = $('#_csrf').val();
        var header = $('#_csrf_header').val();
        var data = $(this).serialize();
        console.log(data);
        var url = "/error/update";
        var params = {'dataTable_reload': 'true', 'tbl': tbl, 'modal_hide': 'true', 'modalID': '#myModal' };
        get_ajax(url,data,success_alert,fail_func,"post","json",params);
    });
});