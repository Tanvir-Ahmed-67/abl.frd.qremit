$(document).ready(function(){
    get_loading();
    var type = getParameterByName("type");
    var tbl = "#user_upload_tbl";
    var page_header = "";
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var pid = getParameterByName("id");
    var date = getParameterByName("date");
    $('#row_report_date').hide();
    var load_data = 1;
    const hTypes = ["11","12","13"];
    if(hTypes.includes(type))   load_data = 0;  
    if(type == 11){
        $(".page-header").html('');
        $('#routing_div').show();
        $('#bank_code').select2({ width: '100%' });
        var url = "/getBankList";
        var params = {'tdiv': '#bank_code', 'key':'bank_code','value': 'bank_name'};
        get_ajax(url,"",show_bank_list,fail_func,"get","json",params);
    }
    if(type == 12 || type == 13){
        $(".page-header").html('');
        $('#exchange_div').show();
        $('#exchange_code').select2({ width: '100%' });
        var url = "/getExchangeListByUserId";
        var params = {'tdiv': '#exchange_code', 'key':'exchangeCode','value': 'exchangeName'};
        get_ajax(url,"",show_exchange_list,fail_func,"get","json",params);
    }

    function show_bank_list(resp,params){
        get_dropdown(resp.data, params);
    }

    function show_exchange_list(resp,params){
        get_dropdown(resp.data, params);
    }

    setTimeout(function(){
        $('#message').hide();
    }, 3000);
    
    function get_report_url(type,date, params){
        switch(type){
            case '1':
            default:
                var url = "/report?date=" + date;
                $('#row_report_date').show();
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
            case '5':
                var url = "/report?date=" + date + "&type=" + type;
                $('#row_report_date').show();
                break;
            case '6':
                var url = "/getAllUsers";
                page_header = "All User Details";
                break;
            case '7':
                var url = "/summaryOfDailyStatement?date=" + date;
                $('#row_report_date').show();
                page_header = "Summary Of Daily Remittances";
                break;
            case '8':
                var url = "/getAllExchangeHouse";   
                page_header = "All Exchange House Details";
                break;
            case '9':
                var url = "/getAllExchangeHouse?activeStatus=2";
                page_header = "All Inactive Exchange House";
                break;
            case '10':
                var url = "/getAllUsers?activeStatus=2";
                page_header = "All Inactive Users";
                break;
            case '11':
                var url = "/getRouting" + params;
                page_header = "Routing Number Search";
                break;
            case '12':
            case '13':
                var url = "/getMonthlyData" + params;
                if(type == '12')    page_header = "Exchange House Wise Report";
                else page_header = "Exchange House Wise Monthly Report";
                break;
        }
        return {'url': url, 'page_header': page_header};
    }

    var url = "/getReportColumn?type=" + type;
    if(load_data == 1)  user_upload_report_ui(url,type,date);
    $(document).off('change','#report_date');
    $(document).on('change','#report_date', function(e){
        e.preventDefault();
        var val = $(this).val();
        user_upload_report_ui(url,type,val);
    });
    
    $(document).off('change', '.routing_search');
    $(document).on('change', '.routing_search', function(e){
        e.preventDefault();
        var val = $(this).val();
        var bank_code = $('#bank_code').val();
        var routing_no = $('#routing_no').val();
        var params = "?bankCode=" + bank_code + "&routingNo=" + routing_no;
        user_upload_report_ui(url,type,"",params);
    });

    $(document).off('click', '#exchange_search');
    $(document).on('click', '#exchange_search', function(e){
        e.preventDefault();
        var exchange_code = $('#exchange_code').val();
        var start_date = $('#start_date').val();
        var end_date = $('#end_date').val();
        if(!exchange_code || !start_date || !end_date){
            alert("Please Select Exchange Code or Start Date or End date");
            return false;
        }
        var params = '?exchangeCode=' + exchange_code + '&startDate=' + start_date + '&endDate=' + end_date;
        user_upload_report_ui(url,type,"",params);
    });

    function user_upload_report_ui(url,type,date, params){
        var rdata = get_report_url(type,date, params);
        if(rdata.page_header)  $(".page-header").html(rdata.page_header);
        var report_url = rdata.url;
        var params = {'tbl': tbl,'url': report_url};
        get_ajax(url,"",get_user_upload_report,fail_func,"get","json",params);
    }
    

    function get_user_upload_report(resp,params){
        var sfun = [upload_report_ui];
        get_dynamic_dataTable(params.tbl, params.url, resp.column, sfun, '', 'get',[], true);        
    }

    function upload_report_ui(resp){
        edit_error_data(tbl);
        delete_error(tbl,csrf_token,csrf_header);
        var btn = "";
        if(type == '7' ){
            btn = '<div class="btn-group">';
            if(resp.dailyStatementUrl)  btn +='<a href="'+ resp.dailyStatementUrl + '" class="btn btn-info text-white">' + resp.dailyStatementTitle + '</a>';
            if(resp.dailyVoucherUrl)  btn +='<a href="'+ resp.dailyVoucherUrl + '" class="btn btn-danger text-white">' + resp.dailyVoucherTitle + '</a>';
            btn += '</div>';
            $('#download_btn').html(btn);
        }
        if(type == 13){
            var exchange_code = $('#exchange_code').val();
            var start_date = $('#start_date').val();
            var end_date = $('#end_date').val();
            var url = "/downloadMonthlyData?exchangeCode=" + exchange_code + "&startDate=" + start_date + "&endDate=" + end_date + "&generateCsv=1";
            btn = '<a href="'+ url + '" class="btn btn-danger text-white">Download Monthly Data</a>'
            $('#download_btn').html(btn);
        }
    }

    $(document).off('click',".view_exchange");
    $(document).on('click',".view_exchange",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var exCode = $("#exCode_"+ id).val();
        var val = $(exCode).val();
        var base_url = $("#base_url").val();
        var url = base_url + "?type=2&exchangeCode=" + exCode + "&id=" + id;
        window.location.href = url;
    });

    $(document).off('click',".view_error");
    $(document).on('click',".view_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var params = { tdiv: '.modal-body'};
        var mparams = { 'modalID': 'myModal', 'modal_wrap':'#modal_wrap','modal_class':'modal-md', 'modal_title': 'View Error Data' };
        var url = "/error/viewError/" + id;
        gen_modal(url,params,mparams);
    });

    $(document).on("submit","#approve_error_form",function(e){
        e.preventDefault();
        var id = $("#id").val();
        var data = {'id':id, '_csrf': csrf_token, '_csrf_header': csrf_header};
        var url  = "/error/approve";
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        get_ajax(url,data,success_modal,fail_func,"post","json",params);
    });

    $(document).off('click',".delete_file");
    $(document).on('click',".delete_file",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url  = "/file/delete/" + id;
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header};
        if(confirm("Are you sure you want to delete this data?")){
            get_ajax(url,data,success_modal,fail_func,"DELETE","json",params);
        }
    });

    $(document).off("click",".reset_pass")
    $(document).on("click",".reset_pass", function(e){
        e.preventDefault();
        var url = "/resetPassword";
        var id = $(this).attr("id");
        var data = {'id':id, '_csrf': csrf_token, '_csrf_header': csrf_header};
        var params = {'dataTable_reload': 'true', 'tbl': tbl, 'pagination': false };
        if(confirm("Are you sure you want to reset password ?")){
            get_ajax(url,data,success_alert,fail_func,"post","json",params);
        }
    });
    $(document).off("click",".activate_exchange")
    $(document).on("click",".activate_exchange", function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url = "/activateExchangeHouse/" + id;
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header};
        var params = {'dataTable_reload': 'true', 'tbl': tbl, 'pagination': false};
        if(confirm("Are you sure you want to activate exchange ?")){
            get_ajax(url,data,success_alert,fail_func,"post","json",params);
        }
    });
    $(document).off("click",".activate_user")
    $(document).on("click",".activate_user", function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url = "/activateUser/" + id;
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header};
        var params = {'dataTable_reload': 'true', 'tbl': tbl, 'pagination': false};
        if(confirm("Are you sure you want to activate exchange ?")){
            get_ajax(url,data,success_alert,fail_func,"post","json",params);
        }
    });
});