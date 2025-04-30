$(document).ready(function () {
    get_loading();
    var tbl = "#cmo_tbl";
    $('#showDataButton').on('click', function (e){
        e.preventDefault();
        var fromDate = $("#report_date").val();
        var toDate = $("#report_end_date").val();
        get_cmo(fromDate, toDate);
        download_btn(fromDate, toDate);

    });
    function get_cmo(fromDate, toDate){
        var url = "/getCmoData?fromDate=" + fromDate + "&toDate=" + toDate;
        var column = ['sl','moDate','moNumber','totalRemittances','totalAmount'];
        var cols = DataTableColumns(column);
        $("#dataSection").show();
        get_dynamic_dataTable(tbl, url, cols);
    }
    function download_btn(from_date, to_date){
        var url = "/downloadCmoInExcelFormat?fromDate="+from_date + "&toDate=" + to_date;
        var ret = '<a id="downloadBtn1" href="' + url + '" class="btn btn-info">Download CMO in Excel Format</a>';
        $("#download_btn").html(ret);
    }
});