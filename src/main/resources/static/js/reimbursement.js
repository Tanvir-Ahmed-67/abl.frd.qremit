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
        var url = "/downloadDailyReimbursementForGovtIncentive?fromDate="+from_date + "&toDate=" + to_date;
        var url2 = "/downloadClaimForCoc?fromDate="+from_date + "&toDate=" + to_date;
        var url3 = "/downloadDailyReimbursementForAgraniIncentive?fromDate="+from_date + "&toDate=" + to_date;
        var ret = '<a id="downloadBtn1" href="' + url + '" class="btn btn-info">Download Reimbursement For Govt Inc.</a>';
        ret += '<a id="downloadBtn2" href="' + url2 + '" class="btn btn-danger">Download Claim For COC</a>';
        ret += '<a id="downloadBtn3" href="' + url3 + '" class="btn btn-info">Download Reimbursement For Agrani Inc.</a>';
        $("#download_btn").html(ret);
    }
});