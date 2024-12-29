$(document).ready(function (){
    $(document).off('change','#report_date');
    $(document).on('change','#report_date', function(e){
        e.preventDefault();
        var val = $(this).val();
        if(!val)    return false;
        var url = "/showDailyReimbursement?date=" + val;
        $.ajax({
            url: url,
        }).done(function (resp){
            console.log(resp);
        }).fail(function (){
            alert("Error getting from server");
        })
    });
});