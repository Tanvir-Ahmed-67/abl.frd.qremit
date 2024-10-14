$(document).ready(function(){
    var type = getParameterByName("type");
    var tbl = "#user_upload_tbl";
    var page_header = "";
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var pid = getParameterByName("id");
    
    switch(type){
        case '1':
        default:
            var url = "/report";
            break;
        case '2':
            var exchangeCode = getParameterByName("exchangeCode");
            var url = "/fileReport?exchangeCode=" + exchangeCode + "&id=" + pid;
            break;
        case '3':
            var url = "/errorReport?id=" + pid;
            page_header = "Error Data Report";
            break;
        case '4':
            var url = "/getErrorUpdateReport";
            page_header = "Error Data Report Update for Admin";
            break;
    }
    if(page_header)  $(".page-header").html(page_header);
    var params = {'tbl': tbl,'url': url};
    var url = "/getReportColumn?type=" + type;
    get_ajax(url,"",get_user_upload_report,fail_func,"get","json",params);

    function get_user_upload_report(resp,params){
        get_dynamic_dataTable(params.tbl, params.url, resp.column);        
    }

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
        var url = "/error/update";
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        get_ajax(url,data,success_modal,fail_func,"post","json",params);
    });
    $(document).off('click',".approve_error");
    $(document).on('click',".approve_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url  = "/error/approve";
        var data = {'id': id};
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        get_ajax(url,data,success_modal,fail_func,"get","json",params);
    });

    $(document).off('click',".delete_error");
    $(document).on('click',".delete_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url  = "/error/delete/" + id;
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header};
        if(confirm("Are you sure you want to delete this data?")){
            get_ajax(url,data,success_modal,fail_func,"DELETE","json",params);
        }
    });
});