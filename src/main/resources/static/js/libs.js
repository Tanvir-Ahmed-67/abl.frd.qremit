function checkDataTable(tbl){
    if ( $.fn.DataTable.isDataTable(tbl) ) {
      $(tbl).DataTable().destroy();
    }
  }

  function DataTableColumns(cols){
    var data = [];
    var keys;
    for(var i in cols){
      keys = { "data" : cols[i]};
      data.push(keys);
    }
    return data;
  }

  function get_dataTable(url,tbl,cols,successfunc,drawfunc,PostParams = [], RequestType = 'get',exportTbl){
    checkDataTable(tbl);
    if(!url) return false;
    var data = DataTableColumns(cols);
    var oSettings = {
      "ajax": {
        "url": url,
        "type": RequestType,
	      "data": PostParams,
      },
      "columns": data,
      "sort": false,
      "initComplete":function(settings,json){
        if(successfunc){
          for(var i in successfunc)  successfunc[i](json);
        }
      },
    };
    if(exportTbl){
      oSettings.dom = 'Bfrtip';
      oSettings.buttons = ['copy', 'csv','pdf', 'print'];
    }
    $(tbl).DataTable(oSettings);
   }

   /*
   * datTable generation without ajax 
   */
  function get_simple_dataTable(tbl,columns,resp){
    checkDataTable(tbl);
    $(tbl).DataTable({
      "columns": columns,
      "data": resp.data,
    });
 
  }


    /*
   * Improved Ajax Method
   * success_fun means succes method to be called after ajax succes
   * fail_fun means failed method to be called after ajax fail
   * params means parameter of sfunc
   * url means which url ajax to be called
   * type means get/post/put/delete method
   * data means passing data for ajax
   * dataType means dataType of content type ex -json,html
   * timeout means ajax timeout (default- 10s)
   */

  function get_ajax(url,data,success_fun,fail_fun,type,dataType,params,timeout){
    if(!url) return false;
    type = (type) ? type:"get";
    data = (data) ? data : "";
    dataType = (dataType) ? dataType : "";
    timeout = (timeout) ? timeout:10000;
    params = (params) ? params:'';
    

    $.ajax({
      url: url,
      type: type,
      dataType: dataType,
      data: data,
      timeout: timeout,
    }).done(function(resp){
      //console.log(resp);
      success_fun(resp,params)
    }).fail(function(params){
      fail_fun(params);
    });

  }

  function getParameterByName(name, url) {
    if (!url) {
      url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return '';
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
  }

  function loadScript(url,id){
    document.addEventListener('DOMContentLoaded', function() {
      var id = document.getElementById(id);
      //var file = "/js/fileUpload.js";
      var scriptElement = document.createElement('script');
      scriptElement.type = 'text/javascript';
      scriptElement.src = url;
      document.querySelector('footer').appendChild(scriptElement);
    });
  }
