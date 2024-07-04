$(document).ready(function(){
    var type = getParameterByName("type");
    var tbl = "#user_upload_tbl";
    switch(type){
        case '1':
        default:
            var url = "/report";
            break;
        case '2':
            var exchangeCode = getParameterByName("exchangeCode");
            var pid = getParameterByName("id");
            var url = "/fileReport?exchangeCode=" + exchangeCode + "&id=" + pid;
            break;
    }
    var params = {'tbl': tbl,'url': url};

    function get_user_upload_report(params){
        $.ajax({
            "url" : params.url,
        }).done(function(resp){
            get_simple_dataTable(params.tbl,resp.columns,resp);
        }).fail(function(){
            alert("Eroor getting from server");
        });
    }
    get_user_upload_report(params);

    $(document).off('click');
    $(document).on('click',".view_exchange",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var exCode = $("#exCode_"+ id).val();
        var val = $(exCode).val();
        var url = "/user-home-page?type=2&exchangeCode=" + exCode + "&id=" + id;
        window.location.href = url;
    });
});