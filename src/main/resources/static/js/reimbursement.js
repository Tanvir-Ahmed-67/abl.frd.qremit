$(document).ready(function () {
    get_loading();
    var tbl = "#remimbursement_tbl";
    $('#showDataButton').on('click', function (e){
        e.preventDefault();
        var fromDate = $("#report_date").val();
        var toDate = $("#report_end_date").val();
        get_remimbursement(fromDate, toDate);
        download_btn(fromDate, toDate);

    });
    function get_remimbursement(fromDate, toDate){
        var url = "/getReimbursementData?fromDate=" + fromDate + "&toDate=" + toDate;
        var column = ['sl','exchangeCode','transactionNo','beneficiaryAccount','branchCode','branchName',
            'mainAmount','govtIncentive','agraniIncentive','type'];
        var cols = DataTableColumns(column);
        $("#dataSection").show();
        get_dynamic_dataTable(tbl, url, cols);
    }
    function download_btn(from_date, to_date){
        var url = "/downloadDailyReimbursement?fromDate="+from_date + "&toDate=" + to_date;
        var url2 = "/downloadDailyReimbursementForIcash?fromDate="+from_date + "&toDate=" + to_date;
        var ret = '<a id="downloadBtn1" href="' + url + '" class="btn btn-info">Download Daily Reimbursement</a>';
        ret += '<a id="downloadBtn2" href="' + url2 + '" class="btn btn-danger">Download Reimbursement For ICash</a>';
        $("#download_btn").html(ret);
    }
});