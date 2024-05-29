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

function downloadOnline() {
    window.location="/downloadonline";
    
    var url = "/downloadonline";
    $.ajax({
        url: url,
        type: "get",
        timeout: "10000",
    }).done(function(resp,status,xhr){
        var cnt = xhr.getResponseHeader("count");
        //$("#onlineCount").text(cnt);
        $("p.onlineCount").text(cnt);
        console.log(cnt);
    }).fail(function(params){
        alert("Error geeting from server");
    });
}

    
function downloadCoc() {
    window.location="/downloadcoc";

}
function downloadAccountPayee() {
    window.location="/downloadaccountpayee";

}
function downloadBeftnMain() {
    window.location="/downloadbeftnMain";

}
function  downloadBeftnIncentive(){
    window.location="/downloadBeftnIncentive";
}