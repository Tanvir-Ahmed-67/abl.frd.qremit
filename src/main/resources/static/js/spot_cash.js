$(document).ready(function(){
    get_loading();
    $('#display').hide();
    $('#alert_msg').hide();
    var tbl = "#spot_cash_tbl";
    $('form').on('submit', function(e){
        e.preventDefault();
        var data = new FormData($(this)[0]);
        $.ajax({
            url: "/spotcash/upload",
            data: data,
            type: 'post',
            contentType: false,
            processData: false,
        }).done(function(resp){
            var msg = "";
            if(resp.msg || resp.errorMessage){
                msg = (resp.msg || resp.errorMessage);
                $('#alert_msg').show();
                $('#alert_msg').html(msg);
            }
            if(resp.err == 1){
                return;
            }
            //if(resp.err == 0)   window.location.href = "/user-home-page?type=16";
            console.log(resp);
            $('#up-form').hide();
            $('#display').show();
            var cols = ["fileName","spotCashCount","errorCount","totalCount","totalAmount","action"];
            var columns = DataTableColumns(cols);
            for(var i in resp.data){
                resp.data[i]['action'] ="";
            }
            console.log(resp.data);
            get_simple_dataTable(tbl,columns, resp);
        });
    });
});