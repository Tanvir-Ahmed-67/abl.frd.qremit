$(document).ready(function(){
    $.ajax({ url: "/adminDashboard",
        context: document.body,
        success: function(){
            alert("done");
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