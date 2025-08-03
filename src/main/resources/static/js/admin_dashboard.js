$(document).ready(function(){
  window.targetAchievementChart = null;
  get_loading();
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

  getDailyProcessedDataByDate();
  
  function getDailyProcessedDataByDate(){
    var url = "/getDailyProcessedDataByDate";
    var params = {};
    get_ajax(url,"", get_processed_data,fail_func,"get","json",params);
  }
  
  function get_processed_data(resp, params){
    if(resp.err == 1) alert(resp.msg);
    var data = resp.data;
    $('#onlineProcessedCount').text(data.online.count);
    $('#onlineProcessedAmount').text(data.online.amount);
    $('#accountPayeeProcessedCount').text(data.accountPayee.count);
    $('#accountPayeeProcessedAmount').text(data.accountPayee.amount);
    $('#cocProcessedCount').text(data.coc.count);
    $('#cocProcessedAmount').text(data.coc.amount);
    $('#beftnMainProcessedCount').text(data.beftnMain.count);
    $('#beftnMainProcessedAmount').text(data.beftnMain.amount);
  }

  $(document).off('click','.downloadBtn');
  $(document).on('click','.downloadBtn', function(e){
    e.preventDefault();
    var id = $(this).attr('id');
    switch(id){
      case 'online':
        downloadOnline();
        break;
      case 'accountpayee':
        downloadAccountPayee();
        break;
      case 'coc':
        downloadCoc();
        break;
      case 'beftn':
        downloadBeftnMain();
        break;
      case 'beftn_incentive':
        downloadBeftnIncentive();
        break;  
    }
  });
  function downloadAccountPayee() {
    var url = "/downloadaccountpayee";
    get_cnt(url,"p.accountPayeeCount");
  }
  function downloadOnline() {
    var url = "/downloadonline";
    get_cnt(url,"p.onlineCount");
  }
    
  function downloadCoc() {
    var url = "/downloadcoc";
    get_cnt(url,"p.cocCount");
  }

  function downloadBeftnMain() {
    var url ="/downloadbeftnMain";
    get_cnt(url,"p.beftnMainCount");
  }
  function  downloadBeftnIncentive(){
    var url ="/downloadBeftnIncentive";
    get_cnt(url,"p.beftnIncentiveCount");
  }
  
  function get_cnt(url,tdiv){
    $.ajax({
        url: url,
        type: "get",
        timeout: "1000000",
        dataType: "json"
    }).done(function(resp){
        $(tdiv).text(resp.count);
        window.location.href = resp.url;
        getDailyProcessedDataByDate();
    }).fail(function(params){
        alert("Error getting from server");
    });
  }
});


  