$(document).ready(function(){
    //get_loading();
    var type = getParameterByName("type");
    var year = '';

    function get_bb_report_url(type, year){
        var page_header = "";
        switch(type){
            case '1':
            default:
                var url = "/bbReport/getYearlyTarget";
                var tbl = "#yearly_target_tbl";
                break;
            case '2':
                var url = "/bbReport/getBankWiseYear?year=" + year;
                var tbl = "#bank_wise_2024_tbl";
                break;
            case '3':
                var url = "/bbReport/getBankWiseCurrentYear";
                var tbl = "#bank_wise_2025_tbl";
                break;
            case '4':
                var url = "/bbReport/getCountryWiseYear?year=" + year;
                var tbl = "#country_wise_2024_tbl";
                break;
            case '5':
                var url = "/bbReport/getExchangeWiseYear?year=" + year;
                var tbl = "#exchange_wise_2024_tbl";
                break;
        }
        return {'url': url, 'page_header': page_header, 'tbl': tbl};
    }

    var url = "/bbReport/getReportColumn?type=" + type;
    bb_report_ui(url,type, year);
    //type = 2
    bb_report_ui("/bbReport/getReportColumn?type=2","2","2024");
    //type = 3
    bb_report_ui("/bbReport/getReportColumn?type=3","3",year);
    //type -4
    bb_report_ui("/bbReport/getReportColumn?type=4","4","2024");
    //type -5
    bb_report_ui("/bbReport/getReportColumn?type=5","5","2024");

    function bb_report_ui(url,type, year){
        var rdata = get_bb_report_url(type, year);
        if(rdata.page_header)  $(".page-header").html(rdata.page_header);
        var report_url = rdata.url;
        var params = {'tbl': rdata.tbl,'url': report_url, 'type': type};
        get_ajax(url,"",get_bb_report,fail_func,"get","json",params);
    }

    function get_bb_report(resp,params){
        var sfun = [view_bb_report];
        get_dynamic_dataTable(params.tbl, params.url, resp.column, sfun,"","get",[], false, false, false, false);
    }

    function view_bb_report(resp){
        if(resp.data[0].date){
            $('.data_update').html(" Updated as on " + resp.data[0].date);
        }
    }
});