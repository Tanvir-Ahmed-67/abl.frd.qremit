$(document).ready(function () {
    get_loading();
    var tbl = "#details_of_daily_coc_ac_payee_online_remittances_tbl";
    $('#showDataButton').on('click', function (e){
        e.preventDefault();
        var fromDate = $("#report_date").val();
        var toDate = $("#report_end_date").val();
        get_detailsOfDailyRemittances(fromDate, toDate);
        download_btn(fromDate, toDate);
    });
    function get_detailsOfDailyRemittances(fromDate, toDate){
        var url = "/detailsOfDailyCocOnlineAndAccountPayee?fromDate=" + fromDate + "&toDate=" + toDate;
        var column = ['totalRowCount','transactionNo','exchangeCode','beneficiaryName','beneficiaryAccount','bankName','bankCode','branchName','branchCode','remitterName','voucherDate','amount','type'];
        var cols = DataTableColumns(column);
        $("#dataSection").show();
        get_dynamic_dataTable(tbl, url, cols);
    }

    function download_btn(from_date, to_date){
        var url = "/downloadCocAndAccountPayeeFileInTextFormatForBranch?type=txt&fromDate="+from_date + "&toDate=" + to_date;
        var url2 = "/downloadOnlineFileInTextFormatForBranch?type=txt&fromDate="+from_date + "&toDate=" + to_date;
        var ret = '<a id="downloadBtn1" href="' + url + '" class="btn btn-info">Download COC And A/C Payee File For Branch</a>';
        ret += '<a id="downloadBtn2" href="' + url2 + '" class="btn btn-danger">Download Online File For Branch</a>';
        $("#download_btn").html(ret);
    }

});