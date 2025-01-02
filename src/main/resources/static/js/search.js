$(document).ready(function(){
    get_loading();
    var sdiv = "#sdiv";
    var display = "#display";
    var sbtn = "#searchBtn";
    var tbl = "#search_tbl";
    var type = getParameterByName("type");
    if(type == 2){
        $('#searchType').val("1");
    }
    
    $('#searchForm').on('submit', function(e){
        e.preventDefault();
        var data = $(this).serialize();
        var url = "/getSearch";
        if(type == "2") url = "";
        var params = {"type": type};
        get_ajax(url,data, view_search,fail_func,"get","json",params);
    });

    function view_search(resp, params){
        if(resp.err == 1){
            alert(resp.msg);
            return false;
        }
        var cols = ['sl','transactionNo','exchangeCode','beneficiaryName','beneficiaryAccount','bankDetails','amount','downloadDateTime','reportDate','type'];
        var columns = DataTableColumns(cols);
        console.log(columns);
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
});