$(document).ready(function(){
    var tbl = "#settlement_tbl";
    var url = "/utils/getSettlement";
    var cols = ["sl", "currentDate", "exchangeName", "action"];
    var sfunc = [generate_process_report_btn];
    get_dataTable(url, tbl, cols, sfunc);

    function generate_process_report_btn(resp){
        if(resp.generateBtn == "1"){
            var btn = '<button type="button" class="btn btn-sm btn-danger process_report">Process Report</button>';
            $("#reportBtn").html(btn);
        }
    }

    $(document).off('click',".process_report");
    $(document).on('click',".process_report", function(e){
        e.preventDefault();
        var url = "/processReport";
        get_ajax(url,"",success_alert, fail_func, "get");
    });
});