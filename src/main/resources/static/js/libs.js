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

  function get_dataTable(url,tbl,cols,successfunc,drawfunc,PostParams = [], RequestType = 'get',exportTbl,paging = true, searching = true, info = true, lengthChange = true){
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
      "paging": paging,
      "searching": searching,
      "info": info,
      "lengthChange": lengthChange,
      "initComplete":function(settings,json){
        if(successfunc){
          for(var i in successfunc)  successfunc[i](json);
        }
        if(exportTbl) $('.dt-buttons button').removeClass('btn-secondary').addClass('btn-info');
      },
    };
    if(exportTbl){
      oSettings.layout = {
        topStart: {
          buttons:['copy','csv','excel','print'],
        }
      };
    }
    $(tbl).DataTable(oSettings);
   }

   /*
   * datTable generation without ajax 
   */
  function get_simple_dataTable(tbl,columns,resp,sort){
    checkDataTable(tbl);
    $(tbl).DataTable({
      "columns": columns,
      "data": resp.data,
      "sort": sort,
    });
 
  }

  function get_dynamic_dataTable(tbl,url,columns,sfun,lazy, RequestType = 'get', PostParams = [],exportTbl, paging = true, searching = true, info = true, lengthChange= true){
    checkDataTable(tbl);
    var data = [];
    var func = [];
    var keys = '';
    for(var i in columns) {
      keys = columns[i].data;
      data.push(keys);
    }
    $(tbl).DataTable({"columns": columns });
    get_dataTable(url,tbl,data,sfun,'',PostParams,RequestType,exportTbl, paging, searching, info, lengthChange);
  }

  function dataTable_reload(tbl,pagination = true){
    //if pagination = false user paging is not reset on reload
    $(tbl).DataTable().ajax.reload(null,pagination);
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
    timeout = (timeout) ? timeout:100000;
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
      var scriptElement = document.createElement('script');
      scriptElement.type = 'text/javascript';
      scriptElement.src = url;
      document.querySelector('footer').appendChild(scriptElement);
    });
  }

  function modal_ui(params, data){ 
    var modal_obj = {
      'modal_title': params.modal_title,
      'id' : params.modalID,
      'modal_content': params.content,
      'modal_class': params.modal_class,
    };
    var modal = ui.modal(modal_obj);
    $(params.modal_wrap).html(modal);
    $('#' + params.modalID).modal('show');
    if(params.sfunc)  params.sfunc(data);
  }
  
  function gen_modal(url,params,mparams,data){
    var cont = "";
    $.ajax({
      url: url,
    }).done(function(resp){
      mparams.content = resp;
      modal_ui(mparams, data);
    }).fail(function(){
      alert("Error getting from server");
    });
  }
    
  function success_modal(resp,params){
    alert(resp.msg);
    if(resp.err == 0)   $('#' + params.modalID).modal('hide');
    if(params.failModalhide)  $('#' + params.modalID).modal('hide');
    var pagination = (params.pagination !== undefined) ? params.pagination: true;
    if(params.reload) dataTable_reload(params.tbl, pagination);
  }

  function fail_func(data){
    alert("Error getting from server");
  }

  function success_alert(resp,params){
    //console.log(resp);
    if(resp.err == 0 && params.success_alert == ""){
    }else{ 
      alert(resp.msg);
    }
    if(resp.err == 0){
      if(params.success_redirect === 'true'){
        window.location.href = resp.url;
      }
      if(params.success_reload === 'true'){
        location.reload();
      }
      if(params.modal_hide === 'true'){
        $(params.modalID).modal('hide');
      }
      if(params.dataTable_reload === 'true'){
        var pagination = (params.pagination !== undefined) ? params.pagination: true;
        dataTable_reload(params.tbl, pagination);
      }
      if(params.form_reset === 'true'){
        $('form')[0].reset();
      }
    }
  }

  var ui = {
    modal: function(obj){
      var ret = '';
      modal_class = (obj.modal_class) ? obj.modal_class:'';
      ret += '<div id="'+obj.id+'" class="modal fade" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">';
        ret += '<div class="modal-dialog '+modal_class+'">';
          ret += '<div class="modal-content">';
            ret += '<div class="modal-header">';
              ret += '<h4 class="modal-title" id="exampleModalLabel">'+ obj.modal_title +'</h4>';
              ret += '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>';
            ret += '</div>';
            ret += '<div class="modal-body">';
              ret += obj.modal_content;
            ret +='</div>';
            ret += '<div class="modal-footer">';
              ret += '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>';
            ret += '</div>';
          ret += '</div>';
        ret +='</div>';
      ret += '</div>';
      return ret;
    },
  }