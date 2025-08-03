function GenerateDetailsReport(format, date) {
    var url = '/downloadDetailsOfDailyStatement?type='+format + "&date=" + date;
    window.location.href = url;
}

$( function() {
    $(document).on('focusin',".dpicker",function(){
      $(this).datepicker({
        changeMonth: true,
        changeYear: true,
        dateFormat: "yy-mm-dd",
        minDate: "-100Y",
        maxDate: "+0d",
        yearRange: "1900:2035" 
      });
    });
});

function edit_error_data(tbl){
    $(document).off('click',".edit_error");
    $(document).on('click',".edit_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var params = { tdiv: '.modal-body'};
        var mparams = { 'modalID': 'myModal', 'modal_wrap':'#modal_wrap','modal_class':'modal-md', 'modal_title': 'Edit Error Data' };
        var url = "/error/editForm/" + id;
        gen_modal(url,params,mparams);
    });
    $(document).on('submit', "#editErrorForm", function(e){
        e.preventDefault();
        var data = $(this).serialize();
        var url = "/error/update";
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal','pagination': false };
        get_ajax(url,data,success_modal,fail_func,"post","json",params);
    });
}

function delete_error(tbl, csrf_token, csrf_header){
    $(document).off('click',".delete_error");
    $(document).on('click',".delete_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url  = "/error/delete/" + id;
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal','pagination': false };
        var data = {'_csrf': csrf_token, '_csrf_header': csrf_header};
        if(confirm("Are you sure you want to delete this data?")){
            get_ajax(url,data,success_modal,fail_func,"DELETE","json",params);
        }
    });
}

function get_loading(){
    $body = $("body");
    $(document).on({
      ajaxStart: function() { $body.addClass("loading");    },
      ajaxStop: function() { $body.removeClass("loading"); }    
    });
  }

