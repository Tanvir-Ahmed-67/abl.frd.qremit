$(document).ready(function(){
    $.ajax({
        type: "GET",
        url: "/adminDashboard",
        context: document.body,
        success: function(){
            var onlineCount = '${onlineCount}';
            $("p.onlineCount").html(10);//in index.html add a div tag with class="show-part-view"
            $("p.cocCount").html(20);
            $("p.accountPayeeCount").html(30);
            $("p.beftnCount").html(40);
        }
    });
});
function downloadCoc() {
    window.location="/downloadcoc";

}
function downloadAccountPayee() {
    window.location="/downloadaccountpayee";

}
function downloadOnline() {
    window.location="/downloadonline";

}
function downloadBeftn() {
    window.location="/downloadbeftn";

}

function  downloadBeftnIncentive(){
    window.location="/downloadBeftnIncentive";
}