$(document).ready(function () {
    get_loading();
    var tbl = "#details_of_daily_remittances_tbl";
    $('#showDataButton').on('click', function (e){
        e.preventDefault();
        var fromDate = $("#report_date").val();
        var toDate = $("#report_end_date").val();
        get_detailsOfDailyRemittances(fromDate, toDate);
        download_btn(fromDate, toDate);
    });
    function get_detailsOfDailyRemittances(fromDate, toDate){
        var url = "/detailsOfDailyStatement?fromDate=" + fromDate + "&toDate=" + toDate;
        var column = ['totalRowCount','transactionNo','exchangeCode','beneficiaryName','beneficiaryAccount','bankName','bankCode','branchName','branchCode','remitterName','voucherDate','amount'];
        var cols = DataTableColumns(column);
        $("#dataSection").show();
        get_dynamic_dataTable(tbl, url, cols);
    }

    function download_btn(from_date, to_date){
        var url = "/downloadDetailsOfDailyStatement?type=pdf&fromDate="+from_date + "&toDate=" + to_date;
        var url2 = "/downloadDetailsOfDailyStatement?type=txt&fromDate="+from_date + "&toDate=" + to_date;
        var ret = '<a id="downloadBtn1" href="' + url + '" class="btn btn-info">Download Search as PDF</a>';
        ret += '<a id="downloadBtn2" href="' + url2 + '" class="btn btn-danger">Download Search as CSV</a>';
        $("#download_btn").html(ret);
    }

});