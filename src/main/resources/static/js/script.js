$(document).ready(function(){
    $.ajax({
        type: "GET",
        url: "/adminDashboard",
        context: document.body,
        success: function(count){
            $("p.onlineCount").html(count[0]);    //in index.html add a div tag with class="show-part-view"
            $("p.cocCount").html(count[1]);
            $("p.accountPayeeCount").html(count[2]);
            $("p.beftnMainCount").html(count[3])
            $("p.beftnIncentiveCount").html(count[4]);
        }
    });
});


function get_cnt(url,tdiv){
    $.ajax({
        url: url,
        type: "get",
        timeout: "10000",
        dataType: "json"
    }).done(function(resp){
        $(tdiv).text(resp.count);
        window.open(resp.url,"_");
    }).fail(function(params){
        alert("Error getting from server");
    });
}

function downloadOnline() {
    console.log("Button clicked");
    var url = "/downloadonline";
    get_cnt(url,"p.onlineCount");
}
/*
$(document).off('click','.download_online');
$(document).on('click','.download_online', function(e){
    e.preventDefault();
    console.log("Button clicked");
    var url = "/downloadonline";
    get_cnt(url,"p.onlineCount");
});
*/
    
function downloadCoc() {
    var url = "/downloadcoc";
    get_cnt(url,"p.cocCount");

}
function downloadAccountPayee() {
    var url = "/downloadaccountpayee";
    get_cnt(url,"p.accountPayeeCount");

}
function downloadBeftnMain() {
    var url ="/downloadbeftnMain";
    get_cnt(url,"p.beftnMainCount");
}
function  downloadBeftnIncentive(){
    var url ="/downloadBeftnIncentive";
    get_cnt(url,"p.beftnIncentiveCount");
}
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
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
        get_ajax(url,data,success_modal,fail_func,"post","json",params);
    });
}

function delete_error(tbl, csrf_token, csrf_header){
    $(document).off('click',".delete_error");
    $(document).on('click',".delete_error",function(e){
        e.preventDefault();
        var id = $(this).attr("id");
        var url  = "/error/delete/" + id;
        var params = {'reload': true, 'tbl': tbl, 'modal_hide': 'true', 'modalID': 'myModal' };
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

