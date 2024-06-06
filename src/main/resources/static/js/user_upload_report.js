$(document).ready(function(){
    function get_user_upload_report(params){
        var tbl = '#user_upload_tbl';
        var url = "/report";

        $.ajax({
            "url" : url,
        }).done(function(resp){
            console.log(resp);
            get_simple_dataTable(tbl,resp.columns,resp);
        }).fail(function(){
            alert("Eroor getting from server");
        });
    }
    get_user_upload_report("");
});