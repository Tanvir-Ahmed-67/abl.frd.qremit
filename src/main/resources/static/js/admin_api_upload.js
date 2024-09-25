$(document).ready(function(){
    var tbl = "#settlement_tbl";
    var url = "/utils/getSettlement";
    var cols = ["sl", "currentDate", "exchangeName", "action"];
    get_dataTable(url, tbl, cols);
});