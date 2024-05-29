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
    window.location = url;
    $.ajax({
        url: url,
        type: "get",
        timeout: "10000",
    }).done(function(resp,status,xhr){
        var cnt = xhr.getResponseHeader("count");
        $(tdiv).text(cnt);
    }).fail(function(params){
        alert("Error geeting from server");
    });
}

function downloadOnline() {
    var url = "/downloadonline";
    get_cnt(url,"p.onlineCount");
}

    
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