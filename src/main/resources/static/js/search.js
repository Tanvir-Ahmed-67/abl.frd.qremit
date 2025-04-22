$(document).ready(function(){
    get_loading();
    var sdiv = "#sdiv";
    var display = "#display";
    var sbtn = "#searchBtn";
    var tbl = "#search_tbl";
    var type = getParameterByName("type");
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    if(type == 2){
        $('#searchType').val("1");
        $("#page-header").html("Data Correction");
    }
    
    $('#searchForm').on('submit', function(e){
        e.preventDefault();
        var data = $(this).serialize() + "&type=" + type;
        var url = "/getSearch";
        var params = {"type": type};
        get_ajax(url,data, view_search,fail_func,"get","json",params);
    });

    function view_search(resp, params){
        if(resp.err == 1){
            alert(resp.msg);
            return false;
        }
        var cols = ['sl','transactionNo','exchangeCode','beneficiaryName','beneficiaryAccount','bankDetails','amount','downloadDateTime','reportDate','type'];
        if(type == '2') cols.push('action');
        var columns = DataTableColumns(cols);
        $(sdiv).hide();
        $(display).show();
        $(sbtn).show();
        get_simple_dataTable(tbl,columns, resp);
    }

    $(document).off('click','#search');
    $(document).on('click','#search', function(e){
        $(sdiv).show();
        $(display).hide();
        $(sbtn).hide();
    });
    
    $(document).off('click','.edit');
    $(document).on('click','.edit', function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var dtype = $("#type_" + id).val();
        var params = { tdiv: '.modal-body','id': id, 'type': dtype};
        var mparams = { 'modalID': 'myModal', 'modal_wrap':'#modal_wrap','modal_class':'modal-md', 'modal_title': 'Edit Data' };
        var url = "/editForm/" + id + "?type=" + dtype;
        gen_search_modal(url, params, mparams);
    });

    function gen_search_modal(url, params, mparams){
        gen_modal(url,params,mparams);
        var url = "/getEditData/" + params.id + "?type=" + params.type;
        get_ajax(url,"",view_edit_data, fail_func,"get","json",params);
    }

    function view_edit_data(resp, params){
        if(resp.err == 1){
            alert(resp.msg);
            return false;
        }
        var data = resp.data;
        $('#type').val(params.type);
        for(var i in data){
            $('#' + i).val(data[i]);
        }
    }

    $(document).on('submit',"#editForm", function(e){
        e.preventDefault();
        var data = $(this).serialize();
        var url = "/update";
        $(sdiv).show();
        $(display).hide();
        $(sbtn).hide();
        var params = { 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        get_ajax(url,data,success_modal,fail_func,"post","json",params);
    });

    $(document).off('click','.delete');
    $(document).on('click','.delete', function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var dtype = $("#type_" + id).val();
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header, 'type': dtype};
        console.log(data);
        var url = "/deleteIndividual/" + id;
        var params = {  'success_reload': 'true'};
        if(confirm("Are you sure you want to delete data ?")){
            get_ajax(url,data,success_alert,fail_func,"DELETE","json",params);
        }
    });
});