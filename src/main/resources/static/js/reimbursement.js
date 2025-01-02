$(document).ready(function () {
    var tbl = "#remimbursement_tbl";
    $('#showDataButton').on('click', function (e){
        e.preventDefault();
        var fromDate = $("#report_date").val();
        var toDate = $("#report_end_date").val();
        get_remimbursement(fromDate, toDate);

    });
    function get_remimbursement(fromDate, toDate){
        var url = "/getReimbursementData?fromDate=" + fromDate + "&toDate=" + toDate;
        var column = ['sl','exchangeCode','transactionNo','beneficiaryAccount','branchCode','branchName',
            'mainAmount','govtIncentiveAmount','agraniIncentiveAmount','type'];
        var cols = DataTableColumns(column);
        $("#dataSection").show();
        get_dynamic_dataTable(tbl, url, cols);
    }
});