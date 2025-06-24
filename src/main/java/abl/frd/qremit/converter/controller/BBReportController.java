package abl.frd.qremit.converter.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.service.BBReportService;
import abl.frd.qremit.converter.service.CommonService;
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/bbReport")
public class BBReportController {
    @Autowired
    BBReportService bbReportService;
    @PersistenceContext
    private EntityManager entityManager;
    protected String tbl = "analytics_abl_target_achievement";
    protected String tbl2 = "analytics_all_bank_remittance";
    protected String tbl4 = "analytics_abl_country_wise";
    protected String tbl5 = "analytics_abl_exchange_wise_remittance";

    @GetMapping
    public String defaultRedirect() {
        return "redirect:/bbReport/index";
    }

    @GetMapping("/index")
    public String index(){
        return "report/bbReport";
    }

    @GetMapping(value="/getReportColumn", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getReportColumnUrl(String type){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, String>> column = getReportColumn(type);
        resp.put("column", column);
        return resp;
    }

    public static List<Map<String, String>> getReportColumn(String type){
        String[] columnData = null;
        String[] columnTitles = null;
        switch(type){
            case "1":
            default:
                columnData = new String[] {"year", "target", "achievement","percentage"};
                columnTitles = new String[] {"Year", "Target", "Achievement","Percentage"};
                break;
            case "2":
                columnData = new String[] {"sl","bankName", "amount","share"};
                columnTitles = new String[] {"SL", "Bank Name", "Amount","Market Share(%)"};
                break;
            case "3":
                columnData = new String[] {"sl","bankName", "totalAmount", "share"};
                columnTitles = new String[] {"SL", "Bank Name","Amount", "Market Share (%)"};
                break;
            case "4":
                columnData = new String[] {"sl","countryName", "amount"};
                columnTitles = new String[] {"SL", "Country Name", "Amount"};
                break;
            case "5":
                columnData = new String[] {"sl","exchangeName", "amount"};
                columnTitles = new String[] {"SL", "Exchange Name", "Amount"};
                break;
        }
        return CommonService.createColumns(columnData, columnTitles);
    }

    @GetMapping(value="/getYearlyTarget", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getYearlyTarget(){
        Map<String, Object> resp = new HashMap<>();
        String sql = "SELECT * FROM " + tbl + " ORDER BY year desc";
        List<Object[]> resultList = entityManager.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        for(Object[] row: resultList){
            Map<String, Object> data = new HashMap<>();
            data.put("sl", i++);
            data.put("year", String.valueOf(row[1]));
            data.put("target", String.valueOf(row[2]));
            data.put("achievement", String.valueOf(row[3]));
            data.put("percentage", String.valueOf(row[4]) + "%");
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }

    @GetMapping(value = "/getBankWiseYear", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getBankWiseYear(@RequestParam String year){
        Map<String, Object> resp = new HashMap<>();
        String sql = "SELECT * FROM " + tbl2 + " WHERE year='" + year + "' ORDER BY CAST(amount AS SIGNED) DESC LIMIT 0,10";
        List<Object[]> resultList = entityManager.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        for(Object[] row: resultList){
            Map<String, Object> data = new HashMap<>();
            data.put("sl", i++);
            data.put("year", String.valueOf(row[2]));
            data.put("bankName", String.valueOf(row[1]));
            data.put("amount", String.valueOf(row[3]));
            data.put("share", String.valueOf(row[4]));
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }

    @GetMapping(value = "/getBankWiseCurrentYear", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getBankWiseCurrentYear(){
        Map<String, Object> resp = new HashMap<>();
        Object result = bbReportService.getBankWiseRemittanceCurrentYear("sum(CAST(amount AS DECIMAL(10,2))) as total", resp, "", true);
        Double total = CommonService.convertStringToDouble(result.toString());
        String field = "*, sum(CAST(amount AS DECIMAL(10,2))) AS totalAmount, MAX(STR_TO_DATE(month_week, '%d-%M-%Y')) AS latestDate";
        String extra = "GROUP BY bank_code ORDER BY totalAmount DESC LIMIT 0,10";
        List<Object[]> resultList = (List<Object[]>) bbReportService.getBankWiseRemittanceCurrentYear(field, resp, extra, false);
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        for(Object[] row: resultList){
            Map<String, Object> data = new HashMap<>();
            String totalAmount = String.valueOf(row[9]);
            Double marketShare = (CommonService.convertStringToDouble(totalAmount)/ total) * 100;
            data.put("sl", i++);
            data.put("year", String.valueOf(row[4]));
            data.put("bankName", String.valueOf(row[2]));
            data.put("totalAmount", totalAmount);
            data.put("month", String.valueOf(row[5]));
            data.put("share", CommonService.convertNumberFormat(marketShare,2)+"%");
            data.put("date", String.valueOf(row[10]));
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }

    @GetMapping(value = "/getCountryWiseYear", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getCountryWiseYear(@RequestParam String year){
        Map<String, Object> resp = new HashMap<>();
        String sql = "SELECT * FROM " + tbl4 + " WHERE year='" + year + "' and country_name != 'OTHER' ORDER BY CAST(usd_million as decimal(10,2)) DESC LIMIT 0,10";
        List<Object[]> resultList = entityManager.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        for(Object[] row: resultList){
            Map<String, Object> data = new HashMap<>();
            data.put("sl", i++);
            data.put("countryName", String.valueOf(row[1]));
            data.put("amount", String.valueOf(row[4]));
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }

    @GetMapping(value = "/getExchangeWiseYear", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getExchangeWiseYear(@RequestParam String year){
        Map<String, Object> resp = new HashMap<>();
        String sql = "SELECT ex_name, sum(CAST(usd_amount as decimal(10,2)))/1000000 as total_usd FROM " + tbl5 + " WHERE year='" + year + "' GROUP by ex_name ORDER BY total_usd DESC LIMIT 0,10";
        List<Object[]> resultList = entityManager.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> dataList = new ArrayList<>();
        int i = 1;
        for(Object[] row: resultList){
            Map<String, Object> data = new HashMap<>();
            data.put("sl", i++);
            data.put("exchangeName", String.valueOf(row[0]));
            data.put("amount", String.valueOf(row[1]));
            dataList.add(data);
        }
        resp.put("data", dataList);
        return resp;
    }
    
    @GetMapping("/upload")
    public String uploadBbReportUi(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/login";
        }
        Map<String, Object> dropdown = getBbReportDropdown();
        model.addAttribute("dropdown", dropdown);
        return "report/bb_report_upload";
    }

    public Map<String, Object> getBbReportDropdown(){
        Map<String, Object> resp = new HashMap<>();
        resp.put("1","Bangladesh Bank Monthly Report");
        return resp;
    }
    
    @PostMapping(value="/uploadReport", produces = "application/json")
    @ResponseBody
    public Map<String, Object> uploadBbReport(@AuthenticationPrincipal MyUserDetails userDetails,@RequestParam("file") MultipartFile file,@RequestParam Map<String, String> formData, Model model){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
            return CommonService.getResp(1, "Session Expired. Please Login again", null);
        }
        String uType = formData.get("uType");
        if(uType.isEmpty())     return CommonService.getResp(1, "Please select Upload Type", null);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
         CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            if(uType.equals("1")){
                resp = processBbMonthlyData(csvRecords);
            }else CommonService.getResp(1, "Invalid Upload Type", null);
        }catch(IOException e){
            e.printStackTrace();
            resp = CommonService.getResp(1, e.getMessage(), null);
        }
        return resp;
    }
    
    public Map<String, Object> processBbMonthlyData(Iterable<CSVRecord> csvRecords){
        Map<String, Object> resp = new HashMap<>();
        for(CSVRecord csvRecord:csvRecords){
            String bankCode = csvRecord.get(1).trim();
            String year= csvRecord.get(4).trim();
            String month = csvRecord.get(5).trim();
            if(bankCode.isEmpty() || year.isEmpty() || month.isEmpty()) return CommonService.getResp(1,"Year or month or bank is empty", null);
            Map<String, Object> where = new HashMap<>();
            where.put("year", year);
            where.put("month", month);
            where.put("bank_code", bankCode);

            Map<String, Object> field = new HashMap<>();
            field.put("year", year);
            field.put("month", month);
            field.put("bank_code", bankCode);
            field.put("bank_name", csvRecord.get(2).trim());
            field.put("bank_category", csvRecord.get(3).trim());
            field.put("month_week", csvRecord.get(6).trim());
            field.put("amount", csvRecord.get(7).trim());
            field.put("market_share", csvRecord.get(8).trim());
            
            resp = bbReportService.addBankWiseRemittanceCurrentYear(field, where);
        }
        return resp;
    }

}
